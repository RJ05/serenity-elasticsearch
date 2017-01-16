package net.serenitybdd.ext.elasticsearch

import com.beust.jcommander.Parameter
import com.beust.jcommander.converters.FileConverter
import java.io.File

class CommandLineArgs {
    @Parameter(names = arrayOf("--host", "-h"), description = "Elasticsearch host address", required = true)
    var host = "localhost"

    @Parameter(names = arrayOf("--port", "-p"), description = "Elasticsearch port")
    var port = 9300

    @Parameter(names = arrayOf("--aux", "-x"), description = "Elasticsearch auxilliary port")
    var auxilliaryPort = 9200

    @Parameter(names = arrayOf("--cluster", "-c"), description = "Elasticsearch Cluster name")
    var cluster = "elasticsearch"

    @Parameter(names = arrayOf("--project", "-pr"), description = "The project name", required = true)
    var project = ""

    @Parameter(names = arrayOf("--environment", "-e"), description = "The environment the tests are running against", required = true)
    var environment = ""

    @Parameter(names = arrayOf("-directory", "-d"), description = "Directory containing the JSON test outcomes", converter = FileConverter::class)
    var directory: File = File("target/site/serenity")

    @Parameter(names = arrayOf("--help", "-?"), help = true, description = "Display this message")
    var help = false
}