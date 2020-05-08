package com.example.coronawatch

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.geojson.Feature
import com.mapbox.geojson.FeatureCollection
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions
import com.mapbox.mapboxsdk.location.LocationComponentOptions
import com.mapbox.mapboxsdk.location.modes.CameraMode
import com.mapbox.mapboxsdk.location.modes.RenderMode
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.style.expressions.Expression
import com.mapbox.mapboxsdk.style.expressions.Expression.*
import com.mapbox.mapboxsdk.style.layers.CircleLayer
import com.mapbox.mapboxsdk.style.layers.Property.NONE
import com.mapbox.mapboxsdk.style.layers.Property.VISIBLE
import com.mapbox.mapboxsdk.style.layers.PropertyFactory.*
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource
import kotlinx.android.synthetic.main.fragment_map.*
import java.net.URISyntaxException
import java.util.*
import kotlin.collections.ArrayList

class MapFragment : Fragment(), PermissionsListener {

    private var mapView: MapView? = null
    private lateinit var mapboxMap: MapboxMap
    private var permissionsManager: PermissionsManager = PermissionsManager(this)
    private lateinit var mContext:Context
    var detached : Boolean = true
    var countriesData = true
    var features = arrayListOf<Feature>()
    val layers = arrayListOf("cases", "deaths", "recovered", "algeriaCases", "algeriaDeaths", "algeriaRecovered")
    var zonesAlgeriaData = arrayListOf<ZoneData>()

