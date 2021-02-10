import java.util.Calendar

object Main extends App {
  val signature: Signature = new Signature("key", "secret")

  //Option A: call with no input. Nonce will be a random UUID and Date will default to current
  val headersDefault: Map[String, String] = signature.buildHeaders()

  //Option B: Provide your own nonce, Date will be defaulted to current
  val headersCustomNonce: Map[String, String] = signature.buildHeaders("nonceValue")

  //Option C: Provide your own nonce and Date
  val headersCustomAll: Map[String, String] = signature.buildHeaders("nonceValue", Calendar.getInstance().getTime)

  headersDefault.foreach(println)
  headersCustomNonce.foreach(println)
  headersCustomAll.foreach(println)
}