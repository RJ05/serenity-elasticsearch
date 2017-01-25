package net.serenitybdd.ext.elasticsearch.model

import com.beust.klaxon.JsonObject

object Tag {
    fun valueOfOfType(tagType: String) = TagValueFinder(tagType)
}

@Suppress("UNCHECKED_CAST")
class TagValueFinder(val tagType : String) {
    fun  fromTestOutcomeIn(json: JsonObject): String {
        return (json.get("tags") as Iterable<JsonObject>).filter { it.get("type") == tagType }
                                                         .first()
                                                         .get("name").toString()
    }
}
