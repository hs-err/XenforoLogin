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

package red.mohist.sodionauth.core.protection.implementations.antiproxies.implementations;

import red.mohist.sodionauth.core.protection.implementations.antiproxies.ProxySystem;
import red.mohist.sodionauth.core.services.Service;
import red.mohist.sodionauth.libs.http.HttpEntity;
import red.mohist.sodionauth.libs.http.client.ResponseHandler;
import red.mohist.sodionauth.libs.http.client.methods.CloseableHttpResponse;
import red.mohist.sodionauth.libs.http.client.methods.HttpGet;
import red.mohist.sodionauth.libs.http.util.EntityUtils;

import java.io.IOException;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QiYun implements ProxySystem {
    private final CopyOnWriteArrayList<String> proxyList;
    private final Pattern pattern = Pattern.compile("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}");

    public QiYun() {
        proxyList = new CopyOnWriteArrayList<>();
    }

    public String getName() {
        return "89ip";
    }

    public void refreshProxies() throws IOException {
        ResponseHandler<String> responseHandler = response -> {
            int status = response.getStatusLine().getStatusCode();
            if (status == 200) {
                HttpEntity entity = response.getEntity();
                return entity != null ? EntityUtils.toString(entity) : null;
            }
            return null;
        };

        HttpGet request = new HttpGet("http://www.89ip.cn/tqdl.html?api=1&num=9999");
        request.addHeader("accept", "*/*");
        CloseableHttpResponse response = Service.httpClient.execute(request);
        String result = responseHandler.handleResponse(response);

        if (result == null) {
            throw new IOException("Result is null");
        }
        Matcher matcher = pattern.matcher(result);
        while (matcher.find()) {
            String ip = matcher.group();
            if (!proxyList.contains(ip)) {
                proxyList.add(ip);
            }
        }
    }

    public boolean isProxy(String ip) {
        return proxyList.contains(ip);
    }
}
