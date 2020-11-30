package main

import (
	"fmt"
	"github.com/stretchr/testify/assert"
	"strings"
	"testing"
	"time"
)

func TestGenerateReturnsAnHMACString(t *testing.T) {
	signature, _ := generate("57502612d1bb2c0001000025276a506e125f464f89132e7ca1773987", "YTUzODc3N2FiODEyNDdiMWE3ZGUwMDU1NDZmNmNmMzA=", "")
	fmt.Print("\n" + signature)
	expectedSignature := "Signature keyId=\"api_key\",algorithm=\"hmac-sha1\",headers=\"date x-mod-nonce\",signature=\""
	assert.Equal(t, expectedSignature, signature[0:86], "generate should return the hmac signature")
}

func TestGenerateThrowsErrorIfApiKeyIsNull(t *testing.T) {
	 _, err := generate("", "api_secret", "")
	 assert.Equal(t, "api_key cannot be empty", err.Message)
}

func TestGenerateThrowsErrorIfApiSecretIsNull(t *testing.T) {
	_, err := generate("api_key", "", "")
	assert.Equal(t, "api_secret cannot be empty", err.Message)
}

func TestGenerateReturnsSignatureWithKeyId(t *testing.T) {
	signature, _ := generate("api_key", "api_secret", "")
	expectedPrefix := "Signature keyId=\"api_key\","
	hasKeyId := strings.HasPrefix(signature, expectedPrefix)
	assert.True(t, hasKeyId, "HMAC signature must contain the keyId")
}

func TestGenerateReturnsSignatureWithAlgorithm(t *testing.T) {
	signature, _ := generate("api_key", "api_secret", "")
	expectedAlgorithm := "algorithm=\"hmac-sha1\","
	actualValue := signature[26:48]
	assert.Equal(t, expectedAlgorithm, actualValue, "HMAC signature must contain the algorithm used")
}

func TestGenerateReturnsSignatureWithHeaders(t *testing.T) {
	signature, _ := generate("api_key", "api_secret", "")
	expectedHeaders := "headers=\"date x-mod-nonce\","
	actualValue := signature[48:75]
	assert.Equal(t, expectedHeaders, actualValue, "HMAC signature must contain the headers")
}

func TestGenerateReturnsSignatureWithSignatureValue(t *testing.T) {
	signature, _ := generate("api_key", "api_secret", "")
	expectedSignature := "signature=\""
	actualValue := signature[75:86]
	assert.Equal(t, expectedSignature, actualValue, "HMAC signature must contain the signature")
}

func TestGenerateReturnsHashedSignature(t *testing.T) {
	signature, _ := generate("api_key", "api_secret", "")
	fmt.Print(signature)
	actualValue := signature[86:117]
	assert.True(t, actualValue != "", "Encoded HMAC signature should be present")
}

func TestGenerateAcceptsANonce(t *testing.T) {
	injectMockDate()
	signature, _ := generate("api_key", "api_secret", "nonce")
	actualValue := signature[86:116]

	expected := "GDiVOYxYOn6n7MVcAzREF_Nv-Kw%3D"
	assert.Equal(t, expected, actualValue, "HMAC signature must contain the signature")
}

func injectMockDate() {
	dateNow = func() time.Time {
		now, _ := time.Parse("2020-01-02", "2020-02-02")
		return now
	}
}
