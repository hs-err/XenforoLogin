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

import java.util.ArrayList;

public class LoginRespone {
    public String accessToken;
    public String clientToken;
    public ArrayList<Profile> availableProfiles;
    public Profile selectedProfile;
    public User user;

    public LoginRespone() {
        availableProfiles = new ArrayList<>();
    }

    public LoginRespone addProfiles(Profile profile) {
        availableProfiles.add(profile);
        return this;
    }

    public LoginRespone selectedProfile(Profile profile) {
        selectedProfile = profile;
        return this;
    }

    public LoginRespone setUser(User user) {
        this.user = user;
        return this;
    }

    public LoginRespone setAccessToken(String accessToken) {
        this.accessToken = accessToken;
        return this;
    }

    public LoginRespone setClientToken(String clientToken) {
        this.clientToken = clientToken;
        return this;
    }
}
