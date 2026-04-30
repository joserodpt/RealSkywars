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

import eu.cloudnetservice.driver.inject.InjectionLayer;
import eu.cloudnetservice.driver.registry.ServiceRegistry;
import eu.cloudnetservice.modules.bridge.player.PlayerManager;
import eu.cloudnetservice.modules.bridge.player.executor.ServerSelectorType;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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
            if (sendViaRustyConnector(player, name)) {
                return;
            }
            return;
        }

        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Connect");
        out.writeUTF(name);
        player.sendPluginMessage(jp, "BungeeCord", out.toByteArray());
    }

    private static boolean sendViaRustyConnector(Player player, String name) {
        try {
            Object kernel = resolveRustyConnectorKernel();
            if (kernel == null) {
                return false;
            }

            Class<?> flagClass = Class.forName("group.aelysium.rustyconnector.common.magic_link.MagicLinkCore$Packets$SendPlayer$Flag");
            @SuppressWarnings({"unchecked", "rawtypes"})
            Set<?> flags = Set.of(Enum.valueOf((Class<Enum>) flagClass, "FAMILY"));

            Method sendId = kernel.getClass().getMethod("sendID", String.class, String.class, Set.class);
            sendId.invoke(kernel, player.getUniqueId().toString(), name, flags);
            return true;
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException | IllegalArgumentException e) {
            return false;
        }
    }

    private static Object resolveRustyConnectorKernel() {
        try {
            Class<?> nestedClass = Class.forName("group.aelysium.rustyconnector.RC$S");
            Method kernelMethod = nestedClass.getMethod("Kernel");
            return kernelMethod.invoke(null);
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException ignored) {
            // Fall through to the alternate layout below.
        }

        try {
            Class<?> rcClass = Class.forName("group.aelysium.rustyconnector.RC");
            Object sInstance = rcClass.getField("S").get(null);
            Method kernelMethod = sInstance.getClass().getMethod("Kernel");
            return kernelMethod.invoke(sInstance);
        } catch (ClassNotFoundException | NoSuchFieldException | NoSuchMethodException | IllegalAccessException | InvocationTargetException ignored) {
            return null;
        }
    }
}
