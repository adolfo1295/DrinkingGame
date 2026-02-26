package com.ac.drinkinggame.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

private const val SUPABASE_URL = "https://aooxodjoarjrxipjdkmt.supabase.co/rest/v1/"
private const val SUPABASE_KEY = "sb_publishable_dCEeriSrqHP9Jx10m3MnWg_J-F8Kcrm"

fun createKtorClient(): HttpClient {
  return HttpClient(OkHttp) {
    defaultRequest {
      url(SUPABASE_URL)
      header("apikey", SUPABASE_KEY)
      header("Authorization", "Bearer $SUPABASE_KEY")
      contentType(ContentType.Application.Json)
    }

    install(ContentNegotiation) {
      json(Json {
        ignoreUnknownKeys = true
        explicitNulls = false
        encodeDefaults = true
      })
    }
  }
}
