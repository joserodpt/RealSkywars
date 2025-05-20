package joserodpt.realskywars.api.utils;

/*
 *   _____            _  _____ _
 *  |  __ \          | |/ ____| |
 *  | |__) |___  __ _| | (___ | | ___   ___      ____ _ _ __ ___
 *  |  _  // _ \/ _` | |\___ \| |/ / | | \ \ /\ / / _` | '__/ __|
 *  | | \ \  __/ (_| | |____) |   <| |_| |\ V  V / (_| | |  \__ \
 *  |_|  \_\___|\__,_|_|_____/|_|\_\\__, | \_/\_/ \__,_|_|  |___/
 *                                   __/ |
 *                                  |___/
 *
 * Licensed under the MIT License
 * @author José Rodrigues © 2019-2025
 * @link https://github.com/joserodpt/RealSkywars
 */

import group.aelysium.rustyconnector.RC;
import group.aelysium.rustyconnector.common.magic_link.MagicLinkCore;
import group.aelysium.rustyconnector.server.ServerKernel;

import eu.cloudnetservice.driver.inject.InjectionLayer;
import eu.cloudnetservice.driver.registry.ServiceRegistry;
import eu.cloudnetservice.modules.bridge.player.PlayerManager;
import eu.cloudnetservice.modules.bridge.player.executor.ServerSelectorType;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Set;

public class BungeecordUtils {
    public static void connect(String name, Player player, JavaPlugin jp) {
        if (player == null) {
            return;
        }
        if (Bukkit.getPluginManager().isPluginEnabled("CloudNet-Bridge")) {
            ServiceRegistry registry = InjectionLayer.ext().instance(ServiceRegistry.class);
            PlayerManager playerManager = registry.firstProvider(PlayerManager.class);
            playerManager.playerExecutor(player.getUniqueId()).connectToTask(name, ServerSelectorType.LOWEST_PLAYERS);
            return;
        }

        if (Bukkit.getPluginManager().isPluginEnabled("rustyconnector-paper")) {
            ServerKernel kernel = RC.S.Kernel();
            kernel.sendID(
                    player.getUniqueId().toString(),
                    name,
                    Set.of(
                            MagicLinkCore.Packets.SendPlayer.Flag.FAMILY //
                    ));
            return;
        }

        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Connect");
        out.writeUTF(name);
        player.sendPluginMessage(jp, "BungeeCord", out.toByteArray());
    }
}
