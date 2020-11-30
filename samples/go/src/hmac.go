package main

import (
	"crypto/hmac"
	"crypto/sha1"
	"encoding/base64"
	"fmt"
	"github.com/google/uuid"
	"net/url"
	"time"
)

const (
	ErrorResponse   = ""
	Algorithm       = "algorithm=\"hmac-sha1\","
	Headers         = "headers=\"date x-mod-nonce\","
	SignaturePrefix = "signature=\""
	SignatureSuffix = "\""
	DateFormat      = "Mon, 02 Jan 2006 15:04:05 GMT"
	Date 			= "date: "
	Nonce = "\nx-mod-nonce: "
)

var dateNow = time.Now

func generate(apiKey string, apiSecret string, nonce string) (string, *SignatureError) {
	validationError := validateInput(apiKey, apiSecret)
	if validationError != nil {
		return ErrorResponse, validationError
	}

	return buildSignature(apiKey, apiSecret, nonce)
}

func buildSignature(apiKey string, apiSecret string, nonce string) (string, *SignatureError) {
	return buildKeyId(apiKey) + Algorithm + Headers + generateEncodedSignature(apiSecret, nonce), nil
}

func buildKeyId(apiKey string) string {
	return "Signature keyId=\"" + apiKey + "\","
}

func generateEncodedSignature(apiSecret string, nonce string) string {

	mac := hmac.New(sha1.New, []byte(apiSecret))

	plainSig := retrieveDate() + buildNonce(nonce)
	fmt.Print("plainSig[" + plainSig + "]")
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
	if nonce == "" {
		nonce = uuid.New().String()
	}
	fmt.Print("nonce [" + nonce + "]")
	return Nonce + nonce
}
