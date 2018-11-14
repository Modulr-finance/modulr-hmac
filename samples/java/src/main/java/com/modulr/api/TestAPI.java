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

// Small demo program to perform a get on an account on the Modulr sanbox
// replace the api key value with your the api-key you were provided with
// replace the secret with the secret key you were provided with
// this can be run from your favorite IDE.
// you might need to change the account number
public class TestAPI {
    private static final String MODULR_URL = "https://api-sandbox.modulrfinance.com/api-sandbox/accounts/A120940C";
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
