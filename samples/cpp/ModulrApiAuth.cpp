#include <iostream>
#include <string>
#include <map>
#include <sstream>
#include <time.h>
#include <string.h>
#include <curl/curl.h>
#include "openssl/hmac.h"
#include "openssl/evp.h"

using namespace std;

class ModulrApiAuth {

public:
  ModulrApiAuth(string key, string secret) : key_(key), secret_(secret) { }

  map<string, string> generateApiAuthHeaders(string nonce) {
    map<string, string> headers;
    string date = generateDate();
    headers["Date"] = date;
    headers["x-mod-nonce"] = nonce;
    string authHeader = buildAuthHeader(nonce, date);
    headers["Authorization"] = authHeader;
    return headers;
  }

private:
  string generateDate() {
    time_t now = time(0);
    struct tm tm = *gmtime(&now);
    strftime(buf_, sizeof buf_, "%a, %d %b %Y %H:%M:%S %Z", &tm);
    return string(buf_);
  }

  string buildSigString(string nonce, string date) {
    stringstream fmt;
    fmt << "date: " << date << "\nx-mod-nonce: " << nonce;
    return fmt.str();
  }

  string buildAuthHeader(string nonce, string date) {
    string sigString = buildSigString(nonce, date);
    unsigned char* digest = HMAC(EVP_sha1(), secret_.c_str(), strlen(secret_.c_str()), (unsigned char*)sigString.c_str(), strlen(sigString.c_str()), NULL, NULL);
    string encoded = base64UrlEncode(digest);
    stringstream fmt;
    fmt << "Signature keyId=\"" << key_ << "\",algorithm=\"hmac-sha1\",headers=\"date x-mod-nonce\",signature=\"" << encoded << "\"";
    return fmt.str();
  }

  string base64UrlEncode(unsigned char *data) {
    CURL *curl = curl_easy_init();
    if (!curl) {
      cerr << "Error initialising curl\n";
      return NULL;
    }

    // Base 64 encode
    EVP_EncodeBlock((unsigned char *)buf_, data, 20);

    // URL encode
    char *enc = curl_easy_escape(curl, buf_, 0);
    string result(enc);

    curl_free(enc);
    curl_easy_cleanup(curl);

    return result;
  }

  string key_;
  string secret_;
  char buf_[512];
};
