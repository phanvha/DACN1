package com.utt.potholes;

import android.Manifest;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.snackbar.Snackbar;

import android.os.CountDownTimer;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.Settings;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.navigation.ui.AppBarConfiguration;

import com.google.android.material.navigation.NavigationView;
import com.utt.API.API_clients;
import com.utt.API_Interface.Pothole_Interface;
import com.utt.API_Interface.User_Interface;
import com.utt.Adapter.DestinationPlaceAdapter;
import com.utt.Adapter.OriginPlaceAdapter;
import com.utt.Database.SQLite;
import com.utt.DerectionHelpers.TaskLoadedCallback;
import com.utt.model.DataPothole;
import com.utt.model.Pothole;
import com.utt.model.mUser;
import com.utt.modules.DirectionFinder;
import com.utt.modules.DirectionFinderListener;
import com.utt.modules.Route;
import com.utt.utils.CheckGoogleAccountStatus;
import com.utt.utils.DistanceBetweenTwoPoints;
import com.utt.utils.MapApiSource;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Menu;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements SensorEventListener, OriginPlaceAdapter.OnShareClickedListener, DestinationPlaceAdapter.OnShareClickedListener,TaskLoadedCallback, OnMapReadyCallback, DirectionFinderListener, NavigationView.OnNavigationItemSelectedListener, LocationListener {

    private static final int RC_SIGN_IN = 122;
    private AppBarConfiguration mAppBarConfiguration;

    private GoogleMap mMap;
    private MarkerOptions place1, place2;
    private Polyline currentPolyline;
    private List<MarkerOptions> markerOptionsList = new ArrayList<>();
    private  boolean defaultInfoWindow = true;
    ImageButton check;
    TextView tt1, t2;
    private ImageView btnFindPath;
    private EditText etOrigin;
    private EditText etDestination;
    private List<Marker> originMarkers = new ArrayList<>();
    private List<Marker> destinationMarkers = new ArrayList<>();
    private List<Marker> markerList = new ArrayList<>();
    Marker marker;
    private List<Polyline> polylinePaths = new ArrayList<>();
    private List<LatLng> latLngList = new ArrayList<>();
    private ProgressDialog progressDialog;
    DrawerLayout drawer;
    NavigationView navigationView;
    private ImageView imgMenu, layer, imgEye;
    GoogleApiClient mGoogleApiClient;
    LocationServices locationServices;
    LocationRequest mLocationRequest;
    private LocationManager locationManager;
    private LocationListener listener;
    private Double latitude, longitude;
    private Location mLastLocation;
    Marker mCurrLocationMarker;
    private int REQUEST_LOCATION = 100;
    private View locationButton;
    private ImageView imCam, imMaps;

    private List<Pothole> potholeList = new ArrayList<>();
    private List<DataPothole> dataPotholeList = new ArrayList<>();
    Pothole pothole;
    public GoogleSignInClient googleSignInClient;
    private TextView tvNav_Username, tvNav_Email, txtNameStart, txtNameEnd, txtLocationStart, txtLocationEnd;
    private ImageView image_Avatar, back_nested_layout;
    private ImageButton btnLogout, btnLogin, imgNotify;
    private ImageButton push_up, push_down;
    private String personName, personGivenName, personFamilyName, personEmail, personId, idToken, serverAuthCode;
    private List<LatLng> listpoint = new ArrayList<>();
    private static int RESULT_ORIGIN = 100;
    private static int RESULT_DESTINATION = 101;
    RelativeLayout layout1;

    BottomSheetBehavior behavior;
    View bottomSheet_listPoint;
    AlertDialog alertDialog;
    private String TAG = "MainActivity";
    private NotificationManagerCompat notificationManager;
    public static final String CHANEL_1_ID = "CHANEL1";
    public static final String CHANEL_2_ID = "CHANEL1";
    private String CHANEL_ID = "CHANEL1";

    private static final long MIN_TIME = 400;
    private static final float MIN_DISTANCE = 1000;
    private LinearLayout view_buble, layout_mylocation, layout_myLocal;
    private Spinner spinner;
    private OriginPlaceAdapter originPlaceAdapter;
    private DestinationPlaceAdapter destinationPlaceAdapter;
    private RecyclerView recyclerViewPlaceOrigin, recyclerViewPlaceDestination;

    Context context;
    private String address;
    private Handler handler;

    SharedPreferences pref;
    private String distance;
    private Double distancePoint;
    SQLite db;
    private Double minPoint = 0.0;
    private int k = 0;

    private static SensorManager sensorManager;
    private static Sensor sensor;

    private float currentX, currentY, currentZ, lastX, lastY, lastZ;
    private boolean isAccelerometerSensorAvailable, itIsNotFirstTime = false;
    private float xDif, yDif, zDif;
    private float shakeThreshold = 5f;
    private Vibrator vibrator;
    private boolean isStatusSensor = false;
    private Runnable runnableCheckIntance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initLayout();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(MainActivity.this, gso);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map2d);
        mapFragment.getMapAsync(this);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    Activity#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for Activity#requestPermissions for more details.
                return;
            }
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME, MIN_DISTANCE, this); //You can also use LocationManager.GPS_PROVIDER and LocationManager.PASSIVE_PROVIDER
        navigationView.setNavigationItemSelectedListener(this);
        //Places.initialize(getApplicationContext(), MapApiSource.GOOGLE_API_KEY, Locale.US);
        getlocation();
        startAnimationLogo(btnFindPath);
        startAnimationNotify();

        new readData().execute();
        checkAccount();

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);

        if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)!=null)
        {
            sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            isAccelerometerSensorAvailable = true;
        }else{
            //Toast.makeText(getApplicationContext(),"Accelerometer is not available!",Toast.LENGTH_SHORT).show();
            Log.e(TAG,"Accelerometer is not available!");
            isAccelerometerSensorAvailable = false;
        }

    }

    private void initLayout() {
        //init places
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), MapApiSource.GOOGLE_API_KEY, Locale.US);
        }
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        tvNav_Username = (TextView)headerView.findViewById(R.id.tvNav_Username);
        tvNav_Email = (TextView) headerView.findViewById(R.id.tvNav_Email);
        image_Avatar = (ImageView) headerView.findViewById(R.id.image_Avatar);
        txtNameStart = (TextView) findViewById(R.id.txtNameStart);
        txtNameEnd = (TextView) findViewById(R.id.txtNameEnd);
        txtLocationEnd = (TextView) findViewById(R.id.txtLocationEnd);
        txtLocationStart = (TextView) findViewById(R.id.txtLocationStart);
        layout_mylocation = findViewById(R.id.layout_mylocation);
        layout_mylocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (latitude!=null&&longitude!=null){
                    LatLng latLng = new LatLng(latitude, longitude);
                    String address = getAddressGeocodeAPI(latLng);
                    etOrigin.setText(address);
                }else{
                    Toast.makeText(getApplicationContext(), "Location not found", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnLogout = (ImageButton) headerView.findViewById(R.id.btnLogout);
        btnLogout.setVisibility(View.GONE);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signOutDialog();
            }
        });

        btnLogin = (ImageButton) headerView.findViewById(R.id.btnLogin);
        btnLogin.setVisibility(View.GONE);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();

            }
        });

        drawer = findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        btnFindPath = (ImageView) findViewById(R.id.btnFindPath);


        imgMenu = (ImageView) findViewById(R.id.btn_menu);
        layer = (ImageView) findViewById(R.id.layer);
