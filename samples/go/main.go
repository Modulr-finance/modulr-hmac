package main

import (
	"fmt"
	"github.com/Modulr-finance/modulr-hmac/pkg/hmac"
)

func main() {
	headers, _ := hmac.GenerateHeaders("api_key", "api_secret", "")

	for key, element := range headers {
		fmt.Println(key, ":", element)
	}
}
