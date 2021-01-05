using System;
using System.Collections.Generic;
using System.Net;
using System.Security.Cryptography;
using System.Text;

namespace TestAPIClient {
    public class AuthHelper {

        private readonly string apiKey;
        private readonly string secret;

        public AuthHelper(string apiKey, string secret) {
            this.apiKey = apiKey;
            this.secret = secret;
        }
        public Dictionary<string, string> GetHeaders(string nonce) {
            return GetHeaders(nonce, DateTime.UtcNow, false);
        }

        public Dictionary<string, string> GetHeaders(string nonce, DateTime dateTime, bool asRetry) {
            var signature = GenerateSignature(nonce, dateTime);

            var headers = new Dictionary<string, string>();
            headers.Add("Authorization", GenerateAuthHeaderContent(apiKey, signature));
            headers.Add("Date", dateTime.ToString("R"));
            headers.Add("x-mod-nonce", nonce);
            headers.Add("x-mod-retry", asRetry.ToString().ToLower());

            return headers;
        }

        private String GenerateAuthHeaderContent(String apiKey, String signature) {
            return $"Signature keyId=\"{apiKey}\",algorithm=\"hmac-sha1\",headers=\"date x-mod-nonce\",signature=\"{signature}\"";
        }

        private string GenerateSignature(string nonce, DateTime dateTime) {
            return HashAndEncode($"date: {dateTime:R}\nx-mod-nonce: {nonce}", secret);
        }

        private static string HashAndEncode(string message, string secret) {
            var ascii = Encoding.ASCII;
            HMACSHA1 hmac = new HMACSHA1(ascii.GetBytes(secret));
            hmac.Initialize();

            byte[] messageBuffer = ascii.GetBytes(message);
            byte[] hash = hmac.ComputeHash(messageBuffer);

            return WebUtility.UrlEncode(Convert.ToBase64String(hash));
        }
    }
}