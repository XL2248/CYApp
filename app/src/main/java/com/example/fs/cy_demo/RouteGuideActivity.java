package com.example.fs.cy_demo;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.overlay.DrivingRouteOverlay;
import com.amap.api.maps.overlay.WalkRouteOverlay;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DrivePath;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RideRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.WalkPath;
import com.amap.api.services.route.WalkRouteResult;
import com.example.fs.cy_demo.Util.AMapUtil;
import com.example.fs.cy_demo.Util.ToastUtil;
import com.example.fs.cy_demo.route.BusResultListAdapter;
import com.example.fs.cy_demo.route.DriveRouteDetailActivity;
import com.example.fs.cy_demo.route.WalkRouteDetailActivity;

/**
 * Created by 龙哥 on 2016/11/28.
 */
public class RouteGuideActivity extends Activity implements AMap.OnMapClickListener,
        AMap.OnMarkerClickListener, AMap.OnInfoWindowClickListener, AMap.InfoWindowAdapter, RouteSearch.OnRouteSearchListener {

    private ImageView IV_back;
    private AutoCompleteTextView Location;// 输入当前位置
    private AutoCompleteTextView Destination;// 输入当前位置


    private AMap aMap;
    private MapView mapView;
    private Context mContext;
    private RouteSearch mRouteSearch;
    private DriveRouteResult mDriveRouteResult;
    private BusRouteResult mBusRouteResult;
    private WalkRouteResult mWalkRouteResult;
    private Double sLati,sLong,eLati,eLong;//117.06452161073683  39.24267365196802崇礼
    private LatLonPoint mStartPoint;//起点，116.335891,39.942295
    private LatLonPoint mEndPoint;//终点，116.481288,39.995576
    //private LatLonPoint mStartPoint = new LatLonPoint(39.942295, 116.335891);//起点，116.335891,39.942295
    //private LatLonPoint mEndPoint = new LatLonPoint(39.995576, 116.481288);//终点，116.481288,39.995576
    private String mCurrentCityName = "天津市";
    private final int ROUTE_TYPE_BUS = 1;
    private final int ROUTE_TYPE_DRIVE = 2;
    private final int ROUTE_TYPE_WALK = 3;


    private LinearLayout mBusResultLayout;
    private RelativeLayout mBottomLayout;
    private TextView mRotueTimeDes, mRouteDetailDes,routeSourch;
    private ImageView mBus;
    private ImageView mDrive;
    private ImageView mWalk;
    private ListView mBusResultList;
    private ProgressDialog progDialog = null;// 搜索时进度条


    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.route_guide_activity);

        mContext = this.getApplicationContext();
        mapView = (MapView) findViewById(R.id.route_map);
        mapView.onCreate(bundle);// 此方法必须重写
        init();

        IV_back = (ImageView) findViewById(R.id.back2_gotomap);
        Location = (AutoCompleteTextView) findViewById(R.id.location);
        Destination = (AutoCompleteTextView) findViewById(R.id.destination);
        routeSourch= (TextView) findViewById(R.id.route_Search);

        IV_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RouteGuideActivity.this, GotoMap.class);
                startActivity(intent);
            }
        });
        Location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RouteGuideActivity.this, InputTip.class);
                startActivityForResult(intent, 1);
            }
        });
        Destination.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RouteGuideActivity.this, InputTip.class);
                startActivityForResult(intent, 3);
            }
        });
        routeSourch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((!Location.getText().toString().equals("")) && (!Destination.getText().toString().equals(""))){
                    setfromandtoMarker();
                    onBusClick(mapView);//默认为公交查询线路
                }else{
                    routeSourch.setClickable(false);//设置按钮状态
                    //Toast.makeText(RouteGuideActivity.this, "请输入起点和终点！", Toast.LENGTH_SHORT).show();//输入为空时进行提示
                }
            }
        });
    }
    /*
       *
       * requestCode  请求标识
       * resultCode第二个页面返回的标志
       *
       * data第二个页面回传的数据
        */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == 2) {
            Bundle bundle=data.getExtras();
            String content=bundle.getString("content");
             sLati=bundle.getDouble("la");
             sLong=bundle.getDouble("lo");
           // Toast.makeText(RouteGuideActivity.this, "回传值111："+content+sLati+",\n"+sLong, Toast.LENGTH_SHORT).show();
            Location.setText(content);
        }
        if (requestCode == 3 && resultCode == 2) {
            Bundle bundle=data.getExtras();
            String content=bundle.getString("content");
            eLati=bundle.getDouble("la");
            eLong=bundle.getDouble("lo");
            //Toast.makeText(RouteGuideActivity.this, "回传值222："+content+eLati+eLong, Toast.LENGTH_SHORT).show();
           Destination.setText(content);
        }
        if ((!Location.getText().toString().equals("")) && (!Destination.getText().toString().equals(""))) {//启动地图,路线规划
            routeSourch.setClickable(true);
            mStartPoint=new LatLonPoint(sLong,sLati);//输入起点坐标
            mEndPoint = new LatLonPoint(eLong,eLati);//输入终点坐标
            //mStartPoint=new LatLonPoint(39.242044,117.065592);
            //mEndPoint = new LatLonPoint(39.20727,117.116154);
            setfromandtoMarker();
            onBusClick(mapView);//默认为公交查询线路
            //ToastUtil.show(mContext, "回传值333："+mStartPoint+",\n"+mEndPoint+Location.getText().toString()+",\n"+Destination.getText());
        }

    }
    private void setfromandtoMarker() {//定位搜索

        aMap.addMarker(new MarkerOptions()
                .position(AMapUtil.convertToLatLng(mStartPoint))
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.start)));
        aMap.addMarker(new MarkerOptions()
                .position(AMapUtil.convertToLatLng(mEndPoint))
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.end)));
    }
    /**
     * 初始化AMap对象
     */
    private void init() {
        if (aMap == null) {
            aMap = mapView.getMap();
        }
        registerListener();
        mRouteSearch = new RouteSearch(this);
        mRouteSearch.setRouteSearchListener(this);
        mBottomLayout = (RelativeLayout) findViewById(R.id.bottom_layout);
        mBusResultLayout = (LinearLayout) findViewById(R.id.bus_result);
        mRotueTimeDes = (TextView) findViewById(R.id.firstline);
        mRouteDetailDes = (TextView) findViewById(R.id.secondline);
        mDrive = (ImageView)findViewById(R.id.route_drive);
        mBus = (ImageView)findViewById(R.id.route_bus);
        mWalk = (ImageView)findViewById(R.id.route_walk);
        mBusResultList = (ListView) findViewById(R.id.bus_result_list);
    }
    /**
     * 注册监听
     */
    private void registerListener() {
        aMap.setOnMapClickListener( RouteGuideActivity.this);
        aMap.setOnMarkerClickListener(RouteGuideActivity.this);
        aMap.setOnInfoWindowClickListener( RouteGuideActivity.this);
        aMap.setInfoWindowAdapter(RouteGuideActivity.this);
    }
    @Override
    public View getInfoContents(Marker arg0) {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public View getInfoWindow(Marker arg0) {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public void onInfoWindowClick(Marker arg0) {
        // TODO Auto-generated method stub
    }
    @Override
    public boolean onMarkerClick(Marker arg0) {
        // TODO Auto-generated method stub
        return false;
    }
    @Override
    public void onMapClick(LatLng arg0) {
        // TODO Auto-generated method stub
    }
    public void onBusClick(View view) {
        searchRouteResult(ROUTE_TYPE_BUS, RouteSearch.BusDefault);
        mDrive.setImageResource(R.drawable.route_drive_normal);
        mBus.setImageResource(R.drawable.bus2);
        mWalk.setImageResource(R.drawable.route_walk_normal);
        mapView.setVisibility(View.GONE);
        mBusResultLayout.setVisibility(View.VISIBLE);
    }
    public void onDriveClick(View view) {
        searchRouteResult(ROUTE_TYPE_DRIVE, RouteSearch.DrivingDefault);
        mDrive.setImageResource(R.drawable.car);
        mBus.setImageResource(R.drawable.route_bus_normal);
        mWalk.setImageResource(R.drawable.route_walk_normal);
        mapView.setVisibility(View.VISIBLE);
        mBusResultLayout.setVisibility(View.GONE);
    }

    public void onWalkClick(View view) {
        searchRouteResult(ROUTE_TYPE_WALK, RouteSearch.WalkDefault);
        mDrive.setImageResource(R.drawable.route_drive_normal);
        mBus.setImageResource(R.drawable.route_bus_normal);
        mWalk.setImageResource(R.drawable.man);
        mapView.setVisibility(View.VISIBLE);
        mBusResultLayout.setVisibility(View.GONE);
    }
    /**
     * 开始搜索路径规划方案
     */
    public void searchRouteResult(int routeType, int mode) {
        if (mStartPoint == null) {
            ToastUtil.show(mContext, "起点未设置");
            return;
        }
        if (mEndPoint == null) {
            ToastUtil.show(mContext, "终点未设置");
        }
        showProgressDialog();
        final RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(
                mStartPoint, mEndPoint);
        if (routeType == ROUTE_TYPE_BUS) {// 公交路径规划
            RouteSearch.BusRouteQuery query = new RouteSearch.BusRouteQuery(fromAndTo, mode,
                    mCurrentCityName, 0);// 第一个参数表示路径规划的起点和终点，第二个参数表示公交查询模式，第三个参数表示公交查询城市区号，第四个参数表示是否计算夜班车，0表示不计算
            mRouteSearch.calculateBusRouteAsyn(query);// 异步路径规划公交模式查询
        } else if (routeType == ROUTE_TYPE_DRIVE) {// 驾车路径规划
            RouteSearch.DriveRouteQuery query = new RouteSearch.DriveRouteQuery(fromAndTo, mode, null,
                    null, "");// 第一个参数表示路径规划的起点和终点，第二个参数表示驾车模式，第三个参数表示途经点，第四个参数表示避让区域，第五个参数表示避让道路
            mRouteSearch.calculateDriveRouteAsyn(query);// 异步路径规划驾车模式查询
        } else if (routeType == ROUTE_TYPE_WALK) {// 步行路径规划
            RouteSearch.WalkRouteQuery query = new RouteSearch.WalkRouteQuery(fromAndTo, mode);
            mRouteSearch.calculateWalkRouteAsyn(query);// 异步路径规划步行模式查询
        }
    }
    @Override
    public void onBusRouteSearched(BusRouteResult result, int errorCode) {
        dissmissProgressDialog();
        mBottomLayout.setVisibility(View.GONE);
        aMap.clear();// 清理地图上的所有覆盖物
        if (errorCode == 1000) {
            if (result != null && result.getPaths() != null) {
                if (result.getPaths().size() > 0) {
                    mBusRouteResult = result;
                    BusResultListAdapter mBusResultListAdapter = new BusResultListAdapter(mContext, mBusRouteResult);
                    mBusResultList.setAdapter(mBusResultListAdapter);
                } else if (result != null && result.getPaths() == null) {
                    ToastUtil.show(mContext, R.string.no_result);
                }
            } else {
                ToastUtil.show(mContext, R.string.no_result);
            }
        } else {
            ToastUtil.showerror(this.getApplicationContext(), errorCode);
        }
    }
    @Override
    public void onDriveRouteSearched(DriveRouteResult result, int errorCode) {
        dissmissProgressDialog();
        aMap.clear();// 清理地图上的所有覆盖物
        if (errorCode == 1000) {
            if (result != null && result.getPaths() != null) {
                if (result.getPaths().size() > 0) {
                    mDriveRouteResult = result;
                    final DrivePath drivePath = mDriveRouteResult.getPaths()
                            .get(0);
                    DrivingRouteOverlay drivingRouteOverlay = new DrivingRouteOverlay(
                            mContext, aMap, drivePath,
                            mDriveRouteResult.getStartPos(),
                            mDriveRouteResult.getTargetPos(), null);
                    drivingRouteOverlay.setNodeIconVisibility(false);//设置节点marker是否显示
                    //drivingRouteOverlay.setIsColorfulline(true);//是否用颜色展示交通拥堵情况，默认true
                    drivingRouteOverlay.removeFromMap();
                    drivingRouteOverlay.addToMap();
                    drivingRouteOverlay.zoomToSpan();
                    mBottomLayout.setVisibility(View.VISIBLE);
                    int dis = (int) drivePath.getDistance();
                    int dur = (int) drivePath.getDuration();
                    String des = AMapUtil.getFriendlyTime(dur)+"("+AMapUtil.getFriendlyLength(dis)+")";
                    mRotueTimeDes.setText(des);
                    mRouteDetailDes.setVisibility(View.VISIBLE);
                    int taxiCost = (int) mDriveRouteResult.getTaxiCost();
                    mRouteDetailDes.setText("打车约"+taxiCost+"元");
                    mBottomLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(mContext,
                                    DriveRouteDetailActivity.class);
                            intent.putExtra("drive_path", drivePath);
                            intent.putExtra("drive_result",
                                    mDriveRouteResult);
                            startActivity(intent);
                        }
                    });
                } else if (result != null && result.getPaths() == null) {
                    ToastUtil.show(mContext, R.string.no_result);
                }

            } else {
                ToastUtil.show(mContext, R.string.no_result);
            }
        } else {
            ToastUtil.showerror(this.getApplicationContext(), errorCode);
        }
    }
    @Override
    public void onWalkRouteSearched(WalkRouteResult result, int errorCode) {
        dissmissProgressDialog();
        aMap.clear();// 清理地图上的所有覆盖物
        if (errorCode == 1000) {
            if (result != null && result.getPaths() != null) {
                if (result.getPaths().size() > 0) {
                    mWalkRouteResult = result;
                    final WalkPath walkPath = mWalkRouteResult.getPaths()
                            .get(0);
                    WalkRouteOverlay walkRouteOverlay = new WalkRouteOverlay(
                            this, aMap, walkPath,
                            mWalkRouteResult.getStartPos(),
                            mWalkRouteResult.getTargetPos());
                    walkRouteOverlay.removeFromMap();
                    walkRouteOverlay.addToMap();
                    walkRouteOverlay.zoomToSpan();
                    mBottomLayout.setVisibility(View.VISIBLE);
                    int dis = (int) walkPath.getDistance();
                    int dur = (int) walkPath.getDuration();
                    String des = AMapUtil.getFriendlyTime(dur)+"("+AMapUtil.getFriendlyLength(dis)+")";
                    mRotueTimeDes.setText(des);
                    mRouteDetailDes.setVisibility(View.GONE);
                    mBottomLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(mContext,
                                    WalkRouteDetailActivity.class);
                            intent.putExtra("walk_path", walkPath);
                            intent.putExtra("walk_result",
                                    mWalkRouteResult);
                            startActivity(intent);
                        }
                    });
                } else if (result != null && result.getPaths() == null) {
                    ToastUtil.show(mContext, R.string.no_result);
                }
            } else {
                ToastUtil.show(mContext, R.string.no_result);
            }
        } else {
            ToastUtil.showerror(this.getApplicationContext(), errorCode);
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
        progDialog.setCancelable(true);
        progDialog.setMessage("正在搜索");
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
     * 方法必须重写
     */
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }
    /**
     * 方法必须重写
     */
    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }
    /**
     * 方法必须重写
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }
    /**
     * 方法必须重写
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }
    @Override
    public void onRideRouteSearched(RideRouteResult arg0, int arg1) {
        // TODO Auto-generated method stub
    }
}