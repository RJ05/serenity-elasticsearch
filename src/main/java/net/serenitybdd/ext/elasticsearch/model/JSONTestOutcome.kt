package net.serenitybdd.ext.elasticsearch.model

import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import org.apache.commons.codec.digest.DigestUtils
import org.joda.time.DateTime
import java.io.File

data class JSONTestOutcome(val testOutcome: File,
                           val environment: String = "") {

    /**
     * The original JSON test outcome
     */
    val json = Parser().parse(testOutcome.inputStream()) as JsonObject

    /**
     * Identifies the test scenario
     */
    val id: String = DigestUtils.sha1Hex(json.get("id").toString())

    val capability: String = Tag.valueOfOfType("capability").fromTestOutcomeIn(json)

    val result: TestResult = TestResult.valueOf(json.get("result").toString())

    val timestamp: DateTime = DateTime.now()

    /**
     * A text message summarizing a test failure, that can be used to check if a failure is different to that of a previous test.
     */
    val testFailureSummary: String = json.getOrElse("testFailureSummary") { "" }
            .toString()

    /**
     * The result tally for the first run of this test
     */
    var outcomeTally: List<Outcome> = listOf(Outcome(result, "", testFailureSummary))
        private set

    fun withHistory(previousOutcomeTally: List<Outcome>): JSONTestOutcome {
        outcomeTally =
                if (previousOutcomeTally.isEmpty()) outcomeTally
                else {
                    listOf(Outcome(result, "", testFailureSummary, 1 + lastOccurenceOf(result, previousOutcomeTally))) + withOutcomeHistory(previousOutcomeTally)
                }

        return this
    }

    fun withOutcomeHistory(previousOutcomeTally: List<Outcome>): List<Outcome> {
        return if (previousOutcomeTally.isEmpty())
            listOf<Outcome>()
        else {
            val head = previousOutcomeTally.first()
            val tail = previousOutcomeTally.drop(1)
            listOf(headOutcome(head, tail)) + withOutcomeHistory(tail)
        }
    }

    private fun headOutcome(head : Outcome, tail: List<Outcome>) = Outcome(head.result, head.id, head.latestFailureSummary,  1 + lastOccurenceOf(head.result, tail))

    private fun lastOccurenceOf(result: TestResult, previousoutcomeTally: List<Outcome>): Int {
        previousoutcomeTally.forEachIndexed { lastOccurenceCount, outcome ->
            if (outcome.result != result) {
                return lastOccurenceCount
            }
        }
        return previousoutcomeTally.size
    }

    fun asJson(): JsonObject {
        val updatedJson = json.copy()
        updatedJson.put("id", id)
        updatedJson.put("timestamp", timestamp)
        updatedJson.put("capability", capability)
        updatedJson.put("environment", environment)
        updatedJson.put("outcomeTally", jsonFormOf(outcomeTally))
        return updatedJson
    }
}