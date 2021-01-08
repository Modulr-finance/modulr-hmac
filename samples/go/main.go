package main

import (
	"fmt"
	"github.com/Modulr-finance/modulr-hmac/hmac"
)

func main() {
	headers, _ := hmac.GenerateHeaders("api-key", "api-secret", "")

	for key, element := range headers {
		fmt.Println(key, ":", element)
	}
}
