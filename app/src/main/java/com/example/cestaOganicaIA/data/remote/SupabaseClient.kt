package com.example.cestaOganicaIA.data.remote

import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

/**
 * Cliente HTTP minimalista para hablar con la API REST de Supabase (PostgREST).
 * No requiere librerías externas: usa solo HttpURLConnection y JSONObject,
 * ambos incluidos en el SDK de Android.
 */
object SupabaseClient {

    private const val BASE_URL = "https://digqjkqfcpupmuhdxlmk.supabase.co"
    private const val API_KEY = "sb_publishable_woEmRUdyRyHAXljwrGJDXQ_2VgU3FSp"

    private fun request(
        method: String,
        path: String,
        body: String? = null,
        preferReturn: Boolean = false
    ): String {
        val url = URL("$BASE_URL$path")
        val conn = url.openConnection() as HttpURLConnection
        conn.requestMethod = method
        conn.setRequestProperty("apikey", API_KEY)
        conn.setRequestProperty("Authorization", "Bearer $API_KEY")
        conn.setRequestProperty("Content-Type", "application/json")
        if (preferReturn) {
            conn.setRequestProperty("Prefer", "return=representation")
        }
        conn.connectTimeout = 15000
        conn.readTimeout = 15000

        if (body != null) {
            conn.doOutput = true
            val writer = OutputStreamWriter(conn.outputStream)
            writer.write(body)
            writer.flush()
            writer.close()
        }

        val responseCode = conn.responseCode
        val stream = if (responseCode in 200..299) conn.inputStream else conn.errorStream

        val response = stream?.let {
            BufferedReader(InputStreamReader(it)).use { reader -> reader.readText() }
        } ?: ""

        conn.disconnect()

        if (responseCode !in 200..299) {
            throw Exception("Supabase error ($responseCode): $response")
        }

        return response
    }

    /** GET genérico. Devuelve un JSONArray con las filas encontradas. */
    fun select(table: String, query: String = ""): JSONArray {
        val path = "/rest/v1/$table${if (query.isNotEmpty()) "?$query" else ""}"
        val response = request("GET", path)
        return JSONArray(response)
    }

    /** INSERT genérico. Devuelve la fila creada como JSONObject. */
    fun insert(table: String, data: JSONObject): JSONObject {
        val path = "/rest/v1/$table"
        val response = request("POST", path, data.toString(), preferReturn = true)
        val array = JSONArray(response)
        return array.getJSONObject(0)
    }

    /** UPDATE genérico filtrando por una condición simple (columna = valor). */
    fun update(table: String, query: String, data: JSONObject) {
        val path = "/rest/v1/$table?$query"
        request("PATCH", path, data.toString(), preferReturn = false)
    }

    /** DELETE genérico filtrando por una condición simple (columna = valor). */
    fun delete(table: String, query: String) {
        val path = "/rest/v1/$table?$query"
        request("DELETE", path)
    }

    /** Codifica un valor para usarlo seguro en query strings (espacios, tildes, etc). */
    fun encode(value: String): String = URLEncoder.encode(value, "UTF-8")
}