//        imCam = (ImageView) findViewById(R.id.imCam);
        imMaps = (ImageView) findViewById(R.id.imMaps);
        btnFindPath.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendRequest();
            }
        });
        imgMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer.openDrawer(Gravity.LEFT);
            }
        });

        //origin
        etOrigin = (EditText) findViewById(R.id.etOrigin);
        etOrigin.setFocusable(true);
        Drawable x = getResources().getDrawable(R.drawable.ic_clear);
        etOrigin.setCompoundDrawables(null, null, x, null);
        recyclerViewPlaceOrigin = findViewById(R.id.recycler_view_place_origin);
        context = MainActivity.this;
        originPlaceAdapter = new OriginPlaceAdapter(getApplicationContext(),MainActivity.this);
        recyclerViewPlaceOrigin.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        recyclerViewPlaceOrigin.setAdapter(originPlaceAdapter);
        originPlaceAdapter.notifyDataSetChanged();
        etOrigin.addTextChangedListener(filterTextWatcherOrigin);
        etOrigin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //init place field list
                recyclerViewPlaceDestination.setVisibility(View.GONE);

            }
        });
        //destination
        etDestination = (EditText) findViewById(R.id.etDestination);
        etDestination.setFocusable(true);
        recyclerViewPlaceDestination = findViewById(R.id.recycler_view_place_destination);
        destinationPlaceAdapter = new DestinationPlaceAdapter(getApplicationContext(), MainActivity.this);
        recyclerViewPlaceDestination.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        recyclerViewPlaceDestination.setAdapter(destinationPlaceAdapter);
        destinationPlaceAdapter.notifyDataSetChanged();
        etDestination.addTextChangedListener(filterTextWatcherDestination);
        etDestination.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //init place field list
                layout_mylocation.setVisibility(View.GONE);
                recyclerViewPlaceOrigin.setVisibility(View.GONE);

            }
        });
        layout_myLocal = (LinearLayout) findViewById(R.id.layout_myLocal);
        imgEye = (ImageView) findViewById(R.id.imgEye);

