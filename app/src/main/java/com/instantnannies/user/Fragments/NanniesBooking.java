package com.instantnannies.user.Fragments;


import android.Manifest;
import android.animation.Animator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.LayerDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.provider.Settings;
import android.text.format.DateUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.instantnannies.user.Activities.AddressActivity;
import com.instantnannies.user.Activities.HistoryActivity;
import com.instantnannies.user.Activities.MainActivity;
import com.instantnannies.user.Activities.Payment;
import com.instantnannies.user.Adapters.NanniesAdapter;
import com.instantnannies.user.CustomFont.CustomBoldTextView;
import com.instantnannies.user.CustomFont.CustomButton;
import com.instantnannies.user.CustomFont.CustomTextView;
import com.instantnannies.user.Helper.ConnectionHelper;
import com.instantnannies.user.Helper.DataParser;
import com.instantnannies.user.Helper.MapAnimator;
import com.instantnannies.user.Helper.SharedHelper;
import com.instantnannies.user.Helper.URLHelper;
import com.instantnannies.user.Models.CardInfo;
import com.instantnannies.user.Models.Driver;
import com.instantnannies.user.Models.Nannies;
import com.instantnannies.user.R;
import com.instantnannies.user.Utils.CustomDialog;
import com.instantnannies.user.Utils.CustomRequestQueue;
import com.instantnannies.user.Utils.Utilities;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static com.instantnannies.user.Utils.CustomRequestQueue.trimMessage;


/**
 * A simple {@link Fragment} subclass.
 */
public class NanniesBooking extends Fragment implements OnMapReadyCallback, LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, GoogleMap.OnCameraMoveListener
{
    private static final String TAG = "NanniesFragment" ;
    Activity activity ;
    Context context;
    View view;
    static GoogleMap mMap;
    public static SupportMapFragment mapFragment = null;
    private MainActivity mainActivity;
    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;
    LocationManager manager;
    private final int ADD_CARD_CODE = 435;
    public static final int REQUEST_LOCATION = 1450;
    Marker currentMarker,AddressMarker;
    int value;
    CameraPosition cmPosition;
    RequestQueue mRequestQueue;
    PlacesClient placesClient;
    public RecyclerView nannies_rv;
    NanniesAdapter nanniesAdapter;
    List<Nannies> nanniesList = new ArrayList<>();
    public CardView nannies_cv;
    public Button confirm_location;
    public TextView nannies_desc,price;
    public ImageView image,shadowBack;
    TextView city_tv;
    TextView address_tv;
    RelativeLayout childs_relative_layout;
    DatePickerDialog datePickerDialog;
    private FusedLocationProviderClient fusedLocationClient;
    TextView date_tv,from_tv,to_tv;
    String scheduledDate = "";
    String scheduledTime = "";
    boolean afterToday = false;
    TimePickerDialog mTimePicker;
    Utilities utils = new Utilities();
    int NAV_DRAWER = 0;
    DrawerLayout drawer;
    CardView address_cv;
    int INTENT_FOR_RESULT = 125;
    String currentLocationAddress="", currentLocationName="", currentLat="",currentLng="";
    AlertDialog slotAlert;
    ImageView imgMenu,imgBack;
    Button verify_code;
    CustomDialog customDialog;
    //Animation
    Animation slide_down, slide_up, slide_up_top, slide_up_down;

    public static String id_of_nany="";
    ConnectionHelper helper;
    Boolean isInternet;
    boolean scheduleTrip = false;
    public NanniesBooking()
    {
        // Required empty public constructor
    }
    private ArrayList<CardInfo> cardInfoArrayList = new ArrayList<>();
    CustomBoldTextView  booking_id;
    CustomTextView lblPaymentType, lblPaymentChange;
    ImageView imgPaymentType;

    //         <!--3. Waiting For Providers ...-->
    TextView txtChange,frmSource;
    LinearLayout AfterAcceptButtonLayout,lnrAfterAcceptedStatus,lnrProviderAccepted;
    RelativeLayout lnrWaitingForProviders;
    CustomBoldTextView lblNoMatch,lblSurgePrice,lblProvider,lblServiceRequested,lblModelNumber,lblStatus;
    RatingBar ratingProvider;
    ImageView imgCenter,imgServiceRequested,imgProvider;
    CustomButton btnCancelRide,btnCancelTrip;
    private boolean mIsShowing;
    private boolean mIsHiding;
    private static final Interpolator INTERPOLATOR = new FastOutSlowInInterpolator();
    CardView srcDestLayout;
    private Marker destinationMarker;
    Boolean isHeight = true;
    View tripLine;
    CustomBoldTextView lblProviderNameRate;

    String isPaid = "", paymentMode = "";
    int totalRideAmount = 0, walletAmountDetected = 0, couponAmountDetected = 0;


    //         <!--2. Approximate Rate ...-->

    LinearLayout lnrApproximate;
    CustomButton btnRequestRideConfirm;
    CustomButton imgSchedule;
    CheckBox chkWallet;
    CustomBoldTextView lblEta;
    CustomBoldTextView lblType;
    CustomBoldTextView lblApproxAmount, surgeDiscount, surgeTxt;
    View lineView;

    LinearLayout ScheduleLayout;
    CustomBoldTextView scheduleDate;
    CustomBoldTextView scheduleTime;
    CustomButton scheduleBtn;

//          <!--5. Invoice Layout ...-->

    LinearLayout lnrInvoice;
    CustomBoldTextView lblBasePrice, lblDistanceCovered, lblExtraPrice, lblTimeTaken, lblDistancePrice, lblCommision, lblTaxPrice, lblTotalPrice, lblPaymentTypeInvoice, lblPaymentChangeInvoice, lblDiscountPrice, lblWalletPrice;
    ImageView imgPaymentTypeInvoice;
    CustomButton btnPayNow;
    CustomButton btnPaymentDoneBtn;
    LinearLayout discountDetectionLayout, walletDetectionLayout;
    LinearLayout bookingIDLayout;

    //          <!--6. Rate provider Layout ...-->

    LinearLayout lnrRateProvider;
    String feedBackRating;
    ImageView imgProviderRate;
    RatingBar ratingProviderRate;
    EditText txtCommentsRate;
    Button btnSubmitReview;
    Float pro_rating;

    //        <!-- Map frame -->
    LinearLayout mapLayout;

    int flowValue = 0;
    Marker marker;
    Double latitude, longitude;
    String currentAddress;
    DisplayMetrics displayMetrics;

    AlertDialog reasonDialog,timerDialog;
    AlertDialog cancelRideDialog;
    String cancalReason = "";

    String current_lat = "", current_lng = "", current_address = "", source_lat = "", source_lng = "", source_address = "",
            dest_lat = "", dest_lng = "", dest_address = "", extend_dest_lat = "", extend_dest_lng = "", extend_dest_address = "";
    String strPickLocation = "", strTag = "", strPickType = "";

    public String PreviousStatus = "";
    Handler handleCheckStatus;
    AlertDialog alert;
    String reqStatus = "";
    Driver driver;
    String is_track = "";

    int height_view;
    //MArkers
    boolean once = true;
    Marker availableProviders;
    private LatLng sourceLatLng;
    private LatLng destLatLng;
    private Marker sourceMarker;
   // private Marker destinationMarker;
    private Marker providerMarker;
    ArrayList<LatLng> points = new ArrayList<LatLng>();
    ArrayList<Marker> lstProviderMarkers = new ArrayList<Marker>();
    private HashMap<Integer, Marker> mHashMap = new HashMap<Integer, Marker>();
    String strTimeTaken = "";
    String previousTag = "";
    public String CurrentStatus = "";
    ImageView imgSos;
    boolean isshow = true;

    //Animation


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        mainActivity = (MainActivity) activity;
        Log.v("nannies_booking", "on_attach_called");

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (activity == null) {
            activity = getActivity();
        }
        if (context == null) {
            context = getContext();
        }
        Log.v("nannies_booking", "on_create_called");
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_nannies_booking, container, false);
        helper = new ConnectionHelper(context);
        isInternet = helper.isConnectingToInternet();

        viewInitializer();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Android M Permission check
            requestPermissions(new String[]{ACCESS_FINE_LOCATION}, 1);
        } else {
            setUpMapIfNeeded();
            MapsInitializer.initialize(getActivity());
        }



        statusCheck();

        return view;
    }


    void viewInitializer()
    {
        mRequestQueue = Volley.newRequestQueue(getActivity());
        Places.initialize(getActivity(),getString(R.string.google_maps_key));
        placesClient = Places.createClient(getActivity());
        nannies_rv = view.findViewById(R.id.nannies_rv);
        nannies_cv = view.findViewById(R.id.nannies_cv);
        confirm_location = view.findViewById(R.id.confirm_location);
        nannies_desc = view.findViewById(R.id.nannies_desc);
        price = view.findViewById(R.id.price);
        image = view.findViewById(R.id.image);
        address_tv = view.findViewById(R.id.address);
        city_tv = view.findViewById(R.id.city);
        childs_relative_layout = view.findViewById(R.id.childs_relative_layout);
        from_tv = view.findViewById(R.id.tv_from);
        to_tv = view.findViewById(R.id.tv_to);
        date_tv = view.findViewById(R.id.tv_date);
        address_cv = view.findViewById(R.id.address_cv);
        imgMenu = view.findViewById(R.id.imgMenu);
        imgBack = view.findViewById(R.id.imgBack);
        lblPaymentChange = view.findViewById(R.id.lblPaymentChange);
        lblPaymentType = view.findViewById(R.id.lblPaymentType);
        imgPaymentType = view.findViewById(R.id.imgPaymentType);
        imgServiceRequested = view.findViewById(R.id.imgServiceRequested);
        lnrProviderAccepted = view.findViewById(R.id.lnrProviderAccepted);
        lnrAfterAcceptedStatus = view.findViewById(R.id.lnrAfterAcceptedStatus);
        imgProvider = view.findViewById(R.id.imgProvider);
        lblProviderNameRate = view.findViewById(R.id.lblProviderName);
        tripLine = (View) view.findViewById(R.id.trip_line);
        lblPaymentChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChooser();
            }
        });
        slide_up = AnimationUtils.loadAnimation(context,R.anim.slide_up);
        shadowBack = (ImageView) view.findViewById(R.id.shadowBack);

        drawer = (DrawerLayout) view.findViewById(R.id.drawer_layout);
        drawer = (DrawerLayout) activity.findViewById(R.id.drawer_layout);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 1);
        nannies_rv.setLayoutManager(gridLayoutManager);
        nannies_rv.setItemAnimator(new DefaultItemAnimator());
        nannies_rv.setNestedScrollingEnabled(false);



        lblBasePrice = view.findViewById(R.id.lblBasePrice);
        lblDistanceCovered = view.findViewById(R.id.lblDistanceCovered);
        lblExtraPrice = view.findViewById(R.id.lblExtraPrice);
        lblTimeTaken = view.findViewById(R.id.lblTimeTaken);
        lblDistancePrice = view.findViewById(R.id.lblDistancePrice);
        lblCommision = view.findViewById(R.id.lblCommision);
        lblTaxPrice = view.findViewById(R.id.lblTaxPrice);
        lblTotalPrice = view.findViewById(R.id.lblTotalPrice);
        lblPaymentTypeInvoice = view.findViewById(R.id.lblPaymentTypeInvoice);
        lblPaymentChangeInvoice = view.findViewById(R.id.lblPaymentChangeInvoice);
        lblWalletPrice = view.findViewById(R.id.lblWalletPrice);
        lblDiscountPrice = view.findViewById(R.id.lblDiscountPrice);



        lnrInvoice = view.findViewById(R.id.lnrInvoice);
        imgPaymentTypeInvoice = view.findViewById(R.id.imgPaymentTypeInvoice);
        btnPayNow = view.findViewById(R.id.btnPayNow);
        btnPayNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                payNow();
            }
        });
        btnPaymentDoneBtn = view.findViewById(R.id.btnPaymentDoneBtn);
        btnPaymentDoneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnPayNow.setVisibility(View.GONE);
                btnPaymentDoneBtn.setVisibility(View.GONE);
                flowValue = 6;
                layoutChanges();
            }
        });
        discountDetectionLayout = view.findViewById(R.id.discountDetectionLayout);
        walletDetectionLayout = view.findViewById(R.id.walletDetectionLayout);
        bookingIDLayout = view.findViewById(R.id.bookingIDLayout);



        imgProviderRate = view.findViewById(R.id.imgProviderRate);


        //         <!--2. Approximate Rate ...-->


        lnrApproximate = view.findViewById(R.id.lnrApproximate);
        imgSchedule = view.findViewById(R.id.imgSchedule);
        chkWallet = view.findViewById(R.id.chkWallet);
        lblEta = view.findViewById(R.id.lblEta);
        lblType = view.findViewById(R.id.lblType);
        lblApproxAmount = view.findViewById(R.id.lblApproxAmount);
        surgeDiscount = view.findViewById(R.id.surgeDiscount);
        surgeTxt = view.findViewById(R.id.surge_txt);
        btnRequestRideConfirm = view.findViewById(R.id.btnRequestRideConfirm);
        lineView = view.findViewById(R.id.lineView);


        //Schedule Layout
        ScheduleLayout =  view.findViewById(R.id.ScheduleLayout);
        scheduleDate = view.findViewById(R.id.scheduleDate);
        scheduleTime =  view.findViewById(R.id.scheduleTime);
        scheduleBtn = view.findViewById(R.id.scheduleBtn);



        //          <!--6. Rate provider Layout ...-->

//        lnrHomeWork = (LinearLayout) view.findViewById(R.id.lnrHomeWork);
//        lnrHome = (LinearLayout) view.findViewById(R.id.lnrHome);
//        lnrWork = (LinearLayout) view.findViewById(R.id.lnrWork);
        lnrRateProvider = (LinearLayout) view.findViewById(R.id.lnrRateProvider);
        lblProviderNameRate = (CustomBoldTextView) view.findViewById(R.id.lblProviderName);
        imgProviderRate = (ImageView) view.findViewById(R.id.imgProviderRate);
        txtCommentsRate = (EditText) view.findViewById(R.id.txtComments);
        ratingProviderRate = (RatingBar) view.findViewById(R.id.ratingProviderRate);
        btnSubmitReview = (CustomButton) view.findViewById(R.id.btnSubmitReview);
        btnSubmitReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                submitReviewCall();
                flowValue = 0;
                layoutChanges();
            }
        });


//         <!--3. Waiting For Providers ...-->
        srcDestLayout = (CardView) view.findViewById(R.id.sourceDestLayout);
        lnrWaitingForProviders = (RelativeLayout) view.findViewById(R.id.lnrWaitingForProviders);
        lblNoMatch = view.findViewById(R.id.lblNoMatch);
        lblProvider = view.findViewById(R.id.lblProvider);
        lblStatus = view.findViewById(R.id.lblStatus);
        lblModelNumber = view.findViewById(R.id.lblModelNumber);
        lblServiceRequested = view.findViewById(R.id.lblServiceRequested);
        ratingProvider = view.findViewById(R.id.ratingProvider);
        AfterAcceptButtonLayout = view.findViewById(R.id.AfterAcceptButtonLayout);


