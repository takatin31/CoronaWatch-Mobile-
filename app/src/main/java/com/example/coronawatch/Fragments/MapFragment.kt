package com.example.coronawatch.Fragments

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.PointF
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
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.example.coronawatch.Activities.CountriesActivity
import com.example.coronawatch.Activities.StatsActivity
import com.example.coronawatch.Adapters.FilterAdapter
import com.example.coronawatch.DataClasses.*
import com.example.coronawatch.R
import com.example.coronawatch.Request.RequestHandler
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
import com.mapbox.mapboxsdk.style.layers.PropertyFactory
import com.mapbox.mapboxsdk.style.layers.PropertyFactory.*
import com.mapbox.mapboxsdk.style.layers.SymbolLayer
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource
import com.wangjie.rapidfloatingactionbutton.contentimpl.labellist.RFACLabelItem
import com.wangjie.rapidfloatingactionbutton.contentimpl.labellist.RapidFloatingActionContentLabelList
import kotlinx.android.synthetic.main.fragment_map.*
import java.net.URISyntaxException
import java.util.*
import kotlin.collections.ArrayList

class MapFragment : Fragment(), PermissionsListener, RapidFloatingActionContentLabelList.OnRapidFloatingActionContentLabelListListener<RFACLabelItem<Int>> {

    private var mapView: MapView? = null
    private lateinit var mapboxMap: MapboxMap
    private var permissionsManager: PermissionsManager = PermissionsManager(this)
    private lateinit var mContext:Context
    var detached : Boolean = true
    var countriesData = true
    var features = arrayListOf<Feature>()
    var centerFeatures = arrayListOf<Feature>()
    val layers = arrayListOf("cases", "deaths", "recovered", "algeriaCases", "algeriaDeaths", "algeriaRecovered", "algeriaDangerZone", "centers")
    var zonesAlgeriaData = arrayListOf<Zone>()
    var isFABOpen = false
    var filterList = arrayListOf<Filter>()
    var riskZoneList = arrayListOf<Zone>()
    var dangerZoneMode : Boolean = false
    var maxValueCases = 0
    var maxValueDeaths = 0
    var maxValueRecovered = 0
    var maxValueCasesAlgeria = 0
    var maxValueDeathsAlgeria = 0
    var maxValueRecoveredAlgeria = 0
    var isCentersShown = false

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
                //enableLocationComponent(it)



                getCountriesData(it)
                getCountryData("DZ", it)
                getCentersData(it)