//        imCam.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                db = SQLite.getInstance(MainActivity.this);
//                if (db.getTotalPotholeTB()!=0){
//                    Toast.makeText(getApplicationContext(), db.getTotalPotholeTB()+", "+db.getAllPothole().get(1).getEmail()+"", Toast.LENGTH_SHORT).show();
//                }
//
//            }
//        });

        bottomSheet_listPoint = findViewById(R.id.nested_bottomsheets);
        behavior = BottomSheetBehavior.from(bottomSheet_listPoint);
        behavior.setPeekHeight(530);
        bottomSheet_listPoint.setVisibility(View.GONE);
        layout1 =  findViewById(R.id.layer1);

        back_nested_layout = (ImageView) findViewById(R.id.back_nested_layout);
        back_nested_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheet_listPoint.setVisibility(View.GONE);
                layout1.setVisibility(View.VISIBLE);
                isStatusSensor = false;
            }
        });

        push_down = (ImageButton) findViewById(R.id.imbtn_push_down);
        push_down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                push_up.setVisibility(View.VISIBLE);
                push_down.setVisibility(View.GONE);
                txtNameStart.setMaxLines(1);
                txtNameEnd.setMaxLines(1);
            }
        });
        push_up = (ImageButton) findViewById(R.id.imbtn_push_up);
        push_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                push_up.setVisibility(View.GONE);
                push_down.setVisibility(View.VISIBLE);
                txtNameStart.setMaxLines(3);
                txtNameEnd.setMaxLines(3);

            }
        });
        imgNotify = (ImageButton) findViewById(R.id.imgNotify);
        imgNotify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                sendNotification("Bạn đang di chuyển gần tới khu vực có mặt đường xấu","Chạm để xem chi tiết");
                Log.d("notify", "check");
                Intent intent = new Intent(MainActivity.this, NotificationActivity.class);
                intent.putExtra("minDistance", minPoint);
                startActivity(intent);
            }
        });
    }

    private TextWatcher filterTextWatcherOrigin = new TextWatcher() {
        public void afterTextChanged(Editable s) {
            if (!s.toString().equals("")) {
                originPlaceAdapter.getFilter().filter(s.toString());
                if (recyclerViewPlaceOrigin.getVisibility() == View.GONE) {
                    layout_mylocation.setVisibility(View.VISIBLE);
                    recyclerViewPlaceOrigin.setVisibility(View.VISIBLE);}
            } else {
                if (recyclerViewPlaceOrigin.getVisibility() == View.VISIBLE) {

                    layout_mylocation.setVisibility(View.GONE);
                    recyclerViewPlaceOrigin.setVisibility(View.GONE);
                }
            }
        }
        public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
        public void onTextChanged(CharSequence s, int start, int before, int count) { }
    };

    private TextWatcher filterTextWatcherDestination = new TextWatcher() {
        public void afterTextChanged(Editable s) {
            if (!s.toString().equals("")) {
                destinationPlaceAdapter.getFilter().filter(s.toString());
                if (recyclerViewPlaceDestination.getVisibility() == View.GONE) {
                    recyclerViewPlaceDestination.setVisibility(View.VISIBLE);
                    recyclerViewPlaceOrigin.setVisibility(View.GONE);
                }
            } else {
                if (recyclerViewPlaceDestination.getVisibility() == View.VISIBLE) {recyclerViewPlaceDestination.setVisibility(View.GONE);}
            }
        }
        public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
        public void onTextChanged(CharSequence s, int start, int before, int count) { }
    };

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if ( v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent( event );
    }

    private String getAddressGeocodeAPI(LatLng latLng){
        try {
            Geocoder geocoder;
            List<Address> addresses;
            geocoder = new Geocoder(this, Locale.getDefault());

            addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

            address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            String country = addresses.get(0).getCountryName();
            String postalCode = addresses.get(0).getPostalCode();
            String knownName = addresses.get(0).getFeatureName();
        }catch (Exception e){
            Log.d(TAG, e.getMessage());
        }
        return address;
    }

    @Override
    public void ShareClicked(String address) {
        Log.d(TAG,""+address);

        etOrigin.setText(address+"");
        layout_mylocation.setVisibility(View.GONE);
        recyclerViewPlaceOrigin.setVisibility(View.GONE);
    }

    @Override
    public void ShareDesClicked(String address) {
        etDestination.setText(address+"");
        recyclerViewPlaceDestination.setVisibility(View.GONE);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        //Log.d("sensor checking", "x:"+sensorEvent.values[0]+", y: "+sensorEvent.values[1]+", z: "+sensorEvent.values[2]);
        currentX = sensorEvent.values[0];//m/s2
        currentY = sensorEvent.values[1];
        currentZ = sensorEvent.values[2];

        if (itIsNotFirstTime)
        {
            xDif = Math.abs(lastX - currentX);
            yDif = Math.abs(lastY - currentY);
            zDif = Math.abs(lastZ - currentZ);

            if ((xDif > shakeThreshold && yDif > shakeThreshold) ||
                    (xDif > shakeThreshold && zDif > shakeThreshold)||
                    (zDif > shakeThreshold && yDif > shakeThreshold))
            {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    //Toast.makeText(getApplicationContext(), "Bạn đang di chuyển trên mặt đường xấu phải không?", Toast.LENGTH_SHORT).show();

                    if (isStatusSensor == true){
                        //Toast.makeText(getApplicationContext(), "Bạn đang di chuyển trên mặt đường xấu phải không?", Toast.LENGTH_SHORT).show();
                        showDialogShaking();
                        vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
                    }else{
                        //Toast.makeText(getApplicationContext(), "Bạn chưa lựa chọn lộ trình", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "Bạn chưa lựa chọn lộ trình");
                    }
                }else{
                    vibrator.vibrate(500);
                    //
                }
            }

        }
        lastX = currentX;
        lastY = currentY;
        lastZ = currentZ;
        itIsNotFirstTime = true;

    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    private void showDialogShaking(){
//        if (list == null){
//            return;
//        }
        final Dialog dialog;
        dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.layout_dialog_shaking);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        Window window = dialog.getWindow();
        window.setGravity(Gravity.CENTER);
        window.getAttributes().windowAnimations = R.style.DialogAnimation;
        Button check = dialog.findViewById(R.id.btn_sendCheckLocation);
        Button btnclose = dialog.findViewById(R.id.btn_close_dialog_sensor);
        check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e(TAG, "Check Sensor dialog!");
            }
        });
        btnclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.setCancelable(true);
        window.setLayout(ActionBar.LayoutParams.WRAP_CONTENT,ActionBar.LayoutParams.WRAP_CONTENT);
        dialog.show();


    }


    class LogoutAccount extends AsyncTask<Void, Void, String> {
        // private ProgressDialog dialog;
        private ProgressDialog dialog = new ProgressDialog(MainActivity.this);

        @Override
        protected void onPreExecute() {
            // This progressbar will load util tast in doInBackground method loads
            dialog = new ProgressDialog(MainActivity.this);
            dialog.setMessage("Please wait ...");
            dialog.setCancelable(true);
            dialog.setTitle("Sign out");
            dialog.setIcon(R.drawable.ic_warning);
            dialog.setMax(100);
            dialog.show();

        }

        @Override
        protected String doInBackground(Void... params) {

            signOutAccountGoogle();
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            dialog.dismiss();
        }
    }

    private void sendNotification(String messageTitle,String messageBody) {
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        Uri soundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            @SuppressLint("WrongConstant")

            NotificationChannel notificationChannel=new NotificationChannel("my_notification","n_channel",NotificationManager.IMPORTANCE_MAX);
            notificationChannel.setDescription("description");
            notificationChannel.setName("Channel Name");
            assert notificationManager != null;
            notificationManager.deleteNotificationChannel("my_notification");

            notificationManager.createNotificationChannel(notificationChannel);
        }

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_notifications)
//                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.maps))
                .setContentTitle(messageTitle)
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(soundUri)
                .setContentIntent(pendingIntent)
                .setDefaults(Notification.DEFAULT_ALL)
                .setOnlyAlertOnce(true)
                .setChannelId("my_notification")
                .setColor(Color.parseColor("#3F5996"));

        //.setProgress(100,50,false);
        assert notificationManager != null;
        int m = (int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE);
        notificationManager.notify(m, notificationBuilder.build());

        SharedPreferences.Editor edit = getSharedPreferences("Settings", Context.MODE_PRIVATE).edit();
        edit.putString("notification", messageBody+"");
        edit.apply();
        edit.commit();

