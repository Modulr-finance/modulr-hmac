# Modulr Finance HMAC

### Samples
Samples directory contain sample code for the following languages:
- Java

#### Java Sample
##### [com.modulr.api.ModulrApiAuth.java](samples/java/src/main/com/modulr/api/ApiKeyAModulrApiAuth.java)
This class is a helper that can generate required headers for a given value of API key and secret. It generates the following headers:
* Authorization
* Date
* x-mod-nonce
* x-mod-retry

To use this class instantiate it using your API key and secret.
```java
ModulrApiAuth modulrAuth = new ModulrApiAuth("KNOWN-TOKEN", "SECRET-TOKEN");
```
Then use one of the generateXXX methods to get a map of headers with the header name as the key.
```java
    Map<String, String> headers = modulrAuth.generateApiAuthHeaders("NONCE"); // replace NONCE with correct nonce to be used

    headers.forEach((key, value) -> System.out.println(key + ": " + value));
```        
OR

```java
    Map<String, String> headers = modulrAuth.generateRetryApiAuthHeaders(); // reuses the nonce used on the last generateApiAuthHeaders call

    headers.forEach((key, value) -> System.out.println(key + ": " + value));
```       

##### [com.modulr.hmac.Hmac.java](samples/java/src/main/com/modulr/hmac/Hmac.java)
This class demonstrates how to use the ModulrApiAuth class. 

### Authentication process
An in depth documentation of the whole authentication process can be found at [Modulr Api Auth](https://modulr-technology-ltd.cloud.tyk.io/portal/api-overview/#Auth)