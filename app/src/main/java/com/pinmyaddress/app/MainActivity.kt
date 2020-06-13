package com.pinmyaddress.app

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.huawei.hms.maps.CameraUpdate
import com.huawei.hms.maps.CameraUpdateFactory
import com.huawei.hms.maps.HuaweiMap
import com.huawei.hms.maps.OnMapReadyCallback
import com.huawei.hms.maps.model.*
import com.huawei.hms.maps.model.CameraUpdateParam.NewLatLngZoom
import com.pinmyaddress.app.utils.Utils
import com.pinmyaddress.app.utils.Utils.context
import com.pinmyaddress.app.utils.Utils.getAddress
import com.pinmyaddress.app.utils.Utils.runtimePermissions
import kotlinx.android.synthetic.main.activity_main.*

private const val MAP_VIEW_BUNDLE_KEY = "MapViewBundleKey"
private const val REQUEST_CODE = 100

class MainActivity : AppCompatActivity(), OnMapReadyCallback,
    HuaweiMap.OnMapClickListener, LocationListener {

    private val markerListener = MarkerListener()

    private var huaweiMap: HuaweiMap? = null
    private var pinMarker: Marker? = null
    private var currentLatLng: LatLng? = null
    private lateinit var address: String

    private lateinit var cameraUpdate: CameraUpdate

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        context = this

        if (!Utils.hasPermissions(this, runtimePermissions)) {
            ActivityCompat.requestPermissions(this, runtimePermissions, REQUEST_CODE)
        }

        val locationManager =
            this.application.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5L, 20F, this)

        var mapViewBundle: Bundle? = null
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_BUNDLE_KEY)
        }
        mapView.onCreate(mapViewBundle)
        mapView.getMapAsync(this)
    }

    override fun onMapReady(map: HuaweiMap?) {
        huaweiMap = map

        currentLatLng?.apply {

            huaweiMap?.addMarker(
                MarkerOptions().position(currentLatLng)
                    .title(address)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_baseline_star_24))
                    .clusterable(true)
            )

            pinMarker = huaweiMap?.addMarker(
                MarkerOptions().position(currentLatLng)
                    .title(address)
                    .draggable(true)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_baseline_pin_drop_24))
            )

            val cameraPosition = CameraPosition.Builder().target(currentLatLng).zoom(11f).build()
            cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition)

            huaweiMap?.apply {
                animateCamera(cameraUpdate)
                setMaxZoomPreference(20F)
                setMinZoomPreference(2F)
                setOnMapClickListener(this@MainActivity)
                setOnMarkerClickListener(markerListener)
                setOnMarkerDragListener(markerListener)
            }
        }

    }

    override fun onMapClick(latLng: LatLng) {
        pinMarker?.apply {
            position = latLng
            title = getAddress(latLng.latitude, latLng.longitude)
            showInfoWindow()
        }
        cameraUpdate.cameraUpdate.newLatLngZoom = NewLatLngZoom(pinMarker?.position, 11f)
        huaweiMap!!.animateCamera(cameraUpdate)
    }

    override fun onLocationChanged(location: Location) {
        huaweiMap?.clear()

        val latitude = location.latitude
        val longitude = location.longitude

        currentLatLng = LatLng(latitude, longitude)
        address = getAddress(latitude, longitude)

    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) { /* no op */ }

    override fun onProviderEnabled(provider: String?) { /* no op */ }

    override fun onProviderDisabled(provider: String?) { /* no op */ }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    override fun onStart() {
        super.onStart()
        mapView!!.onStart()
    }

    override fun onStop() {
        super.onStop()
        mapView!!.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView!!.onDestroy()
    }

    override fun onPause() {
        mapView!!.onPause()
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        mapView!!.onResume()
    }

}