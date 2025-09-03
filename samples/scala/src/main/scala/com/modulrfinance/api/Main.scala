package com.modulrfinance.api

import com.modulrfinance.api.HmacAlgorithm.SHA384

import java.time.Clock

object Main extends App {
  private var signature: Signature = new Signature("key", "secret")

  //Option A: call with no input. Nonce will be a random UUID and replay will be false
  private val headersDefault: Map[String, String] = signature.generateApiAuthHeaders()

  //Option B: Provide your own nonce, replay will be false
  private val headersCustomNonce: Map[String, String] = signature.generateApiAuthHeaders("nonceValue")

  //Option C: Replay a request with your own nonce and x-mod-replay enabled
  private val headersCustomAll: Map[String, String] = signature.generateApiAuthHeaders("nonceValue", true)

  signature = new Signature("key", "secret", Clock.systemUTC(), SHA384)

  //Option D: Use a different HMAC algorithm (SHA384 in this case)
  private val headersCustomAllDifferentAlgorithm: Map[String, String] = signature.generateApiAuthHeaders("nonceValue", true)

  headersDefault.foreach(println)
  headersCustomNonce.foreach(println)
  headersCustomAll.foreach(println)
  headersCustomAllDifferentAlgorithm.foreach(println)
}