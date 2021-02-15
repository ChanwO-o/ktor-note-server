package com.parkchanwoo.data.collections

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

data class Note(
    val title: String,
    val content: String,
    val date: Long, // timestamp
    val owners: List<String>, // list of emails of people who have access
    val color: String, // hex color

    @BsonId // tell mongodb this is id for our document. Bson: binary-represented json
    val id: String = ObjectId().toString() // auto generate id
)