package com.example.ehatraz

import android.Manifest
import android.bluetooth.le.AdvertiseCallback
import android.bluetooth.le.AdvertiseSettings
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.RemoteException
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.ehatraz.beacon.MangeBeacons
import com.example.ehatraz.firebase.FirebaseManger
import com.example.ehatraz.storage.StorgeManger

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_home.*
import org.altbeacon.beacon.*
import org.altbeacon.beacon.BeaconManager.getInstanceForApplication
import java.security.SecureRandom
import java.util.*

class MainActivity() : AppCompatActivity(){


    var beacons: MangeBeacons? = null;
    var store: StorgeManger? = null;
    var firebase: FirebaseManger? = null;
    val key = "0102030405060708090a0b0c0d0e0f19"

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        requetPermiton()
        requetPermiton2()

        beacons = MangeBeacons.getInstance(applicationContext)
        store = StorgeManger.getInstance(applicationContext)
        firebase = FirebaseManger.getInstance(applicationContext)
        val byteArray = ByteArray(16);
        val s = SecureRandom();
        s.nextBytes(byteArray);
        beacons?.bindMangeBeacons()
        beacons?.startTrasmit(byteArray);
        firebase?.read(this);
        firebase?.addKeys();

        val reportPostive = ReportPostive()
        val home = HomeFragment()
        val info = InfoFragment()
        val heatMap = HeatMapFragment()
        val currentFragment = setCurrentFragment(home)

        //mScheduler = getSystemService(JOB_SCHEDULER_SERVICE) as JobScheduler?;

        bottomNavigationView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.report -> setCurrentFragment(reportPostive)
                R.id.home -> setCurrentFragment(home)
                R.id.info -> setCurrentFragment(info)
                R.id.heatmap -> setCurrentFragment(heatMap)
            }
            true
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        beacons?.unbindMangeBecons()

    }

    override fun onResume() {
        super.onResume()
        beacons?.bindMangeBeacons();
    }

    override fun onRestart() {
        super.onRestart()
        beacons?.unbindMangeBecons()

    }

    private fun setCurrentFragment(fragment: Fragment) =
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.flFragment, fragment)
            commit()
        }

    private fun requetPermiton()
    {
        val permissionCheck = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_BACKGROUND_LOCATION
        );
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
                )){
                Toast.makeText(
                    this,
                    "The permission to get BLE location data is required",
                    Toast.LENGTH_SHORT
                ).show()
            }else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(
                        arrayOf(

                            Manifest.permission.ACCESS_BACKGROUND_LOCATION
                        ), 1
                    )
                }
            }
        }

    }
    private fun requetPermiton2()
    {
        val permissionCheck = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        );
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )){
                Toast.makeText(
                    this,
                    "The permission to get BLE location data is required",
                    Toast.LENGTH_SHORT
                ).show()
            }else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(
                        arrayOf(
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_BACKGROUND_LOCATION
                        ), 1
                    )
                }
            }
        }

    }


}