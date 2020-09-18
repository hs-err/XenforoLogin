/*
 * Copyright 2020 Mohist-Community
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

package red.mohist.sodionauth.yggdrasilserver.modules;

import com.google.gson.Gson;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.CharsetUtil;

public class ErrorResponse extends DefaultFullHttpResponse {
    public static final ErrorResponse forbiddenOperation = new ErrorResponse(
            new Response("ForbiddenOperationException","Invalid credentials. Invalid username or password."));
    public static final ErrorResponse illegalArgument = new ErrorResponse(
            new Response("IllegalArgumentException","Access token already has a profile assigned.es"));

    private ErrorResponse(Object content){
        super(HttpVersion.HTTP_1_1,
                HttpResponseStatus.FORBIDDEN,
                Unpooled.copiedBuffer(new Gson().toJson(content), CharsetUtil.UTF_8));
    }
    static class Response{
        String error;
        String errorMessage;
        Response(String error,String errorMessage){
            this.error=error;
            this.errorMessage=errorMessage;
        }
    }
}
