package joserodpt.realskywars.api.utils;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class BungeecordUtils {
    public static void connect(String name, Player player, JavaPlugin jp) {
        if (player == null) {
            return;
        }
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Connect");
        out.writeUTF(name);
        player.sendPluginMessage(jp, "BungeeCord", out.toByteArray());
    }
}
