package net.serenitybdd.ext.elasticsearch.elastic

import net.serenitybdd.ext.elasticsearch.model.Outcome
import net.serenitybdd.ext.elasticsearch.model.Project
import net.serenitybdd.ext.elasticsearch.model.TestResult
import org.elasticsearch.action.search.SearchResponse
import org.elasticsearch.client.transport.TransportClient
import org.elasticsearch.index.query.QueryBuilders
import org.elasticsearch.search.SearchHit
import org.elasticsearch.search.sort.SortBuilders
import org.elasticsearch.search.sort.SortOrder

class StoredResultTallies(val client: TransportClient) {

    val sortBy : String = "timestamp"

    fun currentResultTalliesFor(project: String, scenarioId: String): List<Outcome> {

        val searchResponse = client.prepareSearch(Project.indexNameFor(project))
                .addSort(SortBuilders.fieldSort(sortBy).order(SortOrder.DESC))
                .setFetchSource(arrayOf("timestamp", "startTime", "testFailureSummary", "result"), arrayOf())
                .setQuery(QueryBuilders.queryStringQuery("id:\"" + scenarioId+"\""))
                .get()

        return currentResultTalliesFor(searchResponse).orEmpty()
    }

    private fun currentResultTalliesFor(searchResponse: SearchResponse?): List<Outcome>? {

        return searchResponse?.hits?.hits?.map {
            it.source.get("outcomeTally")
            Outcome(testResultOf(it),
                    it.id,
                    latestFailureSummaryFrom(it)) }
    }

    private fun testResultOf(it: SearchHit) = TestResult.valueOf(it.source.get("result").toString().toUpperCase())
    private fun latestFailureSummaryFrom(it: SearchHit) = it.source.get("testFailureSummary").toString()

}