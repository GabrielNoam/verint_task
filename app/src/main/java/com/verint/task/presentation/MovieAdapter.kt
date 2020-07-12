package com.verint.task.presentation

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.verint.task.R
import com.verint.task.model.Movie
import kotlinx.android.synthetic.main.movie_item.view.*
/**
 * MovieAdapter
 *
 * @author Gabriel Noam
 */
class MovieAdapter(private var activity: Activity, private var movies: List<Movie>) : BaseAdapter() {

    companion object {
        private const val NA = "N/A"
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var itemView = convertView
            ?: LayoutInflater.from(activity)
                .inflate(R.layout.movie_item, parent, false)

        with(itemView) {
            movies[position].apply {
                titleTextView.text = title
                yearTextView.text = year
                if(NA == poster)
                    imageView.let {
                        it.setImageDrawable(activity.getDrawable(R.drawable.no_image))
                        it.scaleType = ImageView.ScaleType.CENTER_INSIDE
                    }
                else
                    Glide.with(activity).load(poster).into(imageView);
            }

            return itemView
        }
    }

    override fun getItem(position: Int) = movies[position]

    override fun getItemId(position: Int) = position.toLong()

    override fun getCount() = movies.size
}