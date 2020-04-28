package com.silatsaktistudios.shreddit.adapter

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.silatsaktistudios.shreddit.R
import com.silatsaktistudios.shreddit.model.Child
import com.silatsaktistudios.shreddit.model.ChildData
import kotlinx.android.synthetic.main.fragment_feed_list_item.view.*

class FeedListAdapter(private var list: List<Child>, val onItemClick: (data: ChildData) -> Unit) :
  RecyclerView.Adapter<FeedListAdapter.ViewHolder>() {

  init {
    list = list.sortedByDescending { it.data.ups }
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    val view = LayoutInflater.from(parent.context)
      .inflate(R.layout.fragment_feed_list_item, parent, false)
    return ViewHolder(view)
  }

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    val item = list[position]

    with(holder.mView) {
      with(item.data) {
        list_item_title.text = title
        list_item_author.text = author
        list_item_subreddit.text = subreddit
        list_item_ups_text.text = "$ups"
        list_item_message_count_text.text = "$num_comments"

        if (thumbnail.contains("http")) {
          Glide.with(holder.mView)
            .load(thumbnail)
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
            .centerInside()
            .into(list_item_image)
        }

        setOnClickListener { onItemClick(this) }
      }
    }
  }

  override fun getItemCount(): Int = list.size

  inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView)
}
