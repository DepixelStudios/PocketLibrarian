package com.depixel.pocketlibrarian

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isGone
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_top_chart.*
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import java.io.IOException


class TopChartActivity : AppCompatActivity() {


    @OptIn(ExperimentalStdlibApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_top_chart)

        topTitle.isGone = true
        supportActionBar?.hide()

        homeButton2.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

//        savedBooks2.setOnClickListener {
//            val intent = Intent(this, ScannedBook::class.java)
//            startActivity(intent)
//        }

        chartItemsRecycler.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        val queryLink = intent.getStringExtra("query")
        val title = intent.getStringExtra("title")

        val request = okhttp3.Request.Builder().url(queryLink).build()
        val client = OkHttpClient()
        var newHeader = ""
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {

            }

            override fun onResponse(call: Call, response: okhttp3.Response) {
                val body = response?.body()?.string()

                val gson = GsonBuilder().create()

                val chartData = gson.fromJson(body, ChartData::class.java)

                var newHeader = topTitle.text.toString() + chartData.num_results.toString() + " " + title
                runOnUiThread{
                    topTitle.setText(newHeader.toString())
                    topTitle.isGone = false
                }

//                chartItemsRecycler.setLayoutManager(GridLayoutManager(this@TopChartActivity, 2))

                runOnUiThread {
                    chartItemsRecycler.adapter = ChartItemsAdapter(chartData)
                }

            }
        })

    }
}

class ChartData(val num_results: Int, val results: Results)

class Results(val books: Array<ChartBooks>)
//
class ChartBooks(val rank: Int, val title: String, val contributor: String, val book_image: String, val isbns: Array<ISBNS>, val buy_links: Array<BuyLinks>)
//
class ISBNS(val isbn10: String, val isbn13: String)
//
class BuyLinks(val name: String, val url: String)

