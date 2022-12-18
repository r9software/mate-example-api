package com.example.sitemateexampleapi.network

object Model {
    data class Result(val lyrics: String)
    data class Error(val error: String)
}