    private val listCountries = arrayListOf("آروبا", "أذربيجان", "أرمينيا", "أسبانيا", "أستراليا", "أفغانستان", "ألبانيا", "ألمانيا", "أنتيجوا وبربودا", "أنجولا", "أنجويلا", "أندورا", "أورجواي", "أوزبكستان", "أوغندا", "أوكرانيا", "أيرلندا", "أيسلندا", "اثيوبيا", "اريتريا", "استونيا", "اسرائيل", "الأرجنتين", "الأردن", "الاكوادور", "الامارات العربية المتحدة", "الباهاما", "البحرين", "البرازيل", "البرتغال", "البوسنة والهرسك", "الجابون", "الجبل الأسود", "الجزائر", "الدانمرك", "الرأس الأخضر", "السلفادور", "السنغال", "السودان", "السويد", "الصحراء الغربية", "الصومال", "الصين", "العراق", "الفاتيكان", "الفيلبين", "القطب الجنوبي", "الكاميرون", "الكونغو - برازافيل", "الكويت", "المجر", "المحيط الهندي البريطاني", "المغرب", "المقاطعات الجنوبية الفرنسية", "المكسيك", "المملكة العربية السعودية", "المملكة المتحدة", "النرويج", "النمسا", "النيجر", "الهند", "الولايات المتحدة الأمريكية", "اليابان", "اليمن", "اليونان", "اندونيسيا", "ايران", "ايطاليا", "بابوا غينيا الجديدة", "باراجواي", "باكستان", "بالاو", "بتسوانا", "بتكايرن", "بربادوس", "برمودا", "بروناي", "بلجيكا", "بلغاريا", "بليز", "بنجلاديش", "بنما", "بنين", "بوتان", "بورتوريكو", "بوركينا فاسو", "بوروندي", "بولندا", "بوليفيا", "بولينيزيا الفرنسية", "بيرو", "تانزانيا", "تايلند", "تايوان", "تركمانستان", "تركيا", "ترينيداد وتوباغو", "تشاد", "توجو", "توفالو", "توكيلو", "تونجا", "تونس", "تيمور الشرقية", "جامايكا", "جبل طارق", "جرينادا", "جرينلاند", "جزر أولان", "جزر الأنتيل الهولندية", "جزر الترك وجايكوس", "جزر القمر", "جزر الكايمن", "جزر المارشال", "جزر الملديف", "جزر الولايات المتحدة البعيدة الصغيرة", "جزر سليمان", "جزر فارو", "جزر فرجين الأمريكية", "جزر فرجين البريطانية", "جزر فوكلاند", "جزر كوك", "جزر كوكوس", "جزر ماريانا الشمالية", "جزر والس وفوتونا", "جزيرة الكريسماس", "جزيرة بوفيه", "جزيرة مان", "جزيرة نورفوك", "جزيرة هيرد وماكدونالد", "جمهورية افريقيا الوسطى", "جمهورية التشيك", "جمهورية الدومينيك", "جمهورية الكونغو الديمقراطية", "جمهورية جنوب افريقيا", "جواتيمالا", "جوادلوب", "جوام", "جورجيا", "جورجيا الجنوبية وجزر ساندويتش الجنوبية", "جيبوتي", "جيرسي", "دومينيكا", "رواندا", "روسيا", "روسيا البيضاء", "رومانيا", "روينيون", "زامبيا", "زيمبابوي", "ساحل العاج", "ساموا", "ساموا الأمريكية", "سان مارينو", "سانت بيير وميكولون", "سانت فنسنت وغرنادين", "سانت كيتس ونيفيس", "سانت لوسيا", "سانت مارتين", "سانت هيلنا", "ساو تومي وبرينسيبي", "سريلانكا", "سفالبارد وجان مايان", "سلوفاكيا", "سلوفينيا", "سنغافورة", "سوازيلاند", "سوريا", "سورينام", "سويسرا", "سيراليون", "سيشل", "شيلي", "صربيا", "صربيا والجبل الأسود", "طاجكستان", "عمان", "غامبيا", "غانا", "غويانا", "غيانا", "غينيا", "غينيا الاستوائية", "غينيا بيساو", "فانواتو", "فرنسا", "فلسطين", "فنزويلا", "فنلندا", "فيتنام", "فيجي", "قبرص", "قرغيزستان", "قطر", "كازاخستان", "كاليدونيا الجديدة", "كرواتيا", "كمبوديا", "كندا", "كوبا", "كوريا الجنوبية", "كوريا الشمالية", "كوستاريكا", "كولومبيا", "كيريباتي", "كينيا", "لاتفيا", "لاوس", "لبنان", "لوكسمبورج", "ليبيا", "ليبيريا", "ليتوانيا", "ليختنشتاين", "ليسوتو", "مارتينيك", "ماكاو الصينية", "مالطا", "مالي", "ماليزيا", "مايوت", "مدغشقر", "مصر", "مقدونيا", "ملاوي", "منطقة غير معرفة", "منغوليا", "موريتانيا", "موريشيوس", "موزمبيق", "مولدافيا", "موناكو", "مونتسرات", "ميانمار", "ميكرونيزيا", "ناميبيا", "نورو", "نيبال", "نيجيريا", "نيكاراجوا", "نيوزيلاندا", "نيوي", "هايتي", "هندوراس", "هولندا", "هونج كونج الصينية")
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
            this.mapboxMap = mapboxMap
            mapboxMap.setStyle(Style.Builder().fromUri("mapbox://styles/ali31/ck8sx67n92lia1io8cl38v9fi")) {

        // Custom map style has been loaded and map is now ready
            Log.i("Succes", "Map loaded Succefully")
            enableLocationComponent(it)

            getCountriesData(it)
            getCountryData("DZ", it)

        }

