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
    lateinit var mContext:Context
    var detached : Boolean = true
    var countriesData = true
    var features = arrayListOf<Feature>()
    var layers = arrayListOf("cases", "deaths", "recovered", "algeriaCases", "algeriaDeaths", "algeriaRecovered")
    var zonesAlgeriaData = arrayListOf<ZoneData>()

    var listGeoId = arrayListOf("AD","AE","AF","AG","AI","AL","AM","AO","AR","AT","AU","AW","AZ","BA","BB","BD","BE","BF","BG","BH","BI","BJ","BM","BN","BO","BR","BS","BT","BW","BY","BZ","CA","CD","CF","CG","CH","CI","CL","CM","CN","CO","CR","CU","CV","CY","CZ","DE","DJ","DK","DM","DO","DZ","EC","EE","EG","ER","ES","ET","FI","FJ","FK","FO","FR","GA","GD","GE","GG","GH","GI","GL","GM","GN","GQ","GT","GU","GW","GY","HN","HR","HT","HU","ID","IE","IL","IM","IN","IQ","IR","IS","IT","JE","JM","JO","JP","KE","KG","KH","KN","KR","KW","KY","KZ","LA","LB","LC","LI","LK","LR","LT","LU","LV","LY","MA","MC","MD","ME","MG","MK","ML","MM","MN","MP","MR","MS","MT","MU","MV","MW","MX","MY","MZ","NC","NE","NG","NI","NL","NO","NP","NZ","OM","PA","PE","PF","PG","PH","PK","PL","PR","PS","PT","PY","QA","RO","RS","RU","RW","SA","SC","SD","SE","SG","SI","SK","SL","SM","SN","SO","SR","ST","SV","SY","SZ","TC","TD","TG","TH","TL","TN","TR","TT","TW","TZ","UA","UG","US","UY","UZ","VA","VC","VE","VG","VI","VN","XK","YE","ZA","ZM","ZW")
    var listLat = arrayListOf(42.546245,23.424076,33.93911,17.060816,18.220554,41.153332,40.069099,-11.202691999999999,-38.416097,47.516231,-25.274398,12.52111,40.143105,43.915886,13.193887,23.684994,50.503887,12.238333,42.733883,25.930414000000003,-3.3730559999999996,9.30769,32.321384,4.535277,-16.290154,-14.235004,25.03428,27.514162,-22.328474,53.709807,17.189877,56.130366,-4.038333000000001,6.611111,-0.228021,46.818188,7.539989,-35.675146999999996,7.369722,35.86166,4.570868,9.748917,21.521757,16.002082,35.126413,49.817492,51.165690999999995,11.825138,56.26392,15.414999,18.735692999999998,28.033886,-1.8312389999999998,58.595271999999994,26.820553000000004,15.179383999999999,40.463667,9.145,61.92411,-16.578193,-51.796253,61.892635,46.227638,-0.803689,12.262775999999999,42.315407,49.465691,7.946527000000001,36.137741,71.706936,13.443182,9.945587,1.6508009999999997,15.783470999999999,13.444304,11.803749,4.860416000000001,15.199998999999998,45.1,18.971187,47.162494,-0.789275,53.41291,31.046051000000002,54.236107,20.593684,33.223191,32.427908,64.96305100000001,41.87194,49.214439,18.109581,30.585164000000002,36.204824,-0.023559,41.20438,12.565679,17.357822,35.907757000000004,29.311659999999996,19.513469,48.019573,19.856270000000002,33.854721000000005,13.909444,47.166000000000004,7.873054,6.4280550000000005,55.169438,49.815273,56.879635,26.3351,31.791702,43.750298,47.411631,42.708678000000006,-18.766947000000002,41.608635,17.570692,21.913965,46.862496,17.33083,21.00789,16.742498,35.937496,-20.348404000000002,3.202778,-13.254307999999998,23.634501,4.210483999999999,-18.665695,-20.904304999999997,17.607789,9.081999,12.865416,52.132633,60.472024,28.394857000000002,-40.900557,21.512583,8.537981,-9.189967,-17.679742,-6.314993,12.879721,30.375321000000003,51.919438,18.220833,31.952162,39.399871999999995,-23.442503,25.354826,45.943160999999996,44.016521000000004,61.52401,-1.940278,23.885942,-4.679574,12.862807,60.128161,1.352083,46.151241,48.669026,8.460555000000001,43.94236,14.497401000000002,5.152149,3.9193050000000005,0.18636,13.794185,34.802075,-26.522503000000004,21.694025,15.454166,8.619543,15.870032,-8.874217,33.886917,38.963745,10.691803,23.69781,-6.369028,48.379433,1.373333,37.09024,-32.522779,41.377491,41.902916,12.984305,6.42375,18.420695000000002,18.335765,14.058323999999999,42.602636,15.552726999999999,-30.559482,-13.133897,-19.015438)
    var listLong = arrayListOf(1.6015540000000001,53.847818000000004,67.709953,-61.796428000000006,-63.068615,20.168331,45.038189,17.873887,-63.616671999999994,14.550072,133.775136,-69.968338,47.576927000000005,17.679076000000002,-59.543198,90.35633100000001,4.469936,-1.561593,25.48583,50.637772,29.918885999999997,2.315834,-64.75737,114.72766899999999,-63.588653,-51.92528,-77.39628,90.433601,24.684866,27.953389,-88.49765,-106.34677099999999,21.758664000000003,20.939444,15.827658999999999,8.227511999999999,-5.54708,-71.542969,12.354722,104.195397,-74.297333,-83.753428,-77.78116700000001,-24.013197,33.429859,15.472961999999999,10.451526,42.590275,9.501785,-61.370976,-70.162651,1.6596259999999998,-78.183406,25.013607,30.802497999999996,39.782334000000006,-3.7492199999999998,40.489672999999996,25.748151,179.414413,-59.523613,-6.9118059999999995,2.213749,11.609444,-61.604170999999994,43.356891999999995,-2.5852779999999997,-1.0231940000000002,-5.345374,-42.604303,-15.310139000000001,-9.696645,10.267895,-90.23075899999999,144.793731,-15.180413,-58.93018000000001,-86.241905,15.2,-72.28521500000001,19.503304,113.921327,-8.243889999999999,34.851612,-4.548056,78.96288,43.679291,53.68804599999999,-19.020835,12.56738,-2.13125,-77.297508,36.238414,138.252924,37.906193,74.766098,104.990963,-62.782998,127.766922,47.481766,-80.566956,66.923684,102.495496,35.862285,-60.97889300000001,9.555373,80.77179699999999,-9.429499,23.881275,6.129583,24.603189,17.228331,-7.09262,7.412841,28.369884999999996,19.37439,46.869107,21.745275,-3.9961660000000006,95.956223,103.84665600000001,145.38468999999998,-10.940835,-62.187366000000004,14.375416,57.552152,73.22068,34.301525,-102.552784,101.97576600000001,35.529562,165.618042,8.081666,8.675277000000001,-85.207229,5.291266,8.468946,84.12400799999999,174.88597099999998,55.923255000000005,-80.782127,-75.015152,-149.40684299999998,143.95555,121.77401699999999,69.345116,19.145135999999997,-66.590149,35.233154,-8.224454,-58.44383199999999,51.183884,24.96676,21.005859,105.31875600000001,29.873888,45.079162,55.491977,30.217636,18.643501,103.819836,14.995463,19.699023999999998,-11.779889,12.457777,-14.452361999999999,46.199616,-56.02778299999999,6.613081,-88.89653,38.996815000000005,31.465866,-71.797928,18.732207,0.824782,100.992541,125.727539,9.537499,35.243322,-61.222503,120.96051499999999,34.888822,31.16558,32.290275,-95.712891,-55.765834999999996,64.585262,12.453389,-61.287228000000006,-66.58973,-64.63996800000001,-64.896335,108.277199,20.902977,48.516388,22.937506,27.849332,29.154857)
    var listCases = arrayListOf<Long>(622,3736,555,21,3,433,967,19,2137,13807,6289,92,1058,948,68,482,28018,484,661,1040,5,35,50,136,300,20727,46,5,13,2226,13,23301,234,11,60,24820,533,6927,820,83097,2709,577,620,8,616,5902,120479,187,5996,16,2759,1825,7257,1304,1939,34,161852,69,2905,16,5,184,93790,49,14,242,209,408,129,11,9,250,18,153,133,38,45,393,1534,33,1410,3842,8928,10743,226,8356,1318,70029,1689,152271,198,69,381,6748,191,377,122,12,10512,1154,53,880,18,619,15,80,199,48,1053,3270,630,25,1545,92,1560,263,102,760,105,31,16,11,7,9,370,319,19,12,4219,4530,20,18,491,318,8,24413,6320,9,1049,546,3234,6848,51,2,4428,5038,6356,788,268,15987,134,2728,5990,3380,13584,120,4033,10,19,10151,2299,1188,728,10,356,278,21,10,4,118,25,14,9,11,76,2551,2,685,52167,112,385,32,2511,53,529951,501,796,8,12,175,3,51,258,283,1,2028,40,14)
    var listCountries = arrayListOf("آروبا", "أذربيجان", "أرمينيا", "أسبانيا", "أستراليا", "أفغانستان", "ألبانيا", "ألمانيا", "أنتيجوا وبربودا", "أنجولا", "أنجويلا", "أندورا", "أورجواي", "أوزبكستان", "أوغندا", "أوكرانيا", "أيرلندا", "أيسلندا", "اثيوبيا", "اريتريا", "استونيا", "اسرائيل", "الأرجنتين", "الأردن", "الاكوادور", "الامارات العربية المتحدة", "الباهاما", "البحرين", "البرازيل", "البرتغال", "البوسنة والهرسك", "الجابون", "الجبل الأسود", "الجزائر", "الدانمرك", "الرأس الأخضر", "السلفادور", "السنغال", "السودان", "السويد", "الصحراء الغربية", "الصومال", "الصين", "العراق", "الفاتيكان", "الفيلبين", "القطب الجنوبي", "الكاميرون", "الكونغو - برازافيل", "الكويت", "المجر", "المحيط الهندي البريطاني", "المغرب", "المقاطعات الجنوبية الفرنسية", "المكسيك", "المملكة العربية السعودية", "المملكة المتحدة", "النرويج", "النمسا", "النيجر", "الهند", "الولايات المتحدة الأمريكية", "اليابان", "اليمن", "اليونان", "اندونيسيا", "ايران", "ايطاليا", "بابوا غينيا الجديدة", "باراجواي", "باكستان", "بالاو", "بتسوانا", "بتكايرن", "بربادوس", "برمودا", "بروناي", "بلجيكا", "بلغاريا", "بليز", "بنجلاديش", "بنما", "بنين", "بوتان", "بورتوريكو", "بوركينا فاسو", "بوروندي", "بولندا", "بوليفيا", "بولينيزيا الفرنسية", "بيرو", "تانزانيا", "تايلند", "تايوان", "تركمانستان", "تركيا", "ترينيداد وتوباغو", "تشاد", "توجو", "توفالو", "توكيلو", "تونجا", "تونس", "تيمور الشرقية", "جامايكا", "جبل طارق", "جرينادا", "جرينلاند", "جزر أولان", "جزر الأنتيل الهولندية", "جزر الترك وجايكوس", "جزر القمر", "جزر الكايمن", "جزر المارشال", "جزر الملديف", "جزر الولايات المتحدة البعيدة الصغيرة", "جزر سليمان", "جزر فارو", "جزر فرجين الأمريكية", "جزر فرجين البريطانية", "جزر فوكلاند", "جزر كوك", "جزر كوكوس", "جزر ماريانا الشمالية", "جزر والس وفوتونا", "جزيرة الكريسماس", "جزيرة بوفيه", "جزيرة مان", "جزيرة نورفوك", "جزيرة هيرد وماكدونالد", "جمهورية افريقيا الوسطى", "جمهورية التشيك", "جمهورية الدومينيك", "جمهورية الكونغو الديمقراطية", "جمهورية جنوب افريقيا", "جواتيمالا", "جوادلوب", "جوام", "جورجيا", "جورجيا الجنوبية وجزر ساندويتش الجنوبية", "جيبوتي", "جيرسي", "دومينيكا", "رواندا", "روسيا", "روسيا البيضاء", "رومانيا", "روينيون", "زامبيا", "زيمبابوي", "ساحل العاج", "ساموا", "ساموا الأمريكية", "سان مارينو", "سانت بيير وميكولون", "سانت فنسنت وغرنادين", "سانت كيتس ونيفيس", "سانت لوسيا", "سانت مارتين", "سانت هيلنا", "ساو تومي وبرينسيبي", "سريلانكا", "سفالبارد وجان مايان", "سلوفاكيا", "سلوفينيا", "سنغافورة", "سوازيلاند", "سوريا", "سورينام", "سويسرا", "سيراليون", "سيشل", "شيلي", "صربيا", "صربيا والجبل الأسود", "طاجكستان", "عمان", "غامبيا", "غانا", "غويانا", "غيانا", "غينيا", "غينيا الاستوائية", "غينيا بيساو", "فانواتو", "فرنسا", "فلسطين", "فنزويلا", "فنلندا", "فيتنام", "فيجي", "قبرص", "قرغيزستان", "قطر", "كازاخستان", "كاليدونيا الجديدة", "كرواتيا", "كمبوديا", "كندا", "كوبا", "كوريا الجنوبية", "كوريا الشمالية", "كوستاريكا", "كولومبيا", "كيريباتي", "كينيا", "لاتفيا", "لاوس", "لبنان", "لوكسمبورج", "ليبيا", "ليبيريا", "ليتوانيا", "ليختنشتاين", "ليسوتو", "مارتينيك", "ماكاو الصينية", "مالطا", "مالي", "ماليزيا", "مايوت", "مدغشقر", "مصر", "مقدونيا", "ملاوي", "منطقة غير معرفة", "منغوليا", "موريتانيا", "موريشيوس", "موزمبيق", "مولدافيا", "موناكو", "مونتسرات", "ميانمار", "ميكرونيزيا", "ناميبيا", "نورو", "نيبال", "نيجيريا", "نيكاراجوا", "نيوزيلاندا", "نيوي", "هايتي", "هندوراس", "هولندا", "هونج كونج الصينية")
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
                    var geocoder = Geocoder(activity, Locale.getDefault())
                    var adresses = geocoder.getFromLocation(it.latitude, it.longitude, 1)
                    Log.i("adresses", adresses.toString())
                    if (!adresses.isEmpty() && adresses[0].countryCode != null){
                        Toast.makeText(activity, adresses[0].countryCode, Toast.LENGTH_LONG).show()
                        val intent = Intent(activity, StatsActivity::class.java)
                        intent.putExtra("countryCode", adresses[0].countryCode)
                        intent.putExtra("countryName", adresses[0].countryName)
                        startActivity(intent)
                    }
                }else{
                    val zoneClicked : Int = zoneClicked(it)
                    if (zoneClicked != -1){
                        Toast.makeText(mContext, zoneClicked.toString(), Toast.LENGTH_LONG).show()
                    }
                }


                true
            }

        }

        val filterIcon: Spinner = filterIconView

        ArrayAdapter.createFromResource(
            mContext,
            R.array.graph_menu,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            filterIcon.adapter = adapter
        }

        var showAlgeriaData = floatingActionButton

        showAlgeriaData.setOnClickListener{
            countriesData = !countriesData
            if (countriesData){
                showDataOnMap(layers[0], mapboxMap.style!!)
            }else{
                showDataOnMap(layers[3], mapboxMap.style!!)
            }
        }



        var searchCountry = searchCountryView
        var adapter = ArrayAdapter<String>(activity!!, android.R.layout.simple_list_item_1, listCountries)
        searchCountry.setAdapter(adapter)
        searchCountry.setOnItemClickListener { parent, view, position, id ->
            var geocoder = Geocoder(activity, Locale("ar"))
            var adresses = geocoder.getFromLocationName(parent.getItemAtPosition(position).toString(), 1)
            val position = CameraPosition.Builder()
                .target(LatLng(adresses[0].latitude, adresses[0].longitude))
                .zoom(3.0)
                .tilt(20.0)
                .build()

            mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(position))

            Toast.makeText(activity, adresses[0].countryCode, Toast.LENGTH_LONG).show()
        }

    }

    private fun showDataOnMap(type : String, loadedMapStyle : Style){
        Log.i("mmlayer", loadedMapStyle.layers.toString())
        for (layer in layers){
            Log.i("mmlayer", layer)
            loadedMapStyle.getLayer(layer)!!.setProperties(visibility(NONE))
        }

        loadedMapStyle.getLayer(type)!!.setProperties(visibility(VISIBLE))
    }


    private fun addCasesOnMap(loadedMapStyle: Style){

        val layer = layers[0]

        var features = FeatureCollection.fromFeatures(features)
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
                    stop(1, min(abs(30),max(abs(3),division(get(layer), abs(200))))),
                    stop(5,min(abs(30),max(abs(5),division(get(layer), abs(100))))),
                    stop(10,max(abs(30), min(abs(20),division(get(layer), abs(50))))),
                    stop(13,max(abs(30),min(abs(20),division(get(layer), abs(10)))))
                )
            )
        )

        loadedMapStyle.addLayer(circles)
    }

    private fun addDeathsOnMap(loadedMapStyle: Style){

        val layer = layers[1]
        var features = FeatureCollection.fromFeatures(features)
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
                    stop(1, min(abs(30),max(abs(5),division(get(layer), abs(50))))),
                    stop(5,min(abs(30),max(abs(8),division(get(layer), abs(20))))),
                    stop(10,max(abs(30), min(abs(20),division(get(layer), abs(10))))),
                    stop(13,max(abs(30),min(abs(20),division(get(layer), abs(5)))))
                )
            )
        )

        loadedMapStyle.addLayer(circles)
    }

    private fun addRecoveredOnMap(loadedMapStyle: Style){

        val layer = layers[2]
        var features = FeatureCollection.fromFeatures(features)
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
                    stop(1, min(abs(30),max(abs(5),division(get(layer), abs(50))))),
                    stop(5,min(abs(30),max(abs(8),division(get(layer), abs(20))))),
                    stop(10,max(abs(30), min(abs(20),division(get(layer), abs(10))))),
                    stop(13,max(abs(30),min(abs(20),division(get(layer), abs(5)))))
                )
            )
        )

        loadedMapStyle.addLayer(circles)
    }


    private fun getCountriesData(loadedMapStyle: Style){
        val urlCountriesData = "${resources.getString(R.string.host)}/api/v0/zone/groupByCountry"

        Log.i("getting data", urlCountriesData)
        // Request a string response from the provided URL.
        val jsonRequestNbrDeaths = JsonObjectRequest(
            Request.Method.GET, urlCountriesData, null,
            Response.Listener { response ->
                var count : Int = response.getInt("count")
                var items = response.getJSONArray("items")
                for (i in 0 until count){
                    var item = items.getJSONObject(i)
                    var nbrCases : Int = item.getInt("totalConfirmed")
                    var nbrDeaths : Int = item.getInt("totalDead")
                    var nbrRecovered : Int = item.getInt("totalRecovered")
                    var countryCode : String = item.getString("counrtyCode")
                    var latLng = CountryInfo.getLatLng(countryCode)
                    var geometry = Point.fromLngLat(latLng.longitude, latLng.latitude)
                    var feature : Feature = Feature.fromGeometry(geometry)
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

    private fun getCountryData(countryCode : String, loadedMapStyle: Style){
        val urlCountriesData = "${resources.getString(R.string.host)}/api/v0/zone/country?cc=$countryCode"
        val features = arrayListOf<Feature>()

        // Request a string response from the provided URL.
        val jsonRequestNbrDeaths = JsonObjectRequest(
            Request.Method.GET, urlCountriesData, null,
            Response.Listener { response ->
                val count : Int = response.getInt("count")
                val items = response.getJSONArray("rows")
                for (i in 0 until count){
                    val item = items.getJSONObject(i)
                    val id = item.getInt("zoneId")
                    val datazone = item.getJSONArray("dataZones").getJSONObject(0)
                    val nbrCases : Int = datazone.getInt("totalConfirmed")
                    val nbrDeaths : Int = datazone.getInt("totalDead")
                    val nbrRecovered : Int = datazone.getInt("totalRecovered")
                    val latLng = LatLng(item.getDouble("latitude"), item.getDouble("longitude"))
                    val zoneData = ZoneData(id, latLng, nbrCases, nbrDeaths, nbrRecovered)
                    zonesAlgeriaData.add(zoneData)
                    val geometry = Point.fromLngLat(latLng.longitude, latLng.latitude)
                    val feature : Feature = Feature.fromGeometry(geometry)
                    feature.addNumberProperty(layers[0], nbrCases)
                    feature.addNumberProperty(layers[1], nbrDeaths)
                    feature.addNumberProperty(layers[2], nbrRecovered)
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
                            when(position){
                                0 -> showDataOnMap(layers[0], loadedMapStyle)
                                1 -> showDataOnMap(layers[1], loadedMapStyle)
                                2 -> showDataOnMap(layers[2], loadedMapStyle)
                            }
                        }else{
                            when(position){
                                0 -> showDataOnMap(layers[3], loadedMapStyle)
                                1 -> showDataOnMap(layers[4], loadedMapStyle)
                                2 -> showDataOnMap(layers[5], loadedMapStyle)
                            }
                        }
                    }
                }

                showDataOnMap(layers[0], loadedMapStyle)

            },
            Response.ErrorListener { Log.d("Error", "Request error") })

        RequestHandler.getInstance(mContext).addToRequestQueue(jsonRequestNbrDeaths)
    }

    private fun addCountryCasesOnMap(features: ArrayList<Feature>, loadedMapStyle : Style) {
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
                    stop(1, min(abs(30),max(abs(3),division(get(layer), abs(200))))),
                    stop(5,min(abs(30),max(abs(5),division(get(layer), abs(100))))),
                    stop(10,max(abs(30), min(abs(20),division(get(layer), abs(50))))),
                    stop(13,max(abs(30),min(abs(20),division(get(layer), abs(10)))))
                )
            )
        )

        loadedMapStyle.addLayer(circles)
    }

    private fun addCountryDeathsOnMap(features: ArrayList<Feature>, loadedMapStyle : Style) {

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
                    stop(1, min(abs(30),max(abs(5),division(get(layer), abs(50))))),
                    stop(5,min(abs(30),max(abs(8),division(get(layer), abs(20))))),
                    stop(10,max(abs(30), min(abs(20),division(get(layer), abs(10))))),
                    stop(13,max(abs(30),min(abs(20),division(get(layer), abs(5)))))
                )
            )
        )

        loadedMapStyle.addLayer(circles)
    }

    private fun addCountryRecoveredOnMap(features: ArrayList<Feature>, loadedMapStyle : Style) {


        val layer = layers[5]


        var features = FeatureCollection.fromFeatures(features)
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
                    stop(1, min(abs(30),max(abs(5),division(get(layer), abs(50))))),
                    stop(5,min(abs(30),max(abs(8),division(get(layer), abs(20))))),
                    stop(10,max(abs(30), min(abs(20),division(get(layer), abs(10))))),
                    stop(13,max(abs(30),min(abs(20),division(get(layer), abs(5)))))
                )
            )
        )

        loadedMapStyle.addLayer(circles)
    }


    private fun zoneClicked(clickLatLng : LatLng) : Int{
        var closestZone : Int = -1
        var closestDistance : Double = Double.MAX_VALUE
        for (zone in zonesAlgeriaData){
            val d = zone.latLng.distanceTo(clickLatLng) / 1000
            if (d < 15 && d < closestDistance){
                closestZone = zone.id
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
