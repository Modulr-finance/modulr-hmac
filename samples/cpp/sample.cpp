#include <iostream>
#include <string>
#include <map>
#include <iterator>
#include "ModulrApiAuth.cpp"

using namespace std;

int main(void) {

  string key = "57502612d1bb2c0001000025fd53850cd9a94861507a5f7cca236882";
  string secret = "NzAwZmIwMGQ0YTJiNDhkMzZjYzc3YjQ5OGQyYWMzOTI=";

  ModulrApiAuth apiAuth(key, secret);

  map<string, string> headers = apiAuth.generateApiAuthHeaders("NONCE");

  map<string, string>::iterator it = headers.begin();
  while (it != headers.end()) {
    cout << it->first << ": " << it->second << endl;
    it++;
  }

  return 0;
}
