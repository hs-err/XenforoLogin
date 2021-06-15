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

import me.gosimple.nbvcxz.Nbvcxz;
import me.gosimple.nbvcxz.resources.*;
import me.gosimple.nbvcxz.scoring.Result;
import org.knownspace.minitask.ITask;
import red.mohist.sodionauth.core.modules.AbstractPlayer;
import red.mohist.sodionauth.core.utils.Config;
import red.mohist.sodionauth.core.utils.Helper;

import java.util.List;
import java.util.Locale;

public class PasswordStrengthService {
    public PasswordStrengthService() {
        Helper.getLogger().info("Initializing passwordStrength service...");
    }

    public Result verify(AbstractPlayer player, String email, String password) {
        DictionaryBuilder dictionaryBuilder = new DictionaryBuilder()
                .setDictionaryName("userInfo")
                .setExclusion(true);
        if (player != null && player.getName() != null) {
            dictionaryBuilder.addWord(player.getName(), 0);
        }
        if (email != null) {
            dictionaryBuilder.addWord(email, 0);
        }
        List<Dictionary> dictionaryList = ConfigurationBuilder.getDefaultDictionaries();
        dictionaryList.add(dictionaryBuilder.createDictionary());

        Configuration configuration = new ConfigurationBuilder()
                .setMinimumEntropy(Config.password.minimumEntropy.doubleValue())
                .setLocale(Locale.forLanguageTag(Config.defaultLang))
                .setDictionaries(dictionaryList)
                .createConfiguration();
        Nbvcxz nbvcxz = new Nbvcxz(configuration);
        return nbvcxz.estimate(password);
    }

    public void sendTip(AbstractPlayer player, Result result) {
        if (!result.isMinimumEntropyMet()) {
            Feedback feedback = result.getFeedback();
            if (feedback != null) {
                if (feedback.getWarningKey() != null) {
                    player.sendMessage(player.getLang().passwordWarnPrefix + feedback.getWarning());
                }
                feedback.getSuggestion().forEach((tip) -> {
                    player.sendMessage(player.getLang().passwordTipPrefix + tip);
                });
            }
        }
    }

    public ITask<Result> verifyAsync(AbstractPlayer player, String email, String password) {
        return Service.threadPool.startup.startTask(() -> {
            try {
                return verify(player, email, password);
            } catch (Exception e) {
                Helper.getLogger().warn("Can't check password for " + player.getName(), e);
                player.sendMessage(player.getLang().errors.server);
                return null;
            }
        });
    }
}
