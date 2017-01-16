package net.serenitybdd.ext.elasticsearch.concurrent

object NumberOfThreads {

    internal val BLOCKING_COEFFICIENT_FOR_IO = 0.9

    fun forIOOperations(): Int {
        val numberOfCores = Runtime.getRuntime().availableProcessors()
        return (numberOfCores / (1 - BLOCKING_COEFFICIENT_FOR_IO)).toInt()
    }
}