//         <!-- Request to providers-->
//        lnrRequestProviders = (LinearLayout) rootView.findViewById(R.id.lnrRequestProviders);
//        rcvServiceTypes = (RecyclerView) rootView.findViewById(R.id.rcvServiceTypes);
        imgPaymentType = (ImageView) view.findViewById(R.id.imgPaymentType);
        lblPaymentType = view.findViewById(R.id.lblPaymentType);
        lblPaymentChange =view.findViewById(R.id.lblPaymentChange);
        booking_id = (CustomBoldTextView) view.findViewById(R.id.booking_id);

    //   btnRequestRides = (MyButton) rootView.findViewById(R.id.btnRequestRides);
     //  txtChange = view.findViewById(R.id.txtChange);
        // imgCenter = (ImageView) rootView.findViewById(R.id.imgCenter);

        btnCancelTrip =view.findViewById(R.id.btnCancelTrip);
        btnCancelRide =view.findViewById(R.id.btnCancelRide);
        btnCancelRide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCancelRideDialog();
            }
        });
        lblSurgePrice=view.findViewById(R.id.lblSurgePrice);
        imgSos = (ImageView) view.findViewById(R.id.imgSos);
        imgSos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSosPopUp();
            }
        });
        //        <!-- Map frame -->
        mapLayout = (LinearLayout) view.findViewById(R.id.mapLayout);
        displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);


        childs_relative_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nannies_cv.setVisibility(View.GONE);
                confirm_location.setVisibility(View.GONE);
                nannies_rv.setVisibility(View.VISIBLE);
            }
        });

        address_cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, AddressActivity.class);
                startActivityForResult(i,INTENT_FOR_RESULT);
            }
        });

        imgMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (NAV_DRAWER == 0) {
                        if (drawer != null)
                            drawer.openDrawer(GravityCompat.START);
                    } else {
                        NAV_DRAWER = 0;
                        if (drawer != null)
                            drawer.closeDrawers();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


        date_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dateTimePopup();
            }
        });

        from_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectTime(from_tv);
            }
        });

        to_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectTime(to_tv);
            }
        });

        confirm_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
                if(!scheduledDate.equalsIgnoreCase("") || !scheduledDate.isEmpty())
                {
                    if(currentDate.equals(scheduledDate))
                    {
                        //Toast.makeText(activity, "Today", Toast.LENGTH_SHORT).show();

                        DateFormat df = new SimpleDateFormat("hh:mm aa");
                        String currentTime = df.format(Calendar.getInstance().getTime());
                        Log.d("Length",String.valueOf(from_tv.getText().toString().length()));
                    if(slotCheck(currentTime,from_tv.getText().toString())==true&&slotCheckForFinishingTime(from_tv.getText().toString(),to_tv.getText().toString())==true)

                    {
                        if(!currentLat.equalsIgnoreCase("")&&!currentLng.equalsIgnoreCase("")&&
                                !currentLocationAddress.equalsIgnoreCase("")&&date_tv.getText().toString().length()!=0
                                &&!from_tv.getText().toString().equalsIgnoreCase("From")&&!to_tv.getText().toString().equalsIgnoreCase("To"))
                        {
                            sendRequest();
                        }
                        else if(currentLat.equalsIgnoreCase("")||currentLng.equalsIgnoreCase("")||
                                currentLocationAddress.equalsIgnoreCase(""))
                        {
                            Toast.makeText(context,"Please select location!",Toast.LENGTH_SHORT).show();

                        }


                    }
                      if(from_tv.getText().toString().equalsIgnoreCase("From"))
                    {
                        Toast.makeText(context,"Please select starting time!",Toast.LENGTH_SHORT).show();

                    }

                     if(to_tv.getText().toString().equalsIgnoreCase("To"))
                    {
                        Toast.makeText(context,"Please select finishing time!",Toast.LENGTH_SHORT).show();

                    }



                   }
                    else
                    {
                        DateFormat df = new SimpleDateFormat("hh:mm aa");
                        String currentTime = df.format(Calendar.getInstance().getTime());
                        Log.d("Length",String.valueOf(from_tv.getText().toString().length()));
                        if(slotCheckForFinishingTime(from_tv.getText().toString(),to_tv.getText().toString())==true)
                        {
                            if(!currentLat.equalsIgnoreCase("")&&!currentLng.equalsIgnoreCase("")&&
                                    !currentLocationAddress.equalsIgnoreCase("")&&date_tv.getText().toString().length()!=0
                                    &&!from_tv.getText().toString().equalsIgnoreCase("From")&&!to_tv.getText().toString().equalsIgnoreCase("To"))
                            {
                                sendRequest();
                            }
                            else if(currentLat.equalsIgnoreCase("")||currentLng.equalsIgnoreCase("")||
                                    currentLocationAddress.equalsIgnoreCase(""))
                            {
                                Toast.makeText(context,"Please select location!",Toast.LENGTH_SHORT).show();

                            }

                        }
                        if(from_tv.getText().toString().equalsIgnoreCase("From"))
                        {
                            Toast.makeText(context,"Please select starting time!",Toast.LENGTH_SHORT).show();

                        }

                        if(to_tv.getText().toString().equalsIgnoreCase("To"))
                        {
                            Toast.makeText(context,"Please select finishing time!",Toast.LENGTH_SHORT).show();

                        }



                    }
                }
                else
                {
                    Toast.makeText(activity, "Please select date!", Toast.LENGTH_SHORT).show();
                }
            }
        });


        getCards();

        checkStatus();

        handleCheckStatus = new Handler();
        //check status every 3 sec
        handleCheckStatus.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (helper.isConnectingToInternet()) {
                    if (!isAdded()) {
                        return;
                    }
                    checkStatus();
                    utils.print("Handler", "Called");
                    if (alert != null && alert.isShowing()) {
                        alert.dismiss();
                        alert = null;
                    }
                }
                else
                    {
                    showDialog();
                }
                handleCheckStatus.postDelayed(this, 3000);
            }
        }, 3000);

        flowValue = 0;
        layoutChanges();


    }

    //region fun
    private void showChooser()
    {
        Intent intent = new Intent(getActivity(), Payment.class);
        startActivityForResult(intent, 5555);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(3000);
        mLocationRequest.setFastestInterval(3000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (ContextCompat.checkSelfPermission(context,
                ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED && mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        if (mMap != null) {
            Log.v("ONLOCCHANGEDCALLED", "onLocationChanged: ");
            if (currentMarker != null)
            {
                currentMarker.remove();
            }

            MarkerOptions markerOptions1 = new MarkerOptions()
                    .position(new LatLng(location.getLatitude(), location.getLongitude()))
                    .anchor(0.5f, 0.5f).icon(BitmapDescriptorFactory.fromResource(R.drawable.current_location));
            currentMarker = mMap.addMarker(markerOptions1);

            Log.v("MAP", "onLocationChanged: 1 " + location.getLatitude());
            Log.v("MAP", "onLocationChanged: 2 " + location.getLongitude());

            currentLat = "" + location.getLatitude();
            currentLng = "" + location.getLongitude();

            current_lat = "" + location.getLatitude();
            current_lng = "" + location.getLongitude();

            if (value == 0) {

                Geocoder geocoder;
                List<Address> addresses;
                geocoder = new Geocoder(context, Locale.getDefault());
                try {

                    addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                    String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                    String city = addresses.get(0).getLocality();
                    String state = addresses.get(0).getAdminArea();
                    String country = addresses.get(0).getCountryName();
                    String postalCode = addresses.get(0).getPostalCode();
                    String knownName = addresses.get(0).getFeatureName();

                    currentLocationAddress=address;

                    address_tv.setText(address);
                    city_tv.setText(city);

                    getServiceList();


                } catch (IOException e) {
                    e.printStackTrace();
                }




                Log.v("value_location", String.valueOf(value));
                LatLng myLocation = new LatLng(location.getLatitude(), location.getLongitude());
                Log.v("location_curr",String.valueOf(myLocation));
                CameraPosition cameraPosition = new CameraPosition.Builder().target(myLocation).zoom(16).build();
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(myLocation);
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                value++;
            }

        }
    }

    @Override
    public void onCameraMove()
    {
        cmPosition = mMap.getCameraPosition();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            getActivity(), R.raw.style_json));

            if (!success) {
                Log.e("Map:Style", "Style parsing failed.");
            } else {
                Log.e("Map:Style", "Style Applied.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e("Map:Style", "Can't find style. Error: ", e);
        }
        mMap = googleMap;
        // do other tasks here
        setupMap();


        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context,
                    ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                //Location Permission already granted
                buildGoogleApiClient();

            } else {
                //Request Location Permission
                checkLocationPermission();
            }
        } else {
            buildGoogleApiClient();

        }
    }

    private void setUpMapIfNeeded() {
        if (mMap == null) {
            FragmentManager fm = getChildFragmentManager();
            mapFragment = ((SupportMapFragment) fm.findFragmentById(R.id.map));
            mapFragment.getMapAsync(this);
        }
        if (mMap != null) {
            setupMap();
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        // Permission Granted
//                        //Toast.makeText(SignInActivity.this, "PERMISSION_GRANTED", Toast.LENGTH_SHORT).show();
                        setUpMapIfNeeded();
                        MapsInitializer.initialize(context);

                        if (ContextCompat.checkSelfPermission(context,
                                ACCESS_FINE_LOCATION)
                                == PackageManager.PERMISSION_GRANTED) {

                            if (mGoogleApiClient == null) {
                                buildGoogleApiClient();
                            }
                            setUpMapIfNeeded();
                            MapsInitializer.initialize(context);

                        }
                    } else {
                        requestPermissions(new String[]{ACCESS_FINE_LOCATION}, 1);
                    }
                }
                break;


            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
    @SuppressWarnings("MissingPermission")
    private void setupMap()
    {
        mMap.setMyLocationEnabled(false);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.setBuildingsEnabled(true);
        mMap.getUiSettings().setCompassEnabled(false);
        mMap.getUiSettings().setRotateGesturesEnabled(false);
        mMap.getUiSettings().setTiltGesturesEnabled(false);
        mMap.setOnCameraMoveListener(this);
    }

    protected synchronized void buildGoogleApiClient()
    {
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(context, ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    ACCESS_FINE_LOCATION)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(context)
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission, please accept to use location functionality")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(getActivity(),
                                        new String[]{ACCESS_FINE_LOCATION},
                                        1);
                            }
                        })
                        .create()
                        .show();
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{ACCESS_FINE_LOCATION},
                        1);
            }
        }
    }

    public void statusCheck()
    {
        manager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER))
        {
            enableLoc();
        }
    }

    private void getCards() {
        Ion.with(this)
                .load(URLHelper.CARD_PAYMENT_LIST)
                .addHeader("X-Requested-With", "XMLHttpRequest")
                .addHeader("Authorization", SharedHelper.getKey(context, "token_type") + " " + SharedHelper.getKey(context, "access_token"))
                .asString()
                .withResponse()
                .setCallback(new FutureCallback<com.koushikdutta.ion.Response<String>>() {
                    @Override
                    public void onCompleted(Exception e, com.koushikdutta.ion.Response<String> response) {
                        // response contains both the headers and the string result
                        try {
                            if (response.getHeaders().code() == 200) {
                                try {
                                    JSONArray jsonArray = new JSONArray(response.getResult());
                                    if (jsonArray.length() > 0)
                                    {
                                        CardInfo cardInfo = new CardInfo();

                                        for (int i = 0; i < jsonArray.length(); i++)
                                        {
                                            JSONObject cardObj = jsonArray.getJSONObject(i);
                                            cardInfo = new CardInfo();
                                            cardInfo.setCardId(cardObj.optString("card_id"));
                                            cardInfo.setCardType(cardObj.optString("brand"));
                                            cardInfo.setLastFour(cardObj.optString("last_four"));
                                            cardInfoArrayList.add(cardInfo);
                                        }
                                    }
                                    if (cardInfoArrayList.size() > 0)
                                    {
                                        getCardDetailsForPayment(cardInfoArrayList.get(0));

                                    }


                                } catch (JSONException e1) {
                                    e1.printStackTrace();
                                }
                            }
                        }
                        catch (Exception e2) {
                            e2.printStackTrace();
                            CardInfo cardInfo = new CardInfo();
                            cardInfo.setCardId("CASH");
                            cardInfo.setCardType("CASH");
                            cardInfo.setLastFour("CASH");
                            cardInfoArrayList.add(cardInfo);
                        }
                    }
                });

    }


    private void enableLoc() {
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle bundle) {

                    }
                    @Override
                    public void onConnectionSuspended(int i) {
                        mGoogleApiClient.connect();
                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult connectionResult) {

                        Log.v("Location error","Location error " + connectionResult.getErrorCode());
                    }
                }).build();
        mGoogleApiClient.connect();

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(30 * 1000);
        locationRequest.setFastestInterval(5 * 1000);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(getActivity(), REQUEST_LOCATION);

                        }catch (NullPointerException | IntentSender.SendIntentException e){
                            e.printStackTrace();
                            try{
                                status.startResolutionForResult(getActivity(), REQUEST_LOCATION);
                            }catch (Exception ee){
                                ee.printStackTrace();
                            }
                        }
                        break;
                }
            }
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mMap = null;
        Log.v("ride_booking", "on_destroy_view_called");
    }

    /*Departure Date Selector*/
    public void dateTimePopup(){
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR); // current year
        int mMonth = c.get(Calendar.MONTH); // current month
        int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
        // date picker dialog
        datePickerDialog = new DatePickerDialog(context,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {

                        // set day of month , month and year value in the edit text
                        String choosedMonth = "";
                        String choosedDate = "";
                        choosedMonth = String.valueOf(monthOfYear+1);
                        String choosedDateFormat = dayOfMonth + "-" + (monthOfYear + 1) + "-" + year;
                        scheduledDate = choosedDateFormat;
                       /* try {
                            choosedMonth = utils.getMonth(choosedDateFormat);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }*/
                        if (dayOfMonth < 10) {
                            choosedDate = "0" + dayOfMonth;
                        } else {
                            choosedDate = "" + dayOfMonth;
                        }

                        if(monthOfYear+1 <10){
                            choosedMonth = "0"+choosedMonth;
                        }else {
                            choosedMonth = ""+choosedMonth;
                        }
                        afterToday = utils.isAfterToday(year, monthOfYear+1, dayOfMonth);
                        scheduledDate = choosedDate+"-" + choosedMonth+"-"+year;
                        date_tv.setText(scheduledDate);
                        datePickerDialog.dismiss();
                        //  selectTime(choosedDate,choosedMonth,year);
                    }
                }, mYear, mMonth, mDay);
        Calendar cal=Calendar.getInstance();
        cal.add(Calendar.MONTH,1);
        long afterTwoMonthsinMilli=cal.getTimeInMillis();
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        //  datePickerDialog.getDatePicker().setMaxDate((System.currentTimeMillis() - 1000) + (1000 * 60 * 60 * 24 * 7));
        datePickerDialog.getDatePicker().setMaxDate(afterTwoMonthsinMilli);
        datePickerDialog.show();
    }


    public void selectTime(final TextView textView){

        Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);

        final String selectedTime = "";
        mTimePicker = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
            int callCount = 0;   //To track number of calls to onTimeSet()

            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {

                if (callCount == 0) {
                    String choosedHour = "";
                    String choosedMinute = "";
                    String choosedTimeZone = "";
                    String choosedTime = "";

                    scheduledTime = selectedHour + ":" + selectedMinute;

                    choosedTime = scheduledTime;

                    if (selectedHour >= 12) {
                        choosedTimeZone = "pm";
                        if(selectedHour > 12){
                            selectedHour = selectedHour - 12;

                        }else{
                            if (selectedHour < 10) {
                                choosedHour = "0" + selectedHour;
                            } else {
                                choosedHour = "" + selectedHour;
                            }
                        }

                    } else {
                        choosedTimeZone = "am";
                        if (selectedHour < 10) {
                            choosedHour = "0" + selectedHour;
                        } else {
                            choosedHour = "" + selectedHour;
                        }
                    }

                    if (selectedMinute < 10) {
                        choosedMinute = "0" + selectedMinute;
                    } else {
                        choosedMinute = "" + selectedMinute;
                    }
                    choosedTime = selectedHour + ":" + choosedMinute + " " + choosedTimeZone;


                    Date date = null;
                    try {
                        date = new SimpleDateFormat("yyyy-mm-dd", Locale.ENGLISH).parse("28-1-2020");
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    long milliseconds = date.getTime();
                    if (!DateUtils.isToday(milliseconds)) {
                        scheduledTime = choosedTime;
                        textView.setText(choosedTime);
                    } else {
                        if (utils.checktimings(scheduledTime)) {
                            scheduledTime = choosedTime;
                            textView.setText(choosedTime);
                        }
                    }


                    //  current_date=date + "-" + month + "-" + year + " " + choosedTime;
                }
                callCount++;

            }
        }, hour, minute, false);//Yes 24 hour time
        mTimePicker.setTitle("Travel Time");
        mTimePicker.show();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == INTENT_FOR_RESULT && resultCode == Activity.RESULT_OK){

            if (currentMarker !=null){
                currentMarker.remove();
            }

            currentLocationAddress = data.getExtras().getString("address");
            currentLocationName = data.getExtras().getString("address_name");
            currentLat = data.getExtras().getString("lat");
            currentLng = data.getExtras().getString("lng");
            LatLng currentLatLng = new LatLng(Double.parseDouble(currentLat),Double.parseDouble(currentLng));
            address_tv.setText(currentLocationAddress);
            city_tv.setText(currentLocationName);
            mMap.clear();
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(currentLatLng)
                    .snippet("Current Service Address")
                    .title(currentLocationAddress)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.user_marker));
            AddressMarker = mMap.addMarker(markerOptions);

           // LatLng myLocation1 = new LatLng(queriedLocation.latitude, queriedLocation.longitude);
            CameraPosition cameraPosition1 = new CameraPosition.Builder().target(currentLatLng).zoom(16).build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition1));


        }
        if (requestCode == ADD_CARD_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                boolean result = data.getBooleanExtra("isAdded", false);
                if (result) {
                    getCards();
                }
            }
        }
        if (requestCode == 5555)
        {
            if (resultCode == Activity.RESULT_OK) {
                CardInfo cardInfo = data.getParcelableExtra("card_info");
                getCardDetailsForPayment(cardInfo);
            }
        }

    }

    Boolean slotCheck( String CurrentTime, String from){

        Boolean Flag=false;
        try {
            int days,hours,min;
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm a");
            Date date1 =simpleDateFormat.parse(CurrentTime);
            Date date2 =simpleDateFormat.parse(from);
            long difference = date2.getTime() - date1.getTime();
            days = (int) (difference / (1000*60*60*24));
            hours = (int) ((difference - (1000*60*60*24*days)) / (1000*60*60));
            min = (int) (difference - (1000*60*60*24*days) - (1000*60*60*hours)) / (1000*60);
            hours = (hours < 0 ? -hours : hours);

            if(hours == 0 || hours < 4)
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage(context.getResources().getString(R.string.slot_alert_message))
                        .setCancelable(false).setTitle(context.getResources().getString(R.string.slot_alert_title))
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                slotAlert.dismiss();
                            }
                        });
                if (slotAlert == null) {
                    slotAlert = builder.create();
                    slotAlert.show();
                }else{
                    slotAlert.show();
                }
                Flag=false;
            }
            else {
                Flag=true;
            }

        }catch (ParseException ex){
            ex.printStackTrace();
        }
        return Flag;
    }
    Boolean slotCheckForFinishingTime( String from, String too)
    {

        Boolean Flag=false;
        try {
            int days,hours,min;
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm a");
            Date date1 =simpleDateFormat.parse(from);
            Date date2 =simpleDateFormat.parse(too);
            long difference = date2.getTime() - date1.getTime();
            days = (int) (difference / (1000*60*60*24));
            hours = (int) ((difference - (1000*60*60*24*days)) / (1000*60*60));
            min = (int) (difference - (1000*60*60*24*days) - (1000*60*60*hours)) / (1000*60);
            hours = (hours < 0 ? -hours : hours);

            if(hours <= 0 )
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage(context.getResources().getString(R.string.slot_alert1_message))
                        .setCancelable(false).setTitle(context.getResources().getString(R.string.slot_alert_title))
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                slotAlert.dismiss();
                            }
                        });
                if (slotAlert == null) {
                    slotAlert = builder.create();
                    slotAlert.show();
                }else{
                    slotAlert.show();
                }
                Flag=false;

            }
            else {
                Flag=true;
            }

        }catch (ParseException ex){
            ex.printStackTrace();
        }
    return Flag;
    }

    public void getServiceList() {

        try {
            customDialog = new CustomDialog(context);
            customDialog.setCancelable(false);

            if (customDialog != null) {
                customDialog.show();
            }

            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(URLHelper.GET_SERVICE_LIST_API, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    try {

                        customDialog.dismiss();
                        utils.print("GetServices", response.toString());
                        if (SharedHelper.getKey(context, "service_type").equalsIgnoreCase("")) {
                            SharedHelper.putKey(context, "service_type", "" + response.optJSONObject(0).optString("id"));
                            id_of_nany=response.optJSONObject(0).optString("id");
                        }

                        if (response.length() > 0) {
                            Picasso.get()
                                    .load(response.getJSONObject(0).optString("image"))
                                    .placeholder(R.drawable.baby)
                                    .error(R.drawable.baby)
                                    .into(image);
                            nannies_desc.setText(response.getJSONObject(0).optString("name"));
                            price.setText("$"+response.getJSONObject(0).optString("fixed")+"/hour");




                            for(int i = 0; i<response.length(); i ++){
                                JSONObject jsonObject = new JSONObject();
                                jsonObject = response.getJSONObject(i);
                                nanniesList.add(new Nannies(jsonObject.getString("name"),jsonObject.getString("fixed"), jsonObject.getString("image") ,0,jsonObject.getString("id") ));

                            }

                         /*   nanniesList.add(new Nannies("Childcare for 1 children","$80/hour", R.drawable.baby ,0));
                            nanniesList.add(new Nannies("Childcare for 2 children","$100/hour", R.drawable.child_two ,0));
                            nanniesList.add(new Nannies("Childcare for 3 children","$120/hour",  R.drawable.child_three,0));*/
                            nanniesAdapter = new NanniesAdapter(context,nanniesList, NanniesBooking.this);
                            nannies_rv.setAdapter(nanniesAdapter);

                        /*    currentPostion = 0;
                            ServiceListAdapter serviceListAdapter = new ServiceListAdapter(response);
                            rcvServiceTypes.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false));
                            rcvServiceTypes.setAdapter(serviceListAdapter);
                            getProvidersList(SharedHelper.getKey(context, "service_type"));*/
                        } else {
                            utils.displayMessage(getView(), context, context.getResources().getString(R.string.no_service));
                        }
                        if (mMap != null) {
                            mMap.clear();
                        }


                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    try {
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
                                        utils.displayMessage(getView(), context, errorObj.optString("message"));
                                    } catch (Exception e) {
                                        utils.displayMessage(getView(), context, context.getResources().getString(R.string.something_went_wrong));
                                    }

                                } else if (response.statusCode == 401) {
                                    refreshAccessToken("SERVICE_LIST");
                                } else if (response.statusCode == 422) {

                                    json = trimMessage(response.data.toString());
                                    if (json != "" && json != null) {
                                        utils.displayMessage(getView(), context, json);
                                    } else {
                                        utils.displayMessage(getView(), context, context.getResources().getString(R.string.please_try_again));
                                    }

                                } else if (response.statusCode == 503) {
                                    utils.displayMessage(getView(), context, context.getResources().getString(R.string.server_down));

                                } else {
                                    utils.displayMessage(getView(), context, context.getResources().getString(R.string.please_try_again));

                                }

                            } catch (Exception e) {
                                utils.displayMessage(getView(), context, context.getResources().getString(R.string.something_went_wrong));

                            }

                        } else {
                            utils.displayMessage(getView(), context, context.getResources().getString(R.string.please_try_again));

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<String, String>();
                    headers.put("X-Requested-With", "XMLHttpRequest");
                    headers.put("Authorization", "" + SharedHelper.getKey(context, "token_type") + " "
                            + SharedHelper.getKey(context, "access_token"));
                    return headers;
                }
            };

            CustomRequestQueue.getInstance(context).addToRequestQueue(jsonArrayRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void refreshAccessToken(final String tag)
    {

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
                if (tag.equalsIgnoreCase("SERVICE_LIST")) {
                    getServiceList();
                }
                else if (tag.equalsIgnoreCase("PROVIDERS_LIST")) {
                    getProvidersList("");
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String json = "";
                NetworkResponse response = error.networkResponse;

                if (response != null && response.data != null) {
                    SharedHelper.putKey(context, "loggedIn", context.getResources().getString(R.string.False));
                    utils.GoToBeginActivity(getActivity());
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

        CustomRequestQueue.getInstance(context).addToRequestQueue(jsonObjectRequest);
    }


    public void sendRequest() {
        customDialog = new CustomDialog(context);
        customDialog.setCancelable(false);
        if (customDialog != null)
            customDialog.show();

        JSONObject object = new JSONObject();
        try {

            object.put("d_latitude", currentLat);
            object.put("d_longitude", currentLng);
            object.put("d_address", currentLocationAddress);
            object.put("service_type", id_of_nany);
            if (SharedHelper.getKey(context, "payment_mode").equals("CASH")) {
                object.put("payment_mode", SharedHelper.getKey(context, "payment_mode"));
            } else {
                object.put("payment_mode", SharedHelper.getKey(context, "payment_mode"));
                object.put("card_id", SharedHelper.getKey(context, "card_id"));
            }
            object.put("schedule_date", scheduledDate);
            object.put("schedule_time_from", from_tv.getText().toString());
            object.put("schedule_time_to", to_tv.getText().toString());



            utils.print("SendRequestInput", "" + object.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

     //   CustomRequestQueue.getInstance(context).cancelRequestInQueue("send_request");

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLHelper.SEND_REQUEST_API, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (response != null) {
                    utils.print("SendRequestResponse", response.toString());
                    if ((customDialog != null) && (customDialog.isShowing()))
                        customDialog.dismiss();
                    if (response.optString("request_id", "").equals("")) {
                        utils.displayMessage(getView(), context, response.optString("message"));
                    } else {
                        SharedHelper.putKey(context, "current_status", "");
                        SharedHelper.putKey(context, "request_id", "" + response.optString("request_id"));
                        if (!scheduledDate.equalsIgnoreCase("") && !scheduledTime.equalsIgnoreCase(""))
                            scheduleTrip = true;
                        else
                            scheduleTrip = false;
                          flowValue = 3;
                          layoutChanges();
                    }
                }
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
                        Log.e("SendREquest", "onErrorResponse: " + errorObj.optString("message"));
                        if (response.statusCode == 400 || response.statusCode == 405 || response.statusCode == 500) {
                            try {
                                utils.showAlert(context, errorObj.optString("error"));
                            } catch (Exception e) {
                                utils.showAlert(context, context.getResources().getString(R.string.something_went_wrong));
                            }
                        } else if (response.statusCode == 401) {
                            refreshAccessToken("SEND_REQUEST");
                        } else if (response.statusCode == 422) {
                            json = trimMessage(new String(response.data));
                            if (json != "" && json != null) {
                                utils.showAlert(context, json);
                            } else {
                                utils.showAlert(context, context.getResources().getString(R.string.please_try_again));
                            }
                        } else if (response.statusCode == 503) {
                            utils.showAlert(context, context.getResources().getString(R.string.server_down));
                        } else {
                            utils.showAlert(context, context.getResources().getString(R.string.please_try_again));
                        }
                    } catch (Exception e) {
                        utils.showAlert(context, context.getResources().getString(R.string.something_went_wrong));
                    }
                } else {
                    utils.showAlert(context, context.getResources().getString(R.string.please_try_again));
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("X-Requested-With", "XMLHttpRequest");
                headers.put("Authorization", "" + SharedHelper.getKey(context, "token_type") + " " + SharedHelper.getKey(context, "access_token"));
                return headers;
            }
        };

        CustomRequestQueue.getInstance(context).addToRequestQueue(jsonObjectRequest);
    }

    public void submitReviewCall() {

        customDialog = new CustomDialog(context);
        customDialog.setCancelable(false);
        if (customDialog != null)
            customDialog.show();

        JSONObject object = new JSONObject();
        try {
            object.put("request_id", SharedHelper.getKey(context, "request_id"));
            object.put("rating", feedBackRating);
            object.put("comment", "" + txtCommentsRate.getText().toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLHelper.RATE_PROVIDER_API, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response)
            {

                utils.print("SubmitRequestResponse", response.toString());
                utils.hideKeypad(context, activity.getCurrentFocus());
                if ((customDialog != null) && (customDialog.isShowing()))
                    customDialog.dismiss();

                mapClear();


                if (!current_lat.equalsIgnoreCase("") && !current_lng.equalsIgnoreCase(""))
                {
                    LatLng myLocation = new LatLng(Double.parseDouble(current_lat), Double.parseDouble(current_lng));
                    CameraPosition cameraPosition = new CameraPosition.Builder().target(myLocation).zoom(16).build();
                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                }

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

                        if (response.statusCode == 400 || response.statusCode == 405 || response.statusCode == 500)
                        {
                            try {
                                utils.displayMessage(getView(), context, errorObj.optString("message"));
                            } catch (Exception e) {
                                utils.displayMessage(getView(), context, context.getResources().getString(R.string.something_went_wrong));
                            }

                        }
                        else if (response.statusCode == 401)
                        {
                            refreshAccessToken("SUBMIT_REVIEW");
                        } else if (response.statusCode == 422)
                        {

                            json = trimMessage(new String(response.data));
                            if (json != "" && json != null) {
                                utils.displayMessage(getView(), context, json);
                            } else {
                                utils.displayMessage(getView(), context, context.getResources().getString(R.string.please_try_again));
                            }
                        }
                        else if (response.statusCode == 503)
                        {
                            utils.displayMessage(getView(), context, context.getResources().getString(R.string.server_down));
                        }
                        else
                        {
                            utils.displayMessage(getView(), context, context.getResources().getString(R.string.please_try_again));
                        }

                    } catch (Exception e) {
                        utils.displayMessage(getView(), context, context.getResources().getString(R.string.something_went_wrong));
                    }

                } else {
                    utils.displayMessage(getView(), context, context.getResources().getString(R.string.please_try_again));
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("X-Requested-With", "XMLHttpRequest");
                headers.put("Authorization", "" + SharedHelper.getKey(context, "token_type") + " " + SharedHelper.getKey(context, "access_token"));
                return headers;
            }
        };

        CustomRequestQueue.getInstance(context).addToRequestQueue(jsonObjectRequest);
    }

    private void getCardDetailsForPayment(CardInfo cardInfo) {
        if (cardInfo.getLastFour().equals("CASH")) {
            SharedHelper.putKey(context, "payment_mode", "CASH");
            imgPaymentType.setImageResource(R.drawable.money_icon);
            lblPaymentType.setText("CASH");
        }
        else {
            if (SharedHelper.getKey(context, "card_id") == null || SharedHelper.getKey(context, "card_id") == ""
                    || SharedHelper.getKey(context, "payment_mode") == null || SharedHelper.getKey(context, "payment_mode") == "") {

                SharedHelper.putKey(context, "card_id", cardInfo.getCardId());
                SharedHelper.putKey(context, "payment_mode", "CARD");
                SharedHelper.putKey(context, "last_four", cardInfo.getLastFour());
                imgPaymentType.setImageResource(R.drawable.visa);
                lblPaymentType.setText("XXXX-XXXX-XXXX-" + cardInfo.getLastFour());
            }
            else
            {

                imgPaymentType.setImageResource(R.drawable.visa);
                lblPaymentType.setText("XXXX-XXXX-XXXX-" + SharedHelper.getKey(context,"last_four"));

            }
        }
    }

    //TODO:======================== Layout Changes =========================
    void layoutChanges() {
        try {
            utils.hideKeypad(getActivity(), getActivity().getCurrentFocus());

//
//            if (lnrApproximate.getVisibility() == View.VISIBLE)
//            {
//                lnrApproximate.startAnimation(slide_down);
//            }
//            else if (ScheduleLayout.getVisibility() == View.VISIBLE)
//            {
//                ScheduleLayout.startAnimation(slide_down);
//            }
//            else if (lnrInvoice.getVisibility() == View.VISIBLE)
//            {
//                lnrInvoice.startAnimation(slide_down);
//            }
//            else if (lnrRateProvider.getVisibility() == View.VISIBLE)
//            {
//                lnrRateProvider.startAnimation(slide_down);
//            }
//

            //TODO: Waiting Animatin
            lnrWaitingForProviders.setVisibility(View.GONE);
            //TODO: Nannies And time selection
            nannies_cv.setVisibility(View.GONE);

            //TODO: Confrim request button
            confirm_location.setVisibility(View.GONE);

            //TODO: Search Address
            address_cv.setVisibility(View.GONE);

            //TODO: Source and Destination name
            srcDestLayout.setVisibility(View.GONE);

            //TODO: Menu
            imgMenu.setVisibility(View.GONE);
            imgBack.setVisibility(View.GONE);

            //TODO: Service
            lnrProviderAccepted.setVisibility(View.GONE);


            //TODO: Invoice
            lnrInvoice.setVisibility(View.GONE);
            //TODO: RATING
            lnrRateProvider.setVisibility(View.GONE);

            lnrApproximate.setVisibility(View.GONE);

            if (flowValue == 0)
            {

                currentLat="";
                currentLng="";
                currentLocationAddress="";
                date_tv.setText("Date");
                from_tv.setText("From");
                to_tv.setText("To");
                nannies_desc.setText("");
                SharedHelper.putKey(context, "service_type", "");
                getProvidersList("");
                id_of_nany="";
                if (imgMenu.getVisibility() == View.GONE)
                {
                    srcDestLayout.setVisibility(View.GONE);
                    srcDestLayout.setOnClickListener(null);
                    if (mMap != null) {
                        mMap.clear();
                        stopAnim();
                        setupMap();
                    }
                }

                address_cv.setVisibility(View.VISIBLE);
                imgMenu.setVisibility(View.VISIBLE);
                nannies_cv.setVisibility(View.VISIBLE);
                nannies_cv.startAnimation(slide_up);

                confirm_location.setVisibility(View.VISIBLE);
                confirm_location.startAnimation(slide_up);


                dest_address = "";
                dest_lat = "";
                dest_lng = "";
                source_lat = "" + current_lat;
                source_lng = "" + current_lng;
                source_address = "" + current_address;
            //    sourceAndDestinationLayout.setVisibility(View.VISIBLE);

                resetHeight();
         //       getProvidersList("");
            }

            if(flowValue ==3)
            {
                lnrWaitingForProviders.setVisibility(View.VISIBLE);

                //sourceAndDestinationLayout.setVisibility(View.GONE);
                if (destinationMarker != null)
                {

                    destinationMarker.setDraggable(false);
                }
                resetHeight();
            }

            else if (flowValue == 4)
            {

                imgMenu.setVisibility(View.VISIBLE);
                lnrProviderAccepted.startAnimation(slide_up);
                lnrProviderAccepted.setVisibility(View.VISIBLE);
                if (sourceMarker != null && destinationMarker != null) {
                    sourceMarker.setDraggable(false);
                    destinationMarker.setDraggable(false);
                }

                setHeight(lnrProviderAccepted);
            }

            else if (flowValue == 5)
            {

                imgMenu.setVisibility(View.VISIBLE);
                lnrInvoice.startAnimation(slide_up);
                lnrInvoice.setVisibility(View.VISIBLE);
                if (sourceMarker != null && destinationMarker != null) {
                    sourceMarker.setDraggable(false);
                    destinationMarker.setDraggable(false);
                }

                setHeight(lnrInvoice);
            }

            else if (flowValue == 6)
            {
                lnrInvoice.setVisibility(View.GONE);
                imgMenu.setVisibility(View.VISIBLE);
                lnrRateProvider.setVisibility(View.VISIBLE);
                lnrRateProvider.startAnimation(slide_up);

             //   lnrRateProvider.startAnimation(slide_down);
                LayerDrawable drawable = (LayerDrawable) ratingProviderRate.getProgressDrawable();
                drawable.getDrawable(0).setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_ATOP);
                drawable.getDrawable(1).setColorFilter(Color.parseColor("#FFAB00"), PorterDuff.Mode.SRC_ATOP);
                drawable.getDrawable(2).setColorFilter(Color.parseColor("#FFAB00"), PorterDuff.Mode.SRC_ATOP);
                ratingProviderRate.setRating(1.0f);
                feedBackRating = "1";
                ratingProviderRate.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                    @Override
                    public void onRatingChanged(RatingBar ratingBar, float rating, boolean b) {
                        if (rating < 1.0f) {
                            ratingProviderRate.setRating(1.0f);
                            feedBackRating = "1";
                        }
                        feedBackRating = String.valueOf((int) rating);
                    }
                });
                if (sourceMarker != null && destinationMarker != null) {
                    sourceMarker.setDraggable(false);
                    destinationMarker.setDraggable(false);
                }

                setHeight(lnrRateProvider);

            }

            else if (flowValue == 8)
            {
                // clear all views
                shadowBack.setVisibility(View.GONE);
                if (sourceMarker != null && destinationMarker != null) {
                    sourceMarker.setDraggable(false);
                    destinationMarker.setDraggable(false);
                }
            }

            //setHeight(lnrWaitingForProviders);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void resetHeight(){
        if(!isHeight) {


            int statusBarHeight = 0;
            int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
            if (resourceId > 0) {
                statusBarHeight = getResources().getDimensionPixelSize(resourceId);
            }

            if (statusBarHeight > convertDpToPixel(24)) {
                ViewGroup.LayoutParams params = mapLayout.getLayoutParams();

                int resetheight = displayMetrics.heightPixels;
                params.height = resetheight;
                mapLayout.setLayoutParams(params);
                mapLayout.requestLayout();
            }else{

                ViewGroup.LayoutParams params = mapLayout.getLayoutParams();

                int resetheight = displayMetrics.heightPixels - statusBarHeight;
                params.height = resetheight;
                mapLayout.setLayoutParams(params);
                mapLayout.requestLayout();
            }



        }
    }

    public void setHeight(final View view){
        isHeight = false;
        resetHeight();
        height_view =0;
        view.measure(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        int width = view.getMeasuredWidth();
        height_view = view.getMeasuredHeight();

        Rect rectangle = new Rect();
        Window window = getActivity().getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(rectangle);
        int statusBarHeight = rectangle.top;
        ViewGroup.LayoutParams params = mapLayout.getLayoutParams();
        int actionBarHeight = 0;
        TypedValue tv = new TypedValue();
        if (getActivity().getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true))
        {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data,getResources().getDisplayMetrics());
        }
        int newheight=0;
        // Changes the height and width to the specified *pixels*
        if(view.getId() == R.id.lnrProviderAccepted) {
            newheight = (rectangle.height() - height_view);
        }
        else if(view.getId() == R.id.lnrApproximate)
        {
            newheight = (rectangle.height() - height_view);
        }
        else
        {
            if(isshow)
            {
                newheight = (rectangle.height() - height_view) - statusBarHeight - actionBarHeight;
                isshow = false;
            }
            else
            {
                newheight = (rectangle.height() - height_view);
            }

        }
        params.height = newheight;
        mapLayout.setLayoutParams(params);
        mapLayout.requestLayout();


    }

    public static int convertDpToPixel ( float dp)
    {
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return Math.round(px);
    }
    private void showCancelRideDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        builder.setTitle(context.getResources().getString(R.string.app_name))
                .setIcon(R.mipmap.ic_launcher)
                .setMessage(context.getResources().getString(R.string.cancel_ride_alert));
        builder.setCancelable(false);
        builder.setPositiveButton(context.getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                showreasonDialog();
            }
        });
        builder.setNegativeButton(context.getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        cancelRideDialog = builder.create();
        cancelRideDialog.show();
    }

    private void showreasonDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.cancel_dialog, null);
        final EditText reasonEtxt = (EditText) view.findViewById(R.id.reason_etxt);
        Button submitBtn = (Button) view.findViewById(R.id.submit_btn);
        builder.setIcon(R.mipmap.ic_launcher)
                .setTitle(R.string.app_name)
                .setView(view)
                .setCancelable(true);
        reasonDialog = builder.create();
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancalReason = reasonEtxt.getText().toString();
                cancelRequest();
                reasonDialog.dismiss();
            }
        });
        reasonDialog.show();
    }

    public void cancelRequest() {
        customDialog = new CustomDialog(context);
        customDialog.setCancelable(false);
        if (customDialog != null)
            customDialog.show();
        JSONObject object = new JSONObject();
        try {
            object.put("request_id", SharedHelper.getKey(context, "request_id"));
            object.put("cancel_reason", cancalReason);
        } catch (Exception e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLHelper.CANCEL_REQUEST_API, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                utils.print("CancelRequestResponse", response.toString());
                Toast.makeText(context, context.getResources().getString(R.string.request_cancel), Toast.LENGTH_SHORT).show();
                if ((customDialog != null) && (customDialog.isShowing()))
                    customDialog.dismiss();
                mapClear();
                SharedHelper.putKey(context, "request_id", "");
                flowValue = 0;
                PreviousStatus = "";
                layoutChanges();
                setupMap();
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
                                utils.displayMessage(getView(), context, errorObj.optString("error"));
                                flowValue = 0;
                                PreviousStatus = "";
                                layoutChanges();
                                setupMap();
                            } catch (Exception e) {
                                utils.displayMessage(getView(), context, context.getResources().getString(R.string.something_went_wrong));
                            }
                            layoutChanges();
                        } else if (response.statusCode == 401) {
                            refreshAccessToken("CANCEL_REQUEST");
                        } else if (response.statusCode == 422) {

                            json = trimMessage(new String(response.data));
                            if (json != "" && json != null) {
                                utils.displayMessage(getView(), context, json);
                            } else {
                                utils.displayMessage(getView(), context, context.getResources().getString(R.string.please_try_again));
                            }
                            layoutChanges();
                        } else if (response.statusCode == 503) {
                            utils.displayMessage(getView(), context, context.getResources().getString(R.string.server_down));
                            layoutChanges();
                        } else {
                            utils.displayMessage(getView(), context, context.getResources().getString(R.string.please_try_again));
                            layoutChanges();
                        }

                    } catch (Exception e) {
                        utils.displayMessage(getView(), context, context.getResources().getString(R.string.something_went_wrong));
                        layoutChanges();
                    }

                } else {
                    utils.displayMessage(getView(), context, context.getResources().getString(R.string.please_try_again));
                    layoutChanges();
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("X-Requested-With", "XMLHttpRequest");
                headers.put("Authorization", "" + SharedHelper.getKey(context, "token_type") + " " + SharedHelper.getKey(context, "access_token"));
                return headers;
            }
        };

        CustomRequestQueue.getInstance(context).addToRequestQueue(jsonObjectRequest);
    }

    private void mapClear() {

        mMap.clear();
        source_lat = "";
        source_lng = "";
        dest_lat = "";
        dest_lng = "";
        if (!current_lat.equalsIgnoreCase("") && !current_lng.equalsIgnoreCase("")) {
            LatLng myLocation = new LatLng(Double.parseDouble(current_lat), Double.parseDouble(current_lng));
            CameraPosition cameraPosition = new CameraPosition.Builder().target(myLocation).zoom(16).build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
    }

    private void stopAnim() {
        if (mMap != null) {
            MapAnimator.getInstance().stopAnim();
        }
    }

    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(context.getResources().getString(R.string.connect_to_network))
                .setCancelable(false)
                .setPositiveButton(context.getResources().getString(R.string.connect_to_wifi), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                    }
                });
        if (alert == null) {
            alert = builder.create();
            alert.show();
        }
    }

    private void setTrackStatus() {

    }

    public void reCreateMap() {
        if (mMap != null) {
            if (!source_lat.equalsIgnoreCase("") && !source_lng.equalsIgnoreCase("")) {
                sourceLatLng = new LatLng(Double.parseDouble(source_lat), Double.parseDouble(source_lng));
            }
            if (!dest_lat.equalsIgnoreCase("") && !dest_lng.equalsIgnoreCase("")) {
                destLatLng = new LatLng(Double.parseDouble(dest_lat), Double.parseDouble(dest_lng));
            }
            utils.print("LatLng", "Source:" + sourceLatLng + " Destination: " + destLatLng);
            //String url = getDirectionsUrl(sourceLatLng, destLatLng);
            String url = getUrl(sourceLatLng.latitude, sourceLatLng.longitude, destLatLng.latitude, destLatLng.longitude);
            FetchUrl fetchUrl = new FetchUrl();
            fetchUrl.execute(url);
           /* DownloadTask downloadTask = new DownloadTask();
            downloadTask.execute(url);*/
        }
    }
    private String getUrl(double source_latitude, double source_longitude, double dest_latitude, double dest_longitude)
    {

        // Origin of route
        String str_origin = "origin=" + source_latitude + "," + source_longitude;

        // Destination of route
        String str_dest = "destination=" + dest_latitude + "," + dest_longitude;


        // Sensor enabled
        String sensor = "sensor=false";

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters+ "&key=" + getString(R.string.google_maps_key);


        return url;
    }

    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();
            Log.d("downloadUrl", data.toString());
            br.close();

        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    // Fetches data from url passed
    private class FetchUrl extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {
            // For storing data from web service
            String data = "";
            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
                Log.d("Background Task data", data.toString());
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
                JSONObject jsonObj = new JSONObject(result);
                if (!jsonObj.optString("status").equalsIgnoreCase("ZERO_RESULTS")){
                    ParserTask parserTask = new ParserTask();
                    // Invokes the thread for parsing the JSON data
                    parserTask.execute(result);
                }else{
                    mMap.clear();
                    stopAnim();
                    flowValue = 0;
                    layoutChanges();
                    gotoCurrentPosition();
                    utils.displayMessage(getView(), context, context.getResources().getString(R.string.no_service));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    public void gotoCurrentPosition(){
        if (!current_lat.equalsIgnoreCase("") && !current_lng.equalsIgnoreCase("")) {
            LatLng myLocation = new LatLng(Double.parseDouble(current_lat), Double.parseDouble(current_lng));
            CameraPosition cameraPosition = new CameraPosition.Builder().target(myLocation).zoom(20).build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        }
    }
    /**
     * A class to parse the Google Places in JSON format
     */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {
        DataParser parser;
        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                Log.d("ParserTask", jsonData[0].toString());
                parser = new DataParser();
                Log.d("ParserTask", parser.toString());

                // Starts parsing data
                routes = parser.parse(jObject);
                Log.d("ParserTask", "Executing routes");
                Log.d("ParserTask", routes.toString());

            } catch (Exception e) {
                Log.d("ParserTask", e.toString());
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;

            if (result != null){
                // Traversing through all the routes
                if (result.size() > 0){
                    for (int i = 0; i < result.size(); i++) {
                        points = new ArrayList<>();
                        lineOptions = new PolylineOptions();

                        // Fetching i-th route
                        List<HashMap<String, String>> path = result.get(i);

                        // Fetching all the points in i-th route
                        for (int j = 0; j < path.size(); j++) {
                            HashMap<String, String> point = path.get(j);

                            double lat = Double.parseDouble(point.get("lat"));
                            double lng = Double.parseDouble(point.get("lng"));
                            LatLng position = new LatLng(lat, lng);

                            points.add(position);
                        }

                        if (!source_lat.equalsIgnoreCase("") && !source_lng.equalsIgnoreCase("")) {
                            LatLng location = new LatLng(Double.parseDouble(source_lat), Double.parseDouble(source_lng));
                            //mMap.clear();
                            if (sourceMarker != null)
                                sourceMarker.remove();
                            MarkerOptions markerOptions = new MarkerOptions()
                                    .position(location).snippet(source_address)
                                    .title("source").draggable(true)
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.user_marker));
                            marker = mMap.addMarker(markerOptions);
                            sourceMarker = mMap.addMarker(markerOptions);
                            //CameraPosition cameraPosition = new CameraPosition.Builder().target(location).zoom(18).build();
                            //mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                        }
                        if (!dest_lat.equalsIgnoreCase("") && !dest_lng.equalsIgnoreCase("")) {
                            destLatLng = new LatLng(Double.parseDouble(dest_lat), Double.parseDouble(dest_lng));
                            if (destinationMarker != null)
                                destinationMarker.remove();
                            MarkerOptions destMarker = new MarkerOptions()
                                    .position(destLatLng).title("destination").snippet(dest_address).draggable(true)
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.provider_marker));
                            destinationMarker = mMap.addMarker(destMarker);
                            LatLngBounds.Builder builder = new LatLngBounds.Builder();
                            builder.include(sourceMarker.getPosition());
                            builder.include(destinationMarker.getPosition());
                            LatLngBounds bounds = builder.build();
                            int padding = 150; // offset from edges of the map in pixels
                            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                            mMap.animateCamera(cu);
                        }

                        if (flowValue == 1){
                            if (sourceMarker != null && destinationMarker != null) {
                                sourceMarker.setDraggable(true);
                                destinationMarker.setDraggable(true);
                            }
                        }else{
                            if (is_track.equalsIgnoreCase("YES") &&
                                    (CurrentStatus.equalsIgnoreCase("STARTED") || CurrentStatus.equalsIgnoreCase("PICKEDUP")
                                            || CurrentStatus.equalsIgnoreCase("ARRIVED"))){
                                if (sourceMarker != null && destinationMarker != null) {
                                    sourceMarker.setDraggable(false);
                                    destinationMarker.setDraggable(true);
                                }
                            }else{
                                if (sourceMarker != null && destinationMarker != null) {
                                    sourceMarker.setDraggable(false);
                                    destinationMarker.setDraggable(false);
                                }
                            }
                        }

                        // Adding all the points in the route to LineOptions
                        lineOptions.addAll(points);
                        lineOptions.width(5);
                        lineOptions.color(Color.BLACK);

                        Log.d("onPostExecute", "onPostExecute lineoptions decoded");

                    }


                    strTimeTaken = parser.getEstimatedTime();

                }else{
                    mMap.clear();
                    utils.displayMessage(getView(), context, context.getResources().getString(R.string.no_service));
                }

            }

            // Drawing polyline in the Google Map for the i-th route
            if (lineOptions != null && points != null) {
                //mMap.addPolyline(lineOptions);
                startAnim(points);
            } else {
                Log.d("onPostExecute", "without Polylines drawn");
            }
        }
    }

    private void startAnim(ArrayList<LatLng> routeList) {
        if (mMap != null && routeList.size() > 1) {
            MapAnimator.getInstance().animateRoute(context, mMap, routeList);
        }
    }
    private void hide(final View view) {
        mIsHiding = true;
        ViewPropertyAnimator animator = view.animate()
                .translationY(view.getHeight())
                .setInterpolator(INTERPOLATOR)
                .setDuration(200);

        animator.setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                // Prevent drawing the View after it is gone
                mIsHiding = false;
                view.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animator) {
                // Canceling a hide should show the view
                mIsHiding = false;
                if (!mIsShowing) {
                    show(view);
                }
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
            }
        });

        animator.start();
    }

    private void show(final View view) {
        mIsShowing = true;
        ViewPropertyAnimator animator = view.animate()
                .translationY(0)
                .setInterpolator(INTERPOLATOR)
                .setDuration(500);

        animator.setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                view.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                mIsShowing = false;
            }

            @Override
            public void onAnimationCancel(Animator animator) {
                // Canceling a show should hide the view
                mIsShowing = false;
                if (!mIsHiding) {
                    hide(view);
                }
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
            }
        });

        animator.start();
    }

    //endregion

    public void payNow() {

        customDialog = new CustomDialog(context);
        customDialog.setCancelable(false);
        if (customDialog != null)
            customDialog.show();

        JSONObject object = new JSONObject();
        try {
            object.put("request_id", SharedHelper.getKey(context, "request_id"));
            object.put("payment_mode", paymentMode);
            object.put("is_paid", isPaid);
            utils.print("PayNowRequest", "" + object.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLHelper.PAY_NOW_API, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                utils.print("PayNowRequestResponse", response.toString());
                if ((customDialog != null) && (customDialog.isShowing()))
                    customDialog.dismiss();
                flowValue = 6;
                layoutChanges();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if ((customDialog != null) && (customDialog.isShowing()))
                    customDialog.dismiss();
                String json = "";
                NetworkResponse response = error.networkResponse;
                if (response != null && response.data != null) {
                    try {
                        JSONObject errorObj = new JSONObject(new String(response.data));

                        if (response.statusCode == 400 || response.statusCode == 405 || response.statusCode == 500) {
                            try {
                                utils.displayMessage(getView(), context, errorObj.optString("message"));
                            } catch (Exception e) {
                                utils.displayMessage(getView(), context, context.getResources().getString(R.string.something_went_wrong));
                            }
                        } else if (response.statusCode == 401)
                        {
                            refreshAccessToken("PAY_NOW");
                        } else if (response.statusCode == 422) {

                            json = trimMessage(new String(response.data));
                            if (json != "" && json != null) {
                                utils.displayMessage(getView(), context, json);
                            } else {
                                utils.displayMessage(getView(), context, context.getResources().getString(R.string.please_try_again));
                            }
                        } else if (response.statusCode == 503) {
                            utils.displayMessage(getView(), context, context.getResources().getString(R.string.server_down));
                        } else {
                            utils.displayMessage(getView(), context, context.getResources().getString(R.string.please_try_again));
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        utils.displayMessage(getView(), context, context.getResources().getString(R.string.something_went_wrong));
                    }

                } else {
                    utils.displayMessage(getView(), context, context.getResources().getString(R.string.please_try_again));
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization", "" + SharedHelper.getKey(context, "token_type") + " " + SharedHelper.getKey(context, "access_token"));
                headers.put("X-Requested-With", "XMLHttpRequest");
                return headers;
            }
        };

        CustomRequestQueue.getInstance(context).addToRequestQueue(jsonObjectRequest);

    }





    private void checkStatus() {
        try {

            utils.print("Handler", "Inside");
            if (isInternet)
            {
                final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URLHelper.REQUEST_STATUS_CHECK_API, null, new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response)
                    {
                        SharedHelper.putKey(context, "req_status", "");
                        try
                        {
                            if (customDialog != null && customDialog.isShowing())
                            {
                                customDialog.dismiss();
                            }
                        } catch (Exception e)
                        {
                            e.printStackTrace();
                        }

                        reqStatus = "";
                        utils.print("Response", "" + response.toString());

                        if (response.optJSONArray("data") != null && response.optJSONArray("data").length() > 0)
                        {
                            utils.print("response", "not null");
                            try {
                                JSONArray requestStatusCheck = response.optJSONArray("data");
                                JSONObject requestStatusCheckObject = requestStatusCheck.getJSONObject(0);
                                //Driver Detail
                                if (requestStatusCheckObject.optJSONObject("provider") != null) {
                                    driver = new Driver();
                                    driver.setFname(requestStatusCheckObject.optJSONObject("provider").optString("first_name"));
                                    driver.setLname(requestStatusCheckObject.optJSONObject("provider").optString("last_name"));
                                    driver.setEmail(requestStatusCheckObject.optJSONObject("provider").optString("email"));
                                    driver.setMobile(requestStatusCheckObject.optJSONObject("provider").optString("mobile"));
                                    driver.setImg(requestStatusCheckObject.optJSONObject("provider").optString("avatar"));
                                    driver.setRating(requestStatusCheckObject.optJSONObject("provider").optString("rating"));
                                }
                                String status = requestStatusCheckObject.optString("status");
                                is_track = requestStatusCheckObject.optString("is_track");
                                SharedHelper.putKey(context, "track_status", is_track);
                                reqStatus = requestStatusCheckObject.optString("status");
                                SharedHelper.putKey(context, "req_status", requestStatusCheckObject.optString("status"));
                                String wallet = requestStatusCheckObject.optString("use_wallet");
                              //  source_lat = requestStatusCheckObject.optString("s_latitude");
                               // source_lng = requestStatusCheckObject.optString("s_longitude");
                                source_lat = "";
                                source_lng = "";
                                dest_lat = requestStatusCheckObject.optString("d_latitude");
                                dest_lng = requestStatusCheckObject.optString("d_longitude");

                                if (!source_lat.equalsIgnoreCase("") && !source_lng.equalsIgnoreCase("")) {
                                    LatLng myLocation = new LatLng(Double.parseDouble(source_lat), Double.parseDouble(source_lng));
                                    CameraPosition cameraPosition = new CameraPosition.Builder().target(myLocation).zoom(16).build();
                                }

                                // surge price
                                if (requestStatusCheckObject.optString("surge").equalsIgnoreCase("1")) {
                                    lblSurgePrice.setVisibility(View.VISIBLE);
                                } else {
                                    lblSurgePrice.setVisibility(View.GONE);
                                }

                                setTrackStatus();

                                utils.print("PreviousStatus", "" + PreviousStatus);

                                if (!PreviousStatus.equals(status)) {
                                    mMap.clear();
                                    PreviousStatus = status;
                                    flowValue = 8;
                                    layoutChanges();
                                    SharedHelper.putKey(context, "request_id", "" + requestStatusCheckObject.optString("id"));
                        //            reCreateMap();
                                    CurrentStatus = status;
                                    utils.print("ResponseStatus", "SavedCurrentStatus: " + CurrentStatus + " Status: " + status);
                                    switch (status) {

                                            //region searching
                                        case "SEARCHING":
                                            show(lnrWaitingForProviders);
                                            //rippleBackground.startRippleAnimation();
                                            strTag = "search_completed";
                                            if (!source_lat.equalsIgnoreCase("") && !source_lng.equalsIgnoreCase("")) {
                                                LatLng myLocation1 = new LatLng(Double.parseDouble(source_lat),
                                                        Double.parseDouble(source_lng));
                                                CameraPosition cameraPosition1 = new CameraPosition.Builder().target(myLocation1).zoom(20).build();
                                                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition1));
                                            }
                                            break;

                                            //endregion

                                            // region cancel
                                        case "CANCELLED":
                                            strTag = "";
                                            if (reasonDialog != null){
                                                if (reasonDialog.isShowing()){
                                                    reasonDialog.dismiss();
                                                }
                                            }
                                            if (cancelRideDialog != null){
                                                if (cancelRideDialog.isShowing()){
                                                    cancelRideDialog.dismiss();
                                                }
                                            }
                                            imgSos.setVisibility(View.GONE);
                                            break;

                                            //endregion

                                            // region accept
                                        case "ACCEPTED":
                                            strTag = "ride_accepted";
                                            try {
                                                JSONObject provider = requestStatusCheckObject.getJSONObject("provider");
                                                JSONObject service_type = requestStatusCheckObject.getJSONObject("service_type");
                                                JSONObject provider_service = requestStatusCheckObject.getJSONObject("provider_service");
                                                SharedHelper.putKey(context, "provider_mobile_no", "" + provider.optString("mobile"));
                                                lblProvider.setText(provider.optString("first_name") + " " + provider.optString("last_name"));
                                                if (provider.optString("avatar").startsWith("http"))
                                                    Picasso.get().load(provider.optString("avatar")).placeholder(R.drawable.ic_dummy_user).error(R.drawable.ic_dummy_user).into(imgProvider);
                                                else
                                                    Picasso.get().load(URLHelper.base + "storage/" + provider.optString("avatar")).placeholder(R.drawable.ic_dummy_user).error(R.drawable.ic_dummy_user).into(imgProvider);
                                                lblServiceRequested.setText(service_type.optString("name"));
                                                lblModelNumber.setText(provider_service.optString("service_model") + "\n" + provider_service.optString("service_number"));
                                                Picasso.get().load(service_type.optString("image"))
                                                        .placeholder(R.drawable.car_select).error(R.drawable.car_select)
                                                        .into(imgServiceRequested);
                                                ratingProvider.setRating(Float.parseFloat(provider.optString("rating")));
                                                //lnrAfterAcceptedStatus.setVisibility(View.GONE);
                                                lblStatus.setText(context.getResources().getString(R.string.arriving));
                                                AfterAcceptButtonLayout.setVisibility(View.VISIBLE);
                                                btnCancelTrip.setText(context.getResources().getString(R.string.cancel_trip));
                                                show(lnrProviderAccepted);
                                                flowValue = 9;
                                                layoutChanges();
                                                if (is_track.equalsIgnoreCase("YES")){
                                                    flowValue = 10;
                                                    address_cv.setVisibility(View.GONE);
                                                }else{
                                                    flowValue = 4;
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                            break;

                                            //endregion


                                            // region started

                                        case "STARTED":
                                            strTag = "ride_started";
                                            try {
                                                JSONObject provider = requestStatusCheckObject.getJSONObject("provider");
                                                JSONObject service_type = requestStatusCheckObject.getJSONObject("service_type");
                                            //    JSONObject provider_service = requestStatusCheckObject.getJSONObject("provider_service");
                                                SharedHelper.putKey(context, "provider_mobile_no", "" + provider.optString("mobile"));
                                                lblProvider.setText(provider.optString("first_name") + " " + provider.optString("last_name"));
                                                if (provider.optString("avatar").startsWith("http"))
                                                    Picasso.get().load(provider.optString("avatar")).placeholder(R.drawable.ic_dummy_user).error(R.drawable.ic_dummy_user).into(imgProvider);
                                                else
                                                    Picasso.get().load(URLHelper.base + "storage/" + provider.optString("avatar")).placeholder(R.drawable.ic_dummy_user).error(R.drawable.ic_dummy_user).into(imgProvider);
                                                lblServiceRequested.setText(service_type.optString("name"));
                                         //       lblModelNumber.setText(provider_service.optString("service_model") + "\n" + provider_service.optString("service_number"));
                                                Picasso.get().load(service_type.optString("image")).placeholder(R.drawable.car_select)
                                                        .error(R.drawable.car_select).into(imgServiceRequested);
                                                ratingProvider.setRating(Float.parseFloat(provider.optString("rating")));
                                                //lnrAfterAcceptedStatus.setVisibility(View.GONE);
                                                lblStatus.setText(context.getResources().getString(R.string.arriving));
                                                btnCancelTrip.setText(context.getResources().getString(R.string.cancel_trip));
                                                AfterAcceptButtonLayout.setVisibility(View.VISIBLE);
                                                if (is_track.equalsIgnoreCase("YES")){
                                                    flowValue = 10;
                                                    txtChange.setVisibility(View.GONE);
                                                }else{
                                                    flowValue = 4;
                                                }
                                                layoutChanges();
                                                if (!requestStatusCheckObject.optString("schedule_at").equalsIgnoreCase("null")) {
                                                    SharedHelper.putKey(context, "current_status", "");
                                                    Intent intent = new Intent(getActivity(), HistoryActivity.class);
                                                    intent.putExtra("tag", "upcoming");
                                                    startActivity(intent);
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                            break;
                                        // endregion

                                            // region arrived
                                        case "ARRIVED":
                                            once = true;

                                            // timerStart.setVisibility(View.VISIBLE);
                                            strTag = "ride_arrived";
                                            utils.print("MyTest", "ARRIVED");
                                            try {
                                                utils.print("MyTest", "ARRIVED TRY");
                                                JSONObject provider = requestStatusCheckObject.getJSONObject("provider");
                                                JSONObject service_type = requestStatusCheckObject.getJSONObject("service_type");
                                             //   JSONObject provider_service = requestStatusCheckObject.getJSONObject("provider_service");
                                                lblProvider.setText(provider.optString("first_name") + " " + provider.optString("last_name"));
                                                if (provider.optString("avatar").startsWith("http"))
                                                    Picasso.get().load(provider.optString("avatar")).placeholder(R.drawable.ic_dummy_user).error(R.drawable.ic_dummy_user).into(imgProvider);
                                                else
                                                    Picasso.get().load(URLHelper.base + "storage/" + provider.optString("avatar")).placeholder(R.drawable.ic_dummy_user).error(R.drawable.ic_dummy_user).into(imgProvider);
                                                lblServiceRequested.setText(service_type.optString("name"));
//                                                lblModelNumber.setText(provider_service.optString("service_model") + "\n" + provider_service.optString("service_number"));
                                                Picasso.get().load(service_type.optString("image")).placeholder(R.drawable.car_select).error(R.drawable.car_select).into(imgServiceRequested);
                                                ratingProvider.setRating(Float.parseFloat(provider.optString("rating")));
                                                lnrAfterAcceptedStatus.setVisibility(View.VISIBLE);
                                                tripLine.setVisibility(View.VISIBLE);
                                                lblStatus.setText(context.getResources().getString(R.string.arrived));
                                                //  reverseTimer(300,timerStart);
                                                btnCancelTrip.setText(context.getResources().getString(R.string.cancel_trip));
                                                //timerCompleted=false;
                                                //
                                                // countDownTimer.cancel();
                                                AfterAcceptButtonLayout.setVisibility(View.VISIBLE);
                                                if (is_track.equalsIgnoreCase("YES")){
                                                    flowValue = 10;
                                                    txtChange.setVisibility(View.GONE);
                                                }else{
                                                    flowValue = 4;
                                                }
                                                layoutChanges();
                                            } catch (Exception e) {
                                                utils.print("MyTest", "ARRIVED CATCH");
                                                e.printStackTrace();
                                            }


                                            break;

                                            //endregion

                                            // region pickup

                                        case "PICKEDUP":
                                            once = true;
                                            strTag = "ride_picked";
                                            //  timerStart.setVisibility(View.GONE);
                                            // timerCompleted=true;
                                            try {
                                                JSONObject provider = requestStatusCheckObject.getJSONObject("provider");
                                                JSONObject service_type = requestStatusCheckObject.getJSONObject("service_type");
                                                lblProvider.setText(provider.optString("first_name") + " " + provider.optString("last_name"));
                                                if (provider.optString("avatar").startsWith("http"))
                                                    Picasso.get().load(provider.optString("avatar")).placeholder(R.drawable.ic_dummy_user).error(R.drawable.ic_dummy_user).into(imgProvider);
                                                else
                                                    Picasso.get().load(URLHelper.base + "storage/" + provider.optString("avatar")).placeholder(R.drawable.ic_dummy_user).error(R.drawable.ic_dummy_user).into(imgProvider);
                                                lblServiceRequested.setText(service_type.optString("name"));
                                                Picasso.get().load(service_type.optString("image")).placeholder(R.drawable.car_select).error(R.drawable.car_select).into(imgServiceRequested);
                                                ratingProvider.setRating(Float.parseFloat(provider.optString("rating")));
                                                lnrAfterAcceptedStatus.setVisibility(View.VISIBLE);
                                                tripLine.setVisibility(View.VISIBLE);
                                                imgSos.setVisibility(View.VISIBLE);
                                                //    countDownTimer.cancel();
                                                //   timerCompleted=false;
                                                lblStatus.setText(context.getResources().getString(R.string.picked_up));
                                                btnCancelTrip.setText(context.getResources().getString(R.string.share));
                                                AfterAcceptButtonLayout.setVisibility(View.VISIBLE);
                                                if (is_track.equalsIgnoreCase("YES")){
                                                    flowValue = 10;
                                                    txtChange.setVisibility(View.VISIBLE);
                                                }else{
                                                    flowValue = 4;
                                              //      txtChange.setVisibility(View.GONE);
                                                }
                                                layoutChanges();
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                            break;

                                            //endregion


                                            //region dropped

                                        case "DROPPED":
                                            once = true;
                                            strTag = "";
                                            imgSos.setVisibility(View.VISIBLE);
                                            try {
                                                JSONObject provider = requestStatusCheckObject.optJSONObject("provider");
                                                if (requestStatusCheckObject.optJSONObject("payment") != null) {
                                                    JSONObject payment = requestStatusCheckObject.optJSONObject("payment");
                                                    isPaid = requestStatusCheckObject.optString("paid");
                                                    totalRideAmount = payment.optInt("payable");
                                                    walletAmountDetected = payment.optInt("wallet");
                                                    couponAmountDetected = payment.optInt("discount");
                                                    paymentMode = requestStatusCheckObject.optString("payment_mode");
                                                    lblDistanceCovered.setText(String.format("%.2f",Float.parseFloat(payment.optString("distance")))+" KM");
                                                    lblBasePrice.setText(SharedHelper.getKey(context, "currency") + "" + payment.optString("fixed"));
                                                    lblTaxPrice.setText(SharedHelper.getKey(context, "currency") + "" + payment.optString("tax"));
                                                    lblDistancePrice.setText(SharedHelper.getKey(context, "currency")
                                                            + "" + payment.optString("distance"));
                                                    lblTimeTaken.setText(requestStatusCheckObject.optString("travel_time") +" mins");
                                                    lblDiscountPrice.setText(SharedHelper.getKey(context, "currency") + "" + couponAmountDetected);
                                                    lblWalletPrice.setText(SharedHelper.getKey(context, "currency") + "" + walletAmountDetected);
                                                    //lblCommision.setText(SharedHelper.getKey(context, "currency") + "" + payment.optString("commision"));
                                                    lblTotalPrice.setText(SharedHelper.getKey(context, "currency") + "" + payment.optString("payable"));

                                                    //Review values set
                                                    lblProviderNameRate.setText(context.getResources().getString(R.string.rate_provider) + " " + provider.optString("first_name") + " " + provider.optString("last_name"));
                                                    if (provider.optString("avatar").startsWith("http"))
                                                    {
                                                        Picasso.get().load(provider.optString("avatar")).placeholder(R.drawable.loading).error(R.drawable.ic_dummy_user).into(imgProvider);
                                                    }
                                                    else
                                                    {
                                                        Picasso.get().load(URLHelper.base + "storage/" + provider.optString("avatar")).placeholder(R.drawable.loading).error(R.drawable.ic_dummy_user).into(imgProvider);
                                                    }

                                                    if (requestStatusCheckObject.optString("booking_id") != null && !requestStatusCheckObject.optString("booking_id").equalsIgnoreCase(""))
                                                    {
                                                        booking_id.setText(requestStatusCheckObject.optString("booking_id"));
                                                    }
                                                    else {
                                                        bookingIDLayout.setVisibility(View.GONE);
                                                        booking_id.setVisibility(View.GONE);
                                                    }

                                                    if (isPaid.equalsIgnoreCase("1") && paymentMode.equalsIgnoreCase("CASH")
                                                            && walletAmountDetected > 0 && couponAmountDetected > 0 && totalRideAmount == 0) {
                                                        btnPayNow.setVisibility(View.GONE);
                                                        btnPaymentDoneBtn.setVisibility(View.VISIBLE);
                                                        walletDetectionLayout.setVisibility(View.VISIBLE);
                                                        discountDetectionLayout.setVisibility(View.VISIBLE);
                                                        flowValue = 5;
                                                        layoutChanges();
                                                        imgPaymentTypeInvoice.setImageResource(R.drawable.money_icon);
                                                        lblPaymentTypeInvoice.setText("CASH");
                                                    } else if (isPaid.equalsIgnoreCase("0") && paymentMode.equalsIgnoreCase("CASH")
                                                            && walletAmountDetected > 0 && couponAmountDetected > 0 && totalRideAmount > 0) {
                                                        btnPayNow.setVisibility(View.GONE);
                                                        btnPaymentDoneBtn.setVisibility(View.GONE);
                                                        walletDetectionLayout.setVisibility(View.VISIBLE);
                                                        discountDetectionLayout.setVisibility(View.VISIBLE);
                                                        flowValue = 5;
                                                        layoutChanges();
                                                        imgPaymentTypeInvoice.setImageResource(R.drawable.money_icon);
                                                        lblPaymentTypeInvoice.setText("CASH");
                                                    } else if (isPaid.equalsIgnoreCase("0") && paymentMode.equalsIgnoreCase("CASH") && walletAmountDetected > 0
                                                            && couponAmountDetected == 0 && totalRideAmount > 0) {
                                                        btnPayNow.setVisibility(View.GONE);
                                                        btnPaymentDoneBtn.setVisibility(View.GONE);
                                                        walletDetectionLayout.setVisibility(View.VISIBLE);
                                                        discountDetectionLayout.setVisibility(View.GONE);
                                                        flowValue = 5;
                                                        layoutChanges();
                                                        imgPaymentTypeInvoice.setImageResource(R.drawable.money_icon);
                                                        lblPaymentTypeInvoice.setText("CASH");
                                                    } else if (isPaid.equalsIgnoreCase("1") && paymentMode.equalsIgnoreCase("CASH")
                                                            && walletAmountDetected > 0 && couponAmountDetected == 0 &&
                                                            totalRideAmount == 0) {
                                                        btnPayNow.setVisibility(View.GONE);
                                                        btnPaymentDoneBtn.setVisibility(View.VISIBLE);
                                                        walletDetectionLayout.setVisibility(View.VISIBLE);
                                                        discountDetectionLayout.setVisibility(View.GONE);
                                                        flowValue = 5;
                                                        layoutChanges();
                                                        imgPaymentTypeInvoice.setImageResource(R.drawable.money_icon);
                                                        lblPaymentTypeInvoice.setText("CASH");
                                                    } else if (isPaid.equalsIgnoreCase("0") && paymentMode.equalsIgnoreCase("CASH")
                                                            && walletAmountDetected == 0 && couponAmountDetected > 0 &&
                                                            totalRideAmount > 0) {
                                                        btnPayNow.setVisibility(View.GONE);
                                                        btnPaymentDoneBtn.setVisibility(View.GONE);
                                                        walletDetectionLayout.setVisibility(View.GONE);
                                                        discountDetectionLayout.setVisibility(View.VISIBLE);
                                                        flowValue = 5;
                                                        layoutChanges();
                                                        imgPaymentTypeInvoice.setImageResource(R.drawable.money_icon);
                                                        lblPaymentTypeInvoice.setText("CASH");
                                                    } else if (isPaid.equalsIgnoreCase("1") && paymentMode.equalsIgnoreCase("CASH")
                                                            && walletAmountDetected == 0 && couponAmountDetected > 0 &&
                                                            totalRideAmount == 0) {
                                                        btnPayNow.setVisibility(View.GONE);
                                                        btnPaymentDoneBtn.setVisibility(View.VISIBLE);
                                                        walletDetectionLayout.setVisibility(View.GONE);
                                                        discountDetectionLayout.setVisibility(View.VISIBLE);
                                                        flowValue = 5;
                                                        layoutChanges();
                                                        imgPaymentTypeInvoice.setImageResource(R.drawable.money_icon);
                                                        lblPaymentTypeInvoice.setText("CASH");
                                                    } else if (isPaid.equalsIgnoreCase("0") && paymentMode.equalsIgnoreCase("CASH")
                                                            && walletAmountDetected == 0 && couponAmountDetected == 0 &&
                                                            totalRideAmount > 0) {
                                                        btnPayNow.setVisibility(View.GONE);
                                                        btnPaymentDoneBtn.setVisibility(View.GONE);
                                                        flowValue = 5;
                                                        layoutChanges();
                                                        imgPaymentTypeInvoice.setImageResource(R.drawable.money_icon);
                                                        lblPaymentTypeInvoice.setText("CASH");
                                                    } else if (isPaid.equalsIgnoreCase("1") && paymentMode.equalsIgnoreCase("CARD")
                                                            && walletAmountDetected > 0 && couponAmountDetected > 0 && totalRideAmount == 0) {
                                                        btnPayNow.setVisibility(View.GONE);
                                                        btnPaymentDoneBtn.setVisibility(View.VISIBLE);
                                                        walletDetectionLayout.setVisibility(View.VISIBLE);
                                                        discountDetectionLayout.setVisibility(View.VISIBLE);
                                                        flowValue = 5;
                                                        layoutChanges();
                                                        imgPaymentTypeInvoice.setImageResource(R.drawable.visa);
                                                        lblPaymentTypeInvoice.setText("CARD");
                                                    } else if (isPaid.equalsIgnoreCase("0") && paymentMode.equalsIgnoreCase("CARD")
                                                            && walletAmountDetected > 0 && couponAmountDetected > 0 && totalRideAmount > 0) {
                                                        btnPayNow.setVisibility(View.VISIBLE);
                                                        btnPaymentDoneBtn.setVisibility(View.GONE);
                                                        walletDetectionLayout.setVisibility(View.VISIBLE);
                                                        discountDetectionLayout.setVisibility(View.VISIBLE);
                                                        flowValue = 5;
                                                        layoutChanges();
                                                        imgPaymentTypeInvoice.setImageResource(R.drawable.visa);
                                                        lblPaymentTypeInvoice.setText("CARD");
                                                    } else if (isPaid.equalsIgnoreCase("0") && paymentMode.equalsIgnoreCase("CARD")
                                                            && walletAmountDetected > 0 && couponAmountDetected == 0 && totalRideAmount > 0) {
                                                        btnPayNow.setVisibility(View.VISIBLE);
                                                        btnPaymentDoneBtn.setVisibility(View.GONE);
                                                        walletDetectionLayout.setVisibility(View.VISIBLE);
                                                        discountDetectionLayout.setVisibility(View.GONE);
                                                        flowValue = 5;
                                                        layoutChanges();
                                                        imgPaymentTypeInvoice.setImageResource(R.drawable.visa);
                                                        lblPaymentTypeInvoice.setText("CARD");
                                                    } else if (isPaid.equalsIgnoreCase("1") && paymentMode.equalsIgnoreCase("CARD") && walletAmountDetected > 0 && couponAmountDetected == 0 && totalRideAmount == 0) {
                                                        btnPayNow.setVisibility(View.GONE);
                                                        btnPaymentDoneBtn.setVisibility(View.VISIBLE);
                                                        walletDetectionLayout.setVisibility(View.VISIBLE);
                                                        discountDetectionLayout.setVisibility(View.GONE);
                                                        flowValue = 5;
                                                        layoutChanges();
                                                        imgPaymentTypeInvoice.setImageResource(R.drawable.visa);
                                                        lblPaymentTypeInvoice.setText("CARD");
                                                    } else if (isPaid.equalsIgnoreCase("0") && paymentMode.equalsIgnoreCase("CARD") && walletAmountDetected == 0 && couponAmountDetected > 0 && totalRideAmount > 0) {
                                                        btnPayNow.setVisibility(View.VISIBLE);
                                                        btnPaymentDoneBtn.setVisibility(View.GONE);
                                                        walletDetectionLayout.setVisibility(View.GONE);
                                                        discountDetectionLayout.setVisibility(View.VISIBLE);
                                                        flowValue = 5;
                                                        layoutChanges();
                                                        imgPaymentTypeInvoice.setImageResource(R.drawable.visa);
                                                        lblPaymentTypeInvoice.setText("CARD");
                                                    } else if (isPaid.equalsIgnoreCase("1") && paymentMode.equalsIgnoreCase("CARD") && walletAmountDetected == 0 && couponAmountDetected > 0 && totalRideAmount == 0) {
                                                        btnPayNow.setVisibility(View.GONE);
                                                        btnPaymentDoneBtn.setVisibility(View.VISIBLE);
                                                        walletDetectionLayout.setVisibility(View.GONE);
                                                        discountDetectionLayout.setVisibility(View.VISIBLE);
                                                        flowValue = 5;
                                                        layoutChanges();
                                                        imgPaymentTypeInvoice.setImageResource(R.drawable.visa);
                                                        lblPaymentTypeInvoice.setText("CARD");
                                                    } else if (isPaid.equalsIgnoreCase("0") && paymentMode.equalsIgnoreCase("CARD")
                                                            && walletAmountDetected == 0 && couponAmountDetected == 0 && totalRideAmount > 0) {
                                                        btnPayNow.setVisibility(View.VISIBLE);
                                                        btnPaymentDoneBtn.setVisibility(View.GONE);
                                                        flowValue = 5;
                                                        layoutChanges();
                                                        imgPaymentTypeInvoice.setImageResource(R.drawable.visa);
                                                        lblPaymentTypeInvoice.setText("CARD");
                                                    } else if (isPaid.equalsIgnoreCase("0") && paymentMode.equalsIgnoreCase("CARD")
                                                            && walletAmountDetected == 0 && couponAmountDetected > 0 && totalRideAmount == 0) {
                                                        btnPayNow.setVisibility(View.GONE);
                                                        btnPaymentDoneBtn.setVisibility(View.VISIBLE);
                                                        walletDetectionLayout.setVisibility(View.GONE);
                                                        discountDetectionLayout.setVisibility(View.VISIBLE);
                                                        flowValue = 5;
                                                        layoutChanges();
                                                        imgPaymentTypeInvoice.setImageResource(R.drawable.visa);
                                                        lblPaymentTypeInvoice.setText("CARD");
                                                    } else if (isPaid.equalsIgnoreCase("0") && paymentMode.equalsIgnoreCase("CASH") && walletAmountDetected == 0 && couponAmountDetected > 0 && totalRideAmount == 0) {
                                                        btnPayNow.setVisibility(View.GONE);
                                                        btnPaymentDoneBtn.setVisibility(View.VISIBLE);
                                                        walletDetectionLayout.setVisibility(View.GONE);
                                                        discountDetectionLayout.setVisibility(View.VISIBLE);
                                                        flowValue = 5;
                                                        layoutChanges();
                                                        imgPaymentTypeInvoice.setImageResource(R.drawable.visa);
                                                        lblPaymentTypeInvoice.setText("CASH");
                                                    }  else if (isPaid.equalsIgnoreCase("1") && paymentMode.equalsIgnoreCase("CASH")
                                                            && walletAmountDetected > 0 && couponAmountDetected == 0 && totalRideAmount > 0) {
                                                        btnPayNow.setVisibility(View.GONE);
                                                        flowValue = 6;
                                                        layoutChanges();
                                                    } else if (isPaid.equalsIgnoreCase("1") && paymentMode.equalsIgnoreCase("CASH")
                                                            && walletAmountDetected == 0 && couponAmountDetected > 0 && totalRideAmount > 0) {
                                                        btnPayNow.setVisibility(View.GONE);
                                                        flowValue = 6;
                                                        layoutChanges();
                                                    } else if (isPaid.equalsIgnoreCase("1") && paymentMode.equalsIgnoreCase("CARD")
                                                            && walletAmountDetected == 0 && couponAmountDetected > 0 && totalRideAmount > 0) {
                                                        btnPayNow.setVisibility(View.GONE);
                                                        flowValue = 6;
                                                        layoutChanges();
                                                    } else if (isPaid.equalsIgnoreCase("1") && paymentMode.equalsIgnoreCase("CASH")
                                                            && walletAmountDetected > 0 && couponAmountDetected > 0 && totalRideAmount > 0) {
                                                        btnPayNow.setVisibility(View.GONE);
                                                        flowValue = 6;
                                                        layoutChanges();
                                                    } else if (isPaid.equalsIgnoreCase("1") && paymentMode.equalsIgnoreCase("CARD")
                                                            && walletAmountDetected > 0 && couponAmountDetected > 0 && totalRideAmount > 0) {
                                                        btnPayNow.setVisibility(View.GONE);
                                                        flowValue = 6;
                                                        layoutChanges();
                                                    }
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                            break;

                                            //endregion

                                            // region schedule
//                                        case "SCHEDULED":
//                                            try
//                                            {
//                                                utils.displayMessage(getView(), context, "Your Ride is successfully scheduled");
//                                                flowValue=0;
//                                                layoutChanges();
//                                            }
//                                            catch (Exception e)
//                                            {
//
//                                            }
//                                            break;
//                                          //  endregion
//

                                            //region complete
                                        case "COMPLETED":
                                            strTag = "";
                                            try {
                                                if (requestStatusCheckObject.optJSONObject("payment") != null)
                                                {
                                                    JSONObject payment = requestStatusCheckObject.optJSONObject("payment");
                                                    JSONObject provider = requestStatusCheckObject.optJSONObject("provider");
                                                    isPaid = requestStatusCheckObject.optString("paid");
                                                    paymentMode = requestStatusCheckObject.optString("payment_mode");
                                                    imgSos.setVisibility(View.GONE);
                                                    totalRideAmount = payment.optInt("payable");
                                                    walletAmountDetected = payment.optInt("wallet");
                                                    couponAmountDetected = payment.optInt("discount");

                                                    lblBasePrice.setText(SharedHelper.getKey(context, "currency") + ""
                                                            + payment.optString("fixed"));
                                                    lblTaxPrice.setText(SharedHelper.getKey(context, "currency") + ""
                                                            + payment.optString("tax"));
                                                    lblDistancePrice.setText(SharedHelper.getKey(context, "currency") + ""
                                                            + payment.optString("distance"));
                                                    lblTotalPrice.setText(SharedHelper.getKey(context, "currency") + ""
                                                            + payment.optString("payable"));
                                                    lblDiscountPrice.setText(SharedHelper.getKey(context, "currency") + "" + couponAmountDetected);

                                                    lblTimeTaken.setText(requestStatusCheckObject.optString("travel_time"));

                                                    lblWalletPrice.setText(SharedHelper.getKey(context, "currency") + "" + walletAmountDetected);

                                                    //Review values set
                                                    lblProviderNameRate.setText(context.getResources().getString(R.string.rate_provider) + " " + provider.optString("first_name") + " " + provider.optString("last_name"));
                                                    if (provider.optString("avatar").startsWith("http")) {
                                                        Picasso.get().load(provider.optString("avatar")).placeholder(R.drawable.loading).error(R.drawable.ic_dummy_user).into(imgProviderRate);
                                                    } else {
                                                        Picasso.get().load(URLHelper.base + "storage/" + provider.optString("avatar")).placeholder(R.drawable.loading).error(R.drawable.ic_dummy_user).into(imgProviderRate);
                                                    }

                                                    if (requestStatusCheckObject.optString("booking_id") != null &&
                                                            !requestStatusCheckObject.optString("booking_id").equalsIgnoreCase("")) {
                                                        booking_id.setText(requestStatusCheckObject.optString("booking_id"));
                                                    } else {
                                                        bookingIDLayout.setVisibility(View.GONE);
                                                        booking_id.setVisibility(View.GONE);
                                                    }

                                                    if (isPaid.equalsIgnoreCase("1") && paymentMode.equalsIgnoreCase("CASH")
                                                            && walletAmountDetected > 0 && couponAmountDetected > 0
                                                            && totalRideAmount == 0) {
                                                        btnPayNow.setVisibility(View.GONE);
                                                        btnPaymentDoneBtn.setVisibility(View.VISIBLE);
                                                        walletDetectionLayout.setVisibility(View.VISIBLE);
                                                        discountDetectionLayout.setVisibility(View.VISIBLE);
                                                        flowValue = 5;
                                                        layoutChanges();
                                                        imgPaymentTypeInvoice.setImageResource(R.drawable.money_icon);
                                                        lblPaymentTypeInvoice.setText("CASH");
                                                    } else if (isPaid.equalsIgnoreCase("0") && paymentMode.equalsIgnoreCase("CASH")
                                                            && walletAmountDetected > 0 && couponAmountDetected > 0 && totalRideAmount > 0) {
                                                        btnPayNow.setVisibility(View.GONE);
                                                        btnPaymentDoneBtn.setVisibility(View.GONE);
                                                        walletDetectionLayout.setVisibility(View.VISIBLE);
                                                        discountDetectionLayout.setVisibility(View.VISIBLE);
                                                        flowValue = 5;
                                                        layoutChanges();
                                                        imgPaymentTypeInvoice.setImageResource(R.drawable.money_icon);
                                                        lblPaymentTypeInvoice.setText("CASH");
                                                    } else if (isPaid.equalsIgnoreCase("0") && paymentMode.equalsIgnoreCase("CASH")
                                                            && walletAmountDetected > 0 && couponAmountDetected == 0 && totalRideAmount > 0) {
                                                        btnPayNow.setVisibility(View.GONE);
                                                        btnPaymentDoneBtn.setVisibility(View.GONE);
                                                        walletDetectionLayout.setVisibility(View.VISIBLE);
                                                        discountDetectionLayout.setVisibility(View.GONE);
                                                        flowValue = 5;
                                                        layoutChanges();
                                                        imgPaymentTypeInvoice.setImageResource(R.drawable.money_icon);
                                                        lblPaymentTypeInvoice.setText("CASH");
                                                    } else if (isPaid.equalsIgnoreCase("1") && paymentMode.equalsIgnoreCase("CASH")
                                                            && walletAmountDetected > 0 && couponAmountDetected == 0 && totalRideAmount == 0) {
                                                        btnPayNow.setVisibility(View.GONE);
                                                        btnPaymentDoneBtn.setVisibility(View.VISIBLE);
                                                        walletDetectionLayout.setVisibility(View.VISIBLE);
                                                        discountDetectionLayout.setVisibility(View.GONE);
                                                        flowValue = 5;
                                                        layoutChanges();
                                                        imgPaymentTypeInvoice.setImageResource(R.drawable.money_icon);
                                                        lblPaymentTypeInvoice.setText("CASH");
                                                    } else if (isPaid.equalsIgnoreCase("0") && paymentMode.equalsIgnoreCase("CASH")
                                                            && walletAmountDetected == 0 && couponAmountDetected > 0 && totalRideAmount > 0) {
                                                        btnPayNow.setVisibility(View.GONE);
                                                        btnPaymentDoneBtn.setVisibility(View.GONE);
                                                        walletDetectionLayout.setVisibility(View.GONE);
                                                        discountDetectionLayout.setVisibility(View.VISIBLE);
                                                        flowValue = 5;
                                                        layoutChanges();
                                                        imgPaymentTypeInvoice.setImageResource(R.drawable.money_icon);
                                                        lblPaymentTypeInvoice.setText("CASH");
                                                    } else if (isPaid.equalsIgnoreCase("1") && paymentMode.equalsIgnoreCase("CASH")
                                                            && walletAmountDetected == 0 && couponAmountDetected > 0 && totalRideAmount == 0) {
                                                        btnPayNow.setVisibility(View.GONE);
                                                        btnPaymentDoneBtn.setVisibility(View.VISIBLE);
                                                        walletDetectionLayout.setVisibility(View.GONE);
                                                        discountDetectionLayout.setVisibility(View.VISIBLE);
                                                        flowValue = 5;
                                                        layoutChanges();
                                                        imgPaymentTypeInvoice.setImageResource(R.drawable.money_icon);
                                                        lblPaymentTypeInvoice.setText("CASH");
                                                    } else if (isPaid.equalsIgnoreCase("0") && paymentMode.equalsIgnoreCase("CASH")
                                                            && walletAmountDetected == 0 && couponAmountDetected == 0 && totalRideAmount > 0) {
                                                        btnPayNow.setVisibility(View.GONE);
                                                        btnPaymentDoneBtn.setVisibility(View.GONE);
                                                        walletDetectionLayout.setVisibility(View.GONE);
                                                        discountDetectionLayout.setVisibility(View.GONE);
                                                        flowValue = 5;
                                                        layoutChanges();
                                                        imgPaymentTypeInvoice.setImageResource(R.drawable.money_icon);
                                                        lblPaymentTypeInvoice.setText("CASH");
                                                    } else if (isPaid.equalsIgnoreCase("1") && paymentMode.equalsIgnoreCase("CASH")
                                                            && walletAmountDetected == 0 && couponAmountDetected == 0 && totalRideAmount > 0) {
                                                        btnPayNow.setVisibility(View.GONE);
                                                        flowValue = 6;
                                                        layoutChanges();
                                                    } else if (isPaid.equalsIgnoreCase("1") && paymentMode.equalsIgnoreCase("CARD")
                                                            && walletAmountDetected > 0 && couponAmountDetected > 0 && totalRideAmount == 0) {
                                                        btnPayNow.setVisibility(View.GONE);
                                                        btnPaymentDoneBtn.setVisibility(View.VISIBLE);
                                                        walletDetectionLayout.setVisibility(View.VISIBLE);
                                                        discountDetectionLayout.setVisibility(View.VISIBLE);
                                                        flowValue = 5;
                                                        layoutChanges();
                                                        imgPaymentTypeInvoice.setImageResource(R.drawable.visa);
                                                        lblPaymentTypeInvoice.setText("CARD");
                                                    } else if (isPaid.equalsIgnoreCase("0") && paymentMode.equalsIgnoreCase("CARD")
                                                            && walletAmountDetected > 0 && couponAmountDetected > 0 && totalRideAmount > 0) {
                                                        btnPayNow.setVisibility(View.VISIBLE);
                                                        btnPaymentDoneBtn.setVisibility(View.GONE);
                                                        walletDetectionLayout.setVisibility(View.VISIBLE);
                                                        discountDetectionLayout.setVisibility(View.VISIBLE);
                                                        flowValue = 5;
                                                        layoutChanges();
                                                        imgPaymentTypeInvoice.setImageResource(R.drawable.visa);
                                                        lblPaymentTypeInvoice.setText("CARD");
                                                    } else if (isPaid.equalsIgnoreCase("0") && paymentMode.equalsIgnoreCase("CARD")
                                                            && walletAmountDetected > 0 && couponAmountDetected == 0 && totalRideAmount > 0) {
                                                        btnPayNow.setVisibility(View.VISIBLE);
                                                        btnPaymentDoneBtn.setVisibility(View.GONE);
                                                        walletDetectionLayout.setVisibility(View.VISIBLE);
                                                        discountDetectionLayout.setVisibility(View.GONE);
                                                        flowValue = 5;
                                                        layoutChanges();
                                                        imgPaymentTypeInvoice.setImageResource(R.drawable.visa);
                                                        lblPaymentTypeInvoice.setText("CARD");
                                                    } else if (isPaid.equalsIgnoreCase("1") && paymentMode.equalsIgnoreCase("CARD")
                                                            && walletAmountDetected > 0 && couponAmountDetected == 0 && totalRideAmount == 0) {
                                                        btnPayNow.setVisibility(View.GONE);
                                                        btnPaymentDoneBtn.setVisibility(View.VISIBLE);
                                                        walletDetectionLayout.setVisibility(View.VISIBLE);
                                                        discountDetectionLayout.setVisibility(View.GONE);
                                                        flowValue = 5;
                                                        layoutChanges();
                                                        imgPaymentTypeInvoice.setImageResource(R.drawable.visa);
                                                        lblPaymentTypeInvoice.setText("CARD");
                                                    } else if (isPaid.equalsIgnoreCase("0") && paymentMode.equalsIgnoreCase("CARD")
                                                            && walletAmountDetected == 0 && couponAmountDetected > 0 && totalRideAmount > 0) {
                                                        btnPayNow.setVisibility(View.VISIBLE);
                                                        btnPaymentDoneBtn.setVisibility(View.GONE);
                                                        walletDetectionLayout.setVisibility(View.GONE);
                                                        discountDetectionLayout.setVisibility(View.VISIBLE);
                                                        flowValue = 5;
                                                        layoutChanges();
                                                        imgPaymentTypeInvoice.setImageResource(R.drawable.visa);
                                                        lblPaymentTypeInvoice.setText("CARD");
                                                    } else if (isPaid.equalsIgnoreCase("1") && paymentMode.equalsIgnoreCase("CARD")
                                                            && walletAmountDetected == 0 && couponAmountDetected > 0 && totalRideAmount == 0) {
                                                        btnPayNow.setVisibility(View.GONE);
                                                        btnPaymentDoneBtn.setVisibility(View.VISIBLE);
                                                        walletDetectionLayout.setVisibility(View.GONE);
                                                        discountDetectionLayout.setVisibility(View.VISIBLE);
                                                        flowValue = 5;
                                                        layoutChanges();
                                                        imgPaymentTypeInvoice.setImageResource(R.drawable.visa);
                                                        lblPaymentTypeInvoice.setText("CARD");
                                                    } else if (isPaid.equalsIgnoreCase("0") && paymentMode.equalsIgnoreCase("CARD") && walletAmountDetected == 0 && couponAmountDetected == 0 && totalRideAmount > 0) {
                                                        btnPayNow.setVisibility(View.VISIBLE);
                                                        btnPaymentDoneBtn.setVisibility(View.GONE);
                                                        walletDetectionLayout.setVisibility(View.GONE);
                                                        discountDetectionLayout.setVisibility(View.GONE);
                                                        flowValue = 5;
                                                        layoutChanges();
                                                        imgPaymentTypeInvoice.setImageResource(R.drawable.visa);
                                                        lblPaymentTypeInvoice.setText("CARD");
                                                    } else if (isPaid.equalsIgnoreCase("1") && paymentMode.equalsIgnoreCase("CARD")
                                                            && walletAmountDetected == 0 && couponAmountDetected == 0 && totalRideAmount > 0) {
                                                        btnPayNow.setVisibility(View.GONE);
                                                        flowValue = 6;
                                                        layoutChanges();
                                                    } else if (isPaid.equalsIgnoreCase("0") && paymentMode.equalsIgnoreCase("CARD")
                                                            && walletAmountDetected == 0 && couponAmountDetected > 0 && totalRideAmount == 0) {
                                                        btnPayNow.setVisibility(View.GONE);
                                                        btnPaymentDoneBtn.setVisibility(View.VISIBLE);
                                                        walletDetectionLayout.setVisibility(View.GONE);
                                                        discountDetectionLayout.setVisibility(View.VISIBLE);
                                                        flowValue = 5;
                                                        layoutChanges();
                                                        imgPaymentTypeInvoice.setImageResource(R.drawable.visa);
                                                        lblPaymentTypeInvoice.setText("CARD");
                                                    } else if (isPaid.equalsIgnoreCase("0") && paymentMode.equalsIgnoreCase("CASH")
                                                            && walletAmountDetected == 0 && couponAmountDetected > 0 && totalRideAmount == 0) {
                                                        btnPayNow.setVisibility(View.GONE);
                                                        btnPaymentDoneBtn.setVisibility(View.VISIBLE);
                                                        walletDetectionLayout.setVisibility(View.GONE);
                                                        discountDetectionLayout.setVisibility(View.VISIBLE);
                                                        flowValue = 5;
                                                        layoutChanges();
                                                        imgPaymentTypeInvoice.setImageResource(R.drawable.visa);
                                                        lblPaymentTypeInvoice.setText("CASH");
                                                    } else if (isPaid.equalsIgnoreCase("1") && paymentMode.equalsIgnoreCase("CASH")
                                                            && walletAmountDetected > 0 && couponAmountDetected == 0 && totalRideAmount > 0) {
                                                        btnPayNow.setVisibility(View.GONE);
                                                        flowValue = 6;
                                                        layoutChanges();
                                                    } else if (isPaid.equalsIgnoreCase("1") && paymentMode.equalsIgnoreCase("CASH")
                                                            && walletAmountDetected > 0 && couponAmountDetected == 0 && totalRideAmount > 0) {
                                                        btnPayNow.setVisibility(View.GONE);
                                                        flowValue = 6;
                                                        layoutChanges();
                                                    } else if (isPaid.equalsIgnoreCase("1") && paymentMode.equalsIgnoreCase("CASH")
                                                            && walletAmountDetected == 0 && couponAmountDetected > 0 && totalRideAmount > 0) {
                                                        btnPayNow.setVisibility(View.GONE);
                                                        flowValue = 6;
                                                        layoutChanges();
                                                    } else if (isPaid.equalsIgnoreCase("1") && paymentMode.equalsIgnoreCase("CARD")
                                                            && walletAmountDetected == 0 && couponAmountDetected > 0 && totalRideAmount > 0) {
                                                        btnPayNow.setVisibility(View.GONE);
                                                        flowValue = 6;
                                                        layoutChanges();
                                                    } else if (isPaid.equalsIgnoreCase("1") && paymentMode.equalsIgnoreCase("CASH")
                                                            && walletAmountDetected > 0 && couponAmountDetected > 0 && totalRideAmount > 0) {
                                                        btnPayNow.setVisibility(View.GONE);
                                                        flowValue = 6;
                                                        layoutChanges();
                                                    }  else if (isPaid.equalsIgnoreCase("1") && paymentMode.equalsIgnoreCase("CARD")
                                                            && walletAmountDetected > 0 && couponAmountDetected > 0 && totalRideAmount > 0) {
                                                        btnPayNow.setVisibility(View.GONE);
                                                        flowValue = 6;
                                                        layoutChanges();

                                                    } else if (isPaid.equalsIgnoreCase("1") && paymentMode.equalsIgnoreCase("CARD")
                                                            && walletAmountDetected > 0 && couponAmountDetected == 0 && totalRideAmount > 0) {
                                                        btnPayNow.setVisibility(View.GONE);
                                                        flowValue = 6;
                                                        layoutChanges();
                                                    }

                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                      //      flowValue=0;
                                      //      layoutChanges();
                                            break;
                                       //     endregion
                                    }
                                }
                                if ("ACCEPTED".equals(status) || "STARTED".equals(status) ||
                                        "ARRIVED".equals(status) || "PICKEDUP".equals(status) || "DROPPED".equals(status))
                                {
                                    utils.print("Livenavigation", "" + status);
                                    utils.print("Destination Current Lat", "" + requestStatusCheckObject.getJSONObject("provider").optString("latitude"));
                                    utils.print("Destination Current Lng", "" + requestStatusCheckObject.getJSONObject("provider").optString("longitude"));
                                    liveNavigation(status, requestStatusCheckObject.getJSONObject("provider").optString("latitude"),
                                            requestStatusCheckObject.getJSONObject("provider").optString("longitude"));
                                }
                            } catch (Exception e)
                            {
                                e.printStackTrace();
                                utils.displayMessage(getView(), context, context.getResources().getString(R.string.something_went_wrong));
                            }
                        }
                        else if (PreviousStatus.equalsIgnoreCase("SEARCHING"))
                        {
                            SharedHelper.putKey(context, "current_status", "");
                            if (scheduledDate != null && scheduledTime != null && !scheduledDate.equalsIgnoreCase("")
                                    && !scheduledTime.equalsIgnoreCase(""))
                            {

                            }
                            else
                            {
                                Toast.makeText(context, context.getResources().getString(R.string.no_drivers_found), Toast.LENGTH_SHORT).show();
                            }

                            strTag = "";
                            PreviousStatus = "";
                         //   flowValue = 0;
                            try
                            {
                                utils.displayMessage(getView(), context, "Your request is successfully scheduled");
                                flowValue=0;
                                layoutChanges();
                            }
                            catch (Exception e)
                            {

                            }
                           // layoutChanges();

                            if (reasonDialog != null)
                            {
                                if (reasonDialog.isShowing())
                                {
                                    reasonDialog.dismiss();
                                }
                            }
                            if (cancelRideDialog != null)
                            {
                                if (cancelRideDialog.isShowing())
                                {
                                    cancelRideDialog.dismiss();
                                }
                            }

                            CurrentStatus = "";
                            mMap.clear();
                            mapClear();
                        }
                        else if (PreviousStatus.equalsIgnoreCase("STARTED"))
                        {
                            SharedHelper.putKey(context, "current_status", "");
                            Toast.makeText(context, context.getResources().getString(R.string.driver_busy), Toast.LENGTH_SHORT).show();
                            strTag = "";
                            PreviousStatus = "";
                            flowValue = 0;
                            layoutChanges();
                            if (reasonDialog != null){
                                if (reasonDialog.isShowing()){
                                    reasonDialog.dismiss();
                                }
                            }
                            if (cancelRideDialog != null){
                                if (cancelRideDialog.isShowing()){
                                    cancelRideDialog.dismiss();
                                }
                            }
                            CurrentStatus = "";
                            mMap.clear();
                            mapClear();
                        }
                        else if (PreviousStatus.equalsIgnoreCase("ARRIVED")) {
                            SharedHelper.putKey(context, "current_status", "");
                            Toast.makeText(context, context.getResources().getString(R.string.driver_busy), Toast.LENGTH_SHORT).show();
                            strTag = "";
                            PreviousStatus = "";
                            flowValue = 0;
                            layoutChanges();
                            if (reasonDialog != null){
                                if (reasonDialog.isShowing()){
                                    reasonDialog.dismiss();
                                }
                            }
                            if (cancelRideDialog != null){
                                if (cancelRideDialog.isShowing()){
                                    cancelRideDialog.dismiss();
                                }
                            }
                            CurrentStatus = "";
                            mMap.clear();
                            mapClear();
                        }
                        else{
                            if (flowValue == 0){
                                getProvidersList("");
                 //               layoutChanges();

                            }else if(flowValue == 1){
         //                       getProvidersList(SharedHelper.getKey(context, "service_type"));
                            }
                            CurrentStatus = "";
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        utils.print("Error", error.toString());
                        try {
                            if (customDialog != null && customDialog.isShowing()) {
                                customDialog.dismiss();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        reqStatus = "";
                        SharedHelper.putKey(context, "req_status", "");
                    }
                }) {
                    @Override
                    public java.util.Map<String, String> getHeaders() throws AuthFailureError {
                        HashMap<String, String> headers = new HashMap<>();
                        headers.put("X-Requested-With", "XMLHttpRequest");
                        headers.put("Authorization", "" + SharedHelper.getKey(context, "token_type") + " " + SharedHelper.getKey(context, "access_token"));
                        return headers;
                    }
                };

                CustomRequestQueue.getInstance(context).addToRequestQueue(jsonObjectRequest);

            }
            else {
                utils.displayMessage(getView(), context, context.getResources().getString(R.string.oops_connect_your_internet));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void setCurrentAddress(){
        Utilities.getAddressUsingLatLng("source", frmSource, context, ""+current_lat, ""+current_lng);
    }

    void getProvidersList(final String strTag) {

        String providers_request = URLHelper.GET_PROVIDERS_LIST_API + "?" +
                "latitude=" + current_lat +
                "&longitude=" + current_lng +
                "&service=" + strTag;

        utils.print("Get all providers", "" + providers_request);
        utils.print("service_type", "" + SharedHelper.getKey(context, "service_type"));

        for (int i = 0; i < lstProviderMarkers.size(); i++) {
            lstProviderMarkers.get(i).remove();
        }

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(providers_request, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                utils.print("GetProvidersList", response.toString());

                LatLngBounds.Builder builder = new LatLngBounds.Builder();

                if (!strTag.equalsIgnoreCase(previousTag)){
                    previousTag = strTag;
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject jsonObj = response.getJSONObject(i);
                            if (mHashMap.containsKey(jsonObj.optString("id"))){
                                mHashMap.get(jsonObj.optString("id")).remove();
                                mHashMap.get(i).remove();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }

                for (int i = 0; i < response.length(); i++) {

                    try {
                        JSONObject jsonObj = response.getJSONObject(i);
                        utils.print("GetProvidersList", jsonObj.getString("latitude") + "," + jsonObj.getString("longitude"));
                        if (!jsonObj.getString("latitude").equalsIgnoreCase("") && !jsonObj.getString("longitude").equalsIgnoreCase("")) {

                            Double proLat = Double.parseDouble(jsonObj.getString("latitude"));
                            Double proLng = Double.parseDouble(jsonObj.getString("longitude"));

                            if (mHashMap.containsKey(jsonObj.optInt("id"))){
                                Marker marker = mHashMap.get(jsonObj.optInt("id"));
                                LatLng startPosition = marker.getPosition();
                                LatLng newPos = new LatLng(proLat, proLng);

                                marker.setPosition(newPos);
                                marker.setRotation(getBearing(startPosition, newPos));
                            }else{
                                MarkerOptions markerOptions = new MarkerOptions()
                                        .anchor(0.5f, 0.75f)
                                        .position(new LatLng(proLat, proLng))
                                        .rotation(0.0f)
                                        .snippet(jsonObj.getString("id"))
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.provider_location_icon));

                                // lstProviderMarkers.add(mMap.addMarker(markerOptions));

                                mHashMap.put(jsonObj.optInt("id"), mMap.addMarker(markerOptions));

                                builder.include(new LatLng(proLat, proLng));
//                            }
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String json = null;
                String Message;
                NetworkResponse response = error.networkResponse;
                if (response != null && response.data != null) {

                    try {
                        JSONObject errorObj = new JSONObject(new String(response.data));

                        if (response.statusCode == 400 || response.statusCode == 405 || response.statusCode == 500) {
                            try {
                                utils.showAlert(context, errorObj.optString("message"));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else if (response.statusCode == 401) {
                            refreshAccessToken("PROVIDERS_LIST");
                        } else if (response.statusCode == 422) {
                            json = trimMessage(new String(response.data));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("X-Requested-With", "XMLHttpRequest");
                headers.put("Authorization", "Bearer " + SharedHelper.getKey(context, "access_token"));
                return headers;
            }
        };
        CustomRequestQueue.getInstance(context).addToRequestQueue(jsonArrayRequest);
    }

    public float getBearing(LatLng oldPosition, LatLng newPosition) {
        double deltaLongitude = newPosition.longitude - oldPosition.longitude;
        double deltaLatitude = newPosition.latitude - oldPosition.latitude;
        double angle = (Math.PI * .5f) - Math.atan(deltaLatitude / deltaLongitude);

        if (deltaLongitude > 0) {
            return (float) angle;
        } else if (deltaLongitude < 0) {
            return (float) (angle + Math.PI);
        } else if (deltaLatitude < 0) {
            return (float) Math.PI;
        }

        return 0.0f;
    }


    public void liveNavigation(String status, String lat, String lng) {
        Log.e("Livenavigation", "ProLat" + lat + " ProLng" + lng);
        if (!lat.equalsIgnoreCase("") && !lng.equalsIgnoreCase("")) {
            Double proLat = Double.parseDouble(lat);
            Double proLng = Double.parseDouble(lng);

            Float rotation = 0.0f;

            MarkerOptions markerOptions = new MarkerOptions()
                    .position(new LatLng(proLat, proLng))
                    .rotation(rotation)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.provider_location_icon));

            if (providerMarker != null) {
                rotation = getBearing(providerMarker.getPosition(), markerOptions.getPosition());
                markerOptions.rotation(rotation * (180.0f / (float) Math.PI));
                providerMarker.remove();
            }

            providerMarker = mMap.addMarker(markerOptions);
        }

    }

    private void showSosPopUp() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        builder.setTitle(context.getResources().getString(R.string.app_name))
                .setIcon(R.mipmap.ic_launcher)
                .setMessage(context.getResources().getString(R.string.emaergeny_call))
                .setCancelable(false);
        builder.setPositiveButton(context.getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, 3);
                } else {
                    Intent intentCall = new Intent(Intent.ACTION_CALL);
                    intentCall.setData(Uri.parse("tel:" + SharedHelper.getKey(context, "sos")));
                    startActivity(intentCall);
                }
            }
        });
        builder.setNegativeButton(context.getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


}