//        long futureInMillis = SystemClock.elapsedRealtime() + delay;
//        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
//        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMillis, pendingIntent);
    }

    private void signOutDialog(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage("Do you want to log out?")
                .setIcon(R.drawable.ic_check)
                .setCancelable(false)
                .setPositiveButton("Log out", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        new LogoutAccount().execute();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Log.d(TAG, "Cancel");
                    }
                });
        alertDialog = builder.create();
        alertDialog.show();
    }

    private void startAnimationLogo(final ImageView imageVie){
        final Handler handler = new Handler();
        final Runnable r = new Runnable() {
            public void run() {
                long longAnimationRotate = 1000;
                ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(imageVie, "rotation",0, 360);
                objectAnimator.setDuration(longAnimationRotate);

                AnimatorSet animator = new AnimatorSet();
                animator.playTogether(objectAnimator);
                animator.start();

                handler.postDelayed(this, 5000);
            }
        };
        handler.postDelayed(r, 5000);
    }

    private void startAnimationNotify(){
        final Handler handler = new Handler();

        final Runnable r = new Runnable() {
            public void run() {
                imgNotify.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shaking));
                handler.postDelayed(this, 6000);
            }
        };
        handler.postDelayed(r, 6000);
    }

    private void checkAccount(){
        if (CheckGoogleAccountStatus.getcheckDataAccount(this) == true){
            getProfileGGAccount();
        }else{
            btnLogout.setVisibility(View.GONE);
            btnLogin.setVisibility(View.VISIBLE);
            //Log.d("Login", "null");
        }
    }

    //gte google profile
    private void getProfileGGAccount(){
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
        if (acct != null) {
            personName = acct.getDisplayName();
            personGivenName = acct.getGivenName();
            personFamilyName = acct.getFamilyName();
            personEmail = acct.getEmail();
            personId = acct.getId();
            Uri personPhoto = acct.getPhotoUrl();
            idToken =acct.getIdToken();
            serverAuthCode = acct.getServerAuthCode();
//            Log.d("get_data_from_gg",
//                    "DisplayName: "+personName+
//                            "GivenName: "+ personGivenName+
//                            "FamilyName: "+personFamilyName+
//                            "Email: "+personEmail+
//                            "Id: "+personId+
//                            "PhotoUrl: "+personPhoto.toString()+
//                            "IdToken: "+idToken+
//                            "ServerAuthCode: "+ serverAuthCode);

            //set layout
            tvNav_Username.setText(personName);
            tvNav_Email.setText(personEmail);
            Glide.with(this).load(String.valueOf(personPhoto)).into(image_Avatar);
            btnLogout.setVisibility(View.VISIBLE);
            btnLogin.setVisibility(View.GONE);
            //saveAccount(personEmail);
        }
    }

    private void signOutAccountGoogle() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        // ...
                        //Toast.makeText(getApplicationContext(), "Đăng xuất thành công !", Toast.LENGTH_SHORT).show();
                        recreate();
//                        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
//                        startActivityForResult(intent, RC_MAIN);
//                        finish();
                        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                    }
                });

    }

    private void signIn() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

