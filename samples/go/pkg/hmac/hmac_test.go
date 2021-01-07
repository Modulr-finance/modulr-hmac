package hmac

import (
	"github.com/stretchr/testify/assert"
	"testing"
	"time"
)

func TestGenerateReturnsAnHMACString(t *testing.T) {
	headers, _ := GenerateHeaders("api_key", "api_secret", "")
	expectedSignature := "Signature keyId=\"api_key\",algorithm=\"hmac-sha1\",headers=\"date x-mod-nonce\",signature=\""
	assert.Equal(t, expectedSignature, headers["Authorization"][0:86], "generate should return the hmac headers")
}

func TestGenerateReturnsADateHeader(t *testing.T) {
	injectMockDate()
	headers, _ := GenerateHeaders("api_key", "api_secret", "")
	expectedDate := "2020-01-02"
	assert.Equal(t, expectedDate, headers["Date"])
}

func TestGenerateReturnsANonceHeaderWithExpectedValue(t *testing.T) {
	nonce := "thisIsTheNonce"
	headers, _ := GenerateHeaders("api_key", "api_secret", nonce)
	assert.Equal(t, nonce, headers["x-mod-nonce"])
}

func TestGenerateReturnsAGeneratedNonceHeaderIfNonceIsEmpty(t *testing.T) {
	headers, _ := GenerateHeaders("api_key", "api_secret", "")
	assert.True(t, headers["x-mod-nonce"] != "", "x-mod-nonce header should have been populated")
}

func TestGenerateThrowsErrorIfApiKeyIsNull(t *testing.T) {
	 _, err := GenerateHeaders("", "api_secret", "")
	 assert.Equal(t, "api_key cannot be empty", err.Message)
}

func TestGenerateThrowsErrorIfApiSecretIsNull(t *testing.T) {
	_, err := GenerateHeaders("api_key", "", "")
	assert.Equal(t, "api_secret cannot be empty", err.Message)
}

func injectMockDate() {
	dateNow = func() time.Time {
		now, _ := time.Parse(time.RFC1123, "Mon, 02 Jan 2020 15:04:05 GMT")
		return now
	}
}
