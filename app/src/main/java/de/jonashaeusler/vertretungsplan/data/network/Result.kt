package de.jonashaeusler.vertretungsplan.data.network

sealed class Result {
    object Success: Result()
    data class Failure(val message: String): Result()
}
