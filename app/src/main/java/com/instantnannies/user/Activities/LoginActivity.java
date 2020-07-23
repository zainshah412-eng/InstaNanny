package com.instantnannies.user.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
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
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.iid.FirebaseInstanceId;
import com.instantnannies.user.CustomFont.CustomTextView;
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

import static com.instantnannies.user.Utils.Utilities.isValidEmail;

public class LoginActivity extends AppCompatActivity {

    Button btn_register,btn_sign_in;
    EditText email,password;
    public Context context = LoginActivity.this;
    public Activity activity = LoginActivity.this;
    ConnectionHelper helper;
    Boolean isInternet;
    CustomDialog customDialog;
    Utilities utils =new Utilities();
    String device_token, device_UDID;
    String TAG = "ActivityEmail";
    CustomTextView forgetPassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        viewInitializer();
    }
    void viewInitializer(){
        btn_register = findViewById(R.id.btn_register);
        btn_sign_in = findViewById(R.id.btn_sign_in);
        email = findViewById(R.id.et_email);
        password = findViewById(R.id.et_password);
        helper = new ConnectionHelper(context);
        isInternet = helper.isConnectingToInternet();
        forgetPassword=findViewById(R.id.forgetPassword);
        GetToken();
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            Intent i =new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(i);
            }
        });

        btn_sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(email.getText().toString().equals("") || email.getText().toString().equalsIgnoreCase(getString(R.string.sample_mail_id))){

                    displayMessage(getString(R.string.email_validation));

                }else{
                    if((!isValidEmail(email.getText().toString()))){

                        displayMessage(getString(R.string.email_validation));

                    }else{

                        if (password.getText().toString().length() == 0) {
                            displayMessage(getString(R.string.password_validation));
                        } else if (password.length() < 6) {
                            displayMessage(getString(R.string.password_size));
                        }else{
                        //    SharedHelper.putKey(LoginActivity.this,"email",email.getText().toString());
                        //    SharedHelper.putKey(context,"password",password.getText().toString());
                            signIn();
                        }

                    /*    Utilities.hideKeyboard(ActivityEmail.this);
                        SharedHelper.putKey(ActivityEmail.this,"email",email.getText().toString());
                        Intent mainIntent = new Intent(ActivityEmail.this, ActivityPassword.class);
                        startActivity(mainIntent);
                        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);*/
                    }


                }
            }
        });
        forgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utilities.hideKeyboard(LoginActivity.this);
                SharedHelper.putKey(context,"password", "");
                Intent mainIntent = new Intent(activity, ForgetPassword.class);
                startActivity(mainIntent);
            }
        });
    }

    public void GetToken(){
        try {
            if(!SharedHelper.getKey(context,"device_token").equals("") && SharedHelper.getKey(context,"device_token") != null) {
                device_token = SharedHelper.getKey(context, "device_token");
                utils.print(TAG, "GCM Registration Token: " + device_token);
            }else{
                device_token = ""+ FirebaseInstanceId.getInstance().getToken();
                SharedHelper.putKey(context, "device_token",""+FirebaseInstanceId.getInstance().getToken());
                utils.print(TAG, "Failed to complete token refresh: " + device_token);
            }
        }catch (Exception e) {
            device_token = "COULD NOT GET FCM TOKEN";
            utils.print(TAG, "Failed to complete token refresh");
        }

        try {
            device_UDID = android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
            utils.print(TAG, "Device UDID:" + device_UDID);
        }catch (Exception e) {
            device_UDID = "COULD NOT GET UDID";
            e.printStackTrace();
            utils.print(TAG, "Failed to complete device UDID");
        }
    }

    public void GoToMainActivity(){
        Intent mainIntent = new Intent(activity, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(mainIntent);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        activity.finish();
    }

    public void GoToBeginActivity(){
        Intent mainIntent = new Intent(activity, LoginActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(mainIntent);
        overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
        activity.finish();
    }

    private void refreshAccessToken() {
        if (isInternet) {
            customDialog = new CustomDialog(activity);
            customDialog.setCancelable(false);
            if(customDialog != null)
                customDialog.show();
            JSONObject object = new JSONObject();
            try {
                object.put("grant_type", "refresh_token");
                object.put("client_id", URLHelper.client_id);
                object.put("client_secret", URLHelper.client_secret);
                object.put("refresh_token", SharedHelper.getKey(context, "refresh_token"));
                object.put("scope", "");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLHelper.login, object, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    if ((customDialog != null) && customDialog.isShowing())
                        customDialog.dismiss();
                    utils.print("SignUpResponse", response.toString());
                    SharedHelper.putKey(context, "access_token", response.optString("access_token"));
                    SharedHelper.putKey(context, "refresh_token", response.optString("refresh_token"));
                    SharedHelper.putKey(context, "token_type", response.optString("token_type"));
                    getProfile();


                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if ((customDialog != null) && customDialog.isShowing())
                        customDialog.dismiss();
                    String json = null;
                    String Message;
                    NetworkResponse response = error.networkResponse;
                    utils.print("MyTest", "" + error);
                    utils.print("MyTestError", "" + error.networkResponse);
                    utils.print("MyTestError1", "" + response.statusCode);

                    if (response != null && response.data != null) {
                        SharedHelper.putKey(context,"loggedIn",getString(R.string.False));
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

        }else {
            displayMessage(getString(R.string.something_went_wrong_net));
        }

    }

    public void getProfile() {
        if (isInternet) {
          /*  customDialog = new CustomDialog(context);
            customDialog.setCancelable(false);
            if(customDialog != null)
                customDialog.show();*/
            JSONObject object = new JSONObject();
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URLHelper.UserProfile+"?device_type=android&device_id="+device_UDID+"&device_token="+device_token, object, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    if ((customDialog != null) && customDialog.isShowing())
                        customDialog.dismiss();
                    utils.print("GetProfile", response.toString());
                    SharedHelper.putKey(context, "id", response.optString("id"));
                    SharedHelper.putKey(context, "first_name", response.optString("first_name"));
                    SharedHelper.putKey(context, "last_name", response.optString("last_name"));
                    SharedHelper.putKey(context, "rating", response.optString("rating"));
                    SharedHelper.putKey(context, "email", response.optString("email"));
                    SharedHelper.putKey(context, "noofchild", response.optString("no_of_children"));
                    SharedHelper.putKey(context, "ages", response.optString("child_age"));

                    if (response.optString("picture").startsWith("http"))
                        SharedHelper.putKey(context, "picture", response.optString("picture"));
                    else
                        SharedHelper.putKey(context, "picture", URLHelper.base+"storage/"+response.optString("picture"));
                    SharedHelper.putKey(context, "gender", response.optString("gender"));
                    SharedHelper.putKey(context, "mobile", response.optString("mobile"));
                    SharedHelper.putKey(context, "wallet_balance", response.optString("wallet_balance"));
                    SharedHelper.putKey(context, "payment_mode", response.optString("payment_mode"));
                    if(!response.optString("currency").equalsIgnoreCase("") && response.optString("currency") != null)
                        SharedHelper.putKey(context, "currency",response.optString("currency"));
                    else
                        SharedHelper.putKey(context, "currency","$");
                    SharedHelper.putKey(context,"sos",response.optString("sos"));
                    SharedHelper.putKey(context, "loggedIn", getString(R.string.True));
                    GoToMainActivity();

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if ((customDialog != null) && customDialog.isShowing())
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
                                refreshAccessToken();
                            } else if (response.statusCode == 422) {

                                json = CustomRequestQueue.trimMessage(new String(response.data));
                                if (json != "" && json != null) {
                                    displayMessage(json);
                                } else {
                                    displayMessage(getString(R.string.please_try_again));
                                }

                            }else if(response.statusCode == 503){
                                displayMessage(getString(R.string.server_down));
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
                    headers.put("Authorization", "" + SharedHelper.getKey(context, "token_type") + " "
                            + SharedHelper.getKey(context, "access_token"));
                    utils.print("authorization",""+SharedHelper.getKey(context, "token_type") + " "
                            + SharedHelper.getKey(context, "access_token"));
                    return headers;
                }
            };

            CustomRequestQueue.getInstance(this).addToRequestQueue(jsonObjectRequest);
        }else{
            displayMessage(getString(R.string.something_went_wrong_net));
        }

    }

    public void displayMessage(String toastString){
        try{
            Snackbar.make(getCurrentFocus(),toastString, Snackbar.LENGTH_SHORT)
                    .setAction("Action", null).show();
        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(this, toastString, Toast.LENGTH_SHORT).show();
        }
    }

    private void signIn() {
        if (helper.isConnectingToInternet()) {
            customDialog = new CustomDialog(activity);
            customDialog.setCancelable(true);
            if(customDialog != null)
                customDialog.show();
            JSONObject object = new JSONObject();
            try {
                object.put("grant_type", "password");
                object.put("client_id", URLHelper.client_id);
                object.put("client_secret", URLHelper.client_secret);
                object.put("username", email.getText().toString());
                object.put("password", password.getText().toString());
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
                   /* if ((customDialog != null) && customDialog.isShowing())
                        customDialog.dismiss();*/
                    utils.print("SignUpResponse", response.toString());
                    Log.d("Response",response.toString());
                    SharedHelper.putKey(context, "access_token", response.optString("access_token"));
                    SharedHelper.putKey(context, "refresh_token", response.optString("refresh_token"));
                    SharedHelper.putKey(context, "token_type", response.optString("token_type"));
                    getProfile();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if ((customDialog != null) && customDialog.isShowing())
                        customDialog.dismiss();
                    String json = null;
                    String Message;
                    NetworkResponse response = error.networkResponse;
                    utils.print("MyTest", "" + error);
                    utils.print("MyTestError", "" + error.networkResponse);

                    if (response != null && response.data != null) {
                        try {
                            JSONObject errorObj = new JSONObject(new String(response.data));

                            if (response.statusCode == 400 || response.statusCode == 405 || response.statusCode == 500 || response.statusCode == 401) {
                                try {
                                    displayMessage(errorObj.optString("message"));
                                } catch (Exception e) {
                                    displayMessage(getString(R.string.something_went_wrong));
                                }
                            }else if (response.statusCode == 422) {
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
        }else {
            displayMessage(context.getResources().getString(R.string.something_went_wrong_net));
        }

    }
}
