package net.serenitybdd.ext.elasticsearch.model

import com.beust.klaxon.JsonArray
import com.beust.klaxon.json

data class Outcome(val result: TestResult,
                   val id: String,
                   val latestFailureSummary: String,
                   val outcomeUnchangedSinceCount: Int = 1)

fun jsonFormOf(resultTallies: List<Outcome>): JsonArray<*> {

    return JsonArray(resultTallies.map { it ->
        json {
            obj(
                    "result" to it.result.name,
                    "id" to it.id,
                    "latestFailureSummary" to it.latestFailureSummary,
                    "outcomeUnchangedSinceCount" to it.outcomeUnchangedSinceCount
            )
        }
    })
}