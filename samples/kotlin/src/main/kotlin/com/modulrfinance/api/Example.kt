package com.modulrfinance.api

import org.apache.http.client.methods.CloseableHttpResponse
import org.apache.http.client.methods.HttpUriRequest
import org.apache.http.client.methods.RequestBuilder
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.impl.client.HttpClients
import org.apache.http.util.EntityUtils

object Example {

    private const val API_KEY = "<YOUR_API_KEY>"
    private const val API_SECRET = "<YOUR_API_SECRET>"

    private const val URL = "https://api-sandbox.modulrfinance.com/api-sandbox/customers"

    @JvmStatic
    fun main(args: Array<String>) {
        // Set up signature helper
        val signature = Signature(API_KEY, API_SECRET)

        // Calculate HMAC signature with current date time and random nonce
        val result = signature.generateApiAuthHeaders()

        // Call the API with required headers
        HttpClients.createDefault().use { client: CloseableHttpClient ->
            val builder: RequestBuilder = RequestBuilder.get()
                .setUri(URL)

            result.forEach { (key, value) -> builder.addHeader(key, value) }

            val request: HttpUriRequest = builder.build()
            client.execute(request).use { response: CloseableHttpResponse ->

                val statusCode = response.statusLine.statusCode
                val body = EntityUtils.toString(response.entity)

                if (statusCode != 200) {
                    println("Unsuccessful API call, code: $statusCode, body: $body")
                } else {
                    println(body)
                }
            }
        }
    }
}
