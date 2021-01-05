using System;
using System.Net.Http;
using System.Net.Http.Headers;
using System.Threading.Tasks;

namespace TestAPIClient {
    // A simple demonstation program that calls the Customers endpoint on the Modult sandbox.
    // Replace the API_KEY and API_SECRET constants with yout values before running
    class Program {
        private const string API_KEY = "YOUR_API_KEY";
        private const string API_SECRET = "YOUR_API_SECRET";

        private static readonly HttpClient client = BuildHttpClient();
        private static readonly AuthHelper authHelper = new AuthHelper(API_KEY, API_SECRET);

        static async Task Main(string[] args) {
            await GetCusotmers();
        }

        private static HttpClient BuildHttpClient() {
            var httpClient = new HttpClient();
            httpClient.DefaultRequestHeaders.Accept.Clear();
            httpClient.DefaultRequestHeaders.Accept.Add(new MediaTypeWithQualityHeaderValue("application/json"));
            httpClient.DefaultRequestHeaders.Add("User-Agent", "Test API Client");

            return httpClient;
        }

        private static async Task GetCusotmers() {
            var request = new HttpRequestMessage {
                Method = HttpMethod.Get,
                RequestUri = new Uri("https://api-sandbox.modulrfinance.com/api-sandbox/customers")
            };

            // Generate a Nonce and add the Modulr headers to the request.
            // A more practical approach may be to implement a Custom Message Handler to automatically
            // add these, rather than repeating the steps for each endpoint
            string nonce = Guid.NewGuid().ToString();
            foreach(var header in authHelper.GetHeaders(nonce)) {
                request.Headers.Add(header.Key, header.Value);
            }

            var response = client.SendAsync(request).Result;
            
            // For the purpose of this demonstartion we will just output the response as a string
            var jsonString = await response.Content.ReadAsStringAsync();
            Console.WriteLine(jsonString);
        }
    }
}
