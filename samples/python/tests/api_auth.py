import unittest
from modulr_hmac.api_auth import Signature

class TestSignature(unittest.TestCase):

    signature = Signature('KNOWN-TOKEN','NzAwZmIwMGQ0YTJiNDhkMzZjYzc3YjQ5OGQyYWMzOTI=')

    def test_hmac_generation(self):
        result = self.signature.calculate()
        headers = result.get_http_headers()

        self.assertIsNotNone(headers['Date'])
        self.assertIsNotNone(headers['x-mod-nonce'])
        self.assertIsNotNone(headers['Authorization'])


    def test_hmac_known_nonce_date(self):
        result = self.signature.calculate('28154b2-9c62b93cc22a-24c9e2-5536d7d','Mon, 25 Jul 2016 16:36:07 GMT')
        headers = result.get_http_headers()

        self.assertEqual(headers['Date'], 'Mon, 25 Jul 2016 16:36:07 GMT')
        self.assertEqual(headers['x-mod-nonce'], '28154b2-9c62b93cc22a-24c9e2-5536d7d')
        self.assertEqual(headers['Authorization'], 'Signature keyId="KNOWN-TOKEN",algorithm="hmac-sha1",headers="date x-mod-nonce",signature="WBMr%2FYdhysbmiIEkdTrf2hP7SfA%3D"')
