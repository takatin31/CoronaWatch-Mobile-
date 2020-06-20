package com.example.coronawatch.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import com.example.coronawatch.Activities.MainActivity
import com.example.coronawatch.DataClasses.Image
import com.example.coronawatch.R


class ImageViewAdapter (
    internal var mContext: Context,
    val list : ArrayList<Image>
) : BaseAdapter() {
    internal var inflater: LayoutInflater
    private val arraylist: ArrayList<Image>

    init {
        inflater = LayoutInflater.from(mContext)
        this.arraylist = ArrayList()
        this.arraylist.addAll(list)
    }

    inner class ViewHolder {


        internal var image: ImageView? = null
    }

    override fun getCount(): Int {
        return list.size
    }

    override fun getItem(position: Int):Image {
        return list[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, view: View?, parent: ViewGroup): View {
        var view = view
        val holder: ViewHolder
        if (view == null) {
            holder = ViewHolder()
            view = inflater.inflate(R.layout.figure_layout, null)
            // Locate the TextViews in listview_item.xml

            holder.image = view!!.findViewById(R.id.figureView) as ImageView
            view.tag = holder
        } else {
            holder = view.tag as ViewHolder
        }
        // Set the results into TextViews

        holder.image!!.setImageResource(list[position].image)

        return view
    }


}