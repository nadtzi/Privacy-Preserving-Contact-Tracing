package com.example.ehatraz

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_log.*
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream

class LogActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log)

        val filePath: String = filesDir.path.toString().toString() + "/file.txt"
        if(read(filePath) != null)
        {
            val mainActivity = Intent(this,MainActivity::class.java);
            startActivity(mainActivity)
        }
        submitButton.setOnClickListener{
            val mainActivity = Intent(this,MainActivity::class.java);
            startActivity(mainActivity)
        }

    }
    fun read(filePath:String) :String
    {
        val file = FileInputStream(filePath)
        val inStream = ObjectInputStream(file)
        val hmcNumber = inStream.readObject()
        inStream.close()
        file.close()
        return hmcNumber.toString()
    }
    fun write(filePath:String,data:String)
    {
        val file = FileOutputStream(filePath)
        val outStream = ObjectOutputStream(file)
        outStream.writeObject(data)
        outStream.close()
        file.close()
    }
}