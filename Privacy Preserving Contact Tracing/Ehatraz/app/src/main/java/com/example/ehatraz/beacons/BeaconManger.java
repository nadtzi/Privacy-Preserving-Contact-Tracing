package com.example.ehatraz.beacons;

import android.app.Activity;
import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.RemoteException;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.example.ehatraz.MainActivity;
import com.example.ehatraz.R;
import com.example.ehatraz.storage.Key;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.BeaconTransmitter;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.startup.BootstrapNotifier;

import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import android.app.Application;
import android.app.Notification;

import org.altbeacon.bluetooth.BluetoothMedic;

import static java.security.AccessController.getContext;

public class BeaconManger extends Application implements  BeaconConsumer {

    public org.altbeacon.beacon.BeaconManager beaconManager;
    private List<Beacon>  beaconsList = new ArrayList<>();
    private List<Key> keys = new ArrayList<>() ;
    private Context context;
    MainActivity mainActivity;
    protected static final String TAG = "EhatrazBeaconManger";

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public BeaconManger(Context context,org.altbeacon.beacon.BeaconManager beaconManager) throws Exception {

        this.context = context;
        this.beaconManager = beaconManager;

        Log.i(TAG,context.getPackageName()+"context");
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("s:0-1=fd6f,p:-:-59,i:2-17,d:18-21"));

        Notification.Builder builder = new Notification.Builder(context);
        builder.setContentTitle("Beacon Scope Active");
        builder.setContentText("Tap and then hit X to exit.");

        if (!BeaconManager.getInstanceForApplication(context).isAnyConsumerBound()) {
            BeaconManager.getInstanceForApplication(context).enableForegroundServiceScanning(builder.build(), 456);
        }
        if (Build.VERSION.SDK_INT >= 21) {
            BluetoothMedic.getInstance().enablePowerCycleOnFailures(context);
        }

        beaconManager.setForegroundBetweenScanPeriod(0);
        beaconManager.setForegroundScanPeriod(1100);


    }



    @Override
    public void onBeaconServiceConnect() {
        beaconManager.removeAllRangeNotifiers();
        beaconManager.addRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                if (beacons.size() > 0) {
                    Log.i(TAG, "Discovered Beacon: "+beacons.iterator().next().getIdentifier(0).toHexString().replace("-",""));
                    if(!beaconsList.contains(beacons.iterator().next()))
                    {
                        keys.add(new Key(beacons.iterator().next().getIdentifier(0).toHexString().replace("-","")));
                    }
                }
            }
        });


        try {
            beaconManager.startRangingBeaconsInRegion(new Region("all exposure beacons", (String) null));
        } catch (RemoteException e) {
            Log.i(TAG, e.toString());
        }
    }


    public void startTrasmit(byte [] t)
    {
        String uuidString = "01020304-0506-0708-090a-0b0c0d0e0f10";

        final Beacon beacon = new Beacon.Builder()
                .setId1(uuidString)
                .setDataFields(Arrays.asList(new Long[]{20l}))
                .setIdentifiers(Arrays.asList(new Identifier[]{Identifier.fromBytes(t, 0, t.length, false)}))
                .build();

        BeaconParser beaconParser = new BeaconParser().setBeaconLayout("s:0-1=fd6f,p:-:-59,i:2-17,d:18-21");
        BeaconTransmitter beaconTransmitter = new BeaconTransmitter(context, beaconParser);
        beaconTransmitter.startAdvertising(beacon, new AdvertiseCallback() {
            @Override
            public void onStartFailure(int errorCode) {
                Log.e(TAG, "Advertisement start failed with code: " + errorCode);
            }

            @Override
            public void onStartSuccess(AdvertiseSettings settingsInEffect) {
                Log.d(TAG, "Advertising Beacon: "+ beacon.getIdentifier(0).toHexString().replace("-",""));
            }
        });
    }
    public void unbind()
    {
        beaconManager.unbind(this);
    }
    public void bind()
    {
        beaconManager.bind(this);
    }


}
