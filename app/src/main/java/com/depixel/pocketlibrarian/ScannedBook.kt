package com.depixel.pocketlibrarian


import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import com.squareup.picasso.Picasso
import jp.wasabeef.picasso.transformations.BlurTransformation
import kotlinx.android.synthetic.main.activity_scanned_book.*
import kotlinx.android.synthetic.main.activity_scanned_book.divider1
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import java.io.*


class ScannedBook : AppCompatActivity() {


    @OptIn(ExperimentalStdlibApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scanned_book)

        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.HORIZONTAL
        scannedBookView_main.layoutManager = layoutManager

        bookBackButton.setOnClickListener {
            val backIntent = Intent(this, MainActivity::class.java)
            startActivity(backIntent) }

        var isbnData = intent.getStringExtra("ISBN") // Receiving the ISBN code from
        var query = intent.getStringExtra("query") // Receiving the query
        var altIsbnData = intent.getStringExtra("ISBN_alt")


        if (query == null) {
            query = isbnData.toString()
        }


        var title = intent.getStringExtra("title") // Recieving the title string from SearchResultsAdapter

        if (title == null) {
            title = query.toString()
        }

        scanResult.text = isbnData // Makes the ISBN the text of scanResult

        var amznUrl = "https://www.amazon.com/s?k=$title&i=stripbooks"
        amazonButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(amznUrl)
            startActivity(intent)
        }

        val bnUrl = "https://www.barnesandnoble.com/s/$isbnData"
        bnButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(bnUrl)
            startActivity(intent)
        }

        val gbUrl = "https://books.google.com/books?vid=ISBN:$isbnData"
        gbButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(gbUrl)
            startActivity(intent)
        }

        if (title == null) {
            title = isbnData
        }
        val grUrl = "https://www.goodreads.com/search?q=$title"
        grButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(grUrl)
            startActivity(intent)
        }


        val urlBase = "https://www.googleapis.com/books/v1/volumes?q=isbn:"
        val urlParameters = "&maxResults=1&key=###" // insert key here
        val url = urlBase + isbnData + urlParameters
        val request = okhttp3.Request.Builder().url(url).build()
        val client = OkHttpClient()

        var altUrl = null
        if (altIsbnData != null) {
            var altUrl = urlBase + altIsbnData + urlParameters
        }
        runRequest(url, altUrl)






    }
    private fun runRequest(url: String?, altUrl: String?) {
        val request = okhttp3.Request.Builder().url(url).build()
        val client = OkHttpClient()
        client.newCall(request).enqueue(object: Callback {
            override fun onFailure(call: Call, e: IOException) {

            }

            override fun onResponse(call: Call, response: okhttp3.Response) {
                val body = response?.body()?.string()
                println(body)

                if (body == "{\n" +
                    "  \"kind\": \"books#volumes\",\n" +
                    "  \"totalItems\": 1\n" +
                    "}\n" || body == "{\n" +
                    "  \"kind\": \"books#volumes\",\n" +
                    "  \"totalItems\": 0\n" +
                    "}\n" )
                {
                    if (altUrl != null) {
                        runRequest(altUrl, "null")
                    }
                    return
                }

                val gson = GsonBuilder().create()

                val bookData = gson.fromJson(body, BookData::class.java)


                runOnUiThread {
                    scannedBookView_main.adapter = ScannedBookAdapter(bookData)
                }

                class ScannedBookAdapter(val bookData: BookData)

                fun String.insert(index: Int, string: String): String {
                    return this.substring(0, index) + string + this.substring(
                        index,
                        this.length
                    )
                }

                if (bookData.items[0].volumeInfo.imageLinks != null) {
                    val secureCoverUrl: String =
                        bookData.items[0].volumeInfo.imageLinks.thumbnail.insert(4, "s")

                    runOnUiThread {
                        Picasso.get()
                            .load(secureCoverUrl)
                            .fit()
                            .transform(BlurTransformation(applicationContext, 25, 1))
                            .into(backgroundBlurredCover)
                        externalLinksLayout.isVisible = true
                        divider1.isVisible = true
                    }

                    val recycledJSON = readFromFile("recentlyScanned.json")

                    val newBookRecycledList: MutableList<Books> = mutableListOf(Books(bookData.items[0].volumeInfo.industryIdentifiers[0].identifier, secureCoverUrl)).toMutableList()

                    val bookRecycled = gson.fromJson(recycledJSON, BookRecycled::class.java)

                    val bookCount = bookRecycled?.books?.count()?.minus(1)
                    if (recycledJSON != "") {
                        for (i in 0..bookCount!!) {
                            if (secureCoverUrl != bookRecycled?.books[i]?.imageUrl) {
                                if (i <= 20) {
                                    newBookRecycledList?.add(
                                        Books(
                                            bookRecycled.books[i]?.isbn,
                                            bookRecycled.books[i]?.imageUrl
                                        )
                                    )
                                }
                            }
                        }
                    }

                    val newBookRecycledArray: Array<Books> = newBookRecycledList.toTypedArray()

                    val newBookRecycledArrayFinal = BookRecycled(newBookRecycledArray)
                    val newBookRecycled = GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create().toJson(newBookRecycledArrayFinal)


                    writeToFile(newBookRecycled, "recentlyScanned.json")
                }
            }
        })
    }


    private fun readFromFile(path: String): String? {
        var ret = ""
        var inputStream: InputStream? = null
        try {
            inputStream = openFileInput(path)
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

    public fun getJsonDataFromAsset(context: Context, fileName: String): BookRecycled? {
        val jsonString: String
        try {
            jsonString = context.assets.open(fileName).bufferedReader().use { it.readText() }
        } catch (ioException: IOException) {
            ioException.printStackTrace()
            return null
        }

        val gson = GsonBuilder().create()

        val bookRecycled = gson.fromJson(jsonString, BookRecycled::class.java)

        return null
    }

    private fun writeToFile(data: String, path: String) {
        try {
            val outputStreamWriter = OutputStreamWriter(openFileOutput(path, MODE_PRIVATE))
            outputStreamWriter.write(data)
            outputStreamWriter.close()
        } catch (e: IOException) {
            Log.e("Exception", "File write failed: $e")
        }
    }

}



class BookData(val items: List<Items>)

class Items(val volumeInfo: VolumeInfo)

class VolumeInfo(val title: String, val authors: JsonArray, val industryIdentifiers: List<IndustryIdentifiers>, val publishedDate: String, val description: String, val pageCount: String, val averageRating: String, val ratingsCount: String, val maturityRating: String, val infoLink: String, val language: String, val imageLinks: ImageLinks, val categories: JsonArray)

class ImageLinks(val thumbnail: String, val smallThumbnail:String)

class IndustryIdentifiers(val identifier: String)

//class RecentlyScannedBooks(val books: List<BooksList>)
//
//class BooksList(val isbn: String, val imageUrl: String)

class SampleBook(val isbn: String, val imageUrl: String)
//class BookRecycled(val books: List<Books>)
//
//class Books(val isbn: String, val imageUrl: String)
