package com.depixel.pocketlibrarian

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.palette.graphics.Palette
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.search_results_row.view.*

class SearchResultsAdapter(val bookData: BookData2): RecyclerView.Adapter<SearchResultsHolder>() {

    override fun getItemCount(): Int {
        return bookData.items.count()
        println(bookData.items.count())
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchResultsHolder {

        val layoutInflater = LayoutInflater.from(parent.context)
        val cellForRow = layoutInflater.inflate(R.layout.search_results_row, parent, false)
        return SearchResultsHolder(cellForRow)
    }

    override fun onBindViewHolder(holder: SearchResultsHolder, position: Int) {
        val items = bookData.items.get(position)

        if (items.volumeInfo.imageLinks != null) {
            fun String.insert(index: Int, string: String): String {
                return this.substring(0, index) + string + this.substring(index, this.length)
            } // creates a function that inserts a string at an location in another string

            val secureCoverUrl = items.volumeInfo?.imageLinks?.thumbnail?.insert(4, "s")

            val bookCoverImage = holder?.view?.coverImage
            Picasso.get().load(secureCoverUrl).fit().into(bookCoverImage)


            Picasso.get()
                .load(secureCoverUrl)
                .into(object : com.squareup.picasso.Target {

                    override fun onBitmapLoaded(bitmap: Bitmap, from: Picasso.LoadedFrom) {
                        /* Save the bitmap or do something with it here */
                        fun createPaletteSync(bitmap: Bitmap): Palette = Palette.from(bitmap).generate()
                        val lightVibrantSwatch = createPaletteSync(bitmap).lightVibrantSwatch?.rgb


                        if (lightVibrantSwatch != null) {
                            holder?.view?.categoryHolder.setBackgroundTintList(ColorStateList.valueOf(lightVibrantSwatch))
                            holder?.view?.categoryHolder.background.alpha = 75
                        }

                    }

                    override fun onPrepareLoad(placeHolderDrawable: Drawable?) {}

                    override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {}

                })
        }



        if (items?.volumeInfo?.authors == null) {
            holder?.view?.Author?.text = "by Unknown"
        }
        else {
            if (items?.volumeInfo?.authors?.count() == 1) {
                holder?.view?.Author?.text = "by " + items?.volumeInfo?.authors[0].asString
            }
            else {
                val andMore = items?.volumeInfo?.authors?.count().toInt() - 1
                holder?.view?.Author?.text = "by " + items?.volumeInfo?.authors[0].asString + " and " + andMore + " more"
            }
        }

        if (items?.volumeInfo?.title == null) {
            holder?.view?.Title?.text = "TITLE NOT FOUND"
        }
        else {
            holder?.view?.Title?.text = items?.volumeInfo?.title
        }



        if (items?.volumeInfo?.categories == null) {
            holder?.view?.categoryHolder.isGone = true
        }
        else {
            holder?.view?.categoryText.text = items.volumeInfo.categories[0].asString.lowercase().capitalizeWords()
        }

        holder?.view?.setOnClickListener {

            val intent = Intent(holder?.view?.context, ScannedBook::class.java)
            intent.putExtra("query", items?.volumeInfo?.title?.toString() + "+${items.volumeInfo.industryIdentifiers[1].identifier?.toString()}")
            intent.putExtra("ISBN", items?.volumeInfo?.industryIdentifiers[0]?.identifier?.toString())
            intent.putExtra("title", items?.volumeInfo?.title)


//            if (items?.volumeInfo?.industryIdentifiers.count() != 2) {
//                intent.putExtra("query", items?.volumeInfo?.title?.toString() + "+${items.volumeInfo.industryIdentifiers[1].identifier?.toString()}")
//                intent.putExtra("ISBN", items?.volumeInfo?.industryIdentifiers[1]?.identifier?.toString())
//            }
//            else {
//                intent.putExtra("query", items?.volumeInfo?.title?.toString() + "+${items.volumeInfo.industryIdentifiers[1].identifier?.toString()}")
//                intent.putExtra("ISBN", items?.volumeInfo?.industryIdentifiers[1]?.identifier?.toString())
//            }
            holder?.view?.context.startActivity(intent)
        }
    }
}

fun manipulateColor(color: Int, factor: Double): Int {
    val a: Int = Color.alpha(color)
    val r = Math.round(Color.red(color) * factor).toInt()
    val g = Math.round(Color.green(color) * factor).toInt()
    val b = Math.round(Color.blue(color) * factor).toInt()
    return Color.argb(
        a,
        Math.min(r, 255),
        Math.min(g, 255),
        Math.min(b, 255)
    )
}

class SearchResultsHolder(val view: View): RecyclerView.ViewHolder(view) {}