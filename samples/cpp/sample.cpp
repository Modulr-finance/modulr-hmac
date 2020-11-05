#include <iostream>
#include <string>
#include <map>
#include <iterator>
#include "ModulrApiAuth.cpp"

using namespace std;

int main(void) {

  string key = "<YOUR-API-KEY>";
  string secret = "<YOUR-SECRET>";

  ModulrApiAuth apiAuth(key, secret);

  map<string, string> headers = apiAuth.generateApiAuthHeaders("NONCE");

  map<string, string>::iterator it = headers.begin();
  while (it != headers.end()) {
    cout << it->first << ": " << it->second << endl;
    it++;
  }

  return 0;
}
