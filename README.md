# Modulr Finance HMAC

### [ModulrAuthHeaderExample.java] (https://github.com/Modulr-finance/modulr-hmac/blob/master/ModulrAuthHeaderExample.java)
This class provides a Java example of generating HMAC Signature headers for use with Modulr API calls.

### [io.swagger.client.auth.ApiKeyAuth.java] (https://github.com/Modulr-finance/modulr-hmac/blob/master/io/swagger/client/auth/ApiKeyAuth.java)
This class provides a drop in replacement for the same file generated from Swagger.io Java client generation. This assumes you set ApiKey to your Modulr issued API Key and ApiKeyPrefix to your Modulr issued API secret in io.swagger.client.Configuration. For example;

```java
public static void main(String[] args) throws ApiException {
	Configuration.getDefaultApiClient().setApiKey("<Your API Key>");
	Configuration.getDefaultApiClient().setApiKeyPrefix("<You API Secret>");
	TransactionsApi api = new TransactionsApi();
	System.out.println(api.getTransactionsUsingGET("A02017NU",0,25));
}
```
