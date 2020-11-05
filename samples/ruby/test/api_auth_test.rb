require 'minitest/autorun'
require_relative '../lib/api_auth'

class TestSignature < Minitest::Test
    def setup
        @signature = Signature.new(api_key: 'KNOWN-TOKEN', api_secret: 'NzAwZmIwMGQ0YTJiNDhkMzZjYzc3YjQ5OGQyYWMzOTI=')
    end

    def test_hmac_generation
        result = @signature.calculate
        headers = result.headers

        refute_nil headers[:'Date']
        refute_nil headers[:'x-mod-nonce']
        refute_nil headers[:'Authorization']
    end

    def test_hmac_known_nonce_date
        result = @signature.calculate(nonce: '28154b2-9c62b93cc22a-24c9e2-5536d7d', timestamp: 'Mon, 25 Jul 2016 16:36:07 GMT')
        headers = result.headers

        assert_equal 'Mon, 25 Jul 2016 16:36:07 GMT', headers[:'Date']
        assert_equal '28154b2-9c62b93cc22a-24c9e2-5536d7d', headers[:'x-mod-nonce']
        assert_equal 'Signature keyId="KNOWN-TOKEN",algorithm="hmac-sha1",headers="date x-mod-nonce",signature="WBMr%2FYdhysbmiIEkdTrf2hP7SfA%3D"', headers[:'Authorization']
    end
end
