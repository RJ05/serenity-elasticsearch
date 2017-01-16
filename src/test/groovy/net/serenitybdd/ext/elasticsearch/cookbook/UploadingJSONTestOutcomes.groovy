package net.serenitybdd.ext.elasticsearch.cookbook

import net.serenitybdd.ext.elasticsearch.elastic.ElasticSearchOutcomeLoader
import net.serenitybdd.ext.elasticsearch.model.JSONTestOutcome
import net.serenitybdd.ext.elasticsearch.model.TestResult
import net.serenitybdd.ext.elasticsearch.upload.UploadOutcomes
import org.apache.commons.codec.digest.DigestUtils
import spock.lang.Specification

import java.nio.file.Paths

class UploadingJSONTestOutcomes extends Specification {

    def console = new ByteArrayOutputStream();

    def setup() {
        System.setOut(new PrintStream(console));
    }

    def "the content of a test outcome is read from the test outcome file"() {
        when:
            def testOutcomeFile = Paths.get(ClassLoader.getResource("/outcomes").path).resolve("04c99a02cb0aec1f5f81a19f3bf37595.json").toFile()
        then:
            new JSONTestOutcome(testOutcomeFile,"").json.get("name")== "should_be_able_to_view_only_incomplete_todos"
    }

    def "key fields are read into the JSON test outcome"() {
        when:
            def testOutcomeFile = Paths.get(ClassLoader.getResource("/outcomes").path).resolve("04c99a02cb0aec1f5f81a19f3bf37595.json").toFile()
            def jsonOutcome = new JSONTestOutcome(testOutcomeFile,"");
        then:
            jsonOutcome.id == DigestUtils.sha1Hex("net.serenitybdd.demos.todos.pageobjects.features.maintain_my_todo_list.FilteringTodos:should_be_able_to_view_only_incomplete_todos") &&
            jsonOutcome.result == TestResult.SUCCESS &&
            jsonOutcome.testFailureSummary == ""
    }

    def "the capability tag is read into the JSON test outcome"() {
        when:
        def testOutcomeFile = Paths.get(ClassLoader.getResource("/outcomes").path).resolve("04c99a02cb0aec1f5f81a19f3bf37595.json").toFile()
        def jsonOutcome = new JSONTestOutcome(testOutcomeFile,"");
        then:
        jsonOutcome.capability == "Maintain my todo list"
    }

    def "Failure causes are recorded in the JSON test outcome"() {
        when:
            def testOutcomeFile = Paths.get(ClassLoader.getResource("/outcomes").path).resolve("4cd1e32ff10124ec69e31606b6bbc307.json").toFile()
            def jsonOutcome = new JSONTestOutcome(testOutcomeFile,"");
        then:
            jsonOutcome.result == TestResult.FAILURE &&
            jsonOutcome.testFailureSummary == "FAILURE;java.lang.AssertionError;\nExpected: is \u003c1\u003e\n     but: was \u003c2\u003e;TodoUserSteps.java:105"
    }


    def "uploads each JSON file in the specified directory"() {
        given:
            def directory = Paths.get(ClassLoader.getResource("/outcomes").path).toFile();
            def outcomeLoader = new ElasticSearchOutcomeLoader("localhost", 9300, 9200, "elasticsearch")
        when:
            UploadOutcomes.fromDirectory(directory).forProject("MYPROJ").using(outcomeLoader)
        then:
            console.toString().contains("42 tests uploaded");
    }

}