import requests
from modulr_hmac.api_auth  import Signature

API_KEY = "57502612d1bb2c00010000256c9568f1f7d540e085052b425d46d233"
API_SECRET = "Y2Q0MzhmZDE2OWIzNDVkNGE5ZTczOTU3ZTAxZWY5NTc="

url = f'https://api-sandbox.modulrfinance.com/api-sandbox/customers'


# Set up signature helper
signature = Signature(API_KEY, API_SECRET)

# calculate mhac signature with current date time and random nonce
result = signature.calculate()

# call the api with requireed headers
response = requests.get(url, headers=result.get_http_headers())

if response.status_code != 200:
    print(f'Unsuccessful API call, code: {response.status_code}, body: {response.text}')
else:
    print(response.text)

