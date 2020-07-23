package com.instantnannies.user.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.iid.FirebaseInstanceId;
import com.instantnannies.user.Helper.ConnectionHelper;
import com.instantnannies.user.Helper.SharedHelper;
import com.instantnannies.user.Helper.URLHelper;
import com.instantnannies.user.R;
import com.instantnannies.user.Utils.CustomDialog;
import com.instantnannies.user.Utils.CustomRequestQueue;
import com.instantnannies.user.Utils.Utilities;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class CodeVerificationActivity extends AppCompatActivity {

    Button verify_code;
    String mVerificationId;
    private FirebaseAuth mAuth;
    EditText otp_et;
    Utilities utils = new Utilities();
    String TAG = "CodeVerificationActivity";
    String device_token, device_UDID;
    CustomDialog customDialog;
    String first_name,last_name,email,password,phone_number;
    ConnectionHelper helper;
    Boolean isInternet;
    //ArrayList<Children> childrenArrayList;
    String child_age,no_of_children;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_code_verification);
        viewInitializer();
    }

    void viewInitializer(){
        verify_code = findViewById(R.id.verify_code);
        otp_et = findViewById(R.id.otp);
        Intent i = getIntent();
        mVerificationId = i.getStringExtra("mVerificationId");
        first_name = i.getStringExtra("first_name");
        last_name = i.getStringExtra("last_name");
        email = i.getStringExtra("email");
        password = i.getStringExtra("password");
        phone_number = i.getStringExtra("phone_number");
        child_age = i.getStringExtra("child_age");
        no_of_children = i.getStringExtra("no_of_children");
      //  childrenArrayList   = (ArrayList<Children>) getIntent().getSerializableExtra("childlist");
        helper = new ConnectionHelper(CodeVerificationActivity.this);
        isInternet = helper.isConnectingToInternet();
        mAuth = FirebaseAuth.getInstance();
        GetToken();
        verify_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String otp = otp_et.getText().toString();
                if(otp.length()>3){
                    customDialog = new CustomDialog(CodeVerificationActivity.this);
                    customDialog.setCancelable(false);
                    if (customDialog != null)
                        customDialog.show();
                    verifyVerificationCode(otp);
                }else{
                    Toast.makeText(CodeVerificationActivity.this, "Please Enter Valid OTP", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void verifyVerificationCode(String otp) {
        //creating the credential
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, otp);

        //signing the user
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(CodeVerificationActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                         /*   if ((customDialog != null) && (customDialog.isShowing()))
                                customDialog.dismiss();*/
                            registerAPI();

                            //verification successful we will start the profile activity
                          /*  Intent intent = new Intent(CodeVerificationActivity.this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);*/

                        } else {

                            //verification unsuccessful.. display an error message
                            if ((customDialog != null) && (customDialog.isShowing()))
                                customDialog.dismiss();

                            String message = "Something is wrong, we will fix it soon...";

                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                message = "Invalid code entered...";
                            }

                            final Snackbar snackbar = Snackbar.make(getCurrentFocus(), message, Snackbar.LENGTH_LONG);
                            snackbar.setAction("Dismiss", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    snackbar.dismiss();
                                }
                            });
                            snackbar.show();
                        }
                    }
                });
    }

    public void GetToken() {
        try {
            device_token = SharedHelper.getKey(CodeVerificationActivity.this, "device_token");
            if (!device_token.equals("") && device_token != null && !device_token.equals("null")) {
                device_token = SharedHelper.getKey(CodeVerificationActivity.this, "device_token");
                utils.print(TAG, "GCM Registration Token: " + device_token);
            } else {
                device_token = ""+ FirebaseInstanceId.getInstance().getToken();
                SharedHelper.putKey(CodeVerificationActivity.this, "device_token",""+FirebaseInstanceId.getInstance().getToken());
                utils.print(TAG, "Failed to complete token refresh: " + device_token);
            }
        } catch (Exception e) {
            device_token = "COULD NOT GET FCM TOKEN";
            utils.print(TAG, "Failed to complete token refresh");
        }

        try {
            device_UDID = android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
            utils.print(TAG, "Device UDID:" + device_UDID);
        } catch (Exception e) {
            device_UDID = "COULD NOT GET UDID";
            e.printStackTrace();
            utils.print(TAG, "Failed to complete device UDID");
        }
    }

    private void registerAPI() {
        /*customDialog = new CustomDialog(CodeVerificationActivity.this);
        customDialog.setCancelable(false);
        if (customDialog != null)
            customDialog.show();*/

        JSONObject object = new JSONObject();
        try {
            object.put("device_type", "android");
            object.put("device_id", device_UDID);
            object.put("device_token", "" + device_token);
            object.put("login_by", "manual");
            object.put("first_name", first_name);
            object.put("last_name", last_name);
            object.put("email", email);
            object.put("password", password);
            object.put("mobile", phone_number);
            object.put("child_age", child_age);
            object.put("no_of_children", no_of_children);
            object.put("picture", "");
            object.put("social_unique_id", "");

        utils.print("InputToRegisterAPI", "" + object);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLHelper.register, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
              //  if ((customDialog != null) && (customDialog.isShowing()))
                    //customDialog.dismiss();
                    utils.print("SignInResponse", response.toString());
                SharedHelper.putKey(CodeVerificationActivity.this, "email", email);
                SharedHelper.putKey(CodeVerificationActivity.this, "password", password);
                signIn();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if ((customDialog != null) && (customDialog.isShowing()))
                    customDialog.dismiss();
                String json = null;
                String Message;
                NetworkResponse response = error.networkResponse;

                if (response != null && response.data != null) {
                    utils.print("MyTest", "" + error);
                    utils.print("MyTestError", "" + error.networkResponse);
                    utils.print("MyTestError1", "" + response.statusCode);
                    try {
                        JSONObject errorObj = new JSONObject(new String(response.data));

                        if (response.statusCode == 400 || response.statusCode == 405 || response.statusCode == 500) {
                            try {
                                displayMessage(errorObj.optString("message"));
                            } catch (Exception e) {
                                displayMessage(getString(R.string.something_went_wrong));
                            }
                        } else if (response.statusCode == 401) {
                            try {
                                if (errorObj.optString("message").equalsIgnoreCase("invalid_token")) {
                                    //   Refresh token
                                } else {
                                    displayMessage(errorObj.optString("message"));
                                }
                            } catch (Exception e) {
                                displayMessage(getString(R.string.something_went_wrong));
                            }

                        } else if (response.statusCode == 422) {

                            json = CustomRequestQueue.trimMessage(new String(response.data));
                            if (json != "" && json != null) {
                                if (json.startsWith("The email has already been taken")) {
                                    displayMessage(getString(R.string.email_exist));
                                }else{
                                    displayMessage(getString(R.string.something_went_wrong));
                                }
                                //displayMessage(json);
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
                        registerAPI();
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

    public void displayMessage(String toastString) {
        utils.print("displayMessage", "" + toastString);
        try{
            Snackbar.make(getCurrentFocus(), toastString, Snackbar.LENGTH_SHORT).setAction("Action", null).show();
        }catch (Exception e){
            try{
                Toast.makeText(CodeVerificationActivity.this,""+toastString,Toast.LENGTH_SHORT).show();
            }catch (Exception ee){
                ee.printStackTrace();
            }
        }
    }

    public void signIn() {
        if (isInternet) {
            /*customDialog = new CustomDialog(CodeVerificationActivity.this);
            customDialog.setCancelable(false);
            if (customDialog != null)
                customDialog.show();*/
            JSONObject object = new JSONObject();
            try {
                object.put("grant_type", "password");
                object.put("client_id", URLHelper.client_id);
                object.put("client_secret", URLHelper.client_secret);
                object.put("username", SharedHelper.getKey(CodeVerificationActivity.this, "email"));
                object.put("password", SharedHelper.getKey(CodeVerificationActivity.this, "password"));
                object.put("scope", "");
                object.put("device_type", "android");
                object.put("device_id", device_UDID);
                object.put("device_token", device_token);
                utils.print("InputToLoginAPI", "" + object);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLHelper.login, object, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
              /*      if ((customDialog != null) && (customDialog.isShowing()))
                        customDialog.dismiss();*/
                    utils.print("SignUpResponse", response.toString());
                    SharedHelper.putKey(CodeVerificationActivity.this, "access_token", response.optString("access_token"));
                    SharedHelper.putKey(CodeVerificationActivity.this, "refresh_token", response.optString("refresh_token"));
                    SharedHelper.putKey(CodeVerificationActivity.this, "token_type", response.optString("token_type"));
                    getProfile();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if ((customDialog != null) && (customDialog.isShowing()))
                        customDialog.dismiss();
                    String json = null;
                    NetworkResponse response = error.networkResponse;
                    if (response != null && response.data != null) {
                        try {
                            JSONObject errorObj = new JSONObject(new String(response.data));

                            if (response.statusCode == 400 || response.statusCode == 405 || response.statusCode == 500) {
                                try {
                                    displayMessage(errorObj.optString("message"));
                                } catch (Exception e) {
                                    displayMessage(getString(R.string.something_went_wrong));
                                }
                            } else if (response.statusCode == 401) {
                                try {
                                    if (errorObj.optString("message").equalsIgnoreCase("invalid_token")) {
                                        //Call Refresh token
                                    } else {
                                        displayMessage(errorObj.optString("message"));
                                    }
                                } catch (Exception e) {
                                    displayMessage(getString(R.string.something_went_wrong));
                                }

                            } else if (response.statusCode == 422) {

                                json = CustomRequestQueue.trimMessage(new String(response.data));
                                if (json != "" && json != null) {
                                    displayMessage(json);
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
                            signIn();
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
        } else {
            displayMessage(getString(R.string.something_went_wrong_net));
        }

    }

    public void getProfile() {
        if (isInternet) {
         /*   customDialog = new CustomDialog(CodeVerificationActivity.this);
            customDialog.setCancelable(false);
            if (customDialog != null)
                customDialog.show();*/
            JSONObject object = new JSONObject();
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URLHelper.UserProfile+"?device_type=android&device_id="+device_UDID+"&device_token="+device_token, object, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    if ((customDialog != null) && (customDialog.isShowing()))
                         customDialog.dismiss();
                    utils.print("GetProfile", response.toString());
                    SharedHelper.putKey(CodeVerificationActivity.this, "id", response.optString("id"));
                    SharedHelper.putKey(CodeVerificationActivity.this, "first_name", response.optString("first_name"));
                    SharedHelper.putKey(CodeVerificationActivity.this, "last_name", response.optString("last_name"));
                    SharedHelper.putKey(CodeVerificationActivity.this, "email", response.optString("email"));
                    SharedHelper.putKey(CodeVerificationActivity.this, "picture", URLHelper.base + "storage/" + response.optString("picture"));
                    SharedHelper.putKey(CodeVerificationActivity.this, "gender", response.optString("gender"));
                    SharedHelper.putKey(CodeVerificationActivity.this, "mobile", response.optString("mobile"));
                    SharedHelper.putKey(CodeVerificationActivity.this, "rating", response.optString("rating"));
                    SharedHelper.putKey(CodeVerificationActivity.this, "wallet_balance", response.optString("wallet_balance"));
                    SharedHelper.putKey(CodeVerificationActivity.this, "payment_mode", response.optString("payment_mode"));
                    if (!response.optString("currency").equalsIgnoreCase("") && response.optString("currency") != null)
                        SharedHelper.putKey(CodeVerificationActivity.this, "currency", response.optString("currency"));
                    else

                    SharedHelper.putKey(CodeVerificationActivity.this, "currency", "$");
                    SharedHelper.putKey(CodeVerificationActivity.this, "sos", response.optString("sos"));
                    SharedHelper.putKey(CodeVerificationActivity.this, "loggedIn", getString(R.string.True));

                    //phoneLogin();
                    GoToMainActivity();
                   /* if (!SharedHelper.getKey(activity,"account_kit_token").equalsIgnoreCase("")) {

                    }else {
                        GoToMainActivity();
                    }*/

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if ((customDialog != null) && (customDialog.isShowing()))
                        customDialog.dismiss();
                    String json = null;
                    String Message;
                    NetworkResponse response = error.networkResponse;
                    if (response != null && response.data != null) {
                        try {
                            JSONObject errorObj = new JSONObject(new String(response.data));

                            if (response.statusCode == 400 || response.statusCode == 405 || response.statusCode == 500) {
                                try {
                                    displayMessage(errorObj.optString("message"));
                                } catch (Exception e) {
                                    displayMessage(getString(R.string.something_went_wrong));
                                }
                            } else if (response.statusCode == 401) {
                                try {
                                    if (errorObj.optString("message").equalsIgnoreCase("invalid_token")) {
                                        refreshAccessToken();
                                    } else {
                                        displayMessage(errorObj.optString("message"));
                                    }
                                } catch (Exception e) {
                                    displayMessage(getString(R.string.something_went_wrong));
                                }

                            } else if (response.statusCode == 422) {

                                json = CustomRequestQueue.trimMessage(new String(response.data));
                                if (json != "" && json != null) {
                                    displayMessage(json);
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
                            getProfile();
                        }
                    }
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<String, String>();
                    headers.put("X-Requested-With", "XMLHttpRequest");
                    headers.put("Authorization", "" + SharedHelper.getKey(CodeVerificationActivity.this, "token_type") + " " + SharedHelper.getKey(CodeVerificationActivity.this, "access_token"));
                    return headers;
                }
            };

            CustomRequestQueue.getInstance(this).addToRequestQueue(jsonObjectRequest);
        } else {
            displayMessage(getString(R.string.something_went_wrong_net));
        }
    }

    private void refreshAccessToken() {


        JSONObject object = new JSONObject();
        try {

            object.put("grant_type", "refresh_token");
            object.put("client_id", URLHelper.client_id);
            object.put("client_secret", URLHelper.client_secret);
            object.put("refresh_token", SharedHelper.getKey(CodeVerificationActivity.this, "refresh_token"));
            object.put("scope", "");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLHelper.login, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                Log.v("SignUpResponse", response.toString());
                SharedHelper.putKey(CodeVerificationActivity.this, "access_token", response.optString("access_token"));
                SharedHelper.putKey(CodeVerificationActivity.this, "refresh_token", response.optString("refresh_token"));
                SharedHelper.putKey(CodeVerificationActivity.this, "token_type", response.optString("token_type"));
                getProfile();


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String json = null;
                String Message;
                NetworkResponse response = error.networkResponse;

                if (response != null && response.data != null) {
                    SharedHelper.putKey(CodeVerificationActivity.this, "loggedIn", getString(R.string.False));
                    GoToBeginActivity();
                } else {
                    if (error instanceof NoConnectionError) {
                        displayMessage(getString(R.string.oops_connect_your_internet));
                    } else if (error instanceof NetworkError) {
                        displayMessage(getString(R.string.oops_connect_your_internet));
                    } else if (error instanceof TimeoutError) {
                        refreshAccessToken();
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

    private void GoToBeginActivity() {
        if (customDialog != null && customDialog.isShowing())
            customDialog.dismiss();
        Intent mainIntent = new Intent(CodeVerificationActivity.this, LoginActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(mainIntent);
        CodeVerificationActivity.this.finish();
    }

    public void GoToMainActivity() {
        if (customDialog != null && customDialog.isShowing())
            customDialog.dismiss();
        Intent mainIntent = new Intent(CodeVerificationActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(mainIntent);
        CodeVerificationActivity.this.finish();
    }

}
