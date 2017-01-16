package net.serenitybdd.ext.elasticsearch.model

object Project {
    fun indexNameFor(project: String): String {
        return "serenity-" + project.toLowerCase()
    }
}