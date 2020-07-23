package com.instantnannies.user.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.instantnannies.user.CustomFont.CustomTypefaceSpan;
import com.instantnannies.user.Fragments.NanniesBooking;
import com.instantnannies.user.Helper.SharedHelper;
import com.instantnannies.user.Helper.URLHelper;
import com.instantnannies.user.R;
import com.instantnannies.user.Utils.CustomDialog;
import com.instantnannies.user.Utils.CustomRequestQueue;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    public Context context = MainActivity.this;
    public Activity activity = MainActivity.this;
    CustomDialog customDialog;
    private View navHeader;
    TextView user_name,rating;
    CircleImageView user_profile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
         /*Navigation Drawer*/
        navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);
        drawerLayout = findViewById(R.id.drawer_layout);
        navHeader = navigationView.getHeaderView(0);
        user_name =   navHeader.findViewById(R.id.user_name);
        rating =  navHeader.findViewById(R.id.rated_text);
        navHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
            startActivity(new Intent(activity, EditProfileActivity.class));
            }
        });
        user_profile =   navHeader.findViewById(R.id.user_profile);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, new NanniesBooking())
                .commitAllowingStateLoss();

        setUpNavigationView();
        loadNavHeader();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        return false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(drawerLayout.isDrawerOpen(navigationView)){
            drawerLayout.closeDrawers();
        }
    }

    private void setUpNavigationView() {
        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                //Check to see which item was being clicked and perform appropriate action
                switch (menuItem.getItemId()) {

                    case R.id.nav_logout:
                        SharedHelper.putKey(context, "current_status", "");
                        SharedHelper.putKey(activity, "loggedIn", getString(R.string.False));
                        SharedHelper.putKey(context,"email","");
                        SharedHelper.putKey(context,"login_by","");

                        showLogoutDialog();
                        return true;

                    case R.id.nav_add_card:
                        startActivity(new Intent(MainActivity.this, AddCard.class));
                        return true;
                        case R.id.nav_help:
                        startActivity(new Intent(MainActivity.this, ActivityHelp.class));
                        return true;

                    case R.id.nav_your_bookings:
                        drawerLayout.closeDrawers();
                      /*  navItemIndex = 2;
                        CURRENT_TAG = TAG_YOURTRIPS;*/
                        SharedHelper.putKey(context, "current_status", "");
                        Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
                        intent.putExtra("tag","past");
                        startActivity(intent);
                        return true;

                    case R.id.nav_share:
                        // launch new intent instead of loading fragment
                        //startActivity(new Intent(MainActivity.this, AboutUsActivity.class));
                        navigateToShareScreen(URLHelper.APP_URL+getPackageName()+"&hl=en");
                        drawerLayout.closeDrawers();
                        return true;
                }


                return true;
            }
        });

        Menu m = navigationView.getMenu();

        for (int i = 0; i < m.size(); i++) {
            MenuItem menuItem = m.getItem(i);
            applyFontToMenuItem(menuItem);

        }

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {

            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank
                super.onDrawerOpened(drawerView);
            }
        };

        //Setting the actionbarToggle to drawer layout
        drawerLayout.addDrawerListener(actionBarDrawerToggle);

        //calling sync state is necessary or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();
    }
    public void navigateToShareScreen(String shareUrl) {
        try{
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, shareUrl + " -via " + getString(R.string.app_name));
            sendIntent.setType("text/plain");
            startActivity(sendIntent);
        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(context, "Share applications not found!", Toast.LENGTH_SHORT).show();
        }

    }
    private void showLogoutDialog() {
        if (!isFinishing()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            builder .setTitle(context.getString(R.string.app_name))
                    .setIcon(R.mipmap.ic_launcher)
                    .setMessage(getString(R.string.logout_alert));
            builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    logout();
                }
            });
            builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                //Reset to previous seletion menu in navigation
                dialog.dismiss();
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.openDrawer(GravityCompat.START);
                }
            });
            builder.setCancelable(false);
            final AlertDialog dialog = builder.create();
            //dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
            dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface arg) {
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(context, R.color.black));
                    dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(context, R.color.black));
                }
            });
            dialog.show();
        }
    }

    private void applyFontToMenuItem(MenuItem mi) {
        Typeface font = Typeface.createFromAsset(getAssets(), "Font/CircularStd-Bold.ttf");
        SpannableString mNewTitle = new SpannableString(mi.getTitle());
        mNewTitle.setSpan(new CustomTypefaceSpan("", font), 0, mNewTitle.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        mi.setTitle(mNewTitle);
    }

    public void logout() {
        customDialog = new CustomDialog(context);
        customDialog.setCancelable(false);
        if (customDialog != null)
            customDialog.show();
        JSONObject object = new JSONObject();
        try {
            object.put("id", SharedHelper.getKey(this,"id"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e("MainActivity", "logout: " + object);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLHelper.LOGOUT, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if ((customDialog != null)&& (customDialog.isShowing()))
                    customDialog.dismiss();
                drawerLayout.closeDrawers();

                SharedHelper.putKey(context, "current_status", "");
                SharedHelper.putKey(activity, "loggedIn", getString(R.string.False));
                SharedHelper.putKey(context,"email","");
                SharedHelper.putKey(context,"login_by","");
                Intent goToLogin = new Intent(activity, LoginActivity.class);
                goToLogin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(goToLogin);
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
                if (response != null && response.data != null) {
                    try {
                        JSONObject errorObj = new JSONObject(new String(response.data));
                        if (response.statusCode == 400 || response.statusCode == 405 || response.statusCode == 500) {
                            try {
                                displayMessage(errorObj.getString("message"));
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
                        } else if (response.statusCode == 503) {
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
                        logout();
                    }
                }
            }
        }) {
            @Override
            public java.util.Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("X-Requested-With", "XMLHttpRequest");
                Log.e("getHeaders: Token", SharedHelper.getKey(context, "access_token") + SharedHelper.getKey(context, "token_type"));
                headers.put("Authorization", "" + "Bearer" + " " + SharedHelper.getKey(context, "access_token"));
                return headers;
            }
        };
        CustomRequestQueue.getInstance(this).addToRequestQueue(jsonObjectRequest);
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

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLHelper.login, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                Log.v("SignUpResponse", response.toString());
                SharedHelper.putKey(context, "access_token", response.optString("access_token"));
                SharedHelper.putKey(context, "refresh_token", response.optString("refresh_token"));
                SharedHelper.putKey(context, "token_type", response.optString("token_type"));
                logout();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String json = null;
                String Message;
                NetworkResponse response = error.networkResponse;

                if (response != null && response.data != null) {
                    SharedHelper.putKey(context, "loggedIn", getString(R.string.False));
                    GoToBeginActivity();
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

    public void GoToBeginActivity() {
        Intent mainIntent = new Intent(activity, LoginActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(mainIntent);
        activity.finish();
    }

    public void displayMessage(String toastString) {
        Log.e("displayMessage", "" + toastString);
        try {
            Snackbar.make(getCurrentFocus(), toastString, Snackbar.LENGTH_SHORT)
                    .setAction("Action", null).show();
        }catch (Exception e){
            try {
                Toast.makeText(context, "" + toastString, Toast.LENGTH_SHORT).show();
            }catch (Exception ee){
                ee.printStackTrace();
            }
        }
    }

    private void loadNavHeader() {
        // name, website
        user_name.setText(SharedHelper.getKey(context, "first_name") + " " + SharedHelper.getKey(context, "last_name"));
        rating.setText("Rating   "+SharedHelper.getKey(context, "rating"));

        // Loading profile image
        //Glide.with(activity).load(SharedHelper.getKey(context, "picture")).placeholder(R.drawable.loading).error(R.drawable.ic_dummy_user).into(imgProfile);
        if (!SharedHelper.getKey(context,"picture").equalsIgnoreCase("")
                && !SharedHelper.getKey(context,"picture").equalsIgnoreCase(null) && SharedHelper.getKey(context,"picture") != null) {
            Picasso.get().load(SharedHelper.getKey(context, "picture"))
                    .placeholder(R.drawable.user_profile)
                    .error(R.drawable.user_profile)
                    .into(user_profile);
        }else {
            Picasso.get().load(R.drawable.user_profile)
                    .placeholder(R.drawable.user_profile)
                    .error(R.drawable.user_profile)
                    .into(user_profile);
        }

        // showing dot next to notifications label
        //  navigationView.getMenu().getItem(3).setActionView(R.layout.menu_dot);
    }
}
