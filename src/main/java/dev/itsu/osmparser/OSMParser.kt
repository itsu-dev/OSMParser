package dev.itsu.urbandeveloper.osm

import dev.itsu.urbandeveloper.osm.model.*
import org.w3c.dom.Element
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.lang.Exception
import javax.xml.parsers.DocumentBuilderFactory

class OSMParser {

    var version = 0.0
    lateinit var generator: String
    lateinit var copyRight: String
    lateinit var attribution: String
    lateinit var license: String

    private val nodes = mutableMapOf<Long, Node>()
    private val ways = mutableMapOf<Long, Way>()

    companion object {
        const val UNKNOWN_STRING = "_unknown"
        const val UNKNOWN_INTEGER = 0
        const val UNKNOWN_LONG: Long = 0
        const val UNKNOWN_DOUBLE = 0.0
    }

    fun parse(path: String) {
        parse(File(path))
    }

    fun parse(file: File) {
        parse(FileInputStream(file))
    }

    fun parse(inputStream: InputStream) {
        try {
            val factory = DocumentBuilderFactory.newInstance()
            val document = factory.newDocumentBuilder().parse(inputStream)
            val root = document.documentElement

            version = root.getAttribute("version")?.toDoubleOrNull() ?: UNKNOWN_DOUBLE
            generator = root.getAttribute("generator") ?: UNKNOWN_STRING
            copyRight = root.getAttribute("copyright") ?: UNKNOWN_STRING
            attribution = root.getAttribute("attribution") ?: UNKNOWN_STRING
            license = root.getAttribute("license") ?: UNKNOWN_STRING

            var nodes = root.getElementsByTagName("node")
            for (i in 0 until nodes.length) processNode(nodes.item(i) as Element)

            nodes = root.getElementsByTagName("way")
            for (i in 0 until nodes.length) processWay(nodes.item(i) as Element)

        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    private fun processNode(element: Element) {
        nodes[element.getAttribute("id")?.toLongOrNull() ?: return] =
                Node(
                        element.getAttribute("id").toLong(),
                        element.getAttribute("visible")?.toBoolean() ?: true,
                        element.getAttribute("version")?.toIntOrNull() ?: UNKNOWN_INTEGER,
                        element.getAttribute("changeset")?.toLongOrNull() ?: UNKNOWN_LONG,
                        element.getAttribute("timestamp") ?: UNKNOWN_STRING,
                        User(
                                element.getAttribute("user") ?: UNKNOWN_STRING,
                                element.getAttribute("uid")?.toLongOrNull() ?: UNKNOWN_LONG
                        ),
                        Pos(
                                element.getAttribute("lon")?.toDoubleOrNull() ?: UNKNOWN_DOUBLE,
                                element.getAttribute("lat")?.toDoubleOrNull() ?: UNKNOWN_DOUBLE
                        )

                ).also {
                    if (element.hasChildNodes()) {
                        val child = element.childNodes
                        var childElement: Element
                        for (i in 0 until child.length) {
                            childElement = if (child.item(i) is Element) child.item(i) as Element else return@also
                            when (childElement.tagName) {
                                "tag" -> it.tags.add(Tag(element.getAttribute("k"), element.getAttribute("v")))
                            }
                        }
                    }
                }
    }

    private fun processWay(element: Element) {
        ways[element.getAttribute("id")?.toLongOrNull() ?: return] =
                Way(
                        element.getAttribute("id").toLong(),
                        element.getAttribute("visible")?.toBoolean() ?: true,
                        element.getAttribute("version")?.toIntOrNull() ?: UNKNOWN_INTEGER,
                        element.getAttribute("changeset")?.toLongOrNull() ?: UNKNOWN_LONG,
                        element.getAttribute("timestamp") ?: UNKNOWN_STRING,
                        User(
                                element.getAttribute("user") ?: UNKNOWN_STRING,
                                element.getAttribute("uid")?.toLongOrNull() ?: UNKNOWN_LONG
                        )

                ).also {
                    if (element.hasChildNodes()) {
                        val child = element.childNodes
                        var childElement: Element
                        for (i in 0 until child.length) {
                            childElement = if (child.item(i) is Element) child.item(i) as Element else return@also
                            when (childElement.tagName) {
                                "tag" -> it.tags.add(Tag(element.getAttribute("k"), element.getAttribute("v")))
                                "nd" -> it.nds.add(Nd(element.getAttribute("ref")?.toLongOrNull() ?: UNKNOWN_LONG))
                            }
                        }
                    }
                }
    }

    fun getNodeById(id: Long) = nodes[id]

    fun getNodes() = nodes

    fun getWayById(id: Long) = ways[id]

    fun getWays() = ways
}