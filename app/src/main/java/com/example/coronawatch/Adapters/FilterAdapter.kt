package com.example.coronawatch.Adapters


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.coronawatch.DataClasses.Filter
import com.example.coronawatch.R
import de.hdodenhof.circleimageview.CircleImageView


class FilterAdapter(context: Context, filterList: ArrayList<Filter>) :
    ArrayAdapter<Filter>(context, 0, filterList) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return initView(position, convertView, parent)
    }

    override fun getDropDownView(
        position: Int, convertView: View?,
        parent: ViewGroup
    ): View {
        return initView(position, convertView, parent)
    }

    private fun initView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView: View? = convertView
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(
                R.layout.map_spinner_item_layout, parent, false
            )
        }
        val colorFilter: CircleImageView = convertView!!.findViewById(R.id.userImage)
        val nameFilter: TextView = convertView.findViewById(R.id.titleFilter)
        val currentItem: Filter? = getItem(position)
        if (currentItem != null) {
            colorFilter.setImageResource(currentItem.color)
            nameFilter.text = currentItem.name
        }
        return convertView!!
    }
}