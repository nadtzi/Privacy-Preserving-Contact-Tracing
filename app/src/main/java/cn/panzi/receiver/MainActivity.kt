package cn.panzi.receiver

//add for notification

import HKDF
import Time_HKDF
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Bundle
import android.os.RemoteException
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.recyclerview.widget.RecyclerView
import cn.panzi.receiver.adapter.BeaconListAdapter
import cn.panzi.receiver.ext.showToast
import cn.panzi.receiver.permission.RequestCallback
import cn.panzi.receiver.permission.RxPermissionRequest
import com.squareup.haha.perflib.Main
import kotlinx.android.synthetic.main.activity_main.*
import org.altbeacon.beacon.*
import java.nio.charset.StandardCharsets
import java.security.SecureRandom
import java.util.*
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity(), BeaconConsumer {

    private val PERMISSION_REQUEST_COARSE_LOCATION: Int = 1001
    private val BEACON_LAYOUT: String = "m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"
    private lateinit var beaconList: ArrayList<Beacon>

    private lateinit var beaconManager: BeaconManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()
        requestPermission()
        //test("test","hugetest")
    }




    private fun initView() {
        beaconList = ArrayList()
        recycle_view.setHasFixedSize(true)
        val linearLayoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)
        linearLayoutManager.orientation = RecyclerView.VERTICAL
        recycle_view.layoutManager = linearLayoutManager
        recycle_view.adapter = BeaconListAdapter(beaconList, this)
    }
    val tekkeys=byteArrtoLongArr(getTEK())

    private fun initBeaconManager() {

    beaconManager = BeaconManager.getInstanceForApplication(this)
        beaconManager.beaconParsers.add(BeaconParser().setBeaconLayout("m:2-3=beac,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"))
        beaconManager.bind(this)
        var texttest = "test data"


        val beacon = Beacon.Builder()
                .setId1(Identifier.fromBytes(getTEK(),0,16,false).toUuidString())
               .setId2("1")
                .setId3("2")



                .setTxPower(-59)
                .setDataFields(mutableListOf(1,2,3))
              //  .setExtraDataFields(mutableListOf(11))
                .build()

        val beaconParser = BeaconParser()
                .setBeaconLayout("m:2-3=beac,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25");
        val beaconTransmitter = BeaconTransmitter(applicationContext, beaconParser)
        beaconTransmitter.startAdvertising(beacon)



    }

    override fun onBeaconServiceConnect() {
        beaconManager.addRangeNotifier { beacons, _ ->
            beacons?.let {
                beaconList.clear()
                beaconList.addAll(beacons)
                recycle_view.adapter?.notifyDataSetChanged()
            }
        }

        try {
            beaconManager.startRangingBeaconsInRegion(Region("", null, null, null))
        } catch (e: RemoteException) {
        }
    }


    private fun requestPermission() {
        val requestPermission = RxPermissionRequest()
        requestPermission.request(this, object : RequestCallback {
            override fun onRequestPermissionSuccess() {
                initBeaconManager()
            }

            override fun onRequestPermissionFailure() {
                showToast(getString(R.string.no_location_permission))
            }

        }, android.Manifest.permission.ACCESS_COARSE_LOCATION)
    }


    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_REQUEST_COARSE_LOCATION -> if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initBeaconManager()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        beaconManager.unbind(this)
    }

    //fun main(args: Array<String>) {
    fun generateTEK():ByteArray{
        val TEK = getTEK();
        /* For whatever reason the same keys are being generated */
        println("Here is the TEK: ${TEK.contentToString()}");

        val RPIK = getKey(TEK,
                "EN-RPIK".toByteArray(Charsets.UTF_8),
                16)
        println("Here is the RPIK: ${RPIK.dk.contentToString()}");

        val AEMK = getKey(TEK,
                "EN-AEMK".toByteArray(Charsets.UTF_8),
                16)
        println("Here is the AEMK: ${AEMK.dk.contentToString()}");

        val RPI = getRPI(RPIK);
        println("Here is the RPI for this 10 minutes ${RPI.contentToString()}");

        val AEM = getAEM("sth to encrypt", AEMK.dk, RPI);
        println("Here is the AEM for this person ${AEM.contentToString()}");
        return TEK
    }

    fun getTEK(): ByteArray{
        val random = SecureRandom();
        val TEK = ByteArray(16);
        random.nextBytes(TEK);
        return TEK;
    }

    fun getKey(tek: ByteArray, info: ByteArray, outputLength: Int): Time_HKDF {
        return Time_HKDF(HKDF().deriveSecrets(tek, info, outputLength))
    }

    fun getRPI(rpik: Time_HKDF): ByteArray{
        /* Time is an issue that will be fixed with android */
        /* Java has no unsigned bytes, it could be done manually later */
        val time = ((rpik.time/600000)%144).toByte()
        val pad:ByteArray = byteArrayOf(0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0)
        val padded_data: ByteArray = "EN-RPI".toByteArray(Charsets.UTF_8) + pad + time

        val key: SecretKey = SecretKeySpec(rpik.dk, "AES")
        val cipher = Cipher.getInstance("AES/ECB/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, key)

        return cipher.doFinal(padded_data)
    }

    fun getAEM(distance: String, AEMK: ByteArray, RPI: ByteArray): ByteArray{
        val key: SecretKey = SecretKeySpec(AEMK, "AES");
        val cipher = Cipher.getInstance("AES/CTR/NoPadding")
        /*
        Source:
        https://cryptodoneright.org/articles/symmetric_algorithms/Mode_CTR/aes_ctr_dev_quickstart.html
        */
        cipher.init(Cipher.ENCRYPT_MODE, key, IvParameterSpec(RPI))

        return cipher.doFinal(distance.toByteArray());
    }
}
