package net.serenitybdd.ext.elasticsearch.elastic

import net.serenitybdd.ext.elasticsearch.concurrent.NumberOfThreads
import net.serenitybdd.ext.elasticsearch.model.JSONTestOutcome
import net.serenitybdd.ext.elasticsearch.model.Project
import net.serenitybdd.ext.elasticsearch.upload.UploadFailedException
import org.elasticsearch.ResourceAlreadyExistsException
import org.elasticsearch.action.bulk.BulkRequestBuilder
import org.elasticsearch.client.transport.TransportClient
import java.io.File
import java.util.*
import java.util.concurrent.Callable
import java.util.concurrent.Executors

data class BulkUploader(val client: TransportClient,
                   val testOutcomes: List<File>,
                   val environment: String="") {

    fun runOnEnvironment(environment: String): BulkUploader {
        return this.copy(environment = environment)
    }

    fun forProject(project: String) {

        createIndex(project)

        val partitions = prepareWorkStreams(project)

        val bulkRequest = prepareBulkRequires(partitions, project)

        val bulkResponse = bulkRequest.get()

        if (bulkResponse.hasFailures()) {
            throw UploadFailedException(bulkResponse.buildFailureMessage())
        }
    }

    private fun createIndex(project: String) {
        try {
            val createIndexRequestBuilder = client.admin().indices().prepareCreate(Project.indexNameFor(project))

            createIndexRequestBuilder.addMapping("scenario", mappingConfig().readText())

            val indexResponse = createIndexRequestBuilder.execute().actionGet()
        } catch (ignore: ResourceAlreadyExistsException) {
        }
    }

    private fun mappingConfig() = File(ClassLoader.getSystemClassLoader().getResource("mappings/scenario.json").toURI())

    private fun prepareBulkRequires(partitions: List<Callable<JSONTestOutcome>>, project: String): BulkRequestBuilder {
        val executorPool = Executors.newFixedThreadPool(NumberOfThreads.forIOOperations())

        val bulkRequest = client.prepareBulk()
        executorPool.invokeAll(partitions).forEach({ testOutcomeFuture ->
            val loadedOutcome = testOutcomeFuture.get()
            bulkRequest.add(
                    client.prepareIndex(Project.indexNameFor(project), "scenario")
                            .setSource(loadedOutcome.asJson())
            )
        })
        return bulkRequest
    }

    private fun prepareWorkStreams(project: String): List<Callable<JSONTestOutcome>> {
        val partitions = ArrayList<Callable<JSONTestOutcome>>()

        testOutcomes.forEach({ testOutcomeFile ->
            partitions.add(
                    Callable<JSONTestOutcome> {

                        val loadedTestOutcome = JSONTestOutcome(testOutcomeFile)

                        loadedTestOutcome.withHistory(previousResultTallies(loadedTestOutcome, project))
                    }
            )
        })
        return partitions
    }

    private fun previousResultTallies(loadedTestOutcome: JSONTestOutcome, project: String) = StoredResultTallies(client).currentResultTalliesFor(project, loadedTestOutcome.id)
}

