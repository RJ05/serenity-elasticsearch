package net.serenitybdd.ext.elasticsearch

import com.beust.jcommander.JCommander
import net.serenitybdd.ext.elasticsearch.elastic.ElasticSearchOutcomeLoader
import net.serenitybdd.ext.elasticsearch.upload.UploadOutcomes

object UploadTestOutcomes {
    @JvmStatic
    fun main(args: Array<String>) {

        val configuration = CommandLineArgs()

        val jCommander = JCommander(configuration, *args)
        jCommander.setProgramName("net.serenitybdd.ext.elasticsearch.UploadTestOutcomes")

        if (configuration.help) {
            jCommander.usage()
        } else {
            UploadOutcomes.fromDirectory(configuration.directory)
                    .thatWhereRunAgainstEnvironment(configuration.environment)
                    .forProject(configuration.project)
                    .using(ElasticSearchOutcomeLoader(configuration.host,
                                                      configuration.port,
                                                      configuration.auxilliaryPort,
                                                      configuration.cluster))
        }
    }
}