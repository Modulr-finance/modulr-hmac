package signature

import (
	"github.com/stretchr/testify/assert"
	"strings"
	"testing"
	"time"
)

func TestGenerateReturnsSignatureWithKeyId(t *testing.T) {
	signature, _ := Build("api_key", "api_secret", "")
	expectedPrefix := "Signature keyId=\"api_key\","
	hasKeyId := strings.HasPrefix(signature, expectedPrefix)
	assert.True(t, hasKeyId, "HMAC signature must contain the keyId")
}

func TestGenerateReturnsSignatureWithAlgorithm(t *testing.T) {
	signature, _ := Build("api_key", "api_secret", "")
	expectedAlgorithm := "algorithm=\"hmac-sha1\","
	actualValue := signature[26:48]
	assert.Equal(t, expectedAlgorithm, actualValue, "HMAC signature must contain the algorithm used")
}

func TestGenerateReturnsSignatureWithHeaders(t *testing.T) {
	signature, _ := Build("api_key", "api_secret", "")
	expectedHeaders := "headers=\"date x-mod-nonce\","
	actualValue := signature[48:75]
	assert.Equal(t, expectedHeaders, actualValue, "HMAC signature must contain the headers")
}

func TestGenerateReturnsSignatureWithSignatureValue(t *testing.T) {
	signature, _ := Build("api_key", "api_secret", "")
	expectedSignature := "signature=\""
	actualValue := signature[75:86]
	assert.Equal(t, expectedSignature, actualValue, "HMAC signature must contain the signature")
}

func TestGenerateReturnsHashedSignature(t *testing.T) {
	signature, _ := Build("api_key", "api_secret", "")
	actualValue := signature[86:117]
	assert.True(t, actualValue != "", "Encoded HMAC signature should be present")
}

func TestGenerateAcceptsANonce(t *testing.T) {
	injectMockDate()
	signature, _ := Build("api_key", "api_secret", "nonce")
	actualValue := signature[86:116]

	expected := "dUIUO_JZUOSjsEhEBb_QoedvHic%3D"
	assert.Equal(t, expected, actualValue, "HMAC signature must contain the signature")
}

func injectMockDate() {
	dateNow = func() time.Time {
		now, _ := time.Parse("2020-01-02", "2020-02-02")
		return now
	}
}
