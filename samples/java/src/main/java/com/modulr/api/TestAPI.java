package com.modulr.api;

import java.io.IOException;
import java.security.SignatureException;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.routing.RoutingSupport;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.support.ClassicRequestBuilder;


// Small demo program to perform an API get call on an account on the Modulr sandbox.
// Replace the api key value with your api-key.
// Replace the secret with your secret key.
// Replace the account id with your account id.
// This can be run from your favorite IDE.
public class TestAPI {

    private static final Logger logger = Logger.getLogger(TestAPI.class.getName());

    private static final String ACCOUNT_ID = "<YOUR ACCOUNT ID>"; // Will be of the format A120940C
    private static final String MODULR_URL = "https://api-sandbox.modulrfinance.com/api-sandbox/accounts/" + ACCOUNT_ID;
    private static final String API_KEY_ID = "<YOUR KEY>";
    private static final String API_KEY_SECRET = "<YOUR SECRET>";

    public static void main(String[] args) throws SignatureException, IOException, HttpException {
        ModulrApiAuth auth = new ModulrApiAuth(API_KEY_ID, API_KEY_SECRET);
        String nonce = UUID.randomUUID().toString();
        Map<String, String> headers = auth.generateApiAuthHeaders(nonce);

        try (CloseableHttpClient client = HttpClients.createDefault()) {
            ClassicRequestBuilder builder = ClassicRequestBuilder.get().setUri(MODULR_URL);
            headers.keySet().forEach(k -> builder.addHeader(k, headers.get(k)));
            headers.keySet().forEach(k -> logger.info(k + " " + headers.get(k)));
            ClassicHttpRequest request = builder.build();
            try (ClassicHttpResponse response = client.executeOpen(RoutingSupport.determineHost(request), request, null)) {
                logger.info(() -> response.getVersion().toString() + " " + response.getCode() + " " + response.getReasonPhrase());
                String responseBody = EntityUtils.toString(response.getEntity());
                logger.info(responseBody);
            }
        }
    }
}
