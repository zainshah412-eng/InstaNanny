package com.instantnannies.user.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
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
import com.instantnannies.user.Adapters.ChildsAdapter;
import com.instantnannies.user.Helper.AppHelper;
import com.instantnannies.user.Helper.ConnectionHelper;
import com.instantnannies.user.Helper.SharedHelper;
import com.instantnannies.user.Helper.URLHelper;
import com.instantnannies.user.Helper.VolleyMultipartRequest;
import com.instantnannies.user.Models.Children;
import com.instantnannies.user.R;
import com.instantnannies.user.Utils.CustomDialog;
import com.instantnannies.user.Utils.CustomRequestQueue;
import com.instantnannies.user.Utils.Utilities;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileDescriptor;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EditProfileActivity extends AppCompatActivity {
    EditText first_name, last_name, mobile_no,email;
    MaterialSpinner spinner_no_of_childs;
    TextView changePasswordTxt;
    List<String> childsList = new ArrayList<>();
    ArrayList<Children> childrenList = new ArrayList<>();
    RecyclerView childs_rv;
    ChildsAdapter Adapter;
    int number_of_children=0;
    List<String> ageslist;
    String ages;
    Boolean isImageChanged = false;
    public static int deviceHeight;
    public static int deviceWidth;
    ImageView profile_Image;
    Button saveBTN;
    Utilities utils = new Utilities();
    private static final int SELECT_PHOTO = 100;
    public static String TAG = "EditProfile";
    public Context context = EditProfileActivity.this;
    public Activity activity = EditProfileActivity.this;
    CustomDialog customDialog;
    ConnectionHelper helper;
    Boolean isInternet;
    Boolean isPermissionGivenAlready = false;
    boolean status = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        viewInitializer();
    }

    void viewInitializer(){
        first_name = findViewById(R.id.first_name);
        last_name = findViewById(R.id.last_name);
        mobile_no = findViewById(R.id.et_phone);
        email = findViewById(R.id.email);
        profile_Image = findViewById(R.id.user_image);
        saveBTN = findViewById(R.id.btn_update_profile);
        changePasswordTxt=findViewById(R.id.changePasswordTxt);
        changePasswordTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(EditProfileActivity.this,ChangePassword.class));
            }
        });

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        deviceHeight = displayMetrics.heightPixels;
        deviceWidth = displayMetrics.widthPixels;

        helper = new ConnectionHelper(context);
        isInternet = helper.isConnectingToInternet();

        if (!SharedHelper.getKey(context, "picture").equalsIgnoreCase("")
                && !SharedHelper.getKey(context, "picture").equalsIgnoreCase(null)
                && SharedHelper.getKey(context, "picture") != null) {
            Picasso.get()
                    .load(SharedHelper.getKey(context, "picture"))
                    .placeholder(R.drawable.user_profile)
                    .error(R.drawable.user_profile)
                    .into(profile_Image);
        } else {
            Picasso.get()
                    .load(R.drawable.user_profile)
                    .placeholder(R.drawable.user_profile)
                    .error(R.drawable.user_profile)
                    .into(profile_Image);
        }
        email.setText(SharedHelper.getKey(context, "email"));
        first_name.setText(SharedHelper.getKey(context, "first_name"));
        last_name.setText(SharedHelper.getKey(context, "last_name"));
        if (SharedHelper.getKey(context, "mobile") != null
                && !SharedHelper.getKey(context, "mobile").equals("null")
                && !SharedHelper.getKey(context, "mobile").equals("")) {
            mobile_no.setText(SharedHelper.getKey(context, "mobile"));
        }

        number_of_children=Integer.parseInt(SharedHelper.getKey(context, "noofchild"));
        ages=SharedHelper.getKey(context, "ages");

        spinner_no_of_childs = findViewById(R.id.spinner_no_of_childs);
        childs_rv = findViewById(R.id.childs_rv);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(EditProfileActivity.this, 1);
        childs_rv.setLayoutManager(gridLayoutManager);
        childs_rv.setItemAnimator(new DefaultItemAnimator());
        childs_rv.setNestedScrollingEnabled(false);

         ageslist = Arrays.asList(ages.split("\\s*,\\s*"));

        childrenList.clear();
        if(number_of_children>0){
            childs_rv.setVisibility(View.VISIBLE);
            for(int i = 0 ; i < Integer.valueOf(number_of_children); i++){
                Log.v("Age",ageslist.get(i));

                childrenList.add(new Children(String.valueOf(i+1),ageslist.get(i)));

            }
            Adapter = new ChildsAdapter(EditProfileActivity.this,childrenList);
            childs_rv.setAdapter(Adapter);
        }else{
            childrenList.clear();
            Adapter = new ChildsAdapter(EditProfileActivity.this,childrenList);
            childs_rv.setAdapter(Adapter);
        }


        spinner_no_of_childs.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {

            @Override public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                // Snackbar.make(view, "Clicked " + item, Snackbar.LENGTH_LONG).show();
                childrenList.clear();
                if(position>0){
                    childs_rv.setVisibility(View.VISIBLE);
                    for(int i = 0 ; i < Integer.valueOf(item); i++){
                        childrenList.add(new Children(String.valueOf(i+1),""));
                    }
                    Adapter = new ChildsAdapter(EditProfileActivity.this,childrenList);
                    childs_rv.setAdapter(Adapter);
                }else{
                    childrenList.clear();
                    Adapter = new ChildsAdapter(EditProfileActivity.this,childrenList);
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
        spinner_no_of_childs.setSelectedIndex(number_of_children);

        saveBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Pattern ps = Pattern.compile(".*[0-9].*");
                Matcher firstName = ps.matcher(first_name.getText().toString());
                Matcher lastName = ps.matcher(last_name.getText().toString());


                if (email.getText().toString().equals("") || email.getText().toString().length() == 0) {
                    displayMessage(context.getResources().getString(R.string.email_validation));
                } else if (mobile_no.getText().toString().equals("") || mobile_no.getText().toString().length() == 0) {
                    displayMessage(context.getResources().getString(R.string.mobile_number_empty));
                } else if (mobile_no.getText().toString().length() < 10 || mobile_no.getText().toString().length() > 20) {
                    displayMessage(context.getResources().getString(R.string.mobile_number_validation));
                } else if (first_name.getText().toString().equals("") || first_name.getText().toString().length() == 0) {
                    displayMessage(context.getResources().getString(R.string.first_name_empty));
                } else if (last_name.getText().toString().equals("") || last_name.getText().toString().length() == 0) {
                    displayMessage(context.getResources().getString(R.string.last_name_empty));
                } else if (firstName.matches()) {
                    displayMessage(context.getResources().getString(R.string.first_name_no_number));
                } else if (lastName.matches()) {
                    displayMessage(context.getResources().getString(R.string.last_name_no_number));
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
                            updateProfile();
                        } else {
                            displayMessage(getString(R.string.something_went_wrong_net));
                        }
                    }else{
                        Toast.makeText(EditProfileActivity.this, "Please Enter Ages of all Children", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });

        profile_Image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkStoragePermission()) {
                        requestPermissions(new String[]{Manifest.permission.CAMERA,
                                Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
                    } else {
                        goToImageIntent();
                    }
                } else {
                    goToImageIntent();
                }
            }
        });
    }

    public void GoToMainActivity() {
        Intent mainIntent = new Intent(activity, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(mainIntent);
        activity.finish();
    }

    public void displayMessage(String toastString) {
        Log.e("displayMessage", "" + toastString);
        try {
            Snackbar.make(getCurrentFocus(), toastString, Snackbar.LENGTH_SHORT)
                    .setAction("Action", null).show();
        }catch (Exception e){
            try{
                Toast.makeText(context,""+toastString,Toast.LENGTH_SHORT).show();
            }catch (Exception ee){
                ee.printStackTrace();
            }
        }
    }


    public void updateProfile() {
        if (isImageChanged) {
            updateProfileWithImage();
        } else {
            updateProfileWithoutImage();
        }
    }
    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    public void onBackPressed() {
        GoToMainActivity();
    }

    private void refreshAccessToken(final String tag) {

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


                utils.print("SignUpResponse", response.toString());
                SharedHelper.putKey(context, "access_token", response.optString("access_token"));
                SharedHelper.putKey(context, "refresh_token", response.optString("refresh_token"));
                SharedHelper.putKey(context, "token_type", response.optString("token_type"));
                if (tag.equalsIgnoreCase("UPDATE_PROFILE_WITH_IMAGE")) {
                    updateProfileWithImage();
                } else {
                    updateProfileWithoutImage();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String json = "";
                NetworkResponse response = error.networkResponse;

                if (response != null && response.data != null) {
                    SharedHelper.putKey(context, "loggedIn", getString(R.string.False));
                    utils.GoToBeginActivity(EditProfileActivity.this);
                } else {
                    if (error instanceof NoConnectionError) {
                        displayMessage(getString(R.string.oops_connect_your_internet));
                    } else if (error instanceof NetworkError) {
                        displayMessage(getString(R.string.oops_connect_your_internet));
                    } else if (error instanceof TimeoutError) {
                        refreshAccessToken(tag);
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

    private void updateProfileWithImage() {
        isImageChanged = false;
        customDialog = new CustomDialog(context);
        customDialog.setCancelable(false);
        if (customDialog != null)
            customDialog.show();
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, URLHelper.UseProfileUpdate, new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                if ((customDialog != null) && (customDialog.isShowing()))
                    customDialog.dismiss();

                String res = new String(response.data);
                try {
                    JSONObject jsonObject = new JSONObject(res);
                    SharedHelper.putKey(context, "id", jsonObject.optString("id"));
                    SharedHelper.putKey(context, "first_name", jsonObject.optString("first_name"));
                    SharedHelper.putKey(context, "last_name", jsonObject.optString("last_name"));
                    SharedHelper.putKey(context, "email", jsonObject.optString("email"));
                    if (jsonObject.optString("picture").equals("") || jsonObject.optString("picture") == null) {
                        SharedHelper.putKey(context, "picture", "");
                    } else {
                        if (jsonObject.optString("picture").startsWith("http"))
                            SharedHelper.putKey(context, "picture", jsonObject.optString("picture"));
                        else
                            SharedHelper.putKey(context, "picture", URLHelper.base + "public/storage/" + jsonObject.optString("picture"));
                    }

                    SharedHelper.putKey(context, "gender", jsonObject.optString("gender"));
                    SharedHelper.putKey(context, "mobile", jsonObject.optString("mobile"));
                    SharedHelper.putKey(context, "wallet_balance", jsonObject.optString("wallet_balance"));
                    SharedHelper.putKey(context, "payment_mode", jsonObject.optString("payment_mode"));
                    GoToMainActivity();
                    Toast.makeText(EditProfileActivity.this, context.getResources().getString(R.string.update_success), Toast.LENGTH_SHORT).show();
                    //displayMessage(getString(R.string.update_success));

                } catch (JSONException e) {
                    e.printStackTrace();
                    displayMessage(context.getResources().getString(R.string.something_went_wrong));
                }


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
                                displayMessage(context.getResources().getString(R.string.something_went_wrong));
                            }
                        } else if (response.statusCode == 401) {
                            refreshAccessToken("UPDATE_PROFILE_WITH_IMAGE");
                        } else if (response.statusCode == 422) {

                            json = CustomRequestQueue.trimMessage(new String(response.data));
                            if (json != "" && json != null) {
                                displayMessage(json);
                            } else {
                                displayMessage(context.getResources().getString(R.string.please_try_again));
                            }

                        } else if (response.statusCode == 503) {
                            displayMessage(context.getResources().getString(R.string.server_down));
                        } else {
                            displayMessage(context.getResources().getString(R.string.please_try_again));
                        }

                    } catch (Exception e) {
                        displayMessage(context.getResources().getString(R.string.something_went_wrong));
                    }

                } else {
                    if (error instanceof NoConnectionError) {
                        displayMessage(context.getResources().getString(R.string.oops_connect_your_internet));
                    } else if (error instanceof NetworkError) {
                        displayMessage(context.getResources().getString(R.string.oops_connect_your_internet));
                    } else if (error instanceof TimeoutError) {
                        updateProfileWithoutImage();
                    }
                }
            }
        }) {
            @Override
            public Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("first_name", first_name.getText().toString());
                params.put("last_name", last_name.getText().toString());
                params.put("email", email.getText().toString());
                params.put("mobile", mobile_no.getText().toString());
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("X-Requested-With", "XMLHttpRequest");
                headers.put("Authorization", "" + SharedHelper.getKey(context, "token_type") + " " + SharedHelper.getKey(context, "access_token"));
                return headers;
            }

            @Override
            protected Map<String, VolleyMultipartRequest.DataPart> getByteData() throws AuthFailureError {
                Map<String, VolleyMultipartRequest.DataPart> params = new HashMap<>();
                params.put("picture", new VolleyMultipartRequest.DataPart("userImage.jpg", AppHelper.getFileDataFromDrawable(profile_Image.getDrawable()), "image/jpeg"));
                return params;
            }
        };
        CustomRequestQueue.getInstance(this).addToRequestQueue(volleyMultipartRequest);

    }

    private void updateProfileWithoutImage() {
        customDialog = new CustomDialog(context);
        customDialog.setCancelable(false);
        if (customDialog != null)
            customDialog.show();
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, URLHelper.UseProfileUpdate, new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                if ((customDialog != null) && (customDialog.isShowing()))
                    customDialog.dismiss();

                String res = new String(response.data);
                try {
                    JSONObject jsonObject = new JSONObject(res);
                    SharedHelper.putKey(context, "id", jsonObject.optString("id"));
                    SharedHelper.putKey(context, "first_name", jsonObject.optString("first_name"));
                    SharedHelper.putKey(context, "last_name", jsonObject.optString("last_name"));
                    SharedHelper.putKey(context, "noofchild", jsonObject.optString("no_of_children"));
                    SharedHelper.putKey(context, "ages", jsonObject.optString("child_age"));
                    SharedHelper.putKey(context, "email", jsonObject.optString("email"));
                    if (jsonObject.optString("picture").equals("") || jsonObject.optString("picture") == null) {
                        SharedHelper.putKey(context, "picture", "");
                    } else {
                        if (jsonObject.optString("picture").startsWith("http"))
                            SharedHelper.putKey(context, "picture", jsonObject.optString("picture"));
                        else
                            SharedHelper.putKey(context, "picture", URLHelper.base + "public/storage/" + jsonObject.optString("picture"));
                    }

                    SharedHelper.putKey(context, "gender", jsonObject.optString("gender"));
                    SharedHelper.putKey(context, "mobile", jsonObject.optString("mobile"));
                    SharedHelper.putKey(context, "wallet_balance", jsonObject.optString("wallet_balance"));
                    SharedHelper.putKey(context, "payment_mode", jsonObject.optString("payment_mode"));
                    GoToMainActivity();
                    Toast.makeText(EditProfileActivity.this, context.getResources().getString(R.string.update_success), Toast.LENGTH_SHORT).show();
                    //displayMessage(getString(R.string.update_success));

                } catch (JSONException e) {
                    e.printStackTrace();
                    displayMessage(context.getResources().getString(R.string.something_went_wrong));
                }


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
                                displayMessage(context.getResources().getString(R.string.something_went_wrong));
                            }
                        } else if (response.statusCode == 401) {
                            refreshAccessToken("UPDATE_PROFILE_WITHOUT_IMAGE");
                        } else if (response.statusCode == 422) {

                            json = CustomRequestQueue.trimMessage(new String(response.data));
                            if (json != "" && json != null) {
                                displayMessage(json);
                            } else {
                                displayMessage(context.getResources().getString(R.string.please_try_again));
                            }

                        } else if (response.statusCode == 503) {
                            displayMessage(context.getResources().getString(R.string.server_down));
                        } else {
                            displayMessage(context.getResources().getString(R.string.please_try_again));
                        }

                    } catch (Exception e) {
                        displayMessage(context.getResources().getString(R.string.something_went_wrong));
                    }

                } else {
                    if (error instanceof NoConnectionError) {
                        displayMessage(context.getResources().getString(R.string.oops_connect_your_internet));
                    } else if (error instanceof NetworkError) {
                        displayMessage(context.getResources().getString(R.string.oops_connect_your_internet));
                    } else if (error instanceof TimeoutError) {
                        updateProfileWithoutImage();
                    }
                }
            }
        }) {

            @Override
            public Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("first_name", first_name.getText().toString());
                params.put("last_name", last_name.getText().toString());
                params.put("email", email.getText().toString());
                params.put("mobile", mobile_no.getText().toString());
                params.put("picture", "");
                params.put("no_of_children", String.valueOf(childrenList.size()));

                try {
                    ages = null;
                    for(int i = 0; i < childrenList.size();i++){
                        if(ages!=null){
                            ages = ages+","+childrenList.get(i).getAge();
                        }else{
                            ages = childrenList.get(i).getAge();
                        }
                    }
                }catch (Exception ex){
                    ex.printStackTrace();
                }


                params.put("child_age", ages);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("X-Requested-With", "XMLHttpRequest");
                headers.put("Authorization", "" + SharedHelper.getKey(context, "token_type") + " " + SharedHelper.getKey(context, "access_token"));
                return headers;
            }
        };
        CustomRequestQueue.getInstance(this).addToRequestQueue(volleyMultipartRequest);

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private boolean checkStoragePermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            for (int grantResult : grantResults) {
                if (grantResult == PackageManager.PERMISSION_GRANTED) {
                    if(!isPermissionGivenAlready) {
                        goToImageIntent();
                    }
                }
            }
        }
    }

    public void goToImageIntent() {
        isPermissionGivenAlready = true;
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PHOTO);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_PHOTO && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri uri = data.getData();
            Bitmap bitmap = null;

            try {
                isImageChanged = true;
                Bitmap resizeImg = getBitmapFromUri(this, uri);
                if (resizeImg != null) {
                    Bitmap reRotateImg = AppHelper.handleSamplingAndRotationBitmap(EditProfileActivity.this, uri);
                    profile_Image.setImageBitmap(reRotateImg);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static Bitmap getBitmapFromUri(@NonNull Context context, @NonNull Uri uri) throws IOException {
        Log.e(TAG, "getBitmapFromUri: Resize uri" + uri);
        ParcelFileDescriptor parcelFileDescriptor =
                context.getContentResolver().openFileDescriptor(uri, "r");
        assert parcelFileDescriptor != null;
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        Log.e(TAG, "getBitmapFromUri: Height" + deviceHeight);
        Log.e(TAG, "getBitmapFromUri: width" + deviceWidth);
        int maxSize = Math.min(deviceHeight, deviceWidth);
        if (image != null) {
            Log.e(TAG, "getBitmapFromUri: Width" + image.getWidth());
            Log.e(TAG, "getBitmapFromUri: Height" + image.getHeight());
            int inWidth = image.getWidth();
            int inHeight = image.getHeight();
            int outWidth;
            int outHeight;
            if (inWidth > inHeight) {
                outWidth = maxSize;
                outHeight = (inHeight * maxSize) / inWidth;
            } else {
                outHeight = maxSize;
                outWidth = (inWidth * maxSize) / inHeight;
            }
            return Bitmap.createScaledBitmap(image, outWidth, outHeight, false);
        } else {
            Toast.makeText(context, context.getResources().getString(R.string.valid_image), Toast.LENGTH_SHORT).show();
            return null;
        }

    }
}
