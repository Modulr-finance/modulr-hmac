package main

import (
	"github.com/Modulr-finance/modulr-hmac/src/signature"
	"github.com/google/uuid"
	"time"
)

const (
	AuthorizationHeader = "Authorization"
	DateHeader          = "Date"
	DateFormat = "2006-01-02"
	EmptyString     = ""
	NonceHeader = "x-mod-nonce"
)

var dateNow = time.Now

func generate(apiKey string, apiSecret string, nonce string) (map[string] string, *ValidationError) {
	validationError := validateInput(apiKey, apiSecret)

	if validationError != nil {
		return nil, validationError
	}
	return buildHeaders(apiKey, apiSecret, nonce), nil
}

func buildHeaders(apiKey string, apiSecret string, nonce string) map[string] string {
	headers := make(map[string] string)
	date := dateNow()
	nonce = generateNonceIfEmpty(nonce)

	headers[DateHeader] = date.Format(DateFormat)
	headers[AuthorizationHeader] = signature.Build(apiKey, apiSecret, nonce, date)
	headers[NonceHeader] = nonce
	return headers
}

func generateNonceIfEmpty(nonce string) string {
	if nonce == EmptyString {
		nonce = uuid.New().String()
	}
	return  nonce
}
