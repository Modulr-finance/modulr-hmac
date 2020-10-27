package main

type SignatureError struct {
	Message string
}

func generate(apiKey string, apiSecret string) (string, *SignatureError) {
	validationError := validateInput(apiKey, apiSecret)
	if validationError != nil {
		return "", validationError
	}

	return "key", nil
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
