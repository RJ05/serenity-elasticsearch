package net.serenitybdd.ext.elasticsearch.cookbook

import net.serenitybdd.ext.elasticsearch.model.JSONTestOutcome
import net.serenitybdd.ext.elasticsearch.model.TestResult
import spock.lang.Specification

import java.nio.file.Paths


class KeepingTrackOfTestResultHistory extends Specification {


    public static final String A_FAILING_TEST_OUTCOME = "4cd1e32ff10124ec69e31606b6bbc307.json"
    public static final String A_SUCCESSFUL_TEST_OUTCOME = "7fbf0b5674ea0af9a73e9a67d473f822.json"

    def "Test outcomes start with no history"() {
        when:
            def testOutcomeFile = Paths.get(ClassLoader.getResource("/outcomes").path).resolve(A_FAILING_TEST_OUTCOME).toFile()
            def jsonOutcome = new JSONTestOutcome(testOutcomeFile,"");
        then:
            jsonOutcome.outcomeTally.size() == 1
        and:
            jsonOutcome.outcomeTally[0].result == TestResult.FAILURE
            jsonOutcome.outcomeTally[0].outcomeUnchangedSinceCount == 1
    }

    def "Test outcomes accumulate history from previous test outcome tallies"() {
        given:
            def testOutcomeFile = Paths.get(ClassLoader.getResource("/outcomes").path).resolve(A_FAILING_TEST_OUTCOME).toFile()
            def jsonOutcome = new JSONTestOutcome(testOutcomeFile,"");
        when:
            jsonOutcome.withHistory(fromPreviousoutcomeTallyFor(A_FAILING_TEST_OUTCOME, A_FAILING_TEST_OUTCOME))
        then:
            jsonOutcome.outcomeTally.size() == 3
        and:
            jsonOutcome.outcomeTally[0].outcomeUnchangedSinceCount == 3
            jsonOutcome.outcomeTally[0].result == TestResult.FAILURE
            jsonOutcome.outcomeTally[1].result == TestResult.FAILURE
            jsonOutcome.outcomeTally[2].result == TestResult.FAILURE

    }

    def "Test outcomes accumulate longer history from previous test outcome tallies"() {
        given:
            def testOutcomeFile = Paths.get(ClassLoader.getResource("/outcomes").path).resolve(A_FAILING_TEST_OUTCOME).toFile()
            def jsonOutcome = new JSONTestOutcome(testOutcomeFile,"");
        when:
            jsonOutcome.withHistory(fromPreviousoutcomeTallyFor(A_FAILING_TEST_OUTCOME, A_FAILING_TEST_OUTCOME))
        then:
            jsonOutcome.outcomeTally.size() == 3
        and:
            jsonOutcome.outcomeTally[0].result == TestResult.FAILURE
            jsonOutcome.outcomeTally[0].outcomeUnchangedSinceCount == 3

            jsonOutcome.outcomeTally[1].result == TestResult.FAILURE
            jsonOutcome.outcomeTally[1].outcomeUnchangedSinceCount == 2

            jsonOutcome.outcomeTally[2].result == TestResult.FAILURE
            jsonOutcome.outcomeTally[2].outcomeUnchangedSinceCount == 1

    }

    def "Test outcomes keeps track of changing result status"() {
        given:
        def testOutcomeFile = Paths.get(ClassLoader.getResource("/outcomes").path).resolve(A_FAILING_TEST_OUTCOME).toFile()
        def jsonOutcome = new JSONTestOutcome(testOutcomeFile,"");

        when:
        jsonOutcome.withHistory(fromPreviousoutcomeTallyFor(A_SUCCESSFUL_TEST_OUTCOME, A_SUCCESSFUL_TEST_OUTCOME))

        then:
        jsonOutcome.outcomeTally[0].result == TestResult.FAILURE
        jsonOutcome.outcomeTally[0].outcomeUnchangedSinceCount == 1

        jsonOutcome.outcomeTally[1].result == TestResult.SUCCESS
        jsonOutcome.outcomeTally[1].outcomeUnchangedSinceCount == 2

        jsonOutcome.outcomeTally[2].result == TestResult.SUCCESS
        jsonOutcome.outcomeTally[2].outcomeUnchangedSinceCount == 1
    }


    def "Test outcomes keeps track of changing result status (failure to success)"() {
        given:
        def testOutcomeFile = Paths.get(ClassLoader.getResource("/outcomes").path).resolve(A_SUCCESSFUL_TEST_OUTCOME).toFile()
        def jsonOutcome = new JSONTestOutcome(testOutcomeFile,"");

        when:
        jsonOutcome.withHistory(fromPreviousoutcomeTallyFor(A_SUCCESSFUL_TEST_OUTCOME, A_FAILING_TEST_OUTCOME, A_FAILING_TEST_OUTCOME))

        then:
        jsonOutcome.outcomeTally[0].result == TestResult.SUCCESS
        jsonOutcome.outcomeTally[0].outcomeUnchangedSinceCount == 2

        jsonOutcome.outcomeTally[1].result == TestResult.SUCCESS
        jsonOutcome.outcomeTally[1].outcomeUnchangedSinceCount == 1

        jsonOutcome.outcomeTally[2].result == TestResult.FAILURE
        jsonOutcome.outcomeTally[2].outcomeUnchangedSinceCount == 2

        jsonOutcome.outcomeTally[3].result == TestResult.FAILURE
        jsonOutcome.outcomeTally[3].outcomeUnchangedSinceCount == 1
    }


    def fromPreviousoutcomeTallyFor(String... outcomeFiles) {

        def jsonOutcome
        def previousJsonOutcome = null
        for(String outcomeFile : outcomeFiles) {
            def testOutcomeFile = Paths.get(ClassLoader.getResource("/outcomes").path).resolve(outcomeFile).toFile()
            jsonOutcome = new JSONTestOutcome(testOutcomeFile,"");
            if (previousJsonOutcome) {
                jsonOutcome.withHistory(previousJsonOutcome.outcomeTally)
            }
            previousJsonOutcome = jsonOutcome
        }
        return jsonOutcome.outcomeTally.reverse()
    }
}