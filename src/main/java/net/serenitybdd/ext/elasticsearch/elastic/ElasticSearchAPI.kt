package net.serenitybdd.ext.elasticsearch.elastic

import org.elasticsearch.client.transport.TransportClient
import org.elasticsearch.common.settings.Settings
import org.elasticsearch.common.transport.InetSocketTransportAddress
import org.elasticsearch.transport.client.PreBuiltTransportClient
import java.io.Closeable
import java.io.File
import java.net.InetAddress

class ElasticSearchAPI(host: String, port: Int = 9200, auxilliaryPort : Int = 9300,
                       cluster: String = "elasticsearch") : Closeable {

    val client: TransportClient =
            PreBuiltTransportClient(Settings.builder().put("cluster.name", cluster).build())
                    .addTransportAddress(InetSocketTransportAddress(InetAddress.getByName(host), port))
                    .addTransportAddress(InetSocketTransportAddress(InetAddress.getByName(host), auxilliaryPort))

    override fun close() {
        client.close()
    }

    fun uploadAll(testOutcomes: List<File>): BulkUploader {
        return BulkUploader(client, testOutcomes)
    }
}
