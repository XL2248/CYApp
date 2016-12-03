package com.example.fs.cy_demo;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapOptions;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.SupportMapFragment;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.overlay.PoiOverlay;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.core.SuggestionCity;
import com.amap.api.services.help.Inputtips;
import com.amap.api.services.help.InputtipsQuery;
import com.amap.api.services.help.Tip;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.example.fs.cy_demo.Util.AMapUtil;
import com.example.fs.cy_demo.Util.ToastUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by 龙哥 on 2016/12/1.
 */
public class InputTip extends FragmentActivity implements TextWatcher, Inputtips.InputtipsListener,AMapLocationListener,LocationListener,LocationSource
        ,PoiSearch.OnPoiSearchListener,AMap.OnCameraChangeListener{

    private String city = "";
    private AutoCompleteTextView mKeywordText;// 输入搜索关键字
    private TextView btn_sure;//确定按钮
    private ListView minputlist;//索引列表
    private String content="";//输入的内容
    private Double latitude=0.0,longitude=0.0;//经纬度
    private AMap aMap;
    private LocationSource.OnLocationChangedListener mListener;
    private AMapLocationClient mlocationClient;
    private AMapLocationClientOption mLocationOption;


    private String keyWord = "";// 要输入的poi搜索关键字
    private ProgressDialog progDialog = null;// 搜索时进度条
    private PoiResult poiResult; // poi返回的结果
    private int currentPage = 0;// 当前页面，从0开始计数
    private PoiSearch.Query query;// Poi查询条件类
    private PoiSearch poiSearch;// POI搜索
    private LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.input_tip);
        minputlist = (ListView)findViewById(R.id.inputlist);
        mKeywordText = (AutoCompleteTextView)findViewById(R.id.input_edittext);
        btn_sure= (TextView) findViewById(R.id.btn_sure);

        minputlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                HashMap<String,String> map=(HashMap<String,String>)minputlist.getItemAtPosition(position);
                //String title=map.get("address");
                String text=map.get("name");//获取点击的是哪项
                mKeywordText.setText(text);//显示出来
                search_tv();//进行定位
                deactivate();//停止定位
            }
        });
        btn_sure.setOnClickListener(new View.OnClickListener() {//点击确定进行回传数据（输入的地点和经纬度）
            @Override
            public void onClick(View v) {
                if(!mKeywordText.getText().toString().equals(""))
                {
                    content = mKeywordText.getText().toString();
                    Intent data = new Intent();
                    Bundle bundle = new Bundle();
                    bundle.putString("content", content);//回传地点
                    bundle.putDouble("la", latitude);//回传纬度
                    bundle.putDouble("lo", longitude);//回传经度
                    data.putExtras(bundle);
                    setResult(2, data);//返回码2
                    finish();
                }else {
                    Toast.makeText(InputTip.this, "请输入关键字！", Toast.LENGTH_SHORT).show();//输入为空时进行提示
                }
            }
        });
        mKeywordText.addTextChangedListener(this);
        init();
    }
    /**
     * 初始化
     */
    private void init() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        LocationProvider gpsProvider = locationManager.getProvider(LocationManager.GPS_PROVIDER);//1.通过GPS定位，较精确，也比较耗电
        LocationProvider netProvider = locationManager.getProvider(LocationManager.NETWORK_PROVIDER);//2.通过网络定位，对定位精度度不高或省点情况可考虑使用
        if (aMap == null) {
            aMap = ((SupportMapFragment)getSupportFragmentManager()//继承不同 Activity FramentActivity
                    .findFragmentById(R.id.tipmap)).getMap();
            setUpMap();
        }
    }
    /**
     * 设置一些amap的属性
     */
    private void setUpMap() {
        aMap.setLocationSource(this);// 设置定位监听
        aMap.setMyLocationEnabled(true);
        aMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);
        UiSettings uiSettings = aMap.getUiSettings();
        uiSettings.setScaleControlsEnabled(true);//设置比例尺
        uiSettings
                .setZoomPosition(AMapOptions.ZOOM_POSITION_RIGHT_CENTER);
        mKeywordText = (AutoCompleteTextView) findViewById(R.id.input_edittext);
        mKeywordText.addTextChangedListener(this);// 添加文本输入框监听事件
        aMap.setOnCameraChangeListener(this);// 对amap添加移动地图事件监听器
    }
    /**
     * 点击搜索按钮
     *
     */
    public void search_tv() {
        keyWord = AMapUtil.checkEditText(mKeywordText);
        if ("".equals(keyWord)) {
            ToastUtil.show(InputTip.this, "请输入搜索关键字");
            return;
        } else {
            doSearchQuery();
        }
    }
    /**
     * 显示进度框
     */
    private void showProgressDialog() {
        if (progDialog == null)
            progDialog = new ProgressDialog(this);
        progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDialog.setIndeterminate(false);
        progDialog.setCancelable(false);
        progDialog.setMessage("正在搜索1:\n" + keyWord);
        progDialog.show();
    }
    /**
     * 隐藏进度框
     */
    private void dissmissProgressDialog() {
        if (progDialog != null) {
            progDialog.dismiss();
        }
    }
    /**
     * 开始进行poi搜索
     */
    protected void doSearchQuery() {
        //showProgressDialog();// 显示进度框
        currentPage = 0;
        query = new PoiSearch.Query(keyWord, "", "");// 第一个参数表示搜索字符串，第二个参数表示poi搜索类型，第三个参数表示poi搜索区域（空字符串代表全国）
        poiSearch = new PoiSearch(this, query);
        query.setPageSize(10);// 设置每页最多返回多少条poiitem//也就是10个图标
        query.setPageNum(currentPage);// 设置查第一页
        poiSearch.setOnPoiSearchListener(this);
        poiSearch.searchPOIAsyn();
    }
    /**
     * poi没有搜索到数据，返回一些推荐城市的信息
     */
    private void showSuggestCity(List<SuggestionCity> cities) {
        String infomation = "推荐城市\n";
        for (int i = 0; i < cities.size(); i++) {
            infomation += "城市名称:" + cities.get(i).getCityName() + "城市区号:"
                    + cities.get(i).getCityCode() + "城市编码:"
                    + cities.get(i).getAdCode() + "\n";
        }
        ToastUtil.show(InputTip.this, infomation);
    }
    /**

     * POI信息查询回调方法
     */
    @Override
    public void onPoiSearched(PoiResult result, int rCode) {
        dissmissProgressDialog();// 隐藏对话框
        if (rCode == 1000) {
            if (result != null && result.getQuery() != null) {// 搜索poi的结果
                if (result.getQuery().equals(query)) {// 是否是同一条
                    poiResult = result;
                    // 取得搜索到的poiitems有多少页
                    List<PoiItem> poiItems = poiResult.getPois();// 取得第一页的poiitem数据，页数从数字0开始
                    List<SuggestionCity> suggestionCities = poiResult
                            .getSearchSuggestionCitys();// 当搜索不到poiitem数据时，会返回含有搜索关键字的城市信息

                    if (poiItems != null && poiItems.size() > 0) {
                        aMap.clear();// 清理之前的图标
                        PoiOverlay poiOverlay = new PoiOverlay(aMap, poiItems);
                        poiOverlay.removeFromMap();
                        poiOverlay.addToMap();
                        poiOverlay.zoomToSpan();
                    } else if (suggestionCities != null
                            && suggestionCities.size() > 0) {
                        showSuggestCity(suggestionCities);
                    } else {
                        ToastUtil.show(InputTip.this,
                                R.string.no_result);
                    }
                }
            } else {
                ToastUtil.show(InputTip.this,
                        R.string.no_result);
            }
        } else {
            ToastUtil.showerror(this, rCode);
        }
    }

    @Override
    public void onPoiItemSearched(PoiItem item, int rCode) {
        // TODO Auto-generated method stub

    }
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count,
                                  int after) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        String newText = s.toString().trim();
        InputtipsQuery inputquery = new InputtipsQuery(newText, city);
        inputquery.setCityLimit(true);
        Inputtips inputTips = new Inputtips(InputTip.this, inputquery);
        inputTips.setInputtipsListener(this);
        inputTips.requestInputtipsAsyn();
    }

    @Override
    public void afterTextChanged(Editable s) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onGetInputtips(final List<Tip> tipList, int rCode) {
        if (rCode == 1000) {
            List<HashMap<String, String>> listString = new ArrayList<HashMap<String, String>>();
            for (int i = 0; i < tipList.size(); i++) {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("name", tipList.get(i).getName());
                map.put("address", tipList.get(i).getDistrict());
                listString.add(map);
            }
            SimpleAdapter aAdapter = new SimpleAdapter(getApplicationContext(), listString, R.layout.item_layout,
                    new String[] {"name","address"}, new int[] {R.id.poi_field_id, R.id.poi_value_id});
            minputlist.setAdapter(aAdapter);
            aAdapter.notifyDataSetChanged();
        }else {
            ToastUtil.showerror(this, rCode);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //mapView.onSaveInstanceState(outState);
    }

    /**
     * 位置改变回调方法
     * @param location 当前的位置
     * @return void
     */
    public void onLocationChanged(Location Location){

    }
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onProviderEnabled(String provider) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onProviderDisabled(String provider) {
        // TODO Auto-generated method stub

    }

    /**
     * 定位成功后回调函数
     */

    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        if (mListener != null && amapLocation != null) {
            if (amapLocation != null
                    && amapLocation.getErrorCode() == 0) {
                mListener.onLocationChanged(amapLocation);// 显示系统小蓝点
            } else {
                String errText = "定位失败," + amapLocation.getErrorCode()+ ": " + amapLocation.getErrorInfo();
                Log.e("AmapErr",errText);
            }
        }
    }
    /**
     * 激活定位
     */

    @Override
    public void activate(LocationSource.OnLocationChangedListener listener) {
        mListener = listener;
        if(locationManager.getProvider(LocationManager.NETWORK_PROVIDER) != null || locationManager.getProvider(LocationManager.GPS_PROVIDER) != null)
        {
            if (mlocationClient == null ) {
                mlocationClient = new AMapLocationClient(this);
                mLocationOption = new AMapLocationClientOption();
                //设置定位监听
                mlocationClient.setLocationListener(this);
                //设置为高精度定位模式
                mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
                //设置定位参数
                mlocationClient.setLocationOption(mLocationOption);
                // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
                // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
                // 在定位结束后，在合适的生命周期调用onDestroy()方法
                // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
                mlocationClient.startLocation();
            }
        }else{
            //无法定位：1、提示用户打开定位服务；2、跳转到设置界面
            Toast.makeText(this, "无法定位，请打开定位服务", Toast.LENGTH_SHORT).show();
            Intent i = new Intent();
            i.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(i);
        }
    }
    /**
     * 停止定位
     * 停止定位
     */

    @Override
    public void deactivate() {
        mListener = null;
        if (mlocationClient != null) {
            mlocationClient.stopLocation();
            mlocationClient.onDestroy();
        }
        mlocationClient = null;
    }

    @Override

    public void onCameraChange(CameraPosition position) {//获取经纬度
        LatLng target = position.target;
        longitude=target.latitude;
        latitude=target.longitude;
        //Toast toast=Toast.makeText(getApplicationContext(),"直接输出经纬度"+latitude+"\n,"+longitude, Toast.LENGTH_SHORT);
        //显示toast信息
       // toast.show();
    }
    @Override

    public void onCameraChangeFinish(CameraPosition position) {

// TODO Auto-generated method stub

    }
}
