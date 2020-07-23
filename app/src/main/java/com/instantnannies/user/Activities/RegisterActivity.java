package com.instantnannies.user.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.hbb20.CountryCodePicker;
import com.instantnannies.user.Adapters.ChildsAdapter;
import com.instantnannies.user.Helper.ConnectionHelper;
import com.instantnannies.user.Helper.URLHelper;
import com.instantnannies.user.Models.Children;
import com.instantnannies.user.R;
import com.instantnannies.user.Utils.CustomDialog;
import com.instantnannies.user.Utils.CustomRequestQueue;
import com.instantnannies.user.Utils.Utilities;
import com.jaredrummler.materialspinner.MaterialSpinner;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    EditText et_phone;
    Button btn_register;
    private String mVerificationId;
    private FirebaseAuth mAuth;
    CountryCodePicker ccp;
    String selected_country_code;
    EditText email, first_name, last_name, password;
    CustomDialog customDialog;
    ConnectionHelper helper;
    Boolean isInternet;
    String TAG = "RegisterActivity";
    String phoneNumber;
    MaterialSpinner spinner_no_of_childs;
    List<String> childsList = new ArrayList<>();
    ArrayList<Children> childrenList = new ArrayList<>();
    RecyclerView childs_rv;
    ChildsAdapter Adapter;
    Utilities utils = new Utilities();

    boolean status = false;
    String child_age;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        viewInitializer();
    }

    void viewInitializer(){
        et_phone = findViewById(R.id.et_phone);
        btn_register = findViewById(R.id.btn_register);
        email = (EditText) findViewById(R.id.email);
        first_name = (EditText) findViewById(R.id.first_name);
        last_name = (EditText) findViewById(R.id.last_name);
        password = (EditText) findViewById(R.id.password);
        ccp = findViewById(R.id.ccp);
        childs_rv = findViewById(R.id.childs_rv);
        spinner_no_of_childs = findViewById(R.id.spinner_no_of_childs);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(RegisterActivity.this, 1);
        childs_rv.setLayoutManager(gridLayoutManager);
        childs_rv.setItemAnimator(new DefaultItemAnimator());
        childs_rv.setNestedScrollingEnabled(false);

        spinner_no_of_childs.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {

            @Override public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
               // Snackbar.make(view, "Clicked " + item, Snackbar.LENGTH_LONG).show();
            childrenList.clear();
            if(position>0){
                childs_rv.setVisibility(View.VISIBLE);
                for(int i = 0 ; i < Integer.valueOf(item); i++){
                    childrenList.add(new Children(String.valueOf(i+1),""));
                }
                Adapter = new ChildsAdapter(RegisterActivity.this,childrenList);
                childs_rv.setAdapter(Adapter);
            }else{
                childrenList.clear();
                Adapter = new ChildsAdapter(RegisterActivity.this,childrenList);
                childs_rv.setAdapter(Adapter);
            }

            }
        });

        childsList.add("Select no of childs");
        childsList.add("1");
        childsList.add("2");
        childsList.add("3");
        childsList.add("4");
        childsList.add("5");
        childsList.add("6");
        childsList.add("7");
        childsList.add("8");
        childsList.add("9");
        childsList.add("10");
        childsList.add("11");
        childsList.add("12");
        childsList.add("13");
        childsList.add("14");
        childsList.add("15");
        childsList.add("16");
        childsList.add("17");
        childsList.add("18");
        childsList.add("19");
        childsList.add("20");
        childsList.add("21");
        childsList.add("22");
        childsList.add("23");
        childsList.add("24");
        childsList.add("25");

        spinner_no_of_childs.setItems(childsList);



        mAuth = FirebaseAuth.getInstance();
        onCountryPickerClick(getCurrentFocus());
        selected_country_code = ccp.getDefaultCountryCodeWithPlus();
        helper = new ConnectionHelper(RegisterActivity.this);
        isInternet = helper.isConnectingToInternet();

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            childrenList.size();

            Pattern ps = Pattern.compile(".*[0-9].*");
            Matcher firstName = ps.matcher(first_name.getText().toString());
            Matcher lastName = ps.matcher(last_name.getText().toString());
            String phone = et_phone.getText().toString();

            phoneNumber = selected_country_code+phone;

            if (email.getText().toString().equals("") || email.getText().toString().equalsIgnoreCase(getString(R.string.sample_mail_id))) {
                displayMessage(getString(R.string.email_validation));
            } else if (!Utilities.isValidEmail(email.getText().toString())) {
                displayMessage(getString(R.string.not_valid_email));
            } else if (first_name.getText().toString().equals("") || first_name.getText().toString().equalsIgnoreCase(getString(R.string.first_name))) {
                displayMessage(getString(R.string.first_name_empty));
            } else if (firstName.matches()) {
                displayMessage(getString(R.string.first_name_no_number));
            } else if (last_name.getText().toString().equals("") || last_name.getText().toString().equalsIgnoreCase(getString(R.string.last_name))) {
                displayMessage(getString(R.string.last_name_empty));
            } else if (lastName.matches()) {
                displayMessage(getString(R.string.last_name_no_number));
            } else if (password.getText().toString().equals("") || password.getText().toString().equalsIgnoreCase(getString(R.string.password_txt))) {
                displayMessage(getString(R.string.password_validation));
            } else if (password.length() < 8 || password.length() > 16) {
                displayMessage(getString(R.string.password_validation1));
            } else if (!Utilities.isValidPassword(password.getText().toString().trim())) {
                displayMessage(getString(R.string.password_validation2));
            } else if(phoneNumber.isEmpty()){
                displayMessage(getString(R.string.mobile_number_empty));
            }else if(childrenList.size()>0){
                for(int i = 0; i< childrenList.size(); i++){
                    if(childrenList.get(i).getAge().isEmpty()){
                        status = false;
                        break;
                    }else {
                        status = true;
                    }
                }
                if (status){
                    if (isInternet) {
                        checkMailAlreadyExit();
                    } else {
                        displayMessage(getString(R.string.something_went_wrong_net));
                    }
                }else{
                    Toast.makeText(RegisterActivity.this, "Please Enter Ages of all Children", Toast.LENGTH_SHORT).show();
                }
            }

                /*{
                    if (isInternet) {
                        checkMailAlreadyExit();
                    } else {
                        displayMessage(getString(R.string.something_went_wrong_net));
                    }
                }*/

             /*   String phone = et_phone.getText().toString();
                PhoneAuthProvider.getInstance().verifyPhoneNumber(
                        selected_country_code+phone,        // Phone number to verify
                        60,                 // Timeout duration
                        TimeUnit.SECONDS,   // Unit of timeout
                        RegisterActivity.this,               // Activity (for callback binding)
                        mCallbacks);*/
            }
        });


    }

    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
            String code = phoneAuthCredential.getSmsCode();
            if ((customDialog != null) && (customDialog.isShowing()))
                customDialog.dismiss();
           /* if (code != null) {
                //editTextCode.setText(code);
                //verifying the code
                verifyVerificationCode(code);
            }else{
                Intent i = new Intent(RegisterActivity.this, CodeVerificationActivity.class);
                i.putExtra("mVerificationId",mVerificationId);
                startActivity(i);

            }*/
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            displayMessage(String.valueOf(e.getMessage()));
            if ((customDialog != null) && (customDialog.isShowing()))
                customDialog.dismiss();
        }
        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            if ((customDialog != null) && (customDialog.isShowing()))
                customDialog.dismiss();
            mVerificationId = s;
            PhoneAuthProvider.ForceResendingToken mResendToken = forceResendingToken;

            try {
                child_age = null;
                for(int i = 0; i < childrenList.size();i++){
                    if(child_age!=null){
                        child_age = child_age+","+childrenList.get(i).getAge();
                    }else{
                        child_age = childrenList.get(i).getAge();
                    }
                }
            }catch (Exception ex){
                ex.printStackTrace();
            }

            Intent i = new Intent(RegisterActivity.this, CodeVerificationActivity.class);
            i.putExtra("mVerificationId",mVerificationId);
            i.putExtra("first_name",first_name.getText().toString());
            i.putExtra("last_name",last_name.getText().toString());
            i.putExtra("email",email.getText().toString());
            i.putExtra("password",password.getText().toString());
            i.putExtra("phone_number",phoneNumber);
            i.putExtra("no_of_children", String.valueOf(childrenList.size()));
            i.putExtra("child_age", child_age);
            startActivity(i);
        }
    };

    public void onCountryPickerClick(View view) {
        ccp.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected() {
                //Alert.showMessage(RegistrationActivity.this, ccp.getSelectedCountryCodeWithPlus());
                selected_country_code = ccp.getSelectedCountryCodeWithPlus();
            }
        });
    }
    public void displayMessage(String toastString) {
        utils.print("displayMessage", "" + toastString);
        try{
            Snackbar.make(getCurrentFocus(), toastString, Snackbar.LENGTH_SHORT)
                    .setAction("Action", null).show();
        }catch (Exception e){
            try{
                Toast.makeText(RegisterActivity.this,""+toastString,Toast.LENGTH_SHORT).show();
            }catch (Exception ee){
                e.printStackTrace();
            }
        }
    }

    public void checkMailAlreadyExit(){
        customDialog = new CustomDialog(RegisterActivity.this);
        customDialog.setCancelable(false);
        if (customDialog != null)
            customDialog.show();
        JSONObject object = new JSONObject();
        try {
            object.put("email", email.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLHelper.CHECK_MAIL_ALREADY_REGISTERED,
                object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                String phone = et_phone.getText().toString();

                phoneNumber = selected_country_code+phone;
                PhoneAuthProvider.getInstance().verifyPhoneNumber(
                        phoneNumber,        // Phone number to verify
                        60,                 // Timeout duration
                        TimeUnit.SECONDS,   // Unit of timeout
                        RegisterActivity.this,               // Activity (for callback binding)
                        mCallbacks);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if ((customDialog != null) && (customDialog.isShowing()))
                    customDialog.dismiss();
                String json = null;
                NetworkResponse response = error.networkResponse;

                if (response != null && response.data != null) {
                    utils.print("MyTest", "" + error);
                    utils.print("MyTestError", "" + error.networkResponse);
                    utils.print("MyTestError1", "" + response.statusCode);
                    try {
                        if (response.statusCode == 422) {

                            json = CustomRequestQueue.trimMessage(new String(response.data));
                            if (json != "" && json != null) {
                                if (json.startsWith(getString(R.string.email_exist))) {
                                    displayMessage(getString(R.string.email_exist));
                                }else{
                                    displayMessage(getString(R.string.something_went_wrong));
                                }
                            } else {
                                displayMessage(getString(R.string.please_try_again));
                            }

                        } else {
                            displayMessage(getString(R.string.please_try_again));
                        }

                    } catch (Exception e) {
                        displayMessage(getString(R.string.something_went_wrong));
                    }
                } else {
                    if (error instanceof NoConnectionError) {
                        displayMessage(getString(R.string.oops_connect_your_internet));
                    } else if (error instanceof NetworkError) {
                        displayMessage(getString(R.string.oops_connect_your_internet));
                    } else if (error instanceof TimeoutError) {
                        checkMailAlreadyExit();
                    }
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("X-Requested-With", "XMLHttpRequest");
                return headers;
            }
        };

        CustomRequestQueue.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }

}
