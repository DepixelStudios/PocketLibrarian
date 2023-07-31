package com.depixel.pocketlibrarian

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import androidx.core.view.isGone
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import kotlinx.android.synthetic.main.activity_search.*
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import java.io.IOException



class SearchActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        supportActionBar?.hide()

        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        searchResults_main.layoutManager = layoutManager

        var query = intent.getStringExtra("query") // Receiving the query from MainActivity
        searchQueryBar.setText(query) // Makes the query the text of searchQueryBar

        cancel_button.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }


        println("Attempting to Fetch JSON")

        val urlBase = "https://www.googleapis.com/books/v1/volumes?q="
        val urlParameters = "&maxResults=20&key=AIzaSyBTvvByXyV9YUqZlTFDJo7spkDjVZwpcMw"
        val finalQuery = query?.replace("\\s".toRegex(), "+")?.toLowerCase()

        val url = urlBase + finalQuery + urlParameters

        val request = okhttp3.Request.Builder().url(url).build()

        val client = OkHttpClient()
        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: okhttp3.Response) {
                val body = response?.body()?.string()
                println(body)

                val gson = GsonBuilder()?.create()

                val bookCheck = gson.fromJson(body, BookCheck::class.java)

                if (bookCheck.totalItems == "0") {
                    runOnUiThread {
                        notFoundTxt.isGone = false
                    }
                }
                else {
                    val bookData = gson.fromJson(body, BookData2::class.java)
                    runOnUiThread {
                        searchResults_main.adapter = SearchResultsAdapter(bookData)
                    }
                }





            }

            override fun onFailure(call: Call, e: IOException) {
                println("Failed to execute request")
            }
        })
    }

}

fun EditText.onSubmit(func: () -> Unit) {
    setOnEditorActionListener { _, actionId, _ ->

        if (actionId == EditorInfo.IME_ACTION_DONE) {
            func()
        }

        true

    }
}

class BookData2(val totalItems: Int, val items: List<Items>)

class Items2(val volumeInfo: VolumeInfo2)

class VolumeInfo2(val title: String, val authors: JsonArray, val categories: JsonArray)

class IndustryIdentifiers2(val identifier: String)



class BookCheck(val totalItems: String)