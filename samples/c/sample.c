#include <stdio.h>
#include <stdlib.h>

#include "ModulrApiAuth.h"

int main(void) {

  const char key[]             = "<YOUR-API-KEY>";
  const char secret[]          = "<YOUR-SECRET>";

  struct Header *headers = generateApiAuthHeaders(key, secret, "NONCE");

  struct Header *h = headers;
  while (h->key != NULL) {
    printf("%s: %s\n", h->key, h->value);
    h++;
  }

  freeHeaders(headers);

  return 0;
}
