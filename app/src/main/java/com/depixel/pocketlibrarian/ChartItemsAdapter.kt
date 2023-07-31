package com.depixel.pocketlibrarian

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.palette.graphics.Palette
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.chart_item_row.view.*
import java.lang.Exception
import java.util.*


class ChartItemsAdapter(val chartData: ChartData): RecyclerView.Adapter<ChartItemsHolder>() {

    override fun getItemCount(): Int {
        return chartData.results.books.count()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChartItemsHolder {

        val layoutInflater = LayoutInflater.from(parent.context)
        val cellForRow = layoutInflater.inflate(R.layout.chart_item_row, parent, false)
        return ChartItemsHolder(cellForRow)
    }

    override fun onBindViewHolder(holder: ChartItemsHolder, position: Int) {
        val books = chartData.results.books.get(position)

        holder?.view?.bookTitle.text = books.title.lowercase().capitalizeWords()
        holder?.view?.bookAuthor.text = books.contributor
        holder?.view?.rankNo.text = books.rank.toString()
//        holder?.view?.bookCard.rotation = (-5..5).random().toFloat()



        val bookCoverImage = holder?.view?.chartItemCover
        val imgUrl = books.book_image.toString()
        Picasso.get().load(imgUrl).fit().into(bookCoverImage)
        val rnd = Random()
        val color: Int = Color.argb(180, rnd.nextInt(200), rnd.nextInt(200), rnd.nextInt(200))
//        holder?.view?.rankNo.setTextColor(color)





        Picasso.get()
            .load(imgUrl)
            .into(object : com.squareup.picasso.Target {

                override fun onBitmapLoaded(bitmap: Bitmap, from: Picasso.LoadedFrom) {
                    /* Save the bitmap or do something with it here */
                    fun createPaletteSync(bitmap: Bitmap): Palette = Palette.from(bitmap).generate()
                    var swatch = createPaletteSync(bitmap).vibrantSwatch?.rgb
                    if (swatch != null) {
                        holder?.view?.rankNo.setTextColor(swatch)
                    }
                }

                override fun onPrepareLoad(placeHolderDrawable: Drawable?) {}

                override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {}

            })


        holder?.view?.chartItem.setOnClickListener {
            val intent = Intent(holder?.view?.context, ScannedBook::class.java)

            if (books?.isbns.count() == 2) {
                intent.putExtra("ISBN", books.isbns[1].isbn13)
                intent.putExtra("ISBN_alt", books.isbns[0].isbn13)

            }
            else
            {
                intent.putExtra("ISBN", books.isbns[0].isbn13)

            }
            holder?.view?.context.startActivity(intent)
        }


    }


}

fun String.capitalizeWords(): String = split(" ").map { it.capitalize() }.joinToString(" ")



class ChartItemsHolder(val view: View): RecyclerView.ViewHolder(view) {

}



