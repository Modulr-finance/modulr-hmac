require 'base64'
require 'date'
require 'erb'
require 'openssl'
require 'securerandom'

class Signature
    def initialize(api_key:, api_secret:)
        @api_key = api_key
        @api_secret = api_secret
    end

    def calculate(nonce: SecureRandom.base64(30), timestamp: DateTime.now.httpdate)
        # Combines date and nonce into a single string that will be signed
        signature_string = "date: #{timestamp}\nx-mod-nonce: #{nonce}"

        # Sign the message using the secret
        digest = OpenSSL::HMAC.digest('SHA1', @api_secret.force_encoding('UTF-8'), signature_string.force_encoding('UTF-8'))

        # Decodes the signed string into base64
        b64 = Base64.encode64(digest)

        # Encodes the string so it is safe for URL
        url_safe_code = ERB::Util.url_encode(b64.strip)

        Result.new(api_key: @api_key, nonce: nonce, signature: url_safe_code, timestamp: timestamp)
    end
end

class Result
    attr_reader :nonce, :signature, :timestamp

    def initialize(api_key:, nonce:, signature:, timestamp:)
        @nonce = nonce
        @signature = signature
        @timestamp = timestamp

        # Adds the key and signed response
        @authorisation = %(Signature keyId="#{api_key}",algorithm="hmac-sha1",headers="date x-mod-nonce",signature="#{signature}")
    end

    def headers
        {
            'Authorization': @authorisation,  # Authorisation header
            'Date': @timestamp,  # Date header
            'x-mod-nonce': @nonce  # Adds nonce
        }
    end
end
