require 'net/http'
require_relative 'lib/api_auth'

API_KEY = "<YOUR_API_KEY>"
API_SECRET = "<YOUR_API_SECRET>"

uri = URI('https://api-sandbox.modulrfinance.com/api-sandbox/customers')

# Set up signature helper
signature = Signature.new(api_key: API_KEY, api_secret: API_SECRET)

# calculate mhac signature with current date time and random nonce
result = signature.calculate

# call the api with required headers
res = nil

Net::HTTP.start(uri.host, uri.port, :use_ssl => uri.scheme == 'https') do |http|
  req = Net::HTTP::Get.new(uri)

  result.headers.each do |header|
    req.[]=(header[0], header[1])
  end

  res = http.request(req)
end

if res.code != 200
    puts "Unsuccessful API call, code: #{res.code}, body: #{res.body}"
else
    puts res.body
end
