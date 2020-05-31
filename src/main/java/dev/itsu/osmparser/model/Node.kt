package dev.itsu.osmparser.model

data class Node(
        val id: Long,
        val visible: Boolean,
        val version: Int,
        val changeSet: Long,
        val timeStamp: String,
        val user: User,
        val position: Pos
) {
    var tags: MutableMap<String, Tag> = mutableMapOf()
}