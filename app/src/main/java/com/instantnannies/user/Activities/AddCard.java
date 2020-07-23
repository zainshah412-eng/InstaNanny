package com.instantnannies.user.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.accounts.NetworkErrorException;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.braintreepayments.cardform.view.CardForm;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonObject;
import com.instantnannies.user.Helper.SharedHelper;
import com.instantnannies.user.Helper.URLHelper;
import com.instantnannies.user.R;
import com.instantnannies.user.Utils.CustomDialog;
import com.instantnannies.user.Utils.CustomRequestQueue;
import com.instantnannies.user.Utils.Utilities;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;
import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.exception.AuthenticationException;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

public class AddCard extends AppCompatActivity
{
    Activity activity;
    Context context;
    Button addCard;
    //EditText cardNumber, cvv, month_and_year;
    CardForm cardForm;
    String Card_Token = "";
    CustomDialog customDialog;
    Utilities utils = new Utilities();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_card);
        viewInitializer();
    }

    void viewInitializer(){
        addCard =  findViewById(R.id.addCard);

        context = AddCard.this;
        activity = AddCard.this;
        cardForm = (CardForm) findViewById(R.id.card_form);
        cardForm.cardRequired(true)
                .expirationRequired(true)
                .cvvRequired(true)
                .postalCodeRequired(false)
                .mobileNumberRequired(false)
                .actionLabel("Add CardDetails")
                .setup(activity);


        addCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customDialog = new CustomDialog(AddCard.this);
                customDialog.setCancelable(false);
                if (customDialog != null)
                    customDialog.show();
                if( cardForm.getCardNumber() == null || cardForm.getExpirationMonth() == null || cardForm.getExpirationYear() == null || cardForm.getCvv() == null ){
                    if ((customDialog != null)&& (customDialog.isShowing()))
                        customDialog.dismiss();
                    displayMessage(context.getResources().getString(R.string.enter_card_details));
                }else{
                    if(cardForm.getCardNumber().equals("") || cardForm.getExpirationMonth().equals("") || cardForm.getExpirationYear().equals("")|| cardForm.getCvv().equals("")){
                        if ((customDialog != null)&& (customDialog.isShowing()))
                            customDialog.dismiss();
                        displayMessage(context.getResources().getString(R.string.enter_card_details));
                    }else {
                        String cardNumber = cardForm.getCardNumber();
                        int month = Integer.parseInt(cardForm.getExpirationMonth());
                        int year = Integer.parseInt(cardForm.getExpirationYear());
                        String cvv = cardForm.getCvv();
                        utils.print("MyTest","CardDetails Number: "+cardNumber+"Month: "+month+" Year: "+year);

                        Card card = new Card(cardNumber, month, year, cvv);
                        try {
                            Stripe stripe = new Stripe(URLHelper.STRIPE_TOKEN);
                            stripe.createToken(card,
                                    new TokenCallback() {
                                        public void onSuccess(Token token) {
                                            // Send token to your server
                                            utils.print("CardToken:"," "+token.getId());
                                            utils.print("CardId:"," "+token.getCard().getLast4());
                                            Card_Token = token.getId();
                                            addCardToAccount(Card_Token);
                                        }
                                        public void onError(Exception error) {
                                            // Show localized error message
                                            displayMessage(context.getResources().getString(R.string.enter_card_details));
                                            if ((customDialog != null)&& (customDialog.isShowing()))
                                                customDialog.dismiss();
                                        }
                                    }
                            );
                        }catch (AuthenticationException e){
                            e.printStackTrace();
                            if ((customDialog != null)&& (customDialog.isShowing()))
                                customDialog.dismiss();
                        }
                    }
                }
            }
        });
    }

    public void displayMessage(String toastString) {
        try {
            Snackbar.make(getCurrentFocus(), toastString, Snackbar.LENGTH_SHORT)
                    .setAction("Action", null).show();
        }catch (Exception ex){
            Toast.makeText(activity, toastString, Toast.LENGTH_SHORT).show();
        }

    }

    public void addCardToAccount(final String cardToken) {

        JsonObject json = new JsonObject();
        json.addProperty("stripe_token", cardToken);

        Ion.with(this)
                .load(URLHelper.ADD_CARD_TO_ACCOUNT_API)
                .addHeader("X-Requested-With", "XMLHttpRequest")
                .addHeader("Authorization", SharedHelper.getKey(AddCard.this, "token_type") + " " + SharedHelper.getKey(context, "access_token"))
                .setJsonObjectBody(json)
                .asString()
                .withResponse()
                .setCallback(new FutureCallback<Response<String>>() {
                    @Override
                    public void onCompleted(Exception e, Response<String> response) {
                        // response contains both the headers and the string result
                        if ((customDialog != null) && (customDialog.isShowing()))
                            customDialog.dismiss();

                        if (e != null) {
                            if (e instanceof NetworkErrorException) {
                                displayMessage(context.getResources().getString(R.string.please_try_again));
                            }
                            if (e instanceof TimeoutException) {
                                addCardToAccount(cardToken);
                            }
                            return;
                        }

                        if (response != null){
                            if (response.getHeaders().code() == 200) {
                                try {
                                    utils.print("SendRequestResponse", response.toString());

                                    JSONObject jsonObject = new JSONObject(response.getResult());
                                    Toast.makeText(AddCard.this, jsonObject.optString("message"), Toast.LENGTH_SHORT).show();
                                    // onBackPressed();
                                    Intent resultIntent = new Intent();
                                    resultIntent.putExtra("isAdded", true);
                                    setResult(Activity.RESULT_OK, resultIntent);
                                    finish();
                                } catch (Exception e1) {
                                    e1.printStackTrace();
                                }
                                customDialog.dismiss();
                            }else if (response.getHeaders().code() == 401){
                                customDialog.dismiss();
                                refreshAccessToken();
                            }
                        }
                    }
                });

       /* JSONObject object = new JSONObject();
        try{
            object.put("stripe_token", cardToken);

        }catch (Exception e){
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLHelper.ADD_CARD_TO_ACCOUNT_API, object , new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                com.appbd.borakh.utils.print("SendRequestResponse",response.toString());
                if ((customDialog != null)&& (customDialog.isShowing()))
                customDialog.dismiss();
               // onBackPressed();
                Intent resultIntent = new Intent();
                resultIntent.putExtra("isAdded", true);
                setResult(Activity.RESULT_OK, resultIntent);
                finish();
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if ((customDialog != null)&& (customDialog.isShowing()))
                customDialog.dismiss();
                String json = null;
                String Message;
                NetworkResponse response = error.networkResponse;
                if(response != null && response.data != null){

                    try {
                        JSONObject errorObj = new JSONObject(new String(response.data));

                        if(response.statusCode == 400 || response.statusCode == 405 || response.statusCode == 500){
                            try{
                                displayMessage(errorObj.getString("message"));
                            }catch (Exception e){
                                displayMessage(errorObj.optString("error"));
                                //displayMessage(getString(R.string.something_went_wrong));
                            }
                            com.appbd.borakh.utils.print("MyTest",""+errorObj.toString());
                        }else if(response.statusCode == 401){
                            refreshAccessToken();
                        }else if(response.statusCode == 422){

                            json = trimMessage(new String(response.data));
                            if(json !="" && json != null) {
                                displayMessage(json);
                            }else{
                                displayMessage(getString(R.string.please_try_again));
                            }
                        }else if(response.statusCode == 503){
                            displayMessage(getString(R.string.server_down));
                        }else{
                            displayMessage(getString(R.string.please_try_again));
                        }

                    }catch (Exception e){
                        displayMessage(getString(R.string.something_went_wrong));
                    }

                }else{
                    displayMessage(getString(R.string.please_try_again));
                }
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("X-Requested-With", "XMLHttpRequest");
                headers.put("Authorization",""+SharedHelper.getKey(context, "token_type")+" "+SharedHelper.getKey(context, "access_token"));
                return headers;
            }
        };

        BorakhApplication.getInstance().addToRequestQueue(jsonObjectRequest);*/
    }


    private void refreshAccessToken() {
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
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLHelper.login, object, new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                Log.v("SignUpResponse", response.toString());
                SharedHelper.putKey(context, "access_token", response.optString("access_token"));
                SharedHelper.putKey(context, "refresh_token", response.optString("refresh_token"));
                SharedHelper.putKey(context, "token_type", response.optString("token_type"));
                addCardToAccount(Card_Token);
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String json = null;
                String Message;
                NetworkResponse response = error.networkResponse;

                if (response != null && response.data != null) {
                    SharedHelper.putKey(context,"loggedIn",context.getResources().getString(R.string.False));
                    GoToBeginActivity();
                } else {
                    if (error instanceof NoConnectionError) {
                        displayMessage(context.getResources().getString(R.string.oops_connect_your_internet));
                    } else if (error instanceof NetworkError) {
                        displayMessage(context.getResources().getString(R.string.oops_connect_your_internet));
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


    public void GoToBeginActivity(){
        Intent mainIntent = new Intent(activity, LoginActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(mainIntent);
        activity.finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
