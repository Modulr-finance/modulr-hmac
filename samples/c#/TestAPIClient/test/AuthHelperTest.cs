using System;
using Xunit;

namespace TestAPIClient.Tests {
    public class AuthHelperTest {
        private AuthHelper underTest = new AuthHelper("KNOWN-TOKEN","NzAwZmIwMGQ0YTJiNDhkMzZjYzc3YjQ5OGQyYWMzOTI=");

        [Fact]
        public void GetHeaders_ForKnownInputs_ReturnsKnownHeaders() {
            string nonce = "28154b2-9c62b93cc22a-24c9e2-5536d7d";
            string expectedDateText = "Mon, 25 Jul 2016 16:36:07 GMT";
            string expectedAuthorisation = "Signature keyId=\"KNOWN-TOKEN\",algorithm=\"hmac-sha1\",headers=\"date x-mod-nonce\",signature=\"WBMr%2FYdhysbmiIEkdTrf2hP7SfA%3D\"";

            DateTime dateTime = DateTimeOffset.Parse(expectedDateText).UtcDateTime;

            var headers = underTest.GetHeaders(nonce, dateTime, false);
            
            Assert.True(headers["x-mod-retry"] == "false", "x-mod-retry header incorrect");
            Assert.True(headers["x-mod-nonce"] == nonce, "x-mod-nonce header incorrect");
            Assert.True(headers["Date"] == expectedDateText, "Date header incorrect");
            Assert.True(headers["Authorization"] == expectedAuthorisation, "Authorization header incorrect");
        }

        [Fact]
        public void GetHeaders_WhenNotProvided_DefaultsToCurrentDateTimeAndRetryFalse() {
            string nonce = Guid.NewGuid().ToString();
            DateTime expectedDateTime = DateTime.UtcNow;

            var headers = underTest.GetHeaders(nonce);
            
            Assert.True(headers["x-mod-retry"] == "false", "x-mod-retry header incorrect");
            Assert.True(headers["x-mod-nonce"] == nonce, "x-mod-nonce header incorrect");

            DateTime headerDateTime = DateTimeOffset.Parse(headers["Date"]).UtcDateTime;
            int secondsDiff = expectedDateTime.Subtract(headerDateTime).Seconds;
            Assert.True(secondsDiff == 0, "Date header incorrect");
        }
    }
}
