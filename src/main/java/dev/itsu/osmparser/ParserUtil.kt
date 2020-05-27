package dev.itsu.urbandeveloper.osm

import kotlin.math.*

object ParserUtil {

    fun worldPosXY(longitude: Double, latitude: Double): Array<Double> {
        val x = (longitude + 180.0) / 360.0 * (1 shl 8)
        val y = ((1.0 - log(tan(latitude * PI / 180.0) + 1.0 / cos(latitude * PI / 180.0), E) / PI) / 2.0 * (1 shl 8))
        return arrayOf(x, y)
    }


}