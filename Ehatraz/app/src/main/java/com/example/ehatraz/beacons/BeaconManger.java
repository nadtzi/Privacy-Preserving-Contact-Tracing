package com.example.ehatraz.beacons;

import android.app.Application;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseSettings;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.google.crypto.tink.subtle.Hkdf;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.BeaconTransmitter;
import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.startup.BootstrapNotifier;

import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.util.Arrays;

public class BeaconManger extends Application implements BootstrapNotifier {

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public BeaconManger(Context c) throws Exception
    {
        String uuidString = "01020304-0506-0708-090a-0b0c0d0e0f10";
        String s = "data,mm";
        SecureRandom random = new SecureRandom();
        byte t[] = new byte[20];
        byte[] x = Hkdf.computeHkdf("HmacSha256",t,null,null,20);
        random.nextBytes(t);
        ByteBuffer buffer = ByteBuffer.wrap(t);


        final Beacon beacon = new Beacon.Builder()
                .setId1(uuidString)
                .setDataFields(Arrays.asList(new Long[] {buffer.getLong()}))
                .build();
// This beacon layout is for the Exposure Notification service Bluetooth Spec
        BeaconParser beaconParser = new BeaconParser()
                .setBeaconLayout("s:0-1=fd6f,p:-:-59,i:2-17,d:18-21");
        BeaconTransmitter beaconTransmitter = new
                BeaconTransmitter(c, beaconParser);
        beaconTransmitter.startAdvertising(beacon, new AdvertiseCallback() {
            @Override
            public void onStartFailure(int errorCode) {
                Log.e("TAG", "Advertisement start failed with code: "+errorCode);
            }

            @Override
            public void onStartSuccess(AdvertiseSettings settingsInEffect) {
                Log.i("TAG", beacon.getDataFields()+"");

            }
        });
    }
    @Override
    public void didEnterRegion(Region region) {

    }

    @Override
    public void didExitRegion(Region region) {

    }

    @Override
    public void didDetermineStateForRegion(int i, Region region) {

    }
    public static String byteArrayToHex(byte[] a) {
        StringBuilder sb = new StringBuilder(a.length * 2);
        for(byte b: a)
            sb.append(String.format("%02x", b));
        return sb.toString();
    }
}
