# Modulr Finance HMAC

Please note the samples are designed to be self contained to demonstrate hmac signature usage.

## Samples

Samples directory contain sample code for the following languages:

- Java
- NodeJS

---

### Java Sample

#### [com.modulr.api.ModulrApiAuth.java](samples/java/src/main/java/com/modulr/api/ModulrApiAuth.java)

This class is a helper that can generate required headers for a given value of API key and secret. It generates the following headers:

- Authorization
- Date
- x-mod-nonce
- x-mod-retry

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

#### [com.modulr.hmac.Hmac.java](samples/java/src/main/java/com/modulr/hmac/Hmac.java)

This class demonstrates how to use the ModulrApiAuth class.

---

### NodeJS Sample

#### [signature.js](samples/nodejs/signature.js)

This class can generate required headers for a given value of API key and secret. It generates the following headers:

- Authorization
- Date
- x-mod-nonce

To use this class, instantiate it using your API key and secret.

```javascript
    const signatureHelper = new signature(API_KEY,API_SECRET);
```

Then use the calculate() to get generated headers.

```javascript
    var result = signatureHelper.calculate();
    var headers = result.getHTTPHeaders();    
```
OR with a specific nonce and date

```javascript
    var result = signatureHelper.calculate('28154b2-9c62b93cc22a-24c9e2-5536d7d','Mon, 25 Jul 2016 16:36:07 GMT');
    var headers = result.getHTTPHeaders();
```


To run the sample, make sure that you 

- Have NodeJS installed
- Updated the API_KEY and API_SECRET in  [index.js](samples/nodejs/index.js) to your API key and secret

Then call from your shell:

```bash
npm i
npm run start
```

## Authentication process

An in depth documentation of the whole authentication process can be found at [Authentication](https://modulr.readme.io/docs/authentication)