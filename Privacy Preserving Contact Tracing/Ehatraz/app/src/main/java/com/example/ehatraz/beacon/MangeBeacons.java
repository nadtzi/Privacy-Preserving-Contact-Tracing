package com.example.ehatraz.beacon;

import android.app.Notification;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseSettings;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.RemoteException;
import android.util.Log;

import com.example.ehatraz.storage.Key;
import com.example.ehatraz.storage.StorgeManger;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.BeaconTransmitter;
import org.altbeacon.beacon.Identifier;

import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;
import org.altbeacon.bluetooth.BluetoothMedic;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class MangeBeacons implements BeaconConsumer {
    protected static final String TAG = "MangeBeacons";
    private String uuidString = "01020304-0506-0708-090a-0b0c0d0e0f10";

    private Context mContext;
    private static MangeBeacons instance = null;
    StorgeManger storgeManger ;
    public ArrayList<Key> keys;
    private BeaconManager beaconManager = null;
    private List<Beacon> beaconsList = new ArrayList<>();

    private MangeBeacons(Context context)
    {
        this.mContext = context;
        // geting instance from beaconManger
        beaconManager = BeaconManager.getInstanceForApplication(context);
        // set up the beacon type need to be advertised or scanned for
        beaconManager.getBeaconParsers().add(new BeaconParser()
                .setBeaconLayout("s:0-1=fd6f,p:-:-59,i:2-17,d:18-21"));
        storgeManger = StorgeManger.getInstance(mContext);

//        Notification.Builder builder = new Notification.Builder(context);
//        builder.setContentTitle("Scanning for Beacons");
//        builder.setContentText("Tap and then hit X to exit.");
//
//        if (!BeaconManager.getInstanceForApplication(context).isAnyConsumerBound()) {
//            BeaconManager.getInstanceForApplication(context).enableForegroundServiceScanning(builder.build(), 456);
//        }
//        if (Build.VERSION.SDK_INT >= 21) {
//            BluetoothMedic.getInstance().enablePowerCycleOnFailures(context);
//        }
//
//        beaconManager.setForegroundBetweenScanPeriod(0);
//        beaconManager.setForegroundScanPeriod(1100);

        keys = (ArrayList<Key>) storgeManger.readBeacons();
        for(Key k: keys)
            System.out.println("the keys are: "+k.getKey());

    }
    // singleton design pattren
    public static MangeBeacons getInstance(Context context)
    {
        if(instance == null)
            instance = new MangeBeacons(context);
        return instance;
    }
    // used to scan for beacons
    @Override
    public void onBeaconServiceConnect() {
        startScaning();
    }


    @Override
    public Context getApplicationContext() {
        return mContext.getApplicationContext();
    }

    @Override
    public void unbindService(ServiceConnection serviceConnection) {
        mContext.unbindService(serviceConnection);
    }
    public void startScaning()
    {

        beaconManager.removeAllRangeNotifiers();
        beaconManager.addRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                if (beacons.size() > 0 ) {
                    Beacon itr = beacons.iterator().next();
                    boolean contain = Key.contains(itr.getIdentifier(0).toHexString().replace("-", ""),keys);
                    if(!contain) {
                        keys.add(new Key(itr.getIdentifier(0).toHexString().replace("-", "")));
                        Log.i(TAG, "The first beacon I see is about " + beacons.iterator().next().getIdentifier(0));
                    }
                }
            }
        });

        try {
            instance.beaconManager.startRangingBeaconsInRegion(new Region("all exposure beacons", (String) null));
        } catch (RemoteException e) {
            Log.i(TAG, e.toString());
        }
    }
    public void startTrasmit(byte [] t)
    {

        final Beacon beacon = new Beacon.Builder()
                .setId1(uuidString)
                .setDataFields(Arrays.asList(new Long[]{20l}))
                .setIdentifiers(Arrays.asList(new Identifier[]{Identifier.fromBytes(t, 0, t.length, false)}))
                .build();

        BeaconParser beaconParser = new BeaconParser().setBeaconLayout("s:0-1=fd6f,p:-:-59,i:2-17,d:18-21");
        BeaconTransmitter beaconTransmitter = new BeaconTransmitter(mContext, beaconParser);
        beaconTransmitter.startAdvertising(beacon, advertiseCallback(beacon));
    }
    public AdvertiseCallback advertiseCallback(Beacon beacon)
    {
        AdvertiseCallback callback = new AdvertiseCallback() {
            @Override
            public void onStartFailure(int errorCode) {
                Log.e(TAG, "Advertisement start failed with code: " + errorCode);
            }

            @Override
            public void onStartSuccess(AdvertiseSettings settingsInEffect) {
                Log.d(TAG, "Advertising Beacon: " + beacon.getIdentifier(0).toHexString().replace("-", ""));
            }
        };
        return callback;
    }
    @Override
    public boolean bindService(Intent intent, ServiceConnection serviceConnection, int i) {
        return mContext.bindService(intent, serviceConnection, i);
    }
    public void unbindMangeBecons()
    {
        storgeManger.writeBeacons(keys);
        instance.beaconManager.unbind(this);
    }
    public void bindMangeBeacons()
    {

        keys = (ArrayList<Key>) storgeManger.readBeacons();
        instance.beaconManager.bind(this);
    }
    /*
    ---- methods in this class
         +startScan():void
         +startTransmiting():void
         +bindMangeBeacons():void
         +unbindMangeBeacons():void
         +advertisCallback(becaon: Beacon):AdvertiseCallback

    ---- varabiles
         - context: Context
         - becaonManger: BecconManger
         - instance: MangeBeacons
    -----relations
           with StrogeManger;
     */
}