//    private String getURL(LatLng origin, LatLng destination, String directionMode){
//        String str_origin = "origin="+origin.latitude+","+origin.longitude;
//        String str_destination = "destination="+destination.latitude+","+destination.longitude;
//        String mode = "mode=="+directionMode;
//        String parameter = str_origin + "&"+ str_destination +"&"+mode;
//        String format = "json";
//        String url = "https://maps.googleapis.com/maps/api/directions/"+format+"?"
//                + parameter + "&key=AIzaSyCI-4RaDocwneRsw2ryTRPMf7NzGV-F1CE";
//        return url;
//
//    }

    @Override
    public void onLocationChanged(final Location location) {
        //new FetchURL(MainActivity.this).execute(getURL(place1.getPosition(), place2.getPosition(),"driving"),"driving");

        imgEye.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (imgEye.getVisibility()==View.VISIBLE){
                    Log.d(TAG, "check eye 1");
                    if (k == 0){
                        //layout_myLocal.setVisibility(View.GONE);
                        imgEye.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary),
                                PorterDuff.Mode.MULTIPLY);
                        k=1;
                    }else if (k==1){
                        imgEye.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.black),
                                PorterDuff.Mode.MULTIPLY);
                        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15.f);
                        mMap.animateCamera(cameraUpdate);
                        locationManager.removeUpdates(MainActivity.this);
                        k=0;
                    }

                }else {
                    Log.d(TAG, "check eye 2");
                }
            }
        });


    }



    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    @Override
    public void onTaskDone(Object... values) {
        if (currentPolyline != null){
            currentPolyline.remove();
        }
        currentPolyline = mMap.addPolyline((PolylineOptions) values[0]);
    }

    class readData extends AsyncTask<Void, Void, String> {
        // private ProgressDialog dialog;
        private ProgressDialog dialog = new ProgressDialog(MainActivity.this);

        @Override
        protected void onPreExecute() {
            // This progressbar will load util tast in doInBackground method loads
            dialog = new ProgressDialog(MainActivity.this);
            dialog.setMessage("Loading...");
            dialog.setCancelable(true);
            dialog.setTitle("Data");
            dialog.setIcon(R.drawable.ic_warning);
            dialog.setMax(100);
            dialog.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            //setMyLocation();
            //getAllUser();
            dataPotholeList = getAllPotholes();
            if (dataPotholeList!=null){
                Log.e("status", "Check My location successfully!"+dataPotholeList.size());

            }else{
                Log.e("status", "Check My location failure");

            }
//            checkAccount();
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            dialog.dismiss();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK){
            if (requestCode == RC_SIGN_IN) {
                Log.e("this", "Login with google account!");
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                handleSignInResult(task);
            }else if (requestCode == RESULT_ORIGIN){
                //init place
                Place place = Autocomplete.getPlaceFromIntent(data);
                etOrigin.setText(place.getAddress());
            }else if (requestCode == RESULT_DESTINATION){
                //init place
                Place place = Autocomplete.getPlaceFromIntent(data);
                etDestination.setText(place.getAddress());
            }
        }else if (resultCode == AutocompleteActivity.RESULT_ERROR){
            Status status = Autocomplete.getStatusFromIntent(data);

            Toast.makeText(getApplicationContext(),""+status.getStatusMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // Signed in successfully, show authenticated UI.
            Log.e("request","Đăng nhập thành công!");
            updateUI_AfterLoginGG(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("Error", "signInResult:failed code=" + e.getStatusCode());
        }
    }

    //update ui after login to GG
    private void updateUI_AfterLoginGG(GoogleSignInAccount account) {
        try {
            String strData = "" + account.getDisplayName();
            //Toast.makeText(getApplicationContext(),strData,Toast.LENGTH_LONG).show();
            Log.e("Name",strData);
            recreate();
            overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);

        } catch (Exception ex) { }
    }

    @Override
    protected void onStart() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        mGoogleApiClient.connect();
        super.onStart();

    }
        @Override
    protected void onResume() {
        super.onResume();
        if (isAccelerometerSensorAvailable){
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);


        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (isAccelerometerSensorAvailable){
            sensorManager.unregisterListener(this);
        }
    }

    private void getAllUser(){
        User_Interface service = API_clients.getClient().create(User_Interface.class);
        Call<mUser> userCall = service.getAllUser();
        userCall.enqueue(new Callback<mUser>() {
            @Override
            public void onResponse(Call<mUser> call, Response<mUser> response) {
                //onSignupSuccess();
                if (response.isSuccessful()) {
                    if (response.body()!=null){
                        Log.d("Data", ""+response.body().getMessage().toString());

                    }
                    Toast.makeText(getApplicationContext(), "thành công !", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(getApplicationContext(), "không thành công !", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<mUser> call, Throwable t) {
                Log.d("Failed: ", t.toString());
            }
        });
    }

    private List<DataPothole> getAllPotholes(){
        Pothole_Interface service = API_clients.getClient().create(Pothole_Interface.class);
        Call<Pothole> userCall = service.getAllPothole();
        userCall.enqueue(new Callback<Pothole>() {
            @Override
            public void onResponse(Call<Pothole> call, Response<Pothole> response) {
                //onSignupSuccess();
                if (response.isSuccessful()) {
                    if (response.body()!=null){
                        Log.e("Data", ""+response.body().getMessage().toString());
                        dataPotholeList = response.body().getDataPothole();
//                        for (int i = dataPotholeList.size() - 1; i>=0 ; --i){
//                            latLngList.add(new LatLng(
//                                    Double.valueOf(dataPotholeList.get(i).getLatitude()),
//                                    Double.valueOf(dataPotholeList.get(i).getLongitude())
//                            ));
//                        }
                        db = SQLite.getInstance(context);
                        if (dataPotholeList!=null && dataPotholeList.size()!=0){
                            if (db.getTotalPotholeTB()==0 ){

                                for (int i = dataPotholeList.size() - 1; i >= 0; --i) {
                                    db.insertDataPothole(new DataPothole(
                                            dataPotholeList.get(i).getId(),
                                            dataPotholeList.get(i).getName(),
                                            dataPotholeList.get(i).getEmail(),
                                            dataPotholeList.get(i).getLatitude(),
                                            dataPotholeList.get(i).getLongitude(),
                                            dataPotholeList.get(i).getImage(),
                                            dataPotholeList.get(i).getNote(),
                                            dataPotholeList.get(i).getVote(),
                                            dataPotholeList.get(i).getCreated_at(),
                                            dataPotholeList.get(i).getUpdated_at()
                                    ));
                                }
                                //Log.e("getTotalPotholeTB", db.getTotalPotholeTB() + "");
                            }else{
                                db.deletePothole();
                                for (int i = dataPotholeList.size() - 1; i >= 0; --i) {
                                    db.insertDataPothole(new DataPothole(
                                            dataPotholeList.get(i).getId(),
                                            dataPotholeList.get(i).getName(),
                                            dataPotholeList.get(i).getEmail(),
                                            dataPotholeList.get(i).getLatitude(),
                                            dataPotholeList.get(i).getLongitude(),
                                            dataPotholeList.get(i).getImage(),
                                            dataPotholeList.get(i).getNote(),
                                            dataPotholeList.get(i).getVote(),
                                            dataPotholeList.get(i).getCreated_at(),
                                            dataPotholeList.get(i).getUpdated_at()
                                    ));
                                }
                                Log.d("getTotalListRouteTB", "= null");
                            }
                        }

                        if (dataPotholeList.size()!=0){
                            addMarkerToMaps(dataPotholeList);
                        }else{
                            Log.e("data pothole list: ", ""+dataPotholeList.size());
                        }
//                        View view = findViewById(R.id.layer1);
//                        Snackbar.make(view, "Data updated! "+db.getTotalPotholeTB(), Snackbar.LENGTH_SHORT)
//                                .setAction("Action", null).show();

                    }
                    //Toast.makeText(getApplicationContext(), "Tải dữ liệu thành công !", Toast.LENGTH_SHORT).show();

                }else {
                    //Toast.makeText(getApplicationContext(), "không thành công !", Toast.LENGTH_SHORT).show();
                    Log.e("get data: ", "không thành công !");
                }

            }
            @Override
            public void onFailure(Call<Pothole> call, Throwable t) {
                Log.d("Failed: ", t.toString());
            }
        });
        if (dataPotholeList !=null){
            Log.d("dataPotholeList", ""+dataPotholeList.size());
            return dataPotholeList;
        }
        else{
            return null;
        }
    }

     private void checkPoint(){
         handler = new Handler();
         final Runnable r = new Runnable() {
             public void run() {
                 getAllPotholes();
                 handler.postDelayed(this, 10000);

             }
         };
         handler.postDelayed(r, 1000);
     }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        switch (item.getItemId()){
            case R.id.nav_home:
                Intent intent1 = new Intent();
                break;
            case R.id.nav_myCheck:
                Intent intent2 = new Intent(MainActivity.this, ProvidedActivity.class);
                intent2.putExtra("latitude", latitude);
                intent2.putExtra("longitude", longitude);
                intent2.putExtra("name", personName);
                intent2.putExtra("email", personEmail);
                startActivity(intent2);
                finish();
                break;
            case R.id.nav_account:
                Intent intent3 = new Intent(MainActivity.this, AccountActivity.class);
                intent3.putExtra("latitude", latitude);
                intent3.putExtra("longitude", longitude);
                startActivity(intent3);
                break;
            case R.id.nav_settings:
                Intent intent4 = new Intent(MainActivity.this, SettingsActivity.class);
                intent4.putExtra("latitude", latitude);
                intent4.putExtra("longitude", longitude);
                startActivity(intent4);
                break;
            case R.id.nav_introduce:
                Intent intent5 = new Intent(MainActivity.this, IntroduceActivity.class);
                intent5.putExtra("latitude", latitude);
                intent5.putExtra("longitude", longitude);
                startActivity(intent5);
                break;
        }
        return true;
    }

    private void sendRequest( ) {
        String origin = etOrigin.getText().toString();
        String destination = etDestination.getText().toString();
        if (origin.isEmpty()) {
            Toast.makeText(this, "Please enter origin address!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (destination.isEmpty()) {
            Toast.makeText(this, "Please enter destination address!", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            new DirectionFinder(this, origin, destination).execute();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void getlocation(){
        locationManager = (LocationManager) getSystemService(Service.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100, 2, this);
            if (locationManager != null) {
                mLastLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }
        }
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            if (mLastLocation != null) {
//                tvlatitude.setText(String.valueOf(location.getLatitude()));
//                tvlongitude.setText(String.valueOf(location.getLongitude()));
//                tvLatitude.setText("Vĩ độ: "+String.valueOf(location.getLatitude()));
//                tvLongitude.setText("Kinh độ: "+String.valueOf(location.getLongitude()));
                latitude = mLastLocation.getLatitude();
                longitude = mLastLocation.getLongitude();
                //Log.d("lat: ",latitude.toString());
                //Log.d("lon: ",longitude.toString());
                //                getAddressFromLocation(location, getApplicationContext(), new GeoCoderHandler());
            }
        } else {
            showGPSDisabledAlertToUser();
        }
    }

    private void showDialog(List<DataPothole> list, LatLng latLng){
        if (list == null){
            return;
        }
        final Dialog dialog;
        dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.layout_dialog);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        Window window = dialog.getWindow();
        window.setGravity(Gravity.CENTER);
        window.getAttributes().windowAnimations = R.style.DialogAnimation;
        ImageButton check = dialog.findViewById(R.id.btnCloseDialog);
        TextView sharers = dialog.findViewById(R.id.txtSharers);
        TextView noteOfShare = dialog.findViewById(R.id.txtNoteOfShare);
        TextView position = dialog.findViewById(R.id.txtLatlng);

        Log.e("Title", dataPotholeList.size()+", "+latLng.latitude+","+latLng.longitude);

        for (int i = dataPotholeList.size() -1; i>=0; --i){
            Double a= Double.valueOf(dataPotholeList.get(i).getLatitude());
            if (a == latLng.latitude){
                sharers.setText(dataPotholeList.get(i).getName());
                noteOfShare.setText(dataPotholeList.get(i).getNote());
                position.setText(dataPotholeList.get(i).getLatitude()+","+ dataPotholeList.get(i).getLongitude());
            }
        }
        check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.setCancelable(true);
        window.setLayout(ActionBar.LayoutParams.WRAP_CONTENT,ActionBar.LayoutParams.WRAP_CONTENT);
        dialog.show();


    }

    @SuppressLint("ResourceType")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter());
        configure_button();
        getlocation();
//        mMap.addMarker(place1);
//        mMap.addMarker(place2);
        checkOpen();
        checkPoint();
        db = SQLite.getInstance(MainActivity.this);
        dataPotholeList = db.getAllPothole();
        if (dataPotholeList!=null){
            getDistance(dataPotholeList);
        }else{
            return;
        }
        checkDistance();
        //showAllMarker();
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                View view = findViewById(R.id.layer1);

                LatLng a = marker.getPosition();
                Snackbar.make(view, ""+a.longitude+","+a.latitude, Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show();
                showDialog(dataPotholeList, a);
            }
        });



