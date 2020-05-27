package dev.itsu.urbandeveloper.osm.model

data class Way(
        val id: Long,
        val visible: Boolean,
        val version: Int,
        val changeSet: Long,
        val timeStamp: String,
        val user: User
) {
    val tags = mutableListOf<Tag>()
    val nds = mutableListOf<Nd>()
}