package com.modulrfinance.api

import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.security.{GeneralSecurityException, SignatureException}
import java.time.format.DateTimeFormatter
import java.time.{Clock, ZoneId}
import java.util.{Base64, Locale, TimeZone, UUID}
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

class Signature(val keyId: String, val hmacSecret: String, val clock: Clock = Clock.systemUTC, val hmacAlgorithm: HmacAlgorithm = HmacAlgorithm.SHA512) {

  private val DatePattern = "EEE, dd MMM yyyy HH:mm:ss z"
  private val DateTimeFormat = DateTimeFormatter.ofPattern(DatePattern, Locale.ENGLISH).withZone(ZoneId.of("GMT"))

  @throws[SignatureException]
  def generateApiAuthHeaders(nonce: String = UUID.randomUUID().toString, isReplay: Boolean = false): Map[String, String] = {
    val formattedDate = DateTimeFormat.format(clock.instant().atZone(ZoneId.of("GMT")))
    val signature = generateSignature(nonce, formattedDate)
    Map("Authorization" -> formatAuthHeader(keyId, signature, hmacAlgorithm),
      "Date" -> formattedDate,
      "x-mod-nonce" -> nonce,
      "x-mod-retry" -> String.valueOf(isReplay))
  }

  @SuppressWarnings(Array("java:S3457")) // System specific line separators are required in the HMAC signature specification
  @throws[SignatureException]
  private def generateSignature(nonce: String, formattedDate: String) = {
    val data = String.format("date: %s\nx-mod-nonce: %s", formattedDate, nonce)
    calculateHmac(data)
  }

  @throws[SignatureException]
  private def calculateHmac(content: String) = try {
    val signingKey = new SecretKeySpec(hmacSecret.getBytes(StandardCharsets.UTF_8), hmacAlgorithm.javaAlgorithmName)
    val mac = Mac.getInstance(hmacAlgorithm.javaAlgorithmName)
    mac.init(signingKey)
    val rawHmac = mac.doFinal(content.getBytes(StandardCharsets.UTF_8))
    val hmac = Base64.getEncoder.encodeToString(rawHmac)
    URLEncoder.encode(hmac, StandardCharsets.UTF_8)
  } catch {
    case e: GeneralSecurityException =>
      throw new SignatureException("Failed to generate HMAC: " + e.getMessage, e)
  }

  private def formatAuthHeader(keyId: String, signature: String, algorithm: HmacAlgorithm) = String.format("Signature keyId=\"%s\",algorithm=\"%s\",headers=\"date x-mod-nonce\",signature=\"%s\"", keyId, algorithm.apiName, signature)

}