                it.addImage(("marker_icon"), BitmapFactory.decodeResource(
                    resources, R.drawable.center_png));


        }

            mapboxMap.addOnMapClickListener {
                if (isCentersShown){
                    handleClickIcon(mapboxMap.projection.toScreenLocation(it))
                }else {
                    if (dangerZoneMode) {
                        val zoneClicked: Int = zoneClicked(it, riskZoneList)
                        if (zoneClicked != -1) {
                            Toast.makeText(mContext, zoneClicked.toString(), Toast.LENGTH_LONG)
                                .show()
                            val intent = Intent(activity, StatsActivity::class.java)
                            val dangerZone = riskZoneList[zoneClicked] as RiskZone
                            intent.putExtra("riskZoneId", dangerZone.zoneRiskId)
                            intent.putExtra("degre", dangerZone.degre)
                            intent.putExtra("reason", dangerZone.cause)
                            intent.putExtra("isCountry", false)
                            intent.putExtra("isDangerZone", true)
                            startActivity(intent)
                        }
                    } else {
                        if (countriesData) {
                            val geocoder = Geocoder(activity, Locale("ar"))
                            val adresses = geocoder.getFromLocation(it.latitude, it.longitude, 1)
                            Log.i("adresses", adresses.toString())
                            if (!adresses.isEmpty() && adresses[0].countryCode != null) {
                                Toast.makeText(activity, adresses[0].countryCode, Toast.LENGTH_LONG)
                                    .show()
                                val intent = Intent(activity, StatsActivity::class.java)
                                intent.putExtra("isCountry", true)
                                intent.putExtra("countryCode", adresses[0].countryCode)
                                intent.putExtra("countryName", adresses[0].countryName)
                                intent.putExtra("isDangerZone", false)
                                startActivity(intent)
                            }
                        } else {

                            val zoneClicked: Int = zoneClicked(it, zonesAlgeriaData)
                            if (zoneClicked != -1) {
                                Toast.makeText(mContext, zoneClicked.toString(), Toast.LENGTH_LONG)
                                    .show()
                                val intent = Intent(activity, StatsActivity::class.java)
                                intent.putExtra("zoneId", zonesAlgeriaData[zoneClicked].id)
                                intent.putExtra("isCountry", false)
                                intent.putExtra("isDangerZone", false)
                                startActivity(intent)
                            }

                        }
                    }
                }


                true
            }

        }

        initFilterList()

        val filterAdapter = FilterAdapter(mContext, filterList)

        filterIconView.adapter = filterAdapter

        menuFloatingBtn.setOnClickListener {
            if(!isFABOpen){
                showFABMenu()
            }else{
                closeFABMenu()
            }
        }

        val showAlgeriaData = algeriaFloatingBtn

        showAlgeriaData.setOnClickListener{
            isCentersShown = false
            dangerZoneMode = false
            centreAcceulsInfoCardView.visibility = View.GONE
            countriesData = !countriesData
            if (countriesData){
                showDataOnMap(layers[0], mapboxMap.style!!)
                showAlgeriaData.setImageResource(R.drawable.ic_algeria)
            }else{
                showDataOnMap(layers[3], mapboxMap.style!!)
                showAlgeriaData.setImageResource(R.drawable.ic_world)
            }
        }

        dangerZoneFloatingBtn.setOnClickListener {
            isCentersShown = false
            dangerZoneMode = true
            centreAcceulsInfoCardView.visibility = View.GONE
            showDataOnMap(layers[6], mapboxMap.style!!)
        }

        countriesStatsFloatingBtn.setOnClickListener {
            val intent = Intent(activity, CountriesActivity::class.java)
            startActivity(intent)
        }

        centersFloatingBtn.setOnClickListener {
            isCentersShown = true
            dangerZoneMode = false
            showDataOnMap(layers[7], mapboxMap.style!!)
            centreAcceulsInfoCardView.visibility = View.VISIBLE
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

    private fun showFABMenu(){
        isFABOpen=true;
        algeriaFloatingBtn.animate().translationY(-resources.getDimension(R.dimen.standard_65))
        dangerZoneFloatingBtn.animate().translationY(-resources.getDimension(R.dimen.standard_125))
        centersFloatingBtn.animate().translationY(-resources.getDimension(R.dimen.standard_185))
        countriesStatsFloatingBtn.animate().translationY(-resources.getDimension(R.dimen.standard_245))

        titleAlgeriaZone.animate().translationY(-resources.getDimension(R.dimen.standard_65))
        titleDangerZone.animate().translationY(-resources.getDimension(R.dimen.standard_125))
        titleCenters.animate().translationY(-resources.getDimension(R.dimen.standard_185))
        titleStatsCountries.animate().translationY(-resources.getDimension(R.dimen.standard_245))

        titleAlgeriaZone.visibility = View.VISIBLE
        titleDangerZone.visibility = View.VISIBLE
        titleCenters.visibility = View.VISIBLE
        titleStatsCountries.visibility = View.VISIBLE
    }

    private fun closeFABMenu(){
        isFABOpen=false;
        algeriaFloatingBtn.animate().translationY(0f)
        dangerZoneFloatingBtn.animate().translationY(0f)
        centersFloatingBtn.animate().translationY(0f)
        countriesStatsFloatingBtn.animate().translationY(0f)

        titleAlgeriaZone.animate().translationY(0f)
        titleDangerZone.animate().translationY(0f)
        titleCenters.animate().translationY(0f)
        titleStatsCountries.animate().translationY(0f)

        titleAlgeriaZone.visibility = View.GONE
        titleDangerZone.visibility = View.GONE
        titleCenters.visibility = View.GONE
        titleStatsCountries.visibility = View.GONE
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
                interpolate(
                    linear(), zoom(),
                    stop(0, mapV(get(layer), 0, maxValueCases, 5, 10)),
                    stop(1, mapV(get(layer), 0, maxValueCases, 6, 12)),
                    stop(3, mapV(get(layer), 0, maxValueCases, 10, 50)),
                    stop(5, mapV(get(layer), 0, maxValueCases, 15, 70)),
                    stop(10, mapV(get(layer), 0, maxValueCases, 20, 85))
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
                    stop(0, mapV(get(layer), 0, maxValueDeaths, 5, 10)),
                    stop(1, mapV(get(layer), 0, maxValueDeaths, 6, 12)),
                    stop(3, mapV(get(layer), 0, maxValueDeaths, 10, 50)),
                    stop(5, mapV(get(layer), 0, maxValueDeaths, 15, 70)),
                    stop(10, mapV(get(layer), 0, maxValueDeaths, 20, 85))
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
                    stop(0, mapV(get(layer), 0, maxValueRecovered, 5, 10)),
                    stop(1, mapV(get(layer), 0, maxValueRecovered, 6, 12)),
                    stop(3, mapV(get(layer), 0, maxValueRecovered, 10, 50)),
                    stop(5, mapV(get(layer), 0, maxValueRecovered, 15, 70)),
                    stop(10, mapV(get(layer), 0, maxValueRecovered, 20, 85))
                )
            )
        )

        loadedMapStyle.addLayer(circles)
    }

    fun normalize (value : Expression, zoom : Expression, min : Int, max : Int)  : Expression
    {
        val newVal : Expression = division(abs(subtract(sqrt(value), abs(min))), subtract(abs(max), abs(min)))
        return product(newVal, pow(abs(2), zoom))
    }


    fun mapV(oldValue : Expression, oldMin : Int, oldMax : Int, newMin : Int, newMax : Int) : Expression{
        val part1 = subtract(oldValue, abs(oldMin))
        val part2 = subtract(abs(newMax), abs(newMin))
        val part3 = product(part1, part2)
        val part4 = subtract(abs(oldMax), abs(oldMin))
        val newV = sum(abs(newMin), division(part3, part4))
        //val NewValue = (((oldValue - oldMin) * (newMax - newMin)) / (oldMax - oldMin)) + newMin
        return newV
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
                for (i in 0 until items.length()){
                    val item = items.getJSONObject(i)
                    val nbrCases : Int = item.getInt("totalConfirmed")
                    val nbrDeaths : Int = item.getInt("totalDead")
                    val nbrRecovered : Int = item.getInt("totalRecovered")

                    if (nbrCases > maxValueCases){
                        maxValueCases = nbrCases
                    }

                    if (nbrDeaths > maxValueDeaths){
                        maxValueDeaths = nbrDeaths
                    }

                    if (nbrRecovered > maxValueRecovered){
                        maxValueRecovered = nbrRecovered
                    }
                    val countryCode : String = item.getString("counrtyCode")
                    Log.i("countryCode", "Code is : $countryCode")
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
                for (i in 0 until items.length()) {
                    val item = items.getJSONObject(i)
                    val id = item.getInt("zoneId")
                    Log.i("itemmm", item.toString())
                    if (item.getJSONArray("dataZones").length() > 0){
                        val datazone = item.getJSONArray("dataZones").getJSONObject(0)
                        val nbrCases: Int = datazone.getInt("totalConfirmed")
                        val nbrDeaths: Int = datazone.getInt("totalDead")
                        val nbrRecovered: Int = datazone.getInt("totalRecovered")

                        if (nbrCases > maxValueCasesAlgeria){
                            maxValueCasesAlgeria = nbrCases
                        }

                        if (nbrDeaths > maxValueDeathsAlgeria){
                            maxValueDeathsAlgeria = nbrDeaths
                        }

                        if (nbrRecovered > maxValueRecoveredAlgeria){
                            maxValueRecoveredAlgeria = nbrRecovered
                        }

                        val latLng = LatLng(item.getDouble("latitude"), item.getDouble("longitude"))
                        val zoneData = ZoneData(
                            id,
                            latLng,
                            nbrCases,
                            nbrDeaths,
                            nbrRecovered
                        )
                        zonesAlgeriaData.add(zoneData)
                        val geometry = Point.fromLngLat(latLng.longitude, latLng.latitude)
                        val feature: Feature = Feature.fromGeometry(geometry)
                        feature.addNumberProperty(layers[3], nbrCases)
                        feature.addNumberProperty(layers[4], nbrDeaths)
                        feature.addNumberProperty(layers[5], nbrRecovered)
                        features.add(feature)
                    }

                }

                addCountryCasesOnMap(features, loadedMapStyle)
                addCountryDeathsOnMap(features, loadedMapStyle)
                addCountryRecoveredOnMap(features, loadedMapStyle)

                getDangerZone(loadedMapStyle)

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

            },
            Response.ErrorListener { Log.d("Error", "Request error") })

        RequestHandler.getInstance(mContext).addToRequestQueue(jsonRequestNbrDeaths)
    }

    fun getDangerZone(loadedMapStyle: Style){
        val urlCountriesData = "${resources.getString(R.string.host)}/api/v0/zoneRisque"
        val pref = mContext.getSharedPreferences(resources.getString(R.string.shared_pref),0)
        val token = pref.getString("token", "")

        val features = arrayListOf<Feature>()
        Log.i("getDangerZone", "we will get data here")
        // Request a string response from the provided URL.
        val jsonRequestNbrDeaths = object : JsonObjectRequest(
            Request.Method.GET, urlCountriesData, null,
            Response.Listener { response ->
                val items = response.getJSONObject("items")
                val count = items.getInt("count")
                val zones = items.getJSONArray("rows")
                for (i in 0 until zones.length()){
                    val zoneRisk = zones.getJSONObject(i)
                    val zoneRiskId = zoneRisk.getInt("zoneRisqueId")
                    val zoneRiskDiameter = 20f//zoneRisk.getDouble("diametre").toFloat()
                    val zoneRiskCause = zoneRisk.getString("cause")
                    val zoneRiskDegre = zoneRisk.getInt("degre")
                    val zoneRiskZoneId = zoneRisk.getInt("zoneZoneId")
                    val zone = zoneRisk.getJSONObject("zone")
                    val latLng = LatLng(zone.getDouble("latitude"), zone.getDouble("longitude"))
                    riskZoneList.add(RiskZone(zoneRiskId, zoneRiskDiameter, zoneRiskCause, zoneRiskDegre, zoneRiskZoneId, latLng))
                    val zoneRiskLat = zoneRisk.getJSONObject("zone").getDouble("latitude")
                    val zoneRiskLon = zoneRisk.getJSONObject("zone").getDouble("longitude")

                    val geometry = Point.fromLngLat(zoneRiskLon, zoneRiskLat)
                    val feature: Feature = Feature.fromGeometry(geometry)

                    feature.addNumberProperty(layers[6], zoneRiskDiameter)
                    features.add(feature)
                }
                Log.i("featuresDangerZone", features.size.toString())
                addCountryDangerZoneOnMap(features, loadedMapStyle)

                showDataOnMap(layers[0], loadedMapStyle)
            },
            Response.ErrorListener { Log.d("Error", "Request error") }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()
                headers.put("Authorization", "Bearer $token")
                return headers
            }
        }


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
                    stop(0, mapV(get(layer), 0, maxValueCasesAlgeria, 3, 3)),
                    stop(1, mapV(get(layer), 0, maxValueCasesAlgeria, 4, 5)),
                    stop(3, mapV(get(layer), 0, maxValueCasesAlgeria, 10, 10)),
                    stop(5, mapV(get(layer), 0, maxValueCasesAlgeria, 15, 30)),
                    stop(10, mapV(get(layer), 0, maxValueCasesAlgeria, 20, 45))
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
                    stop(0, mapV(get(layer), 0, maxValueDeathsAlgeria, 3, 3)),
                    stop(1, mapV(get(layer), 0, maxValueDeathsAlgeria, 4, 5)),
                    stop(3, mapV(get(layer), 0, maxValueDeathsAlgeria, 10, 10)),
                    stop(5, mapV(get(layer), 0, maxValueDeathsAlgeria, 15, 30)),
                    stop(10, mapV(get(layer), 0, maxValueDeathsAlgeria, 20, 45))
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
                    stop(0, mapV(get(layer), 0, maxValueRecoveredAlgeria, 3, 3)),
                    stop(1, mapV(get(layer), 0, maxValueRecoveredAlgeria, 4, 5)),
                    stop(3, mapV(get(layer), 0, maxValueRecoveredAlgeria, 10, 10)),
                    stop(5, mapV(get(layer), 0, maxValueRecoveredAlgeria, 15, 30)),
                    stop(10, mapV(get(layer), 0, maxValueRecoveredAlgeria, 20, 45))
                )
            )
        )

        loadedMapStyle.addLayer(circles)
    }

    //ajouter les données des cas cared dans la map
    fun addCountryDangerZoneOnMap(features: ArrayList<Feature>, loadedMapStyle : Style) {

        val layer = layers[6]

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
            circleColor(Color.parseColor("#60403CD3")),
            circleStrokeWidth(2f),
            circleStrokeColor(Color.parseColor("#d10D0A96")),
            circleRadius(get(layer))
        )

        loadedMapStyle.addLayer(circles)
        Log.i("ciiiiiircles", circles.toString())
    }


    //lorsqu'un cclick est effectué sur la map
    private fun zoneClicked(clickLatLng : LatLng, listZones : ArrayList<Zone>) : Int{
        var closestZone : Int = -1
        var closestDistance : Double = Double.MAX_VALUE
        var index = 0
        for (zone in listZones){
            val d = zone.latLng.distanceTo(clickLatLng) / 1000
            if (d < 15 && d < closestDistance){
                closestZone = index
                closestDistance = d
            }
            index++
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
                .accuracyColor(ContextCompat.getColor(context!!,
                    R.color.darkblue
                ))
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

    fun getCentersData(style: Style) {
        val urlData = "${resources.getString(R.string.host)}/api/v0/centreAcceuil"

        // Request a string response from the provided URL.
        val jsonRequestData = JsonObjectRequest(
            Request.Method.GET, urlData, null,
            Response.Listener { response ->
                val items = response.getJSONArray("rows")
                if (items.length() == 0){
                    Toast.makeText(mContext, "There is no centers yet", Toast.LENGTH_SHORT).show()
                }else{

                    for (i in 0 until items.length()){
                        val item = items.getJSONObject(i)
                        val idCentre = item.getInt("centreAcceuilId")
                        val nomCentre = item.getString("nom")
                        val infoCentre = item.getString("information")
                        val cityCentre = item.getString("city")
                        val nbrLitCentre = item.getInt("nbrLitsTotal")
                        val nbrEmptyLitCentre = item.getInt("nbrLitsLibre")
                        val lat = item.getDouble("latitude")
                        val lng = item.getDouble("longitude")
                        val latLng = LatLng(lat, lng)

                        val geometry = Point.fromLngLat(latLng.longitude, latLng.latitude)
                        val feature : Feature = Feature.fromGeometry(geometry)
                        feature.addStringProperty("nom", nomCentre)
                        feature.addStringProperty("info", infoCentre)
                        feature.addStringProperty("city", cityCentre)
                        feature.addNumberProperty("nbrLit", nbrLitCentre)
                        feature.addNumberProperty("nbrLitLibre", nbrEmptyLitCentre)

                        centerFeatures.add(feature)
                    }

                    addMarkers(style)
                    showDataOnMap(layers[0], style)
                }
            },
            Response.ErrorListener { Log.d("Error", "Request error") })

        RequestHandler.getInstance(mContext).addToRequestQueue(jsonRequestData)
    }

    fun addMarkers(style: Style){

        val layer = layers[7]

        val features = FeatureCollection.fromFeatures(centerFeatures)


        try {
            style.addSource(
                GeoJsonSource(
                    layer,
                    features
                )
            )
        } catch (uriSyntaxException: URISyntaxException) {
            Log.i("Check the URL %s", uriSyntaxException.message)
        }

        style.addLayer(
            SymbolLayer(layer, layer)
                .withProperties(
                    PropertyFactory.iconImage("marker_icon"),
                    iconAllowOverlap(true),
                    iconOffset(arrayOf(0f, -8f))
                )
        )
    }

    private fun handleClickIcon(screenPoint: PointF): Boolean {
        val layer = layers[7]
        val features: List<Feature> =
            mapboxMap.queryRenderedFeatures(screenPoint, layer)
        Log.i("feature", features.toString())
        return if (features.isNotEmpty()) {
            // Show the Feature in the TextView to show that the icon is based on the ICON_PROPERTY key/value
            centerTitle.text = features[0].getStringProperty("nom")
            centerInfo.text = features[0].getStringProperty("info")
            centerCity.text = features[0].getStringProperty("city")
            centerNbrLits.text = features[0].getStringProperty("nbrLit")
            centerNbrLitLibre.text = features[0].getStringProperty("nbrLitLibre")

            true
        } else {
            false
        }
    }



    fun initFilterList(){
        filterList.add(Filter(resources.getString(R.string.place_nbr_infected), R.color.cases_color))
        filterList.add(Filter(resources.getString(R.string.place_nbr_deaths), R.color.deaths_color))
        filterList.add(Filter(resources.getString(R.string.place_nbr_cared), R.color.recovered_color))
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

    override fun onRFACItemIconClick(position: Int, item: RFACLabelItem<RFACLabelItem<Int>>?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onRFACItemLabelClick(position: Int, item: RFACLabelItem<RFACLabelItem<Int>>?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


}
