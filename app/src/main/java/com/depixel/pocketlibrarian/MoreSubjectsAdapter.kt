package com.depixel.pocketlibrarian

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.category_item.view.*


class MoreSubjectsAdapter(val moreSubjects: MoreSubjects): RecyclerView.Adapter<MoreSubjectsHolder>() {

    override fun getItemCount(): Int {
        return moreSubjects.genres.count()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MoreSubjectsHolder {

        val layoutInflater = LayoutInflater.from(parent.context)
        val cellForRow = layoutInflater.inflate(R.layout.more_category_item, parent, false)
        return MoreSubjectsHolder(cellForRow)
    }

    override fun onBindViewHolder(holder: MoreSubjectsHolder, position: Int) {
        val genres = moreSubjects.genres[position]



        holder?.view?.genreText.text = genres.subjectName
        holder?.view?.emoji.text = genres.emoji


        holder?.view?.setOnClickListener {
            val intent = Intent(holder?.view?.context, SearchActivity::class.java)
            intent.putExtra("query", genres.query)
            holder?.view?.context.startActivity(intent)
        }
    }


    }

class MoreSubjectsHolder(val view: View): RecyclerView.ViewHolder(view) {



}

