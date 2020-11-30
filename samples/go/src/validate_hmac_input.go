package main

type SignatureError struct {
	Message string
}

func validateInput (apiKey string, apiSecret string) *SignatureError {
	if apiKey == "" {
		return &SignatureError{ "api_key cannot be empty" }
	}

	if apiSecret == "" {
		return &SignatureError{ "api_secret cannot be empty" }
	}

	return nil
}