            mapboxMap.addOnMapClickListener {
                if (countriesData){
                    val geocoder = Geocoder(activity, Locale("ar"))
                    val adresses = geocoder.getFromLocation(it.latitude, it.longitude, 1)
                    Log.i("adresses", adresses.toString())
                    if (!adresses.isEmpty() && adresses[0].countryCode != null){
                        Toast.makeText(activity, adresses[0].countryCode, Toast.LENGTH_LONG).show()
                        val intent = Intent(activity, StatsActivity::class.java)
                        intent.putExtra("isCountry", true)
                        intent.putExtra("countryCode", adresses[0].countryCode)
                        intent.putExtra("countryName", adresses[0].countryName)
                        startActivity(intent)
                    }
                }else{
                    val zoneClicked : Int = zoneClicked(it)
                    if (zoneClicked != -1){
                        Toast.makeText(mContext, zoneClicked.toString(), Toast.LENGTH_LONG).show()
                        val intent = Intent(activity, StatsActivity::class.java)
                        intent.putExtra("zoneId", zoneClicked)
                        intent.putExtra("isCountry", false)
                        startActivity(intent)
                    }
                }

                true
            }

        }

        val filterIcon: Spinner = filterIconView

        ArrayAdapter.createFromResource(
            mContext,
            R.array.graph_menu,
            R.layout.spinner_item_layout
        ).also { adapter ->
            adapter.setDropDownViewResource(R.layout.spinner_item_layout)
            filterIcon.adapter = adapter
        }

        val showAlgeriaData = changeDataDisplayView

        showAlgeriaData.setOnClickListener{
            countriesData = !countriesData
            if (countriesData){
                showDataOnMap(layers[0], mapboxMap.style!!)
            }else{
                showDataOnMap(layers[3], mapboxMap.style!!)
            }
        }



        val searchCountry = searchCountryView
        val adapter = ArrayAdapter<String>(activity!!, android.R.layout.simple_list_item_1, listCountries)
        searchCountry.setAdapter(adapter)
        searchCountry.setOnItemClickListener { parent, view, position, id ->
            val geocoder = Geocoder(activity, Locale("ar"))
            val adresses = geocoder.getFromLocationName(parent.getItemAtPosition(position).toString(), 1)
            val position = CameraPosition.Builder()
                .target(LatLng(adresses[0].latitude, adresses[0].longitude))
                .zoom(3.0)
                .tilt(20.0)
                .build()

            mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(position))

            Toast.makeText(activity, adresses[0].countryCode, Toast.LENGTH_LONG).show()
        }

    }

    //afficher les data dans la map
    private fun showDataOnMap(type : String, loadedMapStyle : Style){


        for (layer in layers){

            if (loadedMapStyle.getLayer(layer) != null){
                loadedMapStyle.getLayer(layer)!!.setProperties(visibility(NONE))
            }

        }

        if (loadedMapStyle.getLayer(type) != null)
            loadedMapStyle.getLayer(type)!!.setProperties(visibility(VISIBLE))
    }


    //ajouter les données des cas infectés dans la map
    fun addCasesOnMap(loadedMapStyle: Style){

        val layer = layers[0]

        val features = FeatureCollection.fromFeatures(features)
        try {
            loadedMapStyle.addSource(
                GeoJsonSource(
                    layer,
                    features
                )
            )
        } catch (uriSyntaxException: URISyntaxException) {
            Log.i("Check the URL %s", uriSyntaxException.message)
        }

        val circles = CircleLayer(layer, layer)
        circles.setProperties(
            circleColor(Color.parseColor("#40DA1212")),
            circleStrokeWidth(2f),
            circleStrokeColor(Color.parseColor("#90B10000")),
            circleRadius(
                interpolate(linear(), zoom(),
                    stop(0, normalize(get(layer), abs(1), 1, 10)),
                    stop(1, normalize(get(layer), abs(1), 1, 10)),
                    stop(6, normalize(get(layer), abs(3), 1, 10)),
                    stop(10, normalize(get(layer), abs(5), 1, 10)),
                    stop(12, normalize(get(layer), abs(6), 1, 10))
                )
            )
        )

        loadedMapStyle.addLayer(circles)
    }

    //ajouter les données des cas de deces dans la map
    fun addDeathsOnMap(loadedMapStyle: Style){

        val layer = layers[1]
        val features = FeatureCollection.fromFeatures(features)
        try {
            loadedMapStyle.addSource(
                GeoJsonSource(
                    layer,
                    features
                )
            )
        } catch (uriSyntaxException: URISyntaxException) {
            Log.i("Check the URL %s", uriSyntaxException.message)
        }

        val circles = CircleLayer(layer, layer)
        circles.setProperties(
            circleColor(Color.parseColor("#606d3dce")),
            circleStrokeWidth(2f),
            circleStrokeColor(Color.parseColor("#d1482e7c")),
            circleRadius(
                interpolate(linear(), zoom(),
                    stop(0, normalize(get(layer), abs(1), 3, 10)),
                    stop(1, normalize(get(layer), abs(1), 3, 10)),
                    stop(6, normalize(get(layer), abs(3), 3, 10)),
                    stop(10, normalize(get(layer), abs(5), 3, 10)),
                    stop(12, normalize(get(layer), abs(6), 3, 10))
                )
            )
        )

        loadedMapStyle.addLayer(circles)
    }

    //ajouter les données des cas cared dans la map
    fun addRecoveredOnMap(loadedMapStyle: Style){

        val layer = layers[2]
        val features = FeatureCollection.fromFeatures(features)
        try {
            loadedMapStyle.addSource(
                GeoJsonSource(
                    layer,
                    features
                )
            )
        } catch (uriSyntaxException: URISyntaxException) {
            Log.i("Check the URL %s", uriSyntaxException.message)
        }

        val circles = CircleLayer(layer, layer)
        circles.setProperties(
            circleColor(Color.parseColor("#601bb043")),
            circleStrokeWidth(2f),
            circleStrokeColor(Color.parseColor("#d1159a39")),
            circleRadius(
                interpolate(linear(), zoom(),
                    stop(0, normalize(get(layer), abs(1), 3, 10)),
                    stop(1, normalize(get(layer), abs(1), 3, 10)),
                    stop(6, normalize(get(layer), abs(3), 3, 10)),
                    stop(10, normalize(get(layer), abs(5), 3, 10)),
                    stop(12, normalize(get(layer), abs(6), 3, 10))
                )
            )
        )

        loadedMapStyle.addLayer(circles)
    }

    fun normalize (value : Expression, zoom : Expression, min : Int, max : Int)  : Expression
    {


        val newVal : Expression = division(subtract(sqrt(value), abs(min)), subtract(abs(max), abs(min)))
        return product(newVal, pow(abs(2), zoom))
    }


    //recupere les données relatives a tous les pays
    private fun getCountriesData(loadedMapStyle: Style){
        val urlCountriesData = "${resources.getString(R.string.host)}/api/v0/zone/groupByCountry"

        Log.i("getting data", urlCountriesData)
        // Request a string response from the provided URL.
        val jsonRequestNbrDeaths = JsonObjectRequest(
            Request.Method.GET, urlCountriesData, null,
            Response.Listener { response ->
                val count : Int = response.getInt("count")
                val items = response.getJSONArray("items")
                for (i in 0 until count){
                    val item = items.getJSONObject(i)
                    val nbrCases : Int = item.getInt("totalConfirmed")
                    val nbrDeaths : Int = item.getInt("totalDead")
                    val nbrRecovered : Int = item.getInt("totalRecovered")
                    val countryCode : String = item.getString("counrtyCode")
                    val latLng = CountryInfo.getLatLng(countryCode)
                    val geometry = Point.fromLngLat(latLng.longitude, latLng.latitude)
                    val feature : Feature = Feature.fromGeometry(geometry)
                    feature.addNumberProperty(layers[0], nbrCases)
                    feature.addNumberProperty(layers[1], nbrDeaths)
                    feature.addNumberProperty(layers[2], nbrRecovered)
                    features.add(feature)
                }
                addCasesOnMap(loadedMapStyle)
                addDeathsOnMap(loadedMapStyle)
                addRecoveredOnMap(loadedMapStyle)


            },
            Response.ErrorListener { Log.d("Error", "Request error") })

        RequestHandler.getInstance(mContext).addToRequestQueue(jsonRequestNbrDeaths)
    }

    //recupere les données relatives a un pays
    private fun getCountryData(countryCode : String, loadedMapStyle: Style){
        val urlCountriesData = "${resources.getString(R.string.host)}/api/v0/zone/country?cc=$countryCode"
        val features = arrayListOf<Feature>()

        // Request a string response from the provided URL.
        val jsonRequestNbrDeaths = JsonObjectRequest(
            Request.Method.GET, urlCountriesData, null,
            Response.Listener { response ->
                val count : Int = response.getInt("count")
                val items = response.getJSONArray("rows")
                for (i in 0 until count) {
                    val item = items.getJSONObject(i)
                    val id = item.getInt("zoneId")
                    val datazone = item.getJSONArray("dataZones").getJSONObject(0)
                    val nbrCases: Int = datazone.getInt("totalConfirmed")
                    val nbrDeaths: Int = datazone.getInt("totalDead")
                    val nbrRecovered: Int = datazone.getInt("totalRecovered")
                    val latLng = LatLng(item.getDouble("latitude"), item.getDouble("longitude"))
                    val zoneData = ZoneData(id, latLng, nbrCases, nbrDeaths, nbrRecovered)
                    zonesAlgeriaData.add(zoneData)
                    val geometry = Point.fromLngLat(latLng.longitude, latLng.latitude)
                    val feature: Feature = Feature.fromGeometry(geometry)
                    feature.addNumberProperty(layers[3], nbrCases)
                    feature.addNumberProperty(layers[4], nbrDeaths)
                    feature.addNumberProperty(layers[5], nbrRecovered)
                    features.add(feature)
                }

                addCountryCasesOnMap(features, loadedMapStyle)
                addCountryDeathsOnMap(features, loadedMapStyle)
                addCountryRecoveredOnMap(features, loadedMapStyle)

                val filterIcon: Spinner = filterIconView

                filterIcon.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                    override fun onNothingSelected(parent: AdapterView<*>?) {

                    }
                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                        if (countriesData){

                                showDataOnMap(layers[position], loadedMapStyle)

                        }else{

                                showDataOnMap(layers[3+position], loadedMapStyle)

                        }
                    }
                }

                showDataOnMap(layers[0], loadedMapStyle)

            },
            Response.ErrorListener { Log.d("Error", "Request error") })

        RequestHandler.getInstance(mContext).addToRequestQueue(jsonRequestNbrDeaths)
    }

    //ajouter les données des cas infected dans la map
    fun addCountryCasesOnMap(features: ArrayList<Feature>, loadedMapStyle : Style) {
        val layer = layers[3]


        val features = FeatureCollection.fromFeatures(features)
        try {
            loadedMapStyle.addSource(
                GeoJsonSource(
                    layer,
                    features
                )
            )
        } catch (uriSyntaxException: URISyntaxException) {
            Log.i("Check the URL %s", uriSyntaxException.message)
        }

        val circles = CircleLayer(layer, layer)
        circles.setProperties(
            circleColor(Color.parseColor("#40DA1212")),
            circleStrokeWidth(2f),
            circleStrokeColor(Color.parseColor("#90B10000")),
            circleRadius(
                interpolate(linear(), zoom(),
                    stop(0, normalize(get(layer), abs(1), 5, 25)),
                    stop(1, normalize(get(layer), abs(1), 5, 25)),
                    stop(6, normalize(get(layer), abs(3), 5, 25)),
                    stop(10, normalize(get(layer), abs(5), 5, 25)),
                    stop(12, normalize(get(layer), abs(6), 5, 25))
                )
            )
        )

        loadedMapStyle.addLayer(circles)
    }

    //ajouter les données des cas de deces dans la map
    fun addCountryDeathsOnMap(features: ArrayList<Feature>, loadedMapStyle : Style) {

        val layer = layers[4]

        val features = FeatureCollection.fromFeatures(features)


        try {
            loadedMapStyle.addSource(
                GeoJsonSource(
                    layer,
                    features
                )
            )
        } catch (uriSyntaxException: URISyntaxException) {
            Log.i("Check the URL %s", uriSyntaxException.message)
        }

        val circles = CircleLayer(layer, layer)
        circles.setProperties(
            circleColor(Color.parseColor("#606d3dce")),
            circleStrokeWidth(2f),
            circleStrokeColor(Color.parseColor("#d1482e7c")),
            circleRadius(
                interpolate(linear(), zoom(),
                    stop(0, normalize(get(layer), abs(1), 10, 20)),
                    stop(1, normalize(get(layer), abs(1), 10, 20)),
                    stop(6, normalize(get(layer), abs(6), 10, 20)),
                    stop(10, normalize(get(layer), abs(10), 10, 20)),
                    stop(12, normalize(get(layer), abs(12), 10, 20))
                )
            )
        )

        loadedMapStyle.addLayer(circles)
    }

    //ajouter les données des cas cared dans la map
    fun addCountryRecoveredOnMap(features: ArrayList<Feature>, loadedMapStyle : Style) {

        val layer = layers[5]

        val features = FeatureCollection.fromFeatures(features)
        try {
            loadedMapStyle.addSource(
                GeoJsonSource(
                    layer,
                    features
                )
            )
        } catch (uriSyntaxException: URISyntaxException) {
            Log.i("Check the URL %s", uriSyntaxException.message)
        }

        val circles = CircleLayer(layer, layer)
        circles.setProperties(
            circleColor(Color.parseColor("#601bb043")),
            circleStrokeWidth(2f),
            circleStrokeColor(Color.parseColor("#d1159a39")),
            circleRadius(
                interpolate(linear(), zoom(),
                    stop(0, normalize(get(layer), abs(1), 10, 20)),
                    stop(1, normalize(get(layer), abs(1), 10, 20)),
                    stop(6, normalize(get(layer), abs(5), 10, 20)),
                    stop(10, normalize(get(layer), abs(8), 10, 20)),
                    stop(12, normalize(get(layer), abs(10), 10, 20))
                )
            )
        )

        loadedMapStyle.addLayer(circles)
    }


    //lorsqu'un cclick est effectué sur la map
    private fun zoneClicked(clickLatLng : LatLng) : Int{
        var closestZone : Int = -1
        var closestDistance : Double = Double.MAX_VALUE
        for (zone in zonesAlgeriaData){
            val d = zone.latLng.distanceTo(clickLatLng) / 1000
            if (d < 15 && d < closestDistance){
                closestZone = zone.id
                closestDistance = d
            }
        }

        return closestZone
    }

    @SuppressLint("MissingPermission")
    private fun enableLocationComponent(loadedMapStyle: Style) {
        // Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(context)) {

            // Create and customize the LocationComponent's options
            val customLocationComponentOptions = LocationComponentOptions.builder(context!!)
                .trackingGesturesManagement(true)
                .accuracyColor(ContextCompat.getColor(context!!, R.color.darkblue))
                .build()

            val locationComponentActivationOptions = LocationComponentActivationOptions.builder(context!!, loadedMapStyle)
                .locationComponentOptions(customLocationComponentOptions)
                .build()

            // Get an instance of the LocationComponent and then adjust its settings
            mapboxMap.locationComponent.apply {

                // Activate the LocationComponent with options
                activateLocationComponent(locationComponentActivationOptions)

                // Enable to make the LocationComponent visible
                isLocationComponentEnabled = true

                // Set the LocationComponent's camera mode
                cameraMode = CameraMode.TRACKING

                // Set the LocationComponent's render mode
                renderMode = RenderMode.COMPASS
            }
        } else {
            permissionsManager = PermissionsManager(this)
            permissionsManager.requestLocationPermissions(activity)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onExplanationNeeded(permissionsToExplain: List<String>) {
        Toast.makeText(activity, "الرجاء تفعيل الصلاحيات", Toast.LENGTH_LONG).show()
    }

    override fun onPermissionResult(granted: Boolean) {
        if (granted) {
            enableLocationComponent(mapboxMap.style!!)
        } else {
            Toast.makeText(activity, "هاتقك لا يملك كل الصلاحيات", Toast.LENGTH_LONG).show()
        }
    }

    override fun onStart() {
        super.onStart()
        mapView?.onStart()
        Log.i("life cycle", "start")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mapView?.onDestroy()
        Log.i("life cycle", "destroy view")
    }

    override fun onResume() {
        super.onResume()
        mapView?.onResume()
        Log.i("life cycle", "resume")
    }

    override fun onPause() {
        super.onPause()
        mapView?.onPause()
        Log.i("life cycle", "pause")
    }

    override fun onStop() {
        super.onStop()
        mapView?.onStop()
        Log.i("life cycle", "stop")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView?.onSaveInstanceState(outState)
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView?.onDestroy()
        Log.i("life cycle", "destroy")
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView?.onLowMemory()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
        detached = false
    }

    override fun onDetach() {
        super.onDetach()
        detached = true
    }

}