//        googleMap.setMyLocationEnabled(true);
//        Location currentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (latitude!=null && longitude != null){
            LatLng myLatLng = new LatLng(latitude,
                    longitude);
            CameraPosition myPosition = new CameraPosition.Builder()
                    .target(myLatLng).zoom(15.f).build();
            googleMap.animateCamera(
                    CameraUpdateFactory.newCameraPosition(myPosition));
        }
        layer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mMap.getMapType() == GoogleMap.MAP_TYPE_NORMAL){
                    mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                }else
                if (mMap.getMapType() == GoogleMap.MAP_TYPE_HYBRID){
                    mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                }else
                if (mMap.getMapType() == GoogleMap.MAP_TYPE_TERRAIN){
                    mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                }else
                if (mMap.getMapType() == GoogleMap.MAP_TYPE_SATELLITE){
                    mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                }
            }
        });


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
//        mMap.setMyLocationEnabled(true);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map2d);
        // Extract My Location View from maps fragment

        locationButton = mapFragment.getView().findViewById(0x2);
        // Change the visibility of my location button
        if(locationButton != null)
            locationButton.setVisibility(View.GONE);

        findViewById(R.id.myLocation).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mMap != null)
                {
                    if(locationButton != null)
                        locationButton.callOnClick();

                }
            }
        });

    }

    private void addMarkerToMaps(List<DataPothole> dataPotholeList){
        if (mMap==null){
            return;
        }
        if (marker!=null){
            marker.remove();

        }

        Log.d(TAG, "addCustomMarker()");
        for (int i = dataPotholeList.size() -1; i>=0; --i){
            marker = mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(Double.valueOf(dataPotholeList.get(i).getLatitude()), Double.valueOf(dataPotholeList.get(i).getLongitude()) ))
                    .title("User: "+ dataPotholeList.get(i).getName())
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.pothole_marker))
                    .snippet(""+ dataPotholeList.get(i).getNote())
            );


        }

