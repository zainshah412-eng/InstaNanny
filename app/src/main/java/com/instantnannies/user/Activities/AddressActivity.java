package com.instantnannies.user.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.gson.Gson;
import com.instantnannies.user.Adapters.AutoCompleteAdapter;
import com.instantnannies.user.Adapters.RecentPlacesAdapter;
import com.instantnannies.user.Helper.SharedHelper;
import com.instantnannies.user.Models.PlacePredictions;
import com.instantnannies.user.Models.RecentAddressData;
import com.instantnannies.user.R;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AddressActivity extends AppCompatActivity {


    private ListView searchResultLV;
    private AutoCompleteAdapter mAutoCompleteAdapter;
    public PlacePredictions predictions  = new PlacePredictions();
    private Handler handler;
    int kilometer = 20;
    double latitude;
    double longitude;
    String departureLat,departureLng;
    String currentLocationName;
    EditText address_tv;
    ImageView arrow_back;
    RequestQueue mRequestQueue;
    PlacesClient placesClient;
    RecyclerView rvRecentResults;
    TextView recent_locations_tv;
    ArrayList<RecentAddressData> lstRecentList = new ArrayList<RecentAddressData>();
    boolean isLocationAlreadySaved = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);
        viewInitializer();
    }


    /*view Initializer*/
    void viewInitializer(){
        address_tv = findViewById(R.id.address);
        arrow_back = findViewById(R.id.arrow_back);
        searchResultLV = findViewById(R.id.searchResultLV);
        rvRecentResults = findViewById(R.id.rvRecentResults);
        recent_locations_tv = findViewById(R.id.recent_locations_tv);
        mRequestQueue = Volley.newRequestQueue(AddressActivity.this);
        Places.initialize(AddressActivity.this,getString(R.string.google_maps_key));
        placesClient = Places.createClient(AddressActivity.this);

        searchResultLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                searchResultLV.setVisibility(View.GONE);
                hideKeypad(AddressActivity.this.getCurrentFocus());
                mAutoCompleteAdapter = null;
                setGoogleAddress(position);
                address_tv.clearFocus();
            }
        });

        autoCompleteGoogleSearch(address_tv);

        arrow_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        getFavoriteLocations();
    }

    private void hideKeypad(View view){
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) AddressActivity.this.getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
    /*Departure Text Change Listener*/
    private void autoCompleteGoogleSearch(final EditText google_seach_txt){
        google_seach_txt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

            }
        });

        google_seach_txt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE || event != null &&
                        event.getAction() == KeyEvent.ACTION_DOWN &&
                        event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    if (event == null || !event.isShiftPressed()) {
                        // the user is done typing.

                        return true; // consume.
                    }
                }
                return false; // pass on to other listeners.
            }
        });

        google_seach_txt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if(!google_seach_txt.hasFocus()){
                    return;
                }

                // optimised way is to start searching for laction after user has typed minimum 3 chars
                if (google_seach_txt.getText().length() > 0) {
                    //  google_seach_txt.setText(charSequence);

                    searchResultLV.setVisibility(View.GONE);
                    Runnable run = new Runnable() {

                        @Override
                        public void run() {

                            JSONObject object = new JSONObject();
                            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, getPlaceAutoCompleteUrl(google_seach_txt.getText().toString()),
                                    object, new com.android.volley.Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {

                                    if(google_seach_txt.getText().length()<=0){
                                        return;
                                    }
                                    Log.v("PayNowRequestResponse", response.toString());
                                    Log.v("PayNowRequestResponse", response.toString());

                                    if (mAutoCompleteAdapter == null)
                                        searchResultLV.setVisibility(View.VISIBLE);


                                    Gson gson = new Gson();


                                    predictions = gson.fromJson(response.toString(), PlacePredictions.class);


                                    if (mAutoCompleteAdapter == null) {
                                        mAutoCompleteAdapter = new AutoCompleteAdapter(AddressActivity.this, predictions.getPlaces(), AddressActivity.this);

                                        searchResultLV.setAdapter(mAutoCompleteAdapter);


                                    } else {

                                        if(predictions.getPlaces().size() > 0){
                                            searchResultLV.setVisibility(View.VISIBLE);
                                        }


                                        searchResultLV.setVisibility(View.VISIBLE);
                                        searchResultLV.invalidate();

                                        mAutoCompleteAdapter.clear();
                                        mAutoCompleteAdapter.addAll(predictions.getPlaces());
                                        mAutoCompleteAdapter.notifyDataSetChanged();

                                    }
                                }
                            }, new com.android.volley.Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Log.v("PayNowRequestResponse", error.toString());
                                }
                            });
                            mRequestQueue.add(jsonObjectRequest);

                        }

                    };
                    if (handler != null) {
                        handler.removeCallbacksAndMessages(null);
                    } else {
                        handler = new Handler();
                    }
                    handler.postDelayed(run, 1000);

                }else{


                    searchResultLV.setVisibility(View.GONE);

                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    /*Departure Autocomplete*/
    public void setGoogleAddress(int position) {

        Log.v("PLACEID", "Place ID == >"+ predictions.getPlaces().get(position).getPlaceID());
        List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME,Place.Field.LAT_LNG,Place.Field.ADDRESS);
        FetchPlaceRequest request = FetchPlaceRequest.builder(predictions.getPlaces().get(position).getPlaceID(), placeFields)
                .build();
        placesClient.fetchPlace(request).addOnSuccessListener(new OnSuccessListener<FetchPlaceResponse>() {
            @Override
            public void onSuccess(FetchPlaceResponse response) {
                final Place myPlace = response.getPlace();
                LatLng queriedLocation = myPlace.getLatLng();

                departureLat = String.valueOf(queriedLocation.latitude);
                departureLng = String.valueOf(queriedLocation.longitude);
                currentLocationName = myPlace.getAddress();

                address_tv.setText(myPlace.getAddress());
                address_tv.clearFocus();
                searchResultLV.setVisibility(View.GONE);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent();
                        intent.putExtra("address",myPlace.getAddress());
                        intent.putExtra("address_name",myPlace.getName());
                        intent.putExtra("lat",departureLat);
                        intent.putExtra("lng",departureLng);


                        if(!myPlace.getAddress().equalsIgnoreCase("")){
                            String count =  SharedHelper.getKey(AddressActivity.this,"count");

                            Integer locationCount;

                            if(count.equalsIgnoreCase("")){
                                count = "0";
                                locationCount = 0;

                            }else{
                                locationCount = Integer.valueOf(count);
                                locationCount = locationCount+1;
                            }

                            //Duplicate Entry Check
                            for(int i = 0; i <=locationCount ; i++){

                                if(!myPlace.getName().equalsIgnoreCase(SharedHelper.getKey(AddressActivity.this,"recent_location_name"+String.valueOf(i)))){

                                   isLocationAlreadySaved = false;
                                }else{
                                    isLocationAlreadySaved = true;
                                    break;

                                }
                            }

                            if(!isLocationAlreadySaved){
                                SharedHelper.putKey(AddressActivity.this,"recent_location"+String.valueOf(locationCount),myPlace.getAddress());
                                SharedHelper.putKey(AddressActivity.this,"recent_location_name"+String.valueOf(locationCount),myPlace.getName());
                                SharedHelper.putKey(AddressActivity.this,"recent_location_lat"+String.valueOf(locationCount),departureLat);
                                SharedHelper.putKey(AddressActivity.this,"recent_location_lng"+String.valueOf(locationCount),departureLng);
                                SharedHelper.putKey(AddressActivity.this,"count",String.valueOf(locationCount));
                            }

                        }

                        setResult(RESULT_OK, intent);
                        finish();
                    }
                },500);




            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                searchResultLV.setVisibility(View.GONE);

            }
        });
    }

    private String getPlaceAutoCompleteUrl(String input) {
        int meters = kilometer * 1000;
        StringBuilder urlString = new StringBuilder();
        urlString.append("https://maps.googleapis.com/maps/api/place/autocomplete/json");
        urlString.append("?input=");
        try {
            urlString.append(URLEncoder.encode(input, "utf8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        urlString.append("&location=");
        urlString.append(latitude + "," + longitude); // append lat long of current location to show nearby results.
        urlString.append("&radius="+meters+"&&components=country:us&language=en");
        //urlString.append("&language=en");
        urlString.append("&key=" + getResources().getString(R.string.google_maps_key));

        Log.d("FINAL URL:::   ", urlString.toString());
        return urlString.toString();
    }

    private void getFavoriteLocations() {

        Integer int_count;
        String count =  SharedHelper.getKey(AddressActivity.this,"count");

        if(!count.equalsIgnoreCase("")){
            int_count = Integer.valueOf(count);

            for(int i = 0; i <= int_count ; i++){
                RecentAddressData recentAddressData = new RecentAddressData();
                recentAddressData.address = SharedHelper.getKey(AddressActivity.this,"recent_location"+String.valueOf(i));
                recentAddressData.recentLocationName = SharedHelper.getKey(AddressActivity.this,"recent_location_name"+String.valueOf(i));
                recentAddressData.latitude = SharedHelper.getKey(AddressActivity.this,"recent_location_lat"+String.valueOf(i));
                recentAddressData.longitude = SharedHelper.getKey(AddressActivity.this,"recent_location_lng"+String.valueOf(i));
                lstRecentList.add(recentAddressData);
            }

            rvRecentResults.setVisibility(View.VISIBLE);

            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
            rvRecentResults.setLayoutManager(mLayoutManager);
            rvRecentResults.setItemAnimator(new DefaultItemAnimator());
            RecentPlacesAdapter recentPlacesAdapter = new RecentPlacesAdapter(AddressActivity.this,lstRecentList);
            rvRecentResults.setAdapter(recentPlacesAdapter);
        }else{
            recent_locations_tv.setVisibility(View.GONE);
        }



    }
}
