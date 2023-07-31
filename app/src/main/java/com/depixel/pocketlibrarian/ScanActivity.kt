package com.depixel.pocketlibrarian

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.text.isDigitsOnly
import com.budiyev.android.codescanner.*
import kotlinx.android.synthetic.main.activity_scan.*
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import java.io.IOException

private const val CAMERA_REQUEST_CODE = 101


class ScanActivity : AppCompatActivity() {
    private lateinit var codeScanner: CodeScanner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan)

        setupPermissions()
        codeScanner()

        scanBackButton.setOnClickListener {
            val backIntent = Intent(this@ScanActivity, MainActivity::class.java)
            startActivity(backIntent) }

    }


    private fun codeScanner() {
        codeScanner = CodeScanner(this, scanner_view)

        codeScanner.apply {
            camera = CodeScanner.CAMERA_BACK
            formats = CodeScanner.ONE_DIMENSIONAL_FORMATS

            autoFocusMode = AutoFocusMode.SAFE
            scanMode = ScanMode.CONTINUOUS
            isAutoFocusEnabled = true
            isFlashEnabled = false

            decodeCallback = DecodeCallback {
                val code = it.text
                val decodeIntent = Intent(this@ScanActivity, ScannedBook::class.java)
                decodeIntent.putExtra("query", it.text)
                decodeIntent.putExtra("ISBN", it.text)
                startActivity(decodeIntent)

            }

            errorCallback = ErrorCallback {
                runOnUiThread {
                    Log.e("Main", "Camera initialization error: ${it.message}")
                }
            }
        }
        
        scanner_view.setOnClickListener {
            codeScanner.startPreview()
        }
    }

    override fun onResume() {
        super.onResume()
        codeScanner.startPreview()
    }

    override fun onPause() {
        codeScanner.releaseResources()
        super.onPause()
    }
    
    private fun setupPermissions() {
        val permission = ContextCompat.checkSelfPermission(this,
        android.Manifest.permission.CAMERA)

        if (permission != PackageManager.PERMISSION_GRANTED) {
            makeRequest()
        }
    }

    private fun makeRequest() {
        ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.CAMERA), CAMERA_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            CAMERA_REQUEST_CODE -> {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "You need the camera permission to scan books!", Toast.LENGTH_SHORT).show()
                } else {
                    //successful request
                }
            }
        }
    }
}

private fun checkValidCode(isbnData: String): Boolean {
    val urlBase = "https://www.googleapis.com/books/v1/volumes?q=isbn:"
    val urlParameters = "&maxResults=1&key=###" // insert key here
    val url = urlBase + isbnData + urlParameters
    val request = okhttp3.Request.Builder().url(url).build()
    val client = OkHttpClient()
    val body = "{\n" +
            "\"kind\": \"books#volumes\",\n" +
            "\"totalItems\": 0\n" +
            "}"
    client.newCall(request).enqueue(object: Callback {
        override fun onFailure(call: Call, e: IOException) {
            return
        }

        override fun onResponse(call: Call, response: okhttp3.Response) {
            val body = response?.body()?.string()

            return
            }

        })
    return body != "{\n" +
            "\"kind\": \"books#volumes\",\n" +
            "\"totalItems\": 0\n" +
            "}"
}

