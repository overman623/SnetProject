package snetproject.com.main;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.AbsoluteSizeSpan;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Pattern;

import snetproject.com.R;
import snetproject.com.dataset.AlertInputData;
import snetproject.com.dataset.PositionInputData;
import snetproject.com.main.map.MarkerManager;
import snetproject.com.network.ClientService;


public class MainActivity extends FragmentActivity implements OnMapReadyCallback {

    public static final String TAG = "MainActivity";

    public PositionInputData positionInputData = PositionInputData.getInstance();
    public AlertInputData alertInputData = AlertInputData.getInstance();

    public ClientService clientService = null;

    private Button button_connect = null;
    private Button button_people = null;
    private ToggleButton toggle_button_setting = null;

    private LinearLayout linear_layout_alert = null;
    private LinearLayout linear_layout_setting = null;
    private RelativeLayout relative_layout_bottom = null;
    private ImageView image_alert = null;
    private ImageView image_logo_kt = null;
    private ImageView image_kcity = null;
    private ImageView image_gear = null;
    private ImageView image_connect = null;

    public static int boundary = 5; //meter
//    public static String IP = "39.7.224.40";
    public static String IP = "192.168.0.7";
    public static int PORT = 30000;
    public static int vehicleId = 1;

    public GoogleMap mMap = null;

    private LatLng initLocation = new LatLng(0,0);

    private MarkerManager markerManager = null;
    private Marker myMarker = null;
    private Marker otherMarker = null;

    boolean startFlag = true;

    private SharedPreferences setting = null;
    private SharedPreferences.Editor setter = null;

    private Animation startAnimation = null;

    private ImageView image_pc5 = null;
    private ImageView image_lte = null;
    private ImageView image_giga_stellth = null;
    private ImageView image_my_marker_right = null;
    private ImageView image_other_marker_right = null;
    private ImageView image_people = null;
    private ImageView image_layout_bottom = null;

    private TextView text_vehicle_1_id = null;
    private TextView text_vehicle_2_id = null;
    private TextView text_vehicle_1_speed = null;
    private TextView text_vehicle_2_speed = null;
    private TextView text_vehicle_1_heading = null;
    private TextView text_vehicle_2_heading = null;
    private TextView text_my_latitude = null;
    private TextView text_other_latitude = null;
    private TextView text_my_longitude = null;
    private TextView text_other_longitude = null;
    private TextView text_distance = null;

