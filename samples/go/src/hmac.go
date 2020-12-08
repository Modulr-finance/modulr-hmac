package main

import (
	"crypto/hmac"
	"crypto/sha1"
	"encoding/base64"
	"github.com/google/uuid"
	"net/url"
	"time"
)

const (
	Algorithm       = "algorithm=\"hmac-sha1\","
	Headers         = "headers=\"date x-mod-nonce\","
	SignaturePrefix = "signature=\""
	SignatureSuffix = "\""
	DateFormat      = "Mon, 02 Jan 2006 15:04:05 GMT"
	Date 			= "date: "
	Nonce = "\nx-mod-nonce: "
	KeyIdPrefix = "Signature keyId=\""
	EmptyString = ""
)

var dateNow = time.Now

func generate(apiKey string, apiSecret string, nonce string) (map[string] string, *SignatureError) {
	validationError := validateInput(apiKey, apiSecret)
	headers := make(map[string] string)
	if validationError != nil {
		return nil, validationError
	}
	signature, err := buildSignature(apiKey, apiSecret, nonce)

	if err != nil {
		return nil, err
	}

	headers["Authorization"] = signature

	return headers, nil
}

func buildSignature(apiKey string, apiSecret string, nonce string) (string, *SignatureError) {
	return buildKeyId(apiKey) + Algorithm + Headers + generateEncodedSignature(apiSecret, nonce), nil
}

func buildKeyId(apiKey string) string {
	return KeyIdPrefix + apiKey + "\","
}

func generateEncodedSignature(apiSecret string, nonce string) string {

	mac := hmac.New(sha1.New, []byte(apiSecret))

	plainSig := retrieveDate() + buildNonce(nonce)
	mac.Write([]byte(plainSig))
	encodedMac := mac.Sum(nil)
	base64Encoded := base64.URLEncoding.EncodeToString(encodedMac)

	return SignaturePrefix + url.QueryEscape(base64Encoded) + SignatureSuffix
}

func retrieveDate() string {
	now := dateNow()
	return Date + now.Format(DateFormat)
}

func buildNonce(nonce string) string {
	if nonce == EmptyString {
		nonce = uuid.New().String()
	}
	return Nonce + nonce
}
