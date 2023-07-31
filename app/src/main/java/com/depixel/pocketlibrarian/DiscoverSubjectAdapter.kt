package com.depixel.pocketlibrarian

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color.parseColor
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.category_item.view.*


class DiscoverSubjectAdapter(val subjects: Subjects): RecyclerView.Adapter<DiscoverSubjectHolder>() {

    override fun getItemCount(): Int {
        return subjects.genres.count()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiscoverSubjectHolder {

        val layoutInflater = LayoutInflater.from(parent.context)
        val cellForRow = layoutInflater.inflate(R.layout.category_item, parent, false)
        return DiscoverSubjectHolder(cellForRow)
    }

    override fun onBindViewHolder(holder: DiscoverSubjectHolder, position: Int) {
        val genres = subjects.genres[position]


        // background

        val backgroundColors = intArrayOf(
            parseColor("#25FFBB00"),
            parseColor("#25005FED"),
            parseColor("#25FF004E"),
            parseColor("#25FFB500"),
            parseColor("#25FF5700"),
            parseColor("#2500A6ED"),
            parseColor("#2500A6ED")
            )

        holder?.view?.genreText.text = genres.subjectName
        holder?.view?.emoji.text = genres.emoji

        if (position != 5) {
            holder?.view?.genreHolder.setBackgroundTintList(ColorStateList.valueOf(backgroundColors[position]))
        }


        holder?.view?.setOnClickListener {
            if (position != 5) {
                val intent = Intent(holder?.view?.context, SearchActivity::class.java)
                intent.putExtra("query", genres.query)
                holder?.view?.context.startActivity(intent)
            }
            else {
//                val intent = Intent(holder?.view?.context, MainActivity::class.java)
                val intent = Intent(holder?.view?.context, AllSubjectsPopup::class.java)
                intent.putExtra("reason", "more_subjects")
                holder?.view?.context.startActivity(intent)
            }
        }
    }


    }

class DiscoverSubjectHolder(val view: View): RecyclerView.ViewHolder(view) {



}

