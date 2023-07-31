package com.depixel.pocketlibrarian

import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.scanned_book_layout.view.*
import kotlinx.android.synthetic.main.scanned_book_layout.view.divider1


class ScannedBookAdapter(val bookData: BookData): RecyclerView.Adapter<scannedBookHolder>() {

    // numberOfItems
    override fun getItemCount(): Int {
        return 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): scannedBookHolder {

        val layoutInflater = LayoutInflater.from(parent.context)
        val cellForRow = layoutInflater.inflate(R.layout.scanned_book_layout, parent, false)
        return scannedBookHolder(cellForRow)
    }

    override fun onBindViewHolder(holder: scannedBookHolder, position: Int) {
        val items = bookData.items.get(position)

        holder?.view?.descTxt.movementMethod = ScrollingMovementMethod()


        // author text
        if (items.volumeInfo?.authors != null) {
            holder?.view?.bookAuthorTxt?.text = "by " + items?.volumeInfo?.authors[0]?.asString
        }

        // title text
        if (items.volumeInfo?.title != null) {
            holder?.view?.bookTitleTxt?.text = items.volumeInfo?.title?.toString()

        }

        // rating text

        holder?.view?.reviewRating.isGone = true

        if (items?.volumeInfo?.averageRating != null) {
            items.volumeInfo.averageRating?.let {
                if (items.volumeInfo.averageRating.count() == 1) {
                    holder?.view?.reviewRating.text = items.volumeInfo.averageRating + ".0"
                } else {
                    holder?.view?.reviewRating.text = items.volumeInfo.averageRating
                }

                items.volumeInfo.ratingsCount?.let {
                    holder?.view?.reviewRating?.text =
                        holder?.view?.reviewRating.text.toString() + " (${items?.volumeInfo?.ratingsCount})"
                }

                holder?.view?.reviewRating.isGone = false

            }
        }


        // image src

        if (items.volumeInfo.imageLinks != null) {
            fun String.insert(index: Int, string: String): String {
                return this.substring(0, index) + string + this.substring(index, this.length)
            } // creates a function that inserts a string at an location in another string

            val secureCoverUrl = items.volumeInfo?.imageLinks?.thumbnail?.insert(4, "s")

            val bookCoverImage = holder?.view?.bookCoverImg
            Picasso.get().load(secureCoverUrl).fit().into(bookCoverImage)
        }


        //page count

        holder?.view?.pageCountTxt.isGone = true
        holder?.view?.divider1.isGone = true


        if (items.volumeInfo?.pageCount != null) {
            val pageNum = items.volumeInfo.pageCount.toString()
            val pageCount = pageNum + " Pages"
            items.volumeInfo.pageCount?.let {
                holder?.view?.pageCountTxt.text = pageCount
                holder?.view?.pageCountTxt.isGone = false
            }
        }

        // language
        holder?.view?.langTxt.isGone = true

        if (items.volumeInfo?.language != null) {
            items.volumeInfo.language?.let {
                holder?.view?.langTxt.isGone = false
                holder?.view?.divider1.isGone = false


                if (items.volumeInfo.language == "en")
                    holder?.view?.langTxt.text = "English"
                else
                    holder?.view?.langTxt.text = items.volumeInfo.language.capitalize()
            }
        }


        // desc
        holder?.view?.descTxt.isGone = true

        if (items.volumeInfo.description != null) {
            items.volumeInfo.description?.let {

                holder?.view?.descTxt.text = items.volumeInfo.description
                holder?.view?.descTxt.isGone = false
            }
        }


    }
}

class scannedBookHolder(val view: View): RecyclerView.ViewHolder(view) {

}