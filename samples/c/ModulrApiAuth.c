#include <stdio.h>
#include <string.h>
#include <time.h>
#include <uuid/uuid.h>
#include <curl/curl.h>
#include "openssl/hmac.h"
#include "openssl/evp.h"
#include "ModulrApiAuth.h"

static unsigned char buf[512];

struct Header *generateApiAuthHeaders(const char *key, const char *secret, char *nonce) {

  char *date = generateDate();
  if (!date) {
    fprintf(stderr, "Unable to generate date\n");
    return NULL;
  }

  char *authHeader = buildAuthHeader(key, secret, nonce, date);
  if (!authHeader) {
    fprintf(stderr, "Unable to build auth header\n");
    free(date);
    return NULL;
  }

  struct Header *headers = calloc(NUM_HEADERS + 1, sizeof(struct Header));
  if (!headers) {
    fprintf(stderr, "Unable to build headers\n");
    free(date);
    free(authHeader);
    return NULL;
  }

  headers[0].key = strdup("Date");
  headers[0].value = date;

  headers[1].key = strdup("x-mod-nonce");
  headers[1].value = strdup(nonce);

  headers[2].key = strdup("Authorization");
  headers[2].value = authHeader;

  return headers;
}

void freeHeaders(struct Header *headers) {
  struct Header *h = headers;
  while (h->key != NULL) {
    freeHeader(*h);
    h++;
  }

  free(headers);
}

char *buildAuthHeader(const char *key, const char *secret, char *nonce, char *date) {
  char *sigString = buildSigString(nonce, date);
  if (!sigString) {
    fprintf(stderr, "Unable to build sigString\n");
    return NULL;
  }

  unsigned char* digest = HMAC(EVP_sha1(), secret, strlen(secret), (unsigned char*)sigString, strlen(sigString), NULL, NULL);
  char *encoded = base64UrlEncode(digest);
  if (!encoded) {
    fprintf(stderr, "Unable to build url encoded sig string\n");
    free(sigString);
    return NULL;
  }

  free(sigString);
  sprintf(buf, "Signature keyId=\"%s\",algorithm=\"hmac-sha1\",headers=\"date x-mod-nonce\",signature=\"%s\"", key, encoded);
  free(encoded);
  return strdup(buf);
}


char *buildSigString(char *nonce, char *date) {
  sprintf(buf, "date: %s\nx-mod-nonce: %s", date, nonce);
  return strdup(buf);
}


char *generateDate() {
  time_t now = time(0);
  struct tm tm = *gmtime(&now);
  strftime(buf, sizeof buf, "%a, %d %b %Y %H:%M:%S %Z", &tm);
  return strdup(buf);
}

char *base64UrlEncode(unsigned char *data) {
  CURL *curl = curl_easy_init();
  if (!curl) {
    fprintf(stderr, "Error initialising curl\n");
    return NULL;
  }

  // Base 64 encode
  EVP_EncodeBlock(buf, data, 20);

  // URL encode
  char *enc = curl_easy_escape(curl, buf, 0);
  char *ret = strdup(enc);

  curl_free(enc);
  curl_easy_cleanup(curl);
  return ret;
}

void freeHeader(struct Header h) {
  free(h.key);
  free(h.value);
}
