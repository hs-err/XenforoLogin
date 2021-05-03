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

import red.mohist.sodionauth.core.SodionAuthCore;
import red.mohist.sodionauth.core.events.BootEvent;
import red.mohist.sodionauth.core.events.DownEvent;

public class Service {
    public static ConfigureService configure = new ConfigureService();

    public static ThreadPoolService threadPool = new ThreadPoolService();
    public static EventBusService eventBus = new EventBusService();

    public static final HttpClientService httpClient = new HttpClientService();
    public static DatabaseService database = new DatabaseService();

    public static UserService user = new UserService();
    public static AuthService auth = new AuthService();
    public static SessionService session = new SessionService();
    public static ProxyLoginService proxyLogin = new ProxyLoginService();
    public static PasswordStrengthService passwordStrength = new PasswordStrengthService();
    public static RegisterService register = new RegisterService();
    public static LoginService login = new LoginService();
    public static UnRegisterService unRegister = new UnRegisterService();

    public Service() {
        eventBus.register(configure)
                .register(SodionAuthCore.instance)
                .register(threadPool)
                .register(eventBus)
                .register(httpClient)
                .register(database)
                .register(auth)
                .register(session)
                .register(threadPool)
                .register(proxyLogin)
                .register(passwordStrength)
                .register(register)
                .register(login)
                .register(unRegister);
        if (!new BootEvent().post()) {
            new DownEvent().post();
            SodionAuthCore.instance.loadFail();
        }
    }
}