    @SuppressLint("HandlerLeak")
    public Handler mainHandler = new Handler(){ //값을 세팅하는 역할만 한다.

        int[] pc5Array = new int[10];
        int[] lteArray = new int[10];
        int pc5Index = 0;
        int lteIndex = 0;

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1: //position
                    byte[] positionByte = (byte[]) msg.obj;
//                    Mlog.d(TAG,"p : " + byteArrayToHex(positionByte, msg.arg1));
                    positionInputData.setCombine(byteArrayToHex(positionByte, msg.arg1));
                    double calcLatitude = positionInputData.getLatitude() * 0.0000001;
                    double calcLongitude = positionInputData.getLongitude() * 0.0000001;
                    double calcSpeed = positionInputData.getSpeed() * 0.1;
                    double calcCourse = positionInputData.getCourse() * 0.1;

                    if(!startFlag){
                        if(vehicleId == positionInputData.getVehicleId()){
//                            CameraPosition.Builder builder = new CameraPosition.Builder();
//                            builder.target(new LatLng(calcLatitude, calcLongitude)); //카메라 중심 좌표
//                            builder.zoom(17); //줌 설정
//                            CameraPosition cameraPosition = builder.build();
//                            CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
                            startFlag = true;
                        }
                    }

                    if(positionInputData.getInterfaceType() == 1){ //깜빡거리는 식으로 표현함. //v2v //wave
                        pc5Array[pc5Index] = 1;
                        changeTitleImage(R.drawable.pc5_interface_on, image_pc5);
                    }else if(positionInputData.getInterfaceType() == 2){ //v2n //lte
                        lteArray[lteIndex] = 2;
                        changeTitleImage(R.drawable.lte_interface_on, image_lte);
                    }else if(positionInputData.getInterfaceType() == 3){
                    }else{
                        pc5Array[pc5Index] = 0;
                        lteArray[lteIndex] = 0;
                    }

                    pc5Index++;
                    lteIndex++;

                    if(pc5Index == 9){ pc5Index = 0; }
                    if(lteIndex == 9){ lteIndex = 0; }

                    int pc5Sum = 0;
                    for(int pc5X = 0; pc5X < pc5Array.length ; pc5X++){  pc5Sum += pc5Array[pc5X]; }
                    if(pc5Sum == 0){ changeTitleImage(R.drawable.pc5_icon, image_pc5); }

                    int lteSum = 0;
                    for(int lteX = 0; lteX < lteArray.length ; lteX++){ lteSum += lteArray[lteX]; }
                    if(lteSum == 0){ changeTitleImage(R.drawable.lte_interface_icon, image_lte); }

                    if(vehicleId == positionInputData.getVehicleId()){ //자기 Id
                        text_vehicle_1_id.setText(positionInputData.getVehicleId() == 1 ? "위급차" : "구급차");
                        text_my_latitude.setText("위도 : " + getDecimalFormat(calcLatitude));
                        text_my_longitude.setText("경도 : " + getDecimalFormat(calcLongitude));
                        text_vehicle_1_speed.setText(changeFontSize(getDecimalFormat2(calcSpeed)));
                        text_vehicle_1_heading.setText(changeFontSize(getDecimalFormat2(calcCourse)));
                        if(myMarker != null)
                            myMarker.setPosition(new LatLng(calcLatitude, calcLongitude));
                    }else{
                        text_vehicle_2_id.setText(positionInputData.getVehicleId() == 1 ? "위급차" : "구급차");
                        text_other_latitude.setText("위도 : " + getDecimalFormat(calcLatitude));
                        text_other_longitude.setText("경도 : " + getDecimalFormat(calcLongitude));
                        text_vehicle_2_speed.setText(changeFontSize(getDecimalFormat2(calcSpeed)));
                        text_vehicle_2_heading.setText(changeFontSize(getDecimalFormat2(calcCourse)));
                        if(otherMarker != null)
                            otherMarker.setPosition(new LatLng(calcLatitude, calcLongitude));
                    }

                    double distance = distance(myMarker.getPosition().latitude, myMarker.getPosition().longitude, otherMarker.getPosition().latitude, otherMarker.getPosition().longitude, "meter");
                    text_distance.setText(getDecimalFormat2(distance));

                    break;
                case 2: //alert
                    byte[] alertByte = (byte[]) msg.obj;
                    //String alertMessage = byteArrayToHex(alertByte, msg.arg1);
                    Log.d(TAG,"alert input : " +  byteArrayToHex(alertByte, msg.arg1));
                    alertInputData.setCombine(byteArrayToHex(alertByte, msg.arg1));
                    alertLayout(); //들어오면 일단 경보하고....
                    break;
            }
        }

        private String byteArrayToHex(byte[] a, int size) {
            StringBuilder sb = new StringBuilder();
            for(int i = 0; i < size; i++){
                sb.append(String.format("%02x", a[i] & 0xff));
            }
            return sb.toString();
        }
        private double distance(double lat1, double lon1, double lat2, double lon2, String unit) {

            double theta = lon1 - lon2;
            double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));

            dist = Math.acos(dist);
            dist = rad2deg(dist);
            dist = dist * 60 * 1.1515;

            if (unit == "kilometer") {
                dist = dist * 1.609344;
            } else if(unit == "meter"){
                dist = dist * 1609.344;
            }

            return (dist);
        }
        private double deg2rad(double deg) {
            return (deg * Math.PI / 180.0);
        } // This function converts decimal degrees to radians
        private double rad2deg(double rad) {
            return (rad * 180 / Math.PI);
        } // This function converts radians to decimal degrees
        private String getDecimalFormat(double val){
            DecimalFormat form = new DecimalFormat("###.000000");
            return form.format( val );
        }
        private String getDecimalFormat2(double val){
            DecimalFormat form = new DecimalFormat("0.0");
            return form.format( val );
        }
        private SpannableStringBuilder changeFontSize(String data){
            SpannableStringBuilder builder = new SpannableStringBuilder(data);
            builder.setSpan(new AbsoluteSizeSpan(30), data.length() - 2, data.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            return builder;
        }

    };



    private void setMarkerImage(long vehicleId) {
        if(vehicleId == 1){
            Glide.with(this).load(R.drawable.red_car2).into(image_my_marker_right);
            Glide.with(this).load(R.drawable.ambulance_green2).into(image_other_marker_right);
            myMarker = markerManager.changeMyMarker(initLocation, R.drawable.red_car, mMap);
            otherMarker = markerManager.changeOtherMarker(new LatLng(positionInputData.getLatitude() * 0.0000001, positionInputData.getLongitude() * 0.0000001), R.drawable.ambulance_green, mMap);
        }else if(vehicleId == 2){
            Glide.with(this).load(R.drawable.ambulance_green2).into(image_my_marker_right);
            Glide.with(this).load(R.drawable.red_car2).into(image_other_marker_right);
            myMarker = markerManager.changeOtherMarker(new LatLng(positionInputData.getLatitude() * 0.0000001, positionInputData.getLongitude() * 0.0000001), R.drawable.ambulance_green, mMap);
            otherMarker = markerManager.changeMyMarker(initLocation, R.drawable.red_car, mMap);
        }
    }

    private SpannableStringBuilder changeFontSize(String data){
        SpannableStringBuilder builder = new SpannableStringBuilder(data);
        builder.setSpan(new AbsoluteSizeSpan(30), data.length() - 2, data.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return builder;
    } //나중에지움

    private String getDecimalFormat2(double val){
        DecimalFormat form = new DecimalFormat("0.0");
        return form.format( val );
    } //나중에지움

    private void changeTitleImage(int drawable, ImageView view) {
        Glide.with(getBaseContext()).load(drawable).into(view);
    }

    @SuppressLint("HandlerLeak")
    public Handler networkHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case ClientService.NOT_CONNECTED:
                    changeUiTitle(ClientService.NOT_CONNECTED);
                    break;
                case ClientService.LISTEN:
                    changeUiTitle(ClientService.LISTEN);
                    break;
                case ClientService.CONNECTED:
                    changeUiTitle(ClientService.CONNECTED);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        markerManager = new MarkerManager(this);
        markerManager.setCustomMarkerView();

        setting = getSharedPreferences("test", MODE_PRIVATE);
        setter = setting.edit();

        //처음 어플리케이션을 깔았을 때 들어오는 데이터.
        IP = setting.getString("IP", "0.0.0.0");
        PORT = setting.getInt("PORT", 30000);
        vehicleId = setting.getInt("vehicleId", 1);

        PermissionListener permissionlistener = new PermissionListener() {

            @Override
            public void onPermissionGranted() {
                //Toast.makeText(getApplicationContext(), "권한 허가", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "권한 허가");
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                //Toast.makeText(getApplicationContext(), "권한 거부\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
                Log.d(TAG, "권한 거부");
            }

        };

        TedPermission.with(this)
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(Manifest.permission.INTERNET,
                        Manifest.permission.ACCESS_NETWORK_STATE,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_WIFI_STATE,
                        Manifest.permission.CHANGE_WIFI_STATE,
                        Manifest.permission.BLUETOOTH_ADMIN,
                        Manifest.permission.BLUETOOTH,
                        Manifest.permission.RECEIVE_BOOT_COMPLETED)
                .check();

        init();

        setConnectVariable(IP, PORT);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    @SuppressLint("WrongViewCast")
    private void init() {
        //===========================Title side====================================
        image_pc5 = findViewById(R.id.image_pc5);
        image_lte = findViewById(R.id.image_lte);
        image_giga_stellth = findViewById(R.id.image_giga_stellth);

        //===========================Left side====================================
        button_connect = findViewById(R.id.button_connect);
        button_connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (clientService.getConnectState()){
                    case ClientService.NOT_CONNECTED: //접속 시작
                        Log.d(TAG,"thread state " + clientService.getState() + "");
                        Glide.with(getApplicationContext()).load(R.drawable.connect).into(image_connect);
                        clientService = null;
                        setConnectVariable(MainActivity.IP, MainActivity.PORT);
                        if(clientService.getState() == Thread.State.NEW){
                            clientService.start(); //나중에 해재함.
                        }
                        setMarkerImage(vehicleId);
                        break;
                    case ClientService.LISTEN:
                        break;
                    case ClientService.CONNECTED: //접속 종료
                        //접속을 끊으면 RUNNABLE
//                        Glide.with(getApplicationContext()).load(R.drawable.disconnect).into(image_connect);
                        clientService.setMainHandler(null);
                        Log.d(TAG,"thread state " + clientService.getState() + "");
                        if(myMarker != null ) myMarker.remove();
                        if(otherMarker != null ) otherMarker.remove();
                        clientService.connectionLost();
                        break;
                    default:
                        break;
                }
            }
        });

        toggle_button_setting = findViewById(R.id.toggle_button_setting);
        toggle_button_setting.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked){
                    buttonView.setBackgroundColor(Color.parseColor("#ff0000"));
                    buttonView.setChecked(true);
                    Animation anim = AnimationUtils.loadAnimation(getApplicationContext(),
                                            R.anim.flipper_appear_form_left);
                    linear_layout_setting.setVisibility(View.VISIBLE);
                    linear_layout_setting.startAnimation(anim);
                    settingOn();

                }else{
                    buttonView.setBackgroundColor(Color.parseColor("#000000"));
                    Animation anim = AnimationUtils.loadAnimation(getApplicationContext(),
                            R.anim.flipper_disappear_to_left);
                    linear_layout_setting.setVisibility(View.GONE);
                    linear_layout_setting.startAnimation(anim);
                    buttonView.setChecked(false);
                    settingOff();

                }
            }
        });
        linear_layout_setting = findViewById(R.id.include_layout_setting);

        button_people = findViewById(R.id.button_people);
        button_people.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(linear_layout_alert.getVisibility() == View.GONE && relative_layout_bottom.getVisibility() == View.GONE ){
                    linear_layout_alert.setVisibility(View.VISIBLE);
                    relative_layout_bottom.setVisibility(View.VISIBLE);
                }else if(linear_layout_alert.getVisibility() == View.VISIBLE || relative_layout_bottom.getVisibility() == View.VISIBLE){
                    linear_layout_alert.setVisibility(View.GONE);
                    relative_layout_bottom.setVisibility(View.GONE);
                }
            }
        });

        //===========================Right side====================================

        linear_layout_alert = findViewById(R.id.linear_layout_alert);
        relative_layout_bottom = findViewById(R.id.relative_layout_bottom);
        linear_layout_alert.setOnClickListener(new View.OnClickListener() { //경고문 클릭할때
            @Override
            public void onClick(View v) {
                v.setVisibility(View.GONE);
            }
        });
        relative_layout_bottom.setOnClickListener(new View.OnClickListener() { //경고문 클릭할때
            @Override
            public void onClick(View v) {
                relative_layout_bottom.setVisibility(View.GONE);
            }
        });

        image_alert = findViewById(R.id.image_alert);
        image_logo_kt = findViewById(R.id.image_logo_kt);
        image_kcity = findViewById(R.id.image_kcity);
        image_gear = findViewById(R.id.image_gear);
        image_connect = findViewById(R.id.image_connect);
        image_people = findViewById(R.id.image_people);
