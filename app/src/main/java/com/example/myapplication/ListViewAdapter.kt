package com.example.myapplication

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView

import java.util.ArrayList
import java.util.Locale

class ListViewAdapter(
    // Declare Variables

    internal var mContext: Context
) : BaseAdapter() {
    internal var inflater: LayoutInflater
    private val arraylist: ArrayList<PayName>

    init {
        inflater = LayoutInflater.from(mContext)
        this.arraylist = ArrayList()
        this.arraylist.addAll(BlankFragment.payeNamesArrayList)
    }

    inner class ViewHolder {
        internal var name: TextView? = null

        internal var name1: TextView? = null

        internal var image: ImageView? = null
    }

    override fun getCount(): Int {
        return BlankFragment.payeNamesArrayList.size
    }

    override fun getItem(position: Int): PayName {
        return BlankFragment.payeNamesArrayList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, view: View?, parent: ViewGroup): View {
        var view = view
        val holder: ViewHolder
        if (view == null) {
            holder = ViewHolder()
            view = inflater.inflate(R.layout.lv_item, null)
            // Locate the TextViews in listview_item.xml
            holder.name = view!!.findViewById(R.id.image_name) as TextView
            holder.name1 = view!!.findViewById(R.id.image_name2) as TextView
            holder.image = view!!.findViewById(R.id.image) as ImageView
            view.tag = holder
        } else {
            holder = view.tag as ViewHolder
        }
        // Set the results into TextViews
        holder.name!!.setText(BlankFragment.payeNamesArrayList[position].payName)
        holder.name1!!.setText(BlankFragment.payeNamesArrayList[position].imageName)
        holder.image!!.setImageResource(BlankFragment.payeNamesArrayList[position].image)

        return view
    }

    // Filter Class
    fun filter(charText: String) {
        var charText = charText
        charText = charText.toLowerCase(Locale.getDefault())
        BlankFragment.payeNamesArrayList.clear()
        if (charText.length == 0) {
            BlankFragment.payeNamesArrayList.addAll(arraylist)
        } else {
            for (wp in arraylist) {
                if (wp.payName.toLowerCase(Locale.getDefault()).contains(charText)) {
                    BlankFragment.payeNamesArrayList.add(wp)
                }
            }
        }
        notifyDataSetChanged()
    }

}