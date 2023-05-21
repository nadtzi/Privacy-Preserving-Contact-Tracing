package com.example.ehatraz

import android.Manifest
import android.R.attr
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.example.ehatraz.firebase.FirebaseManger
import com.example.ehatraz.gps.GeofenceHelper
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.synthetic.main.fragment_report_postive.*
import kotlinx.android.synthetic.main.login_dailog.view.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ReportPostive.newInstance] factory method to
 * create an instance of this fragment.
 */
class ReportPostive : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var geofencingClient: GeofencingClient? = null
    private var geofenceHelper: GeofenceHelper? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)

        button.setOnClickListener{
            val mDailog = LayoutInflater.from(context).inflate(R.layout.login_dailog, null)
            val mBulider = AlertDialog.Builder(context).setView(mDailog).setTitle("Login")
            val mAler = mBulider.show();
            mDailog.login.setOnClickListener{
                mAler.dismiss();
                val username = mDailog.username.text.toString()
                val password = mDailog.password.text.toString();
                val firebase = FirebaseManger.getInstance(context?.applicationContext)
                val user = firebase.signIn(password, username);
                if( user != null)
                    Toast.makeText(this.context, "Logged In scussfuly", Toast.LENGTH_LONG).show();
                else
                    Toast.makeText(this.context, "not Logged In scussfuly", Toast.LENGTH_LONG).show();



            }
        }
        quarntineBtn.setOnClickListener{
            val mDailog = LayoutInflater.from(context).inflate(R.layout.geofence_dailog, null)
            val mBulider = AlertDialog.Builder(context).setView(mDailog).setTitle("Qurantine")
            val mAler = mBulider.show();
            mDailog.login.setOnClickListener{
                mAler.dismiss();
                val longtitud = mDailog.username.text.toString();
                val altitude = mDailog.password.text.toString();

                geofencingClient =
                    context?.let { it1 -> LocationServices.getGeofencingClient(it1.applicationContext) };
                geofenceHelper = GeofenceHelper(context);
                val temp = LatLng(25.370718,51.548885)
                addGeo(temp, 2.00000000000000000001F);


            }
        }


    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_report_postive, container, false)
    }
    fun addGeo(lating: LatLng, radius: Float)
    {
        val geofence = geofenceHelper!!.getGeofence(
            "1017",
            lating,
            attr.radius.toFloat(),
            Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_DWELL or Geofence.GEOFENCE_TRANSITION_EXIT
        )
        val geofencingRequest = geofenceHelper!!.getGeofencingRequest(geofence)
        val pendingIntent = geofenceHelper!!.pendingIntent

        if (context?.let {
                ActivityCompat.checkSelfPermission(
                    it.applicationContext,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            } != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        geofencingClient!!.addGeofences(geofencingRequest, pendingIntent)
            .addOnSuccessListener { Log.d("geofence", "onSuccess: Geofence Added...") }
            .addOnFailureListener { e ->
                val errorMessage = geofenceHelper!!.getErrorString(e)
                Log.d("geofence", "onFailure: $errorMessage")
            }
    }
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ReportPostive.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ReportPostive().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

}