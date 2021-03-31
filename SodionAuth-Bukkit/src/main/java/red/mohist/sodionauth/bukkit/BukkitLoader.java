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

package red.mohist.sodionauth.bukkit;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.reflections.Reflections;
import red.mohist.sodionauth.bukkit.implementation.BukkitPlayer;
import red.mohist.sodionauth.bukkit.interfaces.BukkitAPIListener;
import red.mohist.sodionauth.core.SodionAuthCore;
import red.mohist.sodionauth.core.events.DownEvent;
import red.mohist.sodionauth.core.events.TickEvent;
import red.mohist.sodionauth.core.interfaces.LogProvider;
import red.mohist.sodionauth.core.interfaces.PlatformAdapter;
import red.mohist.sodionauth.core.modules.AbstractPlayer;
import red.mohist.sodionauth.core.modules.FoodInfo;
import red.mohist.sodionauth.core.modules.LocationInfo;
import red.mohist.sodionauth.core.utils.Config;
import red.mohist.sodionauth.core.utils.Helper;

import java.io.IOException;
import java.util.*;

public class BukkitLoader extends JavaPlugin implements PlatformAdapter {
    public static BukkitLoader instance;
    public SodionAuthCore sodionAuthCore;

    @Override
    public void onEnable() {
        try {
            new Helper(getDataFolder().toString(), new LogProvider() {
                @Override
                public void info(String info) {
                    getLogger().info(info);
                }

                @Override
                public void info(String info, Exception exception) {
                    getLogger().info(info);
                    getLogger().info(exception.toString());
                }

                @Override
                public void warn(String info) {
                    getLogger().warning(info);
                }

                @Override
                public void warn(String info, Exception exception) {
                    getLogger().warning(info);
                    getLogger().warning(exception.toString());
                }
            });

            instance = this;
            Helper.getLogger().info("Hello, SodionAuth!");

            sodionAuthCore = new SodionAuthCore(this);

            hookProtocolLib();

            registerListeners();

            Bukkit.getScheduler().runTaskTimer(this,()->{new TickEvent().post();},0,1);
        } catch (IOException e) {
            e.printStackTrace();
            getLogger().warning("SodionAuth load fail.");
            getServer().shutdown();
        }
    }

    @Override
    public void onDisable() {
        new DownEvent().post();
    }

    @Override
    public void shutdown() {
        getServer().shutdown();
    }

    private void hookProtocolLib() {
    }

    private void registerListeners() {
        {
            int unavailableCount = 0;
            Set<Class<? extends BukkitAPIListener>> classes = new Reflections("red.mohist.sodionauth.bukkit.listeners")
                    .getSubTypesOf(BukkitAPIListener.class);
            for (Class<? extends BukkitAPIListener> clazz : classes) {
                BukkitAPIListener listener;
                try {
                    listener = clazz.getDeclaredConstructor().newInstance();
                } catch (Exception e) {
                    Helper.getLogger().warn(clazz.getName() + " is not available.");
                    unavailableCount++;
                    continue;
                }
                if (!listener.isAvailable()) {
                    Helper.getLogger().warn(clazz.getName() + " is not available.");
                    unavailableCount++;
                    continue;
                }
                org.bukkit.Bukkit.getPluginManager().registerEvents(listener, this);
            }
            if (unavailableCount > 0) {
                Helper.getLogger().warn("Warning: Some features in this plugin is not available on this version of bukkit");
                Helper.getLogger().warn("If your encountered errors, do NOT report to SodionAuth.");
                Helper.getLogger().warn("Error count: " + unavailableCount);
            }
        }
    }

    @Override
    public Collection<AbstractPlayer> getAllPlayer() {
        Collection<AbstractPlayer> allPlayers = new Vector<>();
        for (Player onlinePlayer : getServer().getOnlinePlayers()) {
            allPlayers.add(new BukkitPlayer(onlinePlayer));
        }
        return allPlayers;
    }

    @Override
    public LocationInfo getSpawn(String world) {
        Location spawn_location = Objects.requireNonNull(Bukkit.getWorld(getDefaultWorld())).getSpawnLocation();
        return new LocationInfo(world,
                spawn_location.getX(),
                spawn_location.getY(),
                spawn_location.getZ(),
                spawn_location.getYaw(),
                spawn_location.getPitch());
    }

    @Override
    public String getDefaultWorld() {
        return Bukkit.getWorlds().get(0).getName();
    }

    @Override
    public void onLogin(AbstractPlayer player) {
        Objects.requireNonNull(Bukkit.getPlayer(player.getUniqueId())).updateInventory();
    }

    @Override
    public void sendBlankInventoryPacket(AbstractPlayer player) {
        Player p = Bukkit.getPlayer(player.getUniqueId());
        if (p != null) {
            if (p.isDead()) {
                p.spigot().respawn();
            }
            player.setEffects(new LinkedList<>());
            player.setHealth(20);
            player.setRemainingAir(0);
            player.setMaxHealth(20);
            player.setFood(new FoodInfo());
            if (Config.security.getSpectatorLogin()) {
                player.setGameMode(3);
            } else {
                player.setGameMode(Config.security.getDefaultGamemode());
            }
        }
    }


    public AbstractPlayer player2info(Player player) {
        return new BukkitPlayer(player);
    }
}
