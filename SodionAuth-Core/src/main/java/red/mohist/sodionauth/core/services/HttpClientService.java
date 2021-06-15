/*
 * Copyright 2021 Mohist-Community
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package red.mohist.sodionauth.core.services;

import com.google.common.eventbus.Subscribe;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import red.mohist.sodionauth.core.events.BootEvent;
import red.mohist.sodionauth.core.events.DownEvent;
import red.mohist.sodionauth.core.utils.Helper;

import java.io.IOException;

public class HttpClientService {
    public CloseableHttpClient httpClient;

    @Subscribe
    public void onBoot(BootEvent event) {
        Helper.getLogger().info("Initializing httpClient service...");
        httpClient = HttpClientBuilder.create()
                .disableCookieManagement()
                .disableAuthCaching()
                .disableAutomaticRetries()
                .setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) " +
                        "SodionAuthWeb/1.0 Safari/537.36")
                .build();
    }

    @Subscribe
    public void onDown(DownEvent event) {
        try {
            httpClient.close();
        } catch (IOException ignored) {
        }
    }

    public CloseableHttpResponse execute(HttpUriRequest request) throws IOException {
        return httpClient.execute(request);
    }

    public JsonObject executeAsJson(HttpUriRequest request) throws IOException {
        CloseableHttpResponse response = execute(request);
        if (response == null) {
            throw new IOException("No response");
        }
        switch (response.getStatusLine().getStatusCode()) {
            case 200:
            case 400:
            case 404:
                break;
            default:
                throw new IOException("Server returns status code " + response.getStatusLine().getStatusCode());
        }
        HttpEntity entity = response.getEntity();
        if (entity == null) {
            throw new IOException("Server returns no entity");
        }
        String content = EntityUtils.toString(entity);
        if (content == null || content.equals("")) {
            throw new IOException("Server returns empty entity");
        }
        response.close();
        JsonObject result;
        result = new Gson().fromJson(content, JsonObject.class);
        if (result == null) {
            throw new JsonSyntaxException("Server returned: " + content);
        }
        return result;
    }

}
