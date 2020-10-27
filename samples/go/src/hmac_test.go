package main

import (
	"github.com/stretchr/testify/assert"
	"testing"
)

func TestGenerateReturnsAnHMACString(t *testing.T) {
	var signature, _ = generate("api_key", "api_secret")
	assert.Equal(t, signature, "key", "generate should return the hmac signature")
}

func TestGenerateThrowsErrorIfApiKeyIsNull(t *testing.T) {
	 var _, err = generate("", "api_secret")
	 assert.Equal(t, "api_key cannot be empty", err.Message)
}

func TestGenerateThrowsErrorIfApiSecretIsNull(t *testing.T) {
	var _, err = generate("api_key", "")
	assert.Equal(t, "api_secret cannot be empty", err.Message)
}
