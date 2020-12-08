package main

import "github.com/Modulr-finance/modulr-hmac/src/signature"

func generate(apiKey string, apiSecret string, nonce string) (map[string] string, *ValidationError, *signature.SignatureError) {
	validationError := validateInput(apiKey, apiSecret)
	headers := make(map[string] string)
	if validationError != nil {
		return nil, validationError, nil
	}
	signature, err := signature.Build(apiKey, apiSecret, nonce)

	if err != nil {
		return nil, nil, err
	}

	headers["Authorization"] = signature

	return headers, nil, nil
}

