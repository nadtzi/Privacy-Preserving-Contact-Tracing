package com.example.ehatraz

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_log.*
import org.altbeacon.beacon.BeaconConsumer
import org.altbeacon.beacon.BeaconManager
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream


class LogActivity : AppCompatActivity() {
    protected val TAG = "LogActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log)

        submitButton.setOnClickListener{
            val mainActivity = Intent(this, MainActivity::class.java);
            val regex = """\d{11}""".toRegex()
            val regex2 = """20160969800""".toRegex()
            if (regex.containsMatchIn(hmcEditText.text.toString())&&regex2.containsMatchIn(hmcEditText.text.toString())) {
                startActivity(mainActivity)
            }
            else {
                hmcEditText.setText("")
                Toast.makeText(this, "Error Invalid HMC Number", Toast.LENGTH_SHORT).show()
            }
        }

    }

}