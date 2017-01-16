package net.serenitybdd.ext.elasticsearch.upload

import java.io.File

interface TestOutcomeLoader {
    fun upload(testOutcomes: List<File>,
               project: String,
               environment: String): List<File>
}
