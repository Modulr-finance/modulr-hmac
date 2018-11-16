package com.modulr.api;

import java.io.IOException;
import java.security.SignatureException;
import java.util.Map;
import java.util.UUID;


import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

// Small demo program to perform a API get call on an account on the Modulr sanbox.
// Replace the api key value with your api-key.
// Replace the secret with your secret key.
// Replace the account id with your account id.
// This can be run from your favorite IDE.
public class TestAPI {

    private static final String accountId = "<YOUR ACCOUNT ID>"; // Will be of the format A120940C
    private static final String MODULR_URL = "https://api-sandbox.modulrfinance.com/api-sandbox/accounts/" + accountId;
    private static final String api_key = "<YOUR KEY>";
    private static final String secret = "<YOUR SECRET>";

    public static void main(String[] args) throws SignatureException {
        ModulrApiAuth auth = new ModulrApiAuth(api_key, secret);
        String nonce = UUID.randomUUID().toString();
        Map<String, String> headers = auth.generateApiAuthHeaders(nonce);

        CloseableHttpClient client = HttpClients.createDefault();
        RequestBuilder builder = RequestBuilder.get()
            .setUri(MODULR_URL);
        headers.keySet().forEach(k -> {
            builder.addHeader(k, headers.get(k));
        });
        headers.keySet().forEach(k-> {
            System.out.println(k + " " + headers.get(k));
        });
        HttpUriRequest request = builder.build();
        try {
            CloseableHttpResponse response = client.execute(request);
            System.out.println(response.getStatusLine());
            System.out.println(EntityUtils.toString(response.getEntity()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
