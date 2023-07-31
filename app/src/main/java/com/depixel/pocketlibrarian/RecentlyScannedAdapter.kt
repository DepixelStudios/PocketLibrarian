package com.depixel.pocketlibrarian

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.recently_scanned_row.view.*
import android.content.Intent as Intent

class RecentlyScannedAdapter(val bookRecycled: BookRecycled): RecyclerView.Adapter<RecentlyScannedHolder>() {

    override fun getItemCount(): Int {
        return bookRecycled.books.count()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecentlyScannedHolder {

        val layoutInflater = LayoutInflater.from(parent.context)
        val cellForRow = layoutInflater.inflate(R.layout.recently_scanned_row, parent, false)
        return RecentlyScannedHolder(cellForRow)
    }

    override fun onBindViewHolder(holder: RecentlyScannedHolder, position: Int) {
        val books = bookRecycled.books.get(position)


        // cover
        val bookCoverImage = holder?.view?.bookBG
        val imgUrl = books.imageUrl
        Picasso.get().load(imgUrl).fit().into(bookCoverImage)

        //isbn text
        holder?.view?.ISBNTxt.text = books.isbn

        holder?.view?.setOnClickListener {

            val intent = Intent(holder?.view?.context, ScannedBook::class.java)
            intent.putExtra("ISBN", books.isbn)

            holder?.view?.context.startActivity(intent)
        }
        }


    }

class RecentlyScannedHolder(val view: View): RecyclerView.ViewHolder(view) {



}

