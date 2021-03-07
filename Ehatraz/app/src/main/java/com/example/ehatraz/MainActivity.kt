package com.example.ehatraz

import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.ehatraz.beacons.BeaconManger
import kotlinx.android.synthetic.main.activity_main.*
import org.altbeacon.beacon.BeaconManager
import org.altbeacon.beacon.BeaconParser


class MainActivity : AppCompatActivity()  {
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val reportPostive=ReportPostive()
        val home=HomeFragment()
        val info=InfoFragment()
        val heatMap=HeatMapFragment()
        setCurrentFragment(home)
        //var beaconManager = BeaconManager.getInstanceForApplication(this);
        bottomNavigationView.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.report -> setCurrentFragment(reportPostive)
                R.id.home -> setCurrentFragment(home)
                R.id.info -> setCurrentFragment(info)
                R.id.heatmap -> setCurrentFragment(heatMap)
            }
            true
        }
        var test = BeaconManger(applicationContext)

    }
    private fun setCurrentFragment(fragment: Fragment)=
            supportFragmentManager.beginTransaction().apply {
                replace(R.id.flFragment, fragment)
                commit()
            }
}