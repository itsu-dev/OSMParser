package dev.itsu.osmparser

import java.awt.BasicStroke
import java.awt.Color
import java.awt.Graphics2D
import javax.swing.JFrame
import kotlin.math.abs

var ZOOM = 0

private val wayColor = mapOf<String, Color>(
        "motorway" to Color.MAGENTA,
        "trunk" to Color.RED,
        "primary" to Color.ORANGE,
        "secondary" to Color.YELLOW,
        "tertiary" to Color.BLACK,
        "unclassified" to Color.GRAY,
        "residential" to Color.BLUE,

        "motorway_link" to Color.MAGENTA,
        "trunk_link" to Color.RED,
        "primary_link" to Color.ORANGE,
        "secondary_link" to Color.YELLOW,
        "tertiary_link" to Color.BLACK,
        "unclassified_link" to Color.GRAY
)

fun main() {
    val parser = OSMParser().parse("map.osm")
    var width: Int
    var height: Int
    var minX: Int
    var minY: Int

    while (true) {
        ZOOM++
        ParserUtil.getXYTile(parser.bounds.minLat, parser.bounds.minLon, ZOOM).also { a ->
            ParserUtil.getXYTile(parser.bounds.maxLat, parser.bounds.maxLon, ZOOM).also { b ->
                width = abs(b.first - a.first)
                height = abs(b.second - a.second)
                minX = b.first
                minY = b.second
            }
        }
        if (width >= 800) break
    }

    println(3 % 16)

    val image = JFrame()
    image.setBounds(0, 0, width, height)
    image.isVisible = true
    image.defaultCloseOperation = JFrame.EXIT_ON_CLOSE

    val g = image.graphics as Graphics2D
    g.stroke = BasicStroke(3.0F)

    parser.getWays().values.forEach { way ->
        way.nds.forEachIndexed way@{ index, nd ->
            if (!wayColor.containsKey(way.tags["highway"]?.value ?: return@way)) return@way

            val currentNode = parser.getNodeById(nd.ref) ?: return@way
            val nextNode = parser.getNodeById((if (index + 1 < way.nds.size) way.nds[index + 1] else return@way).ref)
                    ?: return@way
            val currentX: Int
            val currentY: Int
            val nextX: Int
            val nextY: Int

            ParserUtil.getXYTile(currentNode.position.latitude, currentNode.position.longitude, ZOOM).also {
                currentX = it.first
                currentY = it.second
            }

            ParserUtil.getXYTile(nextNode.position.latitude, nextNode.position.longitude, ZOOM).also {
                nextX = it.first
                nextY = it.second
            }

            g.color = wayColor[way.tags["highway"]?.value]
            g.drawLine(
                    abs(currentX - minX),
                    abs(currentY - minY),
                    abs(nextX - minX),
                    abs(nextY - minY)
            )

            g.color = Color.BLACK
            g.drawString(currentNode.tags["name"]?.value ?: "", abs(currentX - minX) * ZOOM, abs(currentY - minY) * ZOOM)
        }
    }
}