package com.depixel.pocketlibrarian

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_main.*
import java.io.*


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.hide()

        recentlyScannedContainer.isGone = true

        scanBookButton.setOnClickListener {
            val intent = Intent(this, ScanActivity::class.java)
            startActivity(intent)
        }


        searchBar.onSubmit {
            if (searchBar.text.toString() != "") {
                searchBar.clearFocus()
                val queryText = searchBar.text.toString()
                val intent = Intent(this, SearchActivity::class.java)
                intent.putExtra("query", queryText)
                startActivity(intent)
            }
        }


        recentlyScannedRecycler.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        val recentlyScannedJSON = readFromFile()

        val gson = GsonBuilder().create()

        if (recentlyScannedJSON != "") {
            // put in code to make recentlyScanned group visible rather than gone
            recentlyScannedContainer.isGone = false
            val bookRecycled = gson.fromJson(recentlyScannedJSON, BookRecycled::class.java)
            recentlyScannedHeader.isVisible = true
            runOnUiThread {
                recentlyScannedRecycler.adapter = RecentlyScannedAdapter(bookRecycled)
            }
        }



        topChartsRecycler.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        val topChartsJSON = getJsonDataFromAsset(applicationContext, "topCharts.json")
        val topCharts = gson.fromJson(topChartsJSON, TopCharts::class.java)
        runOnUiThread {
            topChartsRecycler.adapter = TopChartsAdapter(topCharts)
        }



        subjectsRecycler.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.HORIZONTAL)


        val subjectsJSON = getJsonDataFromAsset(applicationContext,"subjects.json")
        val subjects = gson.fromJson(subjectsJSON, Subjects::class.java)

        runOnUiThread {
            subjectsRecycler.adapter = DiscoverSubjectAdapter(subjects)
        }



        moreSubjectsRecycler.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        val moreSubjectsJSON = getJsonDataFromAsset(applicationContext,"allSubjects.json")
        val moreSubjects = gson.fromJson(moreSubjectsJSON, MoreSubjects::class.java)

        runOnUiThread {
            moreSubjectsRecycler.adapter = MoreSubjectsAdapter(moreSubjects)
        }


    }

    private fun readFromFile(): String? {
        var ret = ""
        var inputStream: InputStream? = null
        try {
            inputStream = openFileInput("recentlyScanned.json")
            if (inputStream != null) {
                val inputStreamReader = InputStreamReader(inputStream)
                val bufferedReader = BufferedReader(inputStreamReader)
                var receiveString: String? = ""
                val stringBuilder = StringBuilder()
                while (bufferedReader.readLine().also { receiveString = it } != null) {
                    stringBuilder.append(receiveString)
                }
                ret = stringBuilder.toString()
            }
        } catch (e: FileNotFoundException) {
            Log.e("login activity", "File not found: " + e.toString())
        } catch (e: IOException) {
            Log.e("login activity", "Can not read file: $e")
        } finally {
            try {
                inputStream?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return ret
    }

    private fun writeToFile(data: String) {
        try {
            val outputStreamWriter = OutputStreamWriter(openFileOutput("recentlyScanned.json", MODE_PRIVATE))
            outputStreamWriter.write(data)
            outputStreamWriter.close()
        } catch (e: IOException) {
            Log.e("Exception", "File write failed: $e")
        }
    }


}

fun getJsonDataFromAsset(context: Context, fileName: String): String? {
    val jsonString: String
    try {
        jsonString = context.assets.open(fileName).bufferedReader().use { it.readText() }
    } catch (ioException: IOException) {
        ioException.printStackTrace()
        return null
    }
    return jsonString

}

class BookRecycled(val books: Array<Books>)
class Books(val isbn: String, val imageUrl: String)

class TopCharts(val charts: Array<Charts>)
class Charts(val itemTitle: String, val background: String, val query: String)

class Subjects(val genres: Array<Genres>)
class Genres(val subjectName: String, val emoji: String, val query: String)

class MoreSubjects(val genres: Array<MoreGenres>)
class MoreGenres(val subjectName: String, val emoji: String, val query: String)