package y2w.ui.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.view.Display;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationClientOption.AMapLocationMode;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.LocationSource;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.UiSettings;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.maps2d.model.MyLocationStyle;
import com.y2w.uikit.utils.StringUtil;
import com.yun2win.demo.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import y2w.common.Config;
import y2w.common.SendUtil;
import y2w.ui.widget.location.LocationBuilder;

/**
 * Created by maa2 on 2016/4/19.
 */
public class LocationActivity extends Activity implements
         AMapLocationListener,AMap.OnMapLoadedListener,LocationSource,AMap.OnMapClickListener {
    private MapView mapView;
    private AMap aMap;
    private OnLocationChangedListener mListener;
    private UiSettings mUiSettings;
    private AMapLocation location;
    private boolean isFirst = true;

    private AMapLocationClient locationClient = null;
    private AMapLocationClientOption locationOption = null;
    private int type;
    private double latitude;
    private double longitude;
    private Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        context = this;
        initControls();
        mapView.onCreate(savedInstanceState);// 此方法必须重写
        locationClient = new AMapLocationClient(this.getApplicationContext());
        locationOption = new AMapLocationClientOption();
        // 设置定位模式为低功耗模式
        locationOption.setLocationMode(AMapLocationMode.Battery_Saving);
        // 设置定位监听
        locationClient.setLocationListener(this);
        initMap();
        initData(this.getIntent().getExtras());
        initActionBar();
    }
    private void initControls(){
        mapView = (MapView) findViewById(R.id.mv_location);
    }
    private void initData(Bundle bundle){
        if(bundle == null){
            return;
        }
        type = bundle.getInt("type",0);
        if(type == 1){
            locationClient.setLocationOption(locationOption);
            // 启动定位
            locationClient.startLocation();
        }else if(type == 2){
            latitude = Double.parseDouble(bundle.getString("latitude","0"));
            longitude = Double.parseDouble(bundle.getString("longitude", "0"));
            setLocation(latitude, longitude);
            Display display = getWindowManager().getDefaultDisplay(); // 为获取屏幕宽、高
            ViewGroup.LayoutParams params = mapView.getLayoutParams();
            params.width = display.getWidth();
            params.height = display.getHeight();
            mapView.setLayoutParams(params);
        }
    }

    private void initMap() {
        if (aMap == null) {
            aMap = mapView.getMap();
            mUiSettings = aMap.getUiSettings();
        }
        // 自定义系统定位小蓝点
        MyLocationStyle myLocationStyle = new MyLocationStyle();
        myLocationStyle.myLocationIcon(BitmapDescriptorFactory
                .fromResource(R.drawable.unchecked));// 设置小蓝点的图标
        myLocationStyle.strokeColor(Color.argb(0, 0, 0, 0));// 设置圆形的边框颜色
        myLocationStyle.radiusFillColor(Color.argb(0, 0, 0, 0));// 设置圆形的填充颜色
        myLocationStyle.strokeWidth(0f);// 设置圆形的边框粗细
        aMap.setMyLocationStyle(myLocationStyle);
        aMap.setMyLocationRotateAngle(180);
        aMap.setLocationSource(this);// 设置定位监听
        mUiSettings.setMyLocationButtonEnabled(false); // 是否显示默认的定位按钮
        aMap.setMyLocationEnabled(true);// 是否可触发定位并显示定位层
        mUiSettings.setScaleControlsEnabled(true);// 设置地图默认的比例尺是否显示
        mUiSettings.setZoomControlsEnabled(false);
        aMap.moveCamera(CameraUpdateFactory.zoomTo(16));
        initMapListener();
    }

    private void initMapListener() {
        aMap.setOnMapLoadedListener(this);
        aMap.setOnMapClickListener(this);
    }

    private TextView texttitle;
    private TextView tv_oper;
    private ImageButton imageButtongroup;
    private void initActionBar(){
        ActionBar actionbar = getActionBar();
        actionbar.setDisplayShowTitleEnabled(false);
        actionbar.setDisplayShowHomeEnabled(false);
        actionbar.setDisplayShowCustomEnabled(true);
        actionbar.setCustomView(R.layout.actionbar_chat);
        texttitle = (TextView) actionbar.getCustomView().findViewById(R.id.text_title);
        tv_oper = (TextView) actionbar.getCustomView().findViewById(R.id.tv_right_oper);
        ImageButton imageButtonClose = (ImageButton) actionbar.getCustomView().findViewById(R.id.left_close);
        texttitle.setText("位置");
        if(type == 1){
            tv_oper.setVisibility(View.VISIBLE);
            tv_oper.setText("发送");
        }else{
            tv_oper.setVisibility(View.GONE);
        }
        tv_oper.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(location != null && location.getErrorCode() == 0){
                    String path = takeScreenShot();
                    if(!StringUtil.isEmpty(path)){
                        Intent intent = new Intent();// 数据是使用Intent返回
                        String result = path+";"+location.getLatitude()+";"+location.getLongitude()+";"+location.getAddress();
                        intent.putExtra("result", result);
                        setResult(RESULT_OK, intent);// 设置返回数据
                    }
                }
                finish();
            }
        });

        imageButtonClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void finish() {
        super.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != locationClient) {
            /**
             * 如果AMapLocationClient是在当前Activity实例化的，
             * 在Activity的onDestroy中一定要执行AMapLocationClient的onDestroy
             */
            locationClient.onDestroy();
            locationClient = null;
            locationOption = null;
        }
    }

    private Handler mHandler = new Handler(){
        public void dispatchMessage(android.os.Message msg) {
            switch (msg.what) {
                case LocationBuilder.MSG_LOCATION_FINISH:
                    AMapLocation aLocation = (AMapLocation)msg.obj;
                    if (aLocation != null) {
                        location = aLocation;
                        if (mListener != null)
                            mListener.onLocationChanged(aLocation);// 显示系统小蓝点
                        if (isFirst) {
                            isFirst = false;
                            setLocation(aLocation.getLatitude(),
                                    aLocation.getLongitude());
                        }
                    }

                    break;
                default:
                    break;
            }
        };
    };

    private void setLocation(double latitude,double longitude){
        if(aMap == null){
            return;
        }
        aMap.moveCamera(CameraUpdateFactory.changeLatLng(new LatLng(
                latitude, longitude)));
        MarkerOptions markerOption = new MarkerOptions();
        markerOption.position(new LatLng(latitude,
                longitude));
        markerOption.draggable(true);
        Marker marker = aMap.addMarker(markerOption);
        marker.setObject("11");//这里可以存储用户数据
    }

    // 定位监听
    @Override
    public void onLocationChanged(AMapLocation loc) {
        if (null != loc && type == 1) {
            Message msg = mHandler.obtainMessage();
            msg.obj = loc;
            msg.what = LocationBuilder.MSG_LOCATION_FINISH;
            mHandler.sendMessage(msg);
        }
    }

    @Override
    public void onMapLoaded() {

    }

    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {

    }

    @Override
    public void deactivate() {

    }

    @Override
    public void onMapClick(LatLng latLng) {

    }

    private String takeScreenShot(){
        //View是你需要截图的View
        String path = "";
        File file = new File(Config.CACHE_PATH_FILE);
        if(!file.exists()){
            file.mkdirs();
        }
        long millis =System.currentTimeMillis();
        file = new File(Config.CACHE_PATH_FILE + "y2w_location_shot_"+millis+".jpg");
        if(file.exists()){
            file.delete();
        }
        mapView.setDrawingCacheEnabled(true);
        mapView.buildDrawingCache();
        Bitmap b1 = mapView.getDrawingCache();
        if(b1 != null){
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(file.getPath());
                if (null != fos)
                {
                    b1.compress(Bitmap.CompressFormat.PNG, 90, fos);
                    fos.flush();
                    fos.close();
                    path = SendUtil.compressOriginPicture(this, file.getPath());
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        mapView.destroyDrawingCache();
        return path;
    }

    public static class LocationType{
        public static int locationSend = 1;
        public static int locationDisplay = 2;
    }
}

