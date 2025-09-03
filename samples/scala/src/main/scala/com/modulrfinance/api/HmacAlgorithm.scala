package com.modulrfinance.api

enum HmacAlgorithm(val javaAlgorithmName: String, val apiName: String) {
  case SHA1 extends HmacAlgorithm("HmacSha1", "hmac-sha1")
  case SHA256 extends HmacAlgorithm("HmacSha256", "hmac-sha256")
  case SHA384 extends HmacAlgorithm("HmacSha384", "hmac-sha384")
  case SHA512 extends HmacAlgorithm("HmacSha512", "hmac-sha512")
}