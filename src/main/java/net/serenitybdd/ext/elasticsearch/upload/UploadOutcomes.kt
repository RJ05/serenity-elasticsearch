package net.serenitybdd.ext.elasticsearch.upload

import java.io.File
import java.io.PrintStream

class UploadOutcomes(private val directory: File,
                     private val project : String,
                     private val environment : String) {

    private val console: PrintStream

    init {
        this.console = System.out
    }

    fun using(testOutcomeLoader: TestOutcomeLoader) {

        val loadedFiles = testOutcomeLoader.upload(testOutcomes(), project, environment)

        val testCount = loadedFiles.count()

        console.printf("%d test%s uploaded", testCount, if (testCount == 1) "" else "s")
    }

    private fun testOutcomes(): List<File> {
        return directory
                .listFiles { it -> it.name.toLowerCase().endsWith(".json") }
                .asList()
    }

    companion object {

        @JvmStatic
        fun fromDirectory(directory: File): UploadBuilder {
            return UploadBuilder(directory)
        }
    }

    class UploadBuilder(val directory: File) {
        var environment : String = ""

        fun forProject(project: String) : UploadOutcomes {
            return UploadOutcomes(directory, project, environment)
        }

        fun thatWhereRunAgainstEnvironment(environment: String): UploadBuilder {
            this.environment = environment
            return this
        }
    }
}
