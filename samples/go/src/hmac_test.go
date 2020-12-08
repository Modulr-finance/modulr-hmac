package main

import (
	"github.com/stretchr/testify/assert"
	"testing"
)

func TestGenerateReturnsAnHMACString(t *testing.T) {
	headers, _, _ := generate("api_key", "api_secret", "")
	expectedSignature := "Signature keyId=\"api_key\",algorithm=\"hmac-sha1\",headers=\"date x-mod-nonce\",headers=\""
	assert.Equal(t, expectedSignature, headers["Authorization"][0:86], "generate should return the hmac headers")
}

func TestGenerateThrowsErrorIfApiKeyIsNull(t *testing.T) {
	 _, err, _ := generate("", "api_secret", "")
	 assert.Equal(t, "api_key cannot be empty", err.Message)
}

func TestGenerateThrowsErrorIfApiSecretIsNull(t *testing.T) {
	_, err, _ := generate("api_key", "", "")
	assert.Equal(t, "api_secret cannot be empty", err.Message)
}

