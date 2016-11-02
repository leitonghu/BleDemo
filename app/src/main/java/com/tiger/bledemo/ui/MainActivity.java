package com.tiger.bledemo.ui;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.tiger.bledemo.service.BluetoothLeService;
import com.tiger.bledemo.R;
import com.tiger.bledemo.util.Tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    private BluetoothAdapter mBluetoothAdapter;
    private static final int REQUEST_ENABLE_BT = 1;
    private Handler mHandler;
    private boolean mConnected = false;
    private boolean isFindDevice = false;
    private boolean isServiceBind = false;
    private boolean mScanning;
    private static final String TAG = "----MyBlooth----";
    // 10秒后停止查找搜索.
    private static final long SCAN_PERIOD = 10000;

    private String mDeviceName;
    private String mDeviceAddress;
    private BluetoothLeService mBluetoothLeService;

    private ArrayList<ArrayList<BluetoothGattCharacteristic>> mGattCharacteristics =
            new ArrayList<ArrayList<BluetoothGattCharacteristic>>();

    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {

        @Override
        public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
//                    mLeDeviceListAdapter.addDevice(device);
//                    mLeDeviceListAdapter.notifyDataSetChanged();
                    Log.i(TAG, "Name = " + device.getName());
                    Log.i(TAG, "Address = " + device.getAddress());
                    Log.i(TAG, "BondState = " + device.getBondState());
                    Log.i(TAG, "Type = " + device.getType());
                    Log.i(TAG, "Uuids = " + device.getUuids());
                    Log.i(TAG, "BluetoothClass = " + device.getBluetoothClass());

                    if (device != null) {
                        if (device.getName() != null) {
                            if (device.getName().equals("AWEI)8")) {

                                isFindDevice = true;
//                    			Toast.makeText(BleLipidActivity.this, "发现设备", Toast.LENGTH_SHORT).show();
                                Log.i(TAG,"-----------Info Start----------");
                                Log.i(TAG, "Name = " + device.getName());
                                Log.i(TAG, "Address = " + device.getAddress());
                                Log.i(TAG, "BondState = " + device.getBondState());
                                Log.i(TAG, "Type = " + device.getType());
                                Log.i(TAG, "Uuids = " + device.getUuids());
                                Log.i(TAG, "BluetoothClass = " + device.getBluetoothClass());
                                Log.i(TAG,"-----------Info End----------");
                                if (mScanning) {
                                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                                    mScanning = false;
                                }
//                           Toast.makeText(BleLipidActivity.this, "连接设备", Toast.LENGTH_SHORT).show();
                             /*if(mBluetoothLeService != null && mServiceConnection != null)
                            	 unbindService(mServiceConnection);*/
                                connectBlooth(device);
                            }
                        }
                    }
                }
            });
        }
    };

    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {

            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                Toast.makeText(MainActivity.this, "不能初始化蓝牙", Toast.LENGTH_SHORT).show();
//                finish();
                onBackPressed();
            }
            // Automatically connects to the device upon successful start-up initialization.
            mBluetoothLeService.connect(mDeviceAddress);

            isServiceBind = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                mConnected = true;
                isFindDevice = true;
//                updateConnectionState(R.string.connected);
//                connectStatus.setText("血脂仪连接成功");
//                loading_pb.setVisibility(View.INVISIBLE);
                Tools.toastInCenter(MainActivity.this, "连接成功");
//                setBleAnim(true);
//                invalidateOptionsMenu();
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                mConnected = false;
                isFindDevice = false;
//                updateConnectionState(R.string.disconnected);
//                connectStatus.setText("血脂仪连接中断");
//                connect.setText("重新连接血脂仪");
//                Tools.toastInCenter(BleLipidActivity.this, "连接中断");
//                invalidateOptionsMenu();
                unbindService(mServiceConnection);