//        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
//            @Override
//            public boolean onMarkerClick(Marker marker) {
//                if (marker.getTitle().equals("")) // if marker source is clicked
//                    Toast.makeText(MainActivity.this, marker.getTitle(), Toast.LENGTH_SHORT).show();// display toast
//                return true;
//            }
//        });
    }

    private Double getDistance(List<DataPothole> dataPotholeList1){
//        handler = new Handler();
//        final Runnable r = new Runnable() {
//            public void run() {
//                if (distancePoint!=null){
//
//                }
//                handler.postDelayed(this, 1000);
//            }
//        };
//        handler.postDelayed(r, 1000);
        getlocation();
         Double m = null;
//         db = SQLite.getInstance(this);
//         if (db.getTotalPotholeTB()!=0){
//             dataPotholeList = db.getAllPothole();
//             for (int i = db.getAllPothole().size()-1;i>=0;--i){
//                 m = DistanceBetweenTwoPoints.getDistance(latitude, longitude, Double.valueOf(dataPotholeList.get(i).getLatitude()), Double.valueOf(dataPotholeList.get(i).getLongitude()));
//                 if (m <= 500.0){
//                     distance = m.toString();
//                     SharedPreferences.Editor edit = getSharedPreferences("Settings", Context.MODE_PRIVATE).edit();
//                     edit.putString("distance", m.toString());
//                     edit.apply();
//                     edit.commit();
//                     return m;
//                 }else{
//                     //Log.d(TAG, "Distance: ok");
//                 }
//             }
//             addMarkerToMaps(dataPotholeList);
//         }else{
//             Log.d(TAG, "countList: "+ db.getTotalPotholeTB());
//

        Log.d(TAG, "countList: "+ dataPotholeList1.size());
        if (dataPotholeList1.size()!=0)
        for (int i = dataPotholeList1.size() -1; i>=0; --i){
            m = DistanceBetweenTwoPoints.getDistance(latitude, longitude, Double.valueOf(dataPotholeList1.get(i).getLatitude()), Double.valueOf(dataPotholeList1.get(i).getLongitude()));
            //Log.d(TAG, "Distance: "+m+", "+i+"");
            minPoint = 500.0;
            if (m < minPoint){
                minPoint = m;
            }

            if (m <= 500.0){
                distancePoint = m;
                return distancePoint;
            }else{
                //Log.d(TAG, "Distance: ok");
            }
            Log.d(TAG, "Distance: "+ m.toString()+"");
        }
        return m;

    }

    private void setNotification(final Double diss){
        handler = new Handler();
        final Runnable r = new Runnable() {
            public void run() {
//                pref = getSharedPreferences("Settings", Activity.MODE_PRIVATE);
//                distance = pref.getString("distance","");
//                String old_distance = pref.getString("old_distance","");
//                Log.d(TAG, "old distance: "+old_distance);
                DecimalFormat decimalFormat = new DecimalFormat("##.##");

                if ((diss!=null || diss != 0) ){
                    String dis = decimalFormat.format(Double.valueOf(distance)).toString();
                    sendNotification("Cảnh báo", "Cách bạn " + dis +"m có đoạn đường có mặt đường xấu");
                    handler.postDelayed(this, 20000);
//                    SharedPreferences.Editor edit = getSharedPreferences("Settings", Context.MODE_PRIVATE).edit();
//                    edit.putString("old_distance", old_distance+"");
//                    edit.apply();
//                    edit.commit();
                }else{
                    handler.postDelayed(this, 1000);
                }
                Log.e("Distance Service: ", distance+"");
            }
        };
        handler.postDelayed(r, 1000);
    }

    private void checkDistance(){
        handler = new Handler();
        runnableCheckIntance = new Runnable() {
            public void run() {
                if (distancePoint!=null){
                    if (distancePoint<=500.0){
                        String distan = distancePoint.toString();

//                        DecimalFormat decimalFormat = new DecimalFormat("##.##");
//                        //Double b=  Double.parseDouble(decimalFormat.format(distancePoint));
//                        String dis = decimalFormat.format(distan);
                        Log.e("Distance Service1: ", distan+"");

                        pref = getSharedPreferences("Settings", Context.MODE_PRIVATE);
                        String x = pref.getString("old_distance", "");
                        if (x!= String.valueOf(distancePoint)){
                            sendNotification("Cảnh báo", "Cách bạn " + distan +"m có đoạn đường có mặt đường xấu");
                            SharedPreferences.Editor edit = getSharedPreferences("Settings", Context.MODE_PRIVATE).edit();
                            edit.putString("old_distance", distancePoint+"");
                            edit.apply();
                            edit.commit();
                            handler.postDelayed(this, 50000);
                        }else{
                            handler.postDelayed(this, 1000);

                        }


                    }else{
                        Log.e("Distance Service2: ", distancePoint+"");
                        handler.postDelayed(this, 1000);
                    }
                }
                //handler.postDelayed(this, 1000);

////                    SharedPreferences.Editor edit = getSharedPreferences("Settings", Context.MODE_PRIVATE).edit();
////                    edit.putString("old_distance", old_distance+"");
////                    edit.apply();
////                    edit.commit();
//                }else{
//                    handler.postDelayed(this, 1000);
//                }
//                Log.e("Distance Service: ", distance+"");
            }
        };
        handler.postDelayed(runnableCheckIntance, 1000);

    }


    class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

        // These are both viewgroups containing an ImageView with id "badge" and two TextViews with id
        // "title" and "snippet".
        private final View mWindow;

        CustomInfoWindowAdapter() {
            mWindow = getLayoutInflater().inflate(R.layout.custom_info_window, null);
        }

        @Override
        public View getInfoWindow(Marker marker) {
            if (defaultInfoWindow) {
                return null;
            }
            render(marker, mWindow);
            return mWindow;
        }

        private void render(Marker marker, View view) {
            String title = marker.getTitle();
            TextView titleUi = ((TextView) view.findViewById(R.id.title));
            if (title != null) {
                // Spannable string allows us to edit the formatting of the text.
                SpannableString titleText = new SpannableString(title);
                titleText.setSpan(new ForegroundColorSpan(Color.RED), 0, titleText.length(), 0);
                titleUi.setText(titleText);
            } else {
                titleUi.setText(title);
            }

            String snippet = marker.getSnippet();
            TextView snippetUi = ((TextView) view.findViewById(R.id.snippet));
            if (snippet != null && snippet.length() > 12) {
                SpannableString snippetText = new SpannableString(snippet);
                snippetText.setSpan(new ForegroundColorSpan(Color.MAGENTA), 0, 10, 0);
                snippetText.setSpan(new ForegroundColorSpan(Color.BLUE), 12, snippet.length(), 0);
                snippetUi.setText(snippetText);
            } else {
                snippetUi.setText(snippet);
            }
        }

        @Override
        public View getInfoContents(Marker marker) {
            return null;
        }
    }

    private Bitmap getMarkerBitmapFromView(@DrawableRes int resId) {

        View customMarkerView = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.map_custom_marker, null);
        ImageView markerImageView = (ImageView) customMarkerView.findViewById(R.id.profile_image);
        markerImageView.setImageResource(resId);
        customMarkerView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        customMarkerView.layout(0, 0, customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight());
        customMarkerView.buildDrawingCache();
        Bitmap returnedBitmap = Bitmap.createBitmap(customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        canvas.drawColor(Color.WHITE, PorterDuff.Mode.SRC_IN);
        Drawable drawable = customMarkerView.getBackground();
        if (drawable != null)
            drawable.draw(canvas);
        customMarkerView.draw(canvas);
        return returnedBitmap;
    }

    private void showAllMarker(){
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (MarkerOptions m:markerOptionsList){
            builder.include(m.getPosition());
        }
        LatLngBounds bounds = builder.build();
        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        int padding = (int) (width * 0.30);

        CameraUpdate cam = CameraUpdateFactory.newLatLngBounds(bounds,width,height,padding);
        mMap.animateCamera(cam);
    }

    void configure_button() {
        // first check for permissions
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET}, 10);
            }
            return;
        }
        mMap.setMyLocationEnabled(true);
