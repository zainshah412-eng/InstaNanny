package com.instantnannies.user.Utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.instantnannies.user.Activities.LoginActivity;
import com.instantnannies.user.Helper.SharedHelper;
import com.instantnannies.user.R;
import com.instantnannies.user.Retrofit.ApiInterface;
import com.instantnannies.user.Retrofit.RetrofitClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Utilities {
    private static ApiInterface mApiInterface;
    public static boolean showLog = true;
    public static String formatted_address = "";
    public static String getMonth(String date) throws ParseException {
        Date d = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).parse(date);
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        String monthName = new SimpleDateFormat("MMM").format(cal.getTime());
        return monthName;
    }
    public static boolean isAfterToday(int year, int month, int day) {
        Calendar today = Calendar.getInstance();
        Calendar myDate = Calendar.getInstance();
        myDate.set(year, month, day);

        if (myDate.before(today)) {
            return false;
        }
        return true;
    }
    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void hideKeypad(Context context, View view){
        // Check if no view has focus:
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public boolean checktimings(String time) {
        String pattern = "HH:mm";
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        try {
            String currentTime = new SimpleDateFormat("HH:mm").format(Calendar.getInstance().getTime());
            Date date1 = sdf.parse(time);
            Date date2 = sdf.parse(currentTime);

            if (date1.after(date2)) {
                return true;
            } else {
                return false;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void print(String tag, String message) {
        if(showLog){
            Log.v(tag,message);
        }
    }

    public void displayMessage(View view, Context context, String toastString) {
        try {
            Snackbar.make(view, toastString, Snackbar.LENGTH_SHORT)
                    .setAction("Action", null).show();
        }catch (Exception e){
            try{
                Toast.makeText(context,""+toastString,Toast.LENGTH_SHORT).show();
            }catch (Exception ee){
                ee.printStackTrace();
            }
        }
    }

    public static boolean isValidEmail(String email) {
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public static boolean isValidPassword(final String password) {
        Pattern pattern;
        Matcher matcher;

        String PASSWORD_PATTERN = "(?=.*[a-z])(?=.*[A-Z])(?=.*[\\d])(?=.*[~`!@#\\$%\\^&\\*\\(\\)\\-_\\+=\\{\\}\\[\\]\\|\\;:\"<>,./\\?]).{8,}";
        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);

        boolean status = matcher.matches();

        return status;
    }

    public void GoToBeginActivity(Activity activity) {
        Intent mainIntent = new Intent(activity, LoginActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(mainIntent);
        activity.finish();
    }


    public void showAlert(Context context, String message){
        try{
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            builder.setMessage(message)
                    .setTitle(context.getResources().getString(R.string.app_name))
                    .setCancelable(true)
                    .setIcon(R.mipmap.ic_launcher)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }catch(Exception e){
            e.printStackTrace();
        }

    }
    public static String getAddressUsingLatLng(final String strType, final TextView txtView, final Context context, String latitude, String longitude)
    {
        mApiInterface = RetrofitClient.getClient().create(ApiInterface.class);
        formatted_address = "Fetching Address...";
        Call<ResponseBody> call = mApiInterface.getResponse(latitude+","+longitude,
                context.getResources().getString(R.string.google_maps_key));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.e("sUCESS","SUCESS"+response.body());
                if (response.body() != null){
                    try {
                        String bodyString = new String(response.body().bytes());
                        Log.e("sUCESS","bodyString"+bodyString);
                        try {
                            JSONObject jsonObj = new JSONObject(bodyString);
                            JSONArray jsonArray = jsonObj.optJSONArray("results");
                            if (jsonArray.length() > 0){
                                formatted_address = jsonArray.optJSONObject(0).optString("formatted_address");
                                Log.v("Formatted Address", ""+formatted_address);
                                txtView.setText(""+formatted_address);
                                if (SharedHelper.getKey(context, "track_status").equalsIgnoreCase("YES") &&
                                        (SharedHelper.getKey(context, "req_status").equalsIgnoreCase("STARTED") || SharedHelper.getKey(context, "req_status").equalsIgnoreCase("PICKEDUP")
                                                || SharedHelper.getKey(context, "req_status").equalsIgnoreCase("ARRIVED"))){
                                    SharedHelper.putKey(context, "extend_address", ""+formatted_address);
                                }else{
                                    if (strType.equalsIgnoreCase("source")){
                                        SharedHelper.putKey(context, "source", ""+formatted_address);
                                    }else{
                                        SharedHelper.putKey(context, "destination", ""+formatted_address);
                                    }
                                }
                            }else{
                                Toast.makeText(context, "Service not available!", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            formatted_address = "";
                            SharedHelper.putKey(context, "source", "");
                            SharedHelper.putKey(context, "destination", "");
                            txtView.setText("");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        formatted_address = "";
                        SharedHelper.putKey(context, "source", "");
                        SharedHelper.putKey(context, "destination", "");
                        txtView.setText("");
                    }
                }else{
                    formatted_address = "";
                    SharedHelper.putKey(context, "source", "");
                    SharedHelper.putKey(context, "destination", "");
                    txtView.setText("");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("onFailure","onFailure"+call.request().url());
                formatted_address = "";
                SharedHelper.putKey(context, "source", "");
                SharedHelper.putKey(context, "destination", "");
                txtView.setText("");
            }
        });
        return ""+formatted_address;
    }

}