//                setBleAnim(false);
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                // Show all the supported services and characteristics on the user interface.
//                Tools.toastInCenter(BleLipidActivity.this, "发现服务");
                displayGattServices(mBluetoothLeService.getSupportedGattServices());
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                try {
//                    displayData(intent.getStringExtra(BluetoothLeService.EXTRA_DATA));
                } catch (Exception e) {
                    e.printStackTrace();
//                    checkStatus.setText("数据监测失败");
                }
            }
        }

    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initUI();
        checkBle();
        checkBleOpen();
        scanLeDevice(true);
        setRegister();
    }

    private void setRegister() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        registerReceiver(receiver,intentFilter);
    }

    private void initUI(){

        mHandler = new Handler();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    /**
     * 检查蓝牙相关配置，是否有蓝牙，是否是蓝牙4.0
     */
    private void checkBle(){
        // 检查当前手机是否支持ble 蓝牙,如果不支持退出程序
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
            finish();
        }

        // 初始化 Bluetooth adapter, 通过蓝牙管理器得到一个参考蓝牙适配器(API必须在以上android4.3或以上和版本)
        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            mBluetoothAdapter = bluetoothManager.getAdapter();
        }

        // 检查设备上是否支持蓝牙4.0
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, R.string.error_bluetooth_not_supported, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
    }

    /**
     * 为了确保设备上蓝牙能使用, 如果当前蓝牙设备没启用,弹出对话框向用户要求授予权限来启用
     */
    private void checkBleOpen() {
        if (!mBluetoothAdapter.isEnabled()) {
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        }
    }


    private void scanLeDevice(final boolean enable) {

        setConnectUI(enable);

        if (enable) {
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {

                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
//                    invalidateOptionsMenu();
                    if (!isFindDevice) {
                        if (mScanning) {
                            Tools.toastInCenter(MainActivity.this, R.string.scan_time_out);
//                            connect.setText("重新连接血脂仪");
//                            loading_pb.setVisibility(View.INVISIBLE);
                        }
                    }
                    isFindDevice = false;
                    mScanning = false;
                }
            }, SCAN_PERIOD);//在界面做一个十秒扫描的提示

            mScanning = true;
            mBluetoothAdapter.startLeScan(mLeScanCallback);
//            unbindService(mServiceConnection);
        } else {
            mScanning = false;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }
//        invalidateOptionsMenu();
    }

    private void setConnectUI(boolean isConnect) {
        if (isConnect) {
//            connect.setText(R.string.disconnect_device);
//            loading_pb.setVisibility(View.VISIBLE);
        } else {
//            connect.setText(R.string.connect_device);
//            loading_pb.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // User chose not to enable Bluetooth.
        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
            finish();
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void connectBlooth(BluetoothDevice device) {
        mDeviceName = device.getName();
        mDeviceAddress = device.getAddress();
//    	mScanning = false;
        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
    }



    private void displayGattServices(List<BluetoothGattService> gattServices) {
        if (gattServices == null) return;
        String serviceUUid = null;
        String charUUid = null;
        String serviceCharacterValue = null;
        String charCharacterValue = null;

        ArrayList<ArrayList<HashMap<String, String>>> gattCharacteristicData
                = new ArrayList<ArrayList<HashMap<String, String>>>();
        mGattCharacteristics = new ArrayList<ArrayList<BluetoothGattCharacteristic>>();

        // Loops through available GATT Services.
        for (BluetoothGattService gattService : gattServices) {

            serviceUUid = gattService.getUuid().toString();
            serviceCharacterValue = serviceUUid.substring(4, 8);

//            if (serviceCharacterValue.toLowerCase().equals("f808")) {

                ArrayList<HashMap<String, String>> gattCharacteristicGroupData =
                        new ArrayList<HashMap<String, String>>();
                List<BluetoothGattCharacteristic> gattCharacteristics =
                        gattService.getCharacteristics();
                ArrayList<BluetoothGattCharacteristic> charas =
                        new ArrayList<BluetoothGattCharacteristic>();

                // Loops through available Characteristics.
                for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                    charas.add(gattCharacteristic);
                    HashMap<String, String> currentCharaData = new HashMap<String, String>();
                    charUUid = gattCharacteristic.getUuid().toString();
//                    currentCharaData.put(
//                            LIST_NAME, SampleGattAttributes.lookup(uuid, unknownCharaString));
//                    currentCharaData.put(LIST_UUID, uuid);

                    Log.i(TAG, "ServiceUUID: " + serviceUUid + " CharUUID:" + charUUid);
//                  gattCharacteristicGroupData.add(currentCharaData);
                    charCharacterValue = charUUid.substring(4, 8);
                    if (charCharacterValue.toLowerCase().equals("fa52")) {
                        mBluetoothLeService.setCharacteristicNotification(
                                gattCharacteristic, true);
                    }
                    if (charCharacterValue.toLowerCase().equals("fa18")) {
//                        operatGattCharacteristic = gattCharacteristic;
                    }
                }
                mGattCharacteristics.add(charas);
                gattCharacteristicData.add(gattCharacteristicGroupData);
//            }
        }
    }












    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
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
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
