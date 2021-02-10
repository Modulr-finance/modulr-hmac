import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.text.SimpleDateFormat
import java.util.{Base64, Calendar, Date, Locale, TimeZone, UUID}

import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

class Signature(val apiKey: String, val apiSecret: String) {

  private val DatePattern: String = "EEE, dd MMM yyyy HH:mm:ss z"
  private val HmacSha1Algorithm: String = "HmacSHA1"

  def buildHeaders(nonce: String = UUID.randomUUID().toString,
                   date: Date = Calendar.getInstance().getTime): Map[String, String] = {
    val timestamp = formatDate(date)
    val signatureString = s"date: $timestamp\nx-mod-nonce: $nonce"

    val hmacSignature = calculateHmac(signatureString)

    Map("Authorization" -> formatAuthHeader(apiKey, hmacSignature),
      "Date" -> timestamp,
      "x-mod-nonce" -> nonce)
  }

  private def formatAuthHeader(key: String, signature: String): String = {
    s"Signature keyId= ${'"'}$key${'"'}," +
      s"algorithm=${'"'}hmac-sha1${'"'}," +
      s"headers=${'"'}date x-mod-nonce${'"'}," +
      s"signature=${'"'}$signature${'"'}"
  }

  private def calculateHmac(message: String): String = {
    val signingKey = new SecretKeySpec(apiSecret.getBytes, HmacSha1Algorithm)

    val mac = Mac.getInstance(HmacSha1Algorithm)
    mac.init(signingKey)

    // compute the hmac on input data bytes
    val rawHmac = mac.doFinal(message.getBytes())

    // base64-encode the hmac
    val hmac = Base64.getEncoder.encodeToString(rawHmac)
    URLEncoder.encode(hmac, StandardCharsets.UTF_8.name())
  }

  private def formatDate(date: Date): String = {
    val simpleDateFormat = new SimpleDateFormat(DatePattern, Locale.UK)
    simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"))
    simpleDateFormat.format(date)
  }
}