package com.depixel.pocketlibrarian

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.top_charts_row.view.*


class TopChartsAdapter(val topCharts: TopCharts): RecyclerView.Adapter<TopChartsHolder>() {

    override fun getItemCount(): Int {
        return topCharts.charts.count()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopChartsHolder {

        val layoutInflater = LayoutInflater.from(parent.context)
        val cellForRow = layoutInflater.inflate(R.layout.top_charts_row, parent, false)
        return TopChartsHolder(cellForRow)
    }

    override fun onBindViewHolder(holder: TopChartsHolder, position: Int) {
        val charts = topCharts.charts.get(position)


        // background

        val imageIds = intArrayOf(
            R.drawable.bullseye_gradient,
            R.drawable.pink_triangle_gradient,
            R.drawable.bestsellers_bg,
            R.drawable.red_to_blue_gradient,
            R.drawable.japan_vector
        )

        holder?.view?.discoverCardBG.setImageResource(imageIds[position])

        holder?.view?.topChartsTitle.text = charts.itemTitle



        holder?.view?.setOnClickListener {
            val intent = Intent(holder?.view?.context, TopChartActivity::class.java)
            intent.putExtra("query", charts.query)
            intent.putExtra("title", charts.itemTitle)
            holder?.view?.context.startActivity(intent)
        }
    }
}

class TopChartsHolder(val view: View): RecyclerView.ViewHolder(view) {



}

