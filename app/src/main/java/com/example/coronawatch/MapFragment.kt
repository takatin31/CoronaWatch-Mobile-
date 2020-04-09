package com.example.coronawatch


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_map.*

class MapFragment : Fragment() {

    private var mapView: MapView? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        Mapbox.getInstance(activity!!, getString(R.string.mapbox_access_token))
        return inflater.inflate(R.layout.fragment_map, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {


        mapView = mapBoxView
        mapView?.onCreate(savedInstanceState)
        mapView?.getMapAsync { mapboxMap ->

            mapboxMap.setStyle(Style.Builder().fromUri("mapbox://styles/ali31/ck8sx67n92lia1io8cl38v9fi")) {

                // Custom map style has been loaded and map is now ready
                Log.i("Succes", "Map loaded Succefully")
            }


        }
    }


    override fun onStart() {
        super.onStart()
        mapView?.onStart()
    }



    override fun onDestroyView() {
        super.onDestroyView()
        mapView?.onDestroy()
    }


}
