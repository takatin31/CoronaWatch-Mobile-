package com.example.myapplication

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.series.BarGraphSeries
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class BlankFragment2 : Fragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
    val v:View =    inflater.inflate(R.layout.fragment_blank_fragment2, container, false)
        val ghraph=v.findViewById (R.id.graph)as GraphView
        val name =v.findViewById (R.id.image_name) as TextView
        val image =v.findViewById (R.id.image) as ImageView
        val bundle = this.arguments
        val pos:String?=bundle?.getString("name")
        val imagepos:Int=bundle!!.getInt("image",0)


        name.setText(pos)
        var images = resources.obtainTypedArray(R.array.images)
        var i= resources.getIntArray(R.array.images)[imagepos]
        image.setImageResource(images.getResourceId(imagepos,-1))
        val series = LineGraphSeries<DataPoint>(arrayOf(

            DataPoint(0.0, 1.0),
            DataPoint(1.0, 5.0),
            DataPoint(2.0, 3.0),
            DataPoint(3.0, 2.0),
            DataPoint(4.0, 6.0)


        ))
        ghraph.addSeries(series)
        val ghraph1=v.findViewById (R.id.graph1)as GraphView
        val series1 = BarGraphSeries<DataPoint>(arrayOf(
            DataPoint(0.0, 1.0),
            DataPoint(1.0, 5.0),
            DataPoint(2.0, 3.0),
            DataPoint(3.0, 2.0),
            DataPoint(4.0, 6.0)


        ))
        ghraph1.addSeries(series1)
        return inflater.inflate(R.layout.fragment_blank_fragment2, container, false)
    }

    // TODO: Rename method, update argument and hook method into UI event

    }

