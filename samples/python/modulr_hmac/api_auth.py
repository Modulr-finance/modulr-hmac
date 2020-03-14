import codecs
import hashlib
import hmac
import secrets
import urllib.parse
from datetime import datetime
from time import mktime
from wsgiref.handlers import format_date_time


class Signature:
    api_key = ""
    api_secret = ""

    def __init__(self, api_key, api_secret):
        self.api_key = api_key
        self.api_secret = api_secret


    def calculate(self, nonce = None, timestamp = None): 
        
        if nonce is None:
            # Generates a secure random string for the nonce
            nonce = secrets.token_urlsafe(30)

        if timestamp is None:
            # Getting current time
            now = datetime.now()
            stamp = mktime(now.timetuple())

            # Formats time into this format --> Mon, 25 Jul 2016 16:36:07 GMT
            formatted_time = format_date_time(stamp)
        else:
            formatted_time = timestamp

        # Combines date and nonce into a single string that will be signed
        signature_string = 'date' + ': ' + formatted_time + '\n' + 'x-mod-nonce' + ': ' + nonce

        # Encodes secret and message into a format that can be signed
        secret = bytes(self.api_secret, encoding='utf-8')
        message = bytes(signature_string, encoding='utf-8')

        # Signing process
        digester = hmac.new(secret, message, hashlib.sha1)

        # Converts to hex
        hex_code = digester.hexdigest()

        # Decodes the signed string in hex into base64
        b64 = codecs.encode(codecs.decode(hex_code, 'hex'), 'base64').decode().strip()

        # Encodes the string so it is safe for URL
        url_safe_code = urllib.parse.quote(b64, safe='')
 
        return Result(formatted_time, nonce, self.api_key, url_safe_code)


class Result:
    timestamp = ""
    nonce = ""
    encoded_signature = ""
    authorisation = ''

    def __init__(self, timestamp, nonce, api_key, encoded_signature):
        self.timestamp = timestamp
        self.nonce = nonce
        self.encoded_signature = encoded_signature

        # Adds the key and signed response
        self.authorisation = f'Signature keyId="{api_key}",algorithm="hmac-sha1",headers="date x-mod-nonce",signature="{encoded_signature}"'


    def get_timestamp(self):
        return self.timestamp

    def get_nonce(self):
        return self.nonce

    def get_http_headers(self):
        return {
            'Authorization': self.authorisation,  # Authorisation header
            'Date': self.timestamp,  # Date header
            'x-mod-nonce': self.nonce,  # Adds nonce
        }

    def get_signature(self):
        return self.encoded_signature
