package com.silatsaktistudios.shreddit.view

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.silatsaktistudios.shreddit.R
import com.silatsaktistudios.shreddit.model.Child
import kotlinx.android.synthetic.main.fragment_feed_list_item.view.*

class FeedListAdapter(private val list: List<Child>) : RecyclerView.Adapter<FeedListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_feed_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]

        with(holder.mView) {
            item_number.text = item.data.id
            content.text = item.data.title
        }
    }

    override fun getItemCount(): Int = list.size

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        var number: TextView = TextView(mView.context)
        var content: TextView = TextView(mView.context)
    }
}