//        image_alert_bottom = findViewById(R.id.image_alert_bottom);
        image_my_marker_right = findViewById(R.id.image_my_marker_right);
        image_other_marker_right = findViewById(R.id.image_other_marker_right);
        image_layout_bottom = findViewById(R.id.image_layout_bottom);

        text_vehicle_1_id = findViewById(R.id.text_vehicle_1_id);
        text_vehicle_2_id = findViewById(R.id.text_vehicle_2_id);
        text_vehicle_1_speed = findViewById(R.id.text_vehicle_1_speed);
        text_vehicle_2_speed = findViewById(R.id.text_vehicle_2_speed);
        text_vehicle_1_heading = findViewById(R.id.text_vehicle_1_heading);
        text_vehicle_2_heading = findViewById(R.id.text_vehicle_2_heading);
        text_my_latitude = findViewById(R.id.text_my_latitude);
        text_other_latitude = findViewById(R.id.text_other_latitude);
        text_my_longitude = findViewById(R.id.text_my_longitude);
        text_other_longitude = findViewById(R.id.text_other_longitude);

        text_distance = findViewById(R.id.text_distance);
        text_distance.setText("0");

        Glide.with(this).load(R.drawable.warning_driveway3).into(image_alert);
        Glide.with(this).load(R.drawable.logo_kt_final).into(image_logo_kt);

        Glide.with(this).load(R.drawable.giga_korea).into(image_kcity); // giga korea logo 활성화
