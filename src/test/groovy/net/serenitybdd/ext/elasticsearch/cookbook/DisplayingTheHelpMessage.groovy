package net.serenitybdd.ext.elasticsearch.cookbook

import net.serenitybdd.ext.elasticsearch.UploadTestOutcomes
import spock.lang.Specification

class DisplayingTheHelpMessage extends Specification {

    def console = new ByteArrayOutputStream();

    def setup() {
        System.setOut(new PrintStream(console));
    }

    def "should display the help message with the --help option"() {
        when:
            UploadTestOutcomes.main("--help");
        then:
            console.toString().contains("Usage: java -jar elasticsearch-uploader.jar [options]")
            console.toString().contains("--host")
            console.toString().contains("--port")
    }
}