//        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        listener = new LocationListener() {
            @Override
            public void onLocationChanged(final Location location) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                Log.d("Location:",  "latitude = " + latitude +", Longitude = "+ longitude);
//                tvstreet.setText("Tên đường: "+streetName);
//                tvaddress.setText("Địa chỉ: "+addressName);
//                CountDownTimer countDownTimer = new CountDownTimer(86400000,5000) {
//                    @Override
//                    public void onTick(long l) {
//                        if (latitude!=null&&longitude!=null){
//                            Log.d("track...:","lat: "+latitude+", lon: "+longitude);
//                        }
//                    }
//                    @Override
//                    public void onFinish() {
//                    }
//                };
//                countDownTimer.start();
                mMap.clear();
                //Place current location marker
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                markerOptions.title("Current Position");
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
                mCurrLocationMarker = mMap.addMarker(markerOptions);

                //move map camera
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(15.f));
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {
                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(i);
            }
        };
    }

    @Override
    public void onDirectionFinderStart() {
        progressDialog = ProgressDialog.show(this, "Please wait.",
                "Finding direction..!", true);
        isStatusSensor = false;
        if (originMarkers != null) {
            for (Marker marker : originMarkers) {
                marker.remove();
            }
        }

        if (destinationMarkers != null) {
            for (Marker marker : destinationMarkers) {
                marker.remove();
            }
        }

        if (polylinePaths != null) {
            for (Polyline polyline:polylinePaths ) {
                polyline.remove();
            }
        }
    }

    @Override
    public void onDirectionFinderSuccess(List<Route> routes) {
        progressDialog.dismiss();
        polylinePaths = new ArrayList<>();
        originMarkers = new ArrayList<>();
        destinationMarkers = new ArrayList<>();

        for (Route route : routes) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(route.startLocation, 16));
            bottomSheet_listPoint.setVisibility(View.VISIBLE);
            layout1.setVisibility(View.GONE);
            ((TextView) findViewById(R.id.tvDuration)).setText(route.duration.text);
            ((TextView) findViewById(R.id.txtDistanceCar)).setText(route.distance.text);
            txtNameStart.setText(route.getStartAddress());
            txtNameEnd.setText(route.getEndAddress());
            txtLocationStart.setText(route.getStartLocation().latitude+" - "+route.getStartLocation().longitude);
            txtLocationEnd.setText(route.getEndLocation().latitude+" - "+route.getEndLocation().longitude);


            LatLng latLng = new LatLng(route.startLocation.latitude, route.startLocation.longitude);
            CameraPosition myPosition = new CameraPosition.Builder()
                    .target(latLng).zoom(15.f).build();
            mMap.animateCamera(
                    CameraUpdateFactory.newCameraPosition(myPosition));

            originMarkers.add(mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.start_blue))
                    .title(route.startAddress)
                    .position(route.startLocation)));
            destinationMarkers.add(mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.end_green))
                    .title(route.endAddress)
                    .position(route.endLocation)));

            PolylineOptions polylineOptions = new PolylineOptions().
                    geodesic(true).
                    color(Color.MAGENTA).
                    width(10);

            for (int i = 0; i < route.points.size(); i++) {

                polylineOptions.add(route.points.get(i));
                Log.e("Route", ""+route.points.size());

            }
            polylinePaths.add(mMap.addPolyline(polylineOptions));
            isStatusSensor = true;
        }
    }

    private void showGPSDisabledAlertToUser() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("GPS is disabled in your device. Would you like to enable it?")
                .setCancelable(false)
                .setPositiveButton("Goto Settings Page To Enable GPS", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent callGPSSettingIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(callGPSSettingIntent);
                    }
                });
        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }


    //check to state
    public void checkOpen(){
        CountDownTimer countDownTimer = new CountDownTimer(886400000, 10000) {
            @Override
            public void onTick(long l) {
                Log.d("MainActivity", "Checking...!");
                try {
                    for (DataPothole dt: dataPotholeList){
                        listpoint =  new ArrayList<>();
                        listpoint.add(new LatLng(Double.valueOf(dt.getLatitude()), Double.valueOf(dt.getLongitude())));
                    }
                }catch (Exception e){}
//                if (check == false){
//                    Log.d("Kiem tra: ", "Ngoài vùng!");
//                }
//                if (mp != null){
//                    imageView.setImageResource(R.drawable.open_audio);
//                    tvState.setText("Đang phát nhạc...");
//                }else {
//                    imageView.setImageResource(R.drawable.no_audio);
//                    tvState.setText("");
//                }
//                imageView.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        imageView.setImageResource(R.drawable.no_audio);
//                        tvState.setText("");
//                        mp.stop();
//                        mp.release();
//                        mp = null;
//                    }
//                });
            }
            @Override
            public void onFinish() {
                Log.d("Failed", "Error");
            }
        };
        countDownTimer.start();
    }

}
