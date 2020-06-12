package com.pinmyaddress.app

import com.huawei.hms.maps.HuaweiMap
import com.huawei.hms.maps.model.Marker
import com.pinmyaddress.app.utils.Utils.getAddress

class MarkerListener : HuaweiMap.OnMarkerClickListener,
    HuaweiMap.OnMarkerDragListener {

    override fun onMarkerClick(marker: Marker): Boolean {
        marker.showInfoWindow()
        return true
    }

    override fun onMarkerDragEnd(marker: Marker) {
        marker.title = getAddress(marker.position.latitude, marker.position.longitude)
        marker.showInfoWindow()
    }

    override fun onMarkerDragStart(marker: Marker) {
        /* no op */
    }

    override fun onMarkerDrag(marker: Marker) {
        /* no op */
    }

}