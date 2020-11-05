#ifndef MODULR_API_AUTH_H
#define MODULR_API_AUTH_H

#define NUM_HEADERS 3

struct Header {
  char *key;
  char *value;
};

// public
struct Header *generateApiAuthHeaders(const char *, const char *, char *);
void freeHeaders(struct Header *);

// private
char *buildAuthHeader(const char *, const char *, char *, char *);
char *buildSigString(char *, char *);
char *base64UrlEncode(unsigned char *);
char *generateDate();
void freeHeader(struct Header);


#endif