//        Glide.with(this).load(R.drawable.kcity_real).into(image_kcity); // kcity logo 활성화

        Glide.with(this).load(R.drawable.gear).into(image_gear);
        Glide.with(this).load(R.drawable.connect).into(image_connect);
        Glide.with(this).load(R.drawable.people).into(image_people);
        Glide.with(this).load(R.drawable.pc5_icon).into(image_pc5);
        Glide.with(this).load(R.drawable.lte_interface_icon).into(image_lte);
        Glide.with(this).load(R.drawable.giga_stealth_icon).into(image_giga_stellth);
        Glide.with(this).load(R.drawable.red_car2).into(image_my_marker_right);
        Glide.with(this).load(R.drawable.ambulance_green2).into(image_other_marker_right);
        Glide.with(this).load(R.drawable.dummy6).into(image_layout_bottom);

        text_vehicle_1_speed.setText(changeFontSize("0.0")); //초기화
        text_vehicle_1_heading.setText(changeFontSize("0.0")); //초기화
        text_vehicle_2_speed.setText(changeFontSize("0.0")); //초기화
        text_vehicle_2_heading.setText(changeFontSize("0.0")); //초기화

    }

    public void setConnectVariable(String ip, int port){
        Log.d(TAG, "ip : " + ip + " port : " + port);
        if(clientService == null) {
            //타이틀 및 버튼 UI 바꾸기
            clientService = new ClientService(ip, port, networkHandler);
            //clientService.start(); //서버 접속 시작
        }
    }

    private static String getTimeText(){
        Date dt = new Date(System.currentTimeMillis());
        String sdf = new SimpleDateFormat("a hh:mm").format(dt);
        return sdf;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        CameraPosition.Builder builder = new CameraPosition.Builder();
        builder.target(new LatLng(37.241905, 126.774112)); //카메라 중심 좌표
        builder.bearing(90); //회전 각도 설정
        builder.zoom(17.1f);
        CameraPosition cameraPosition = builder.build();
        CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
        mMap.moveCamera(cameraUpdate);

        LatLng KCITY = new LatLng(37.243005, 126.774062); //이미지 오버레이

        GroundOverlayOptions newarkMap = new GroundOverlayOptions()
                .image(BitmapDescriptorFactory.fromResource(R.drawable.mini_map)).transparency(0.0f)
                .position(KCITY, 1260f, 568f).bearing(90);

        GroundOverlay imageOverlay = mMap.addGroundOverlay(newarkMap);

    }

    public void changeUiTitle(int connectionState){
        switch (connectionState){
            case ClientService.NOT_CONNECTED:
                linear_layout_alert.setVisibility(View.GONE);
                startFlag = true;
                Glide.with(this).load(R.drawable.connect).into(image_connect);
                changeTitleImage(R.drawable.lte_interface_icon, image_lte);
                changeTitleImage(R.drawable.pc5_icon, image_pc5);
                changeTitleImage(R.drawable.giga_stealth_icon, image_giga_stellth);
                break;
            case ClientService.LISTEN:
                break;
            case ClientService.CONNECTED:
                clientService.setMainHandler(mainHandler);
                //clientService.sendPositionMessage(positionOutputData, MainActivity.period);
                Glide.with(this).load(R.drawable.disconnect).into(image_connect);
                changeTitleImage(R.drawable.giga_stealth_on, image_giga_stellth);
                startFlag = false;
                break;
            default:
                break;
        }
    }

    public void settingOn(){

        final EditText edit_ip_1 = findViewById(R.id.edit_ip_1);
        final EditText edit_ip_2 = findViewById(R.id.edit_ip_2);
        final EditText edit_ip_3 = findViewById(R.id.edit_ip_3);
        final EditText edit_ip_4 = findViewById(R.id.edit_ip_4);

        final EditText edit_port = findViewById(R.id.edit_port);
        final EditText edit_vehicle_id = findViewById(R.id.edit_vehicle_id);

        final Button button_setting_save = findViewById(R.id.button_setting_save);
        final Button button_setting_close = findViewById(R.id.button_setting_close);
        ImageView image_setting_close = findViewById(R.id.image_setting_close);
        Glide.with(this).load(R.drawable.disconnect).into(image_setting_close);

        String [] ipStrings = MainActivity.IP.split("\\.");

        edit_ip_1.setText(ipStrings[0]);
        edit_ip_2.setText(ipStrings[1]);
        edit_ip_3.setText(ipStrings[2]);
        edit_ip_4.setText(ipStrings[3]);

        edit_port.setText(MainActivity.PORT+"");
        edit_vehicle_id.setText(MainActivity.vehicleId+"");

        button_setting_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String ip = edit_ip_1.getText().toString();
                ip += "." + edit_ip_2.getText().toString();
                ip += "." + edit_ip_3.getText().toString();
                ip += "." + edit_ip_4.getText().toString();

                String port = edit_port.getText().toString();
                String vehicleId = edit_vehicle_id.getText().toString();

                boolean bCheck = true;
                bCheck &= Pattern.matches("^[0-9]*$", port);
                bCheck &= Pattern.matches("^[0-9]*$", vehicleId);
                bCheck &= Pattern.matches("^[0-9]*$", edit_ip_1.getText().toString());
                bCheck &= Pattern.matches("^[0-9]*$", edit_ip_2.getText().toString());
                bCheck &= Pattern.matches("^[0-9]*$", edit_ip_3.getText().toString());
                bCheck &= Pattern.matches("^[0-9]*$", edit_ip_4.getText().toString());

                if(bCheck){ //모두 숫자만 입력할때
                    MainActivity.IP = ip;
                    MainActivity.PORT = Integer.parseInt(port);

                    if(Pattern.matches("^[1-2]*$", vehicleId)){
                        MainActivity.vehicleId = Integer.parseInt(vehicleId);
                    }else{
                        MainActivity.vehicleId = 1;
                    }

                    setter.putString("IP", ip);
                    setter.putInt("PORT", MainActivity.PORT);
                    setter.putInt("vehicleId", MainActivity.vehicleId);
                    setter.commit();
                }else{ //그렇지 않을때
                }
                Log.d(TAG, "save click");

                toggle_button_setting.performClick();

            }
        });

        button_setting_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "cancel click");
                toggle_button_setting.performClick();
            }
        });

    }

    private void settingOff() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(linear_layout_setting.getApplicationWindowToken(), 0);
    }

    public void alertLayout(){
        Log.d(TAG, "linear_layout_alert on" );
        linear_layout_alert.setVisibility(View.VISIBLE);
        relative_layout_bottom.setVisibility(View.VISIBLE);
    }

}