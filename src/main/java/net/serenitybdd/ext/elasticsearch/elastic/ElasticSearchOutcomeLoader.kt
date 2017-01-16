package net.serenitybdd.ext.elasticsearch.elastic

import net.serenitybdd.ext.elasticsearch.upload.TestOutcomeLoader
import java.io.File

class ElasticSearchOutcomeLoader(private val host: String,
                                 private val port: Int,
                                 private val auxilliaryPort: Int,
                                 private val cluster: String) : TestOutcomeLoader {
    override fun upload(testOutcomes: List<File>,
                        project: String,
                        environment: String): List<File> {

        ElasticSearchAPI(host, port, auxilliaryPort, cluster).use {
            it.uploadAll(testOutcomes)
                    .runOnEnvironment(environment)
                    .forProject(project)
        }
        return testOutcomes
    }
}
