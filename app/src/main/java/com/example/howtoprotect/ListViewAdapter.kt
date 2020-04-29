package com.example.howtoprotect

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView

class ListViewAdapter (
    internal var mContext: Context
    ) : BaseAdapter() {
        internal var inflater: LayoutInflater
        private val arraylist: ArrayList<Image>

        init {
            inflater = LayoutInflater.from(mContext)
            this.arraylist = ArrayList()
            this.arraylist.addAll(MainActivity.imageList)
        }

        inner class ViewHolder {


            internal var image: ImageView? = null
        }

        override fun getCount(): Int {
            return MainActivity.imageList.size
        }

        override fun getItem(position: Int):Image {
            return MainActivity.imageList[position]
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

                holder.image = view!!.findViewById(R.id.image) as ImageView
                view.tag = holder
            } else {
                holder = view.tag as ViewHolder
            }
            // Set the results into TextViews

            holder.image!!.setImageResource(MainActivity.imageList[position].image)

            return view
        }


    }