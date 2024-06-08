package joserodpt.realskywars.api.config;

import joserodpt.realskywars.api.player.RSWPlayer;
import joserodpt.realskywars.api.utils.Text;
import org.bukkit.command.CommandSender;

public enum TranslatableLine {
    BOSSBAR_ARENA_RUNTIME("Strings.Boss-Bar.Run-Time"),
    BOSSBAR_ARENA_STARTING("Strings.Boss-Bar.Starting"),
    BOSSBAR_ARENA_END("Strings.Boss-Bar.End"),
    BOSSBAR_ARENA_WAIT("Strings.Boss-Bar.Wait"),
    SEARCH_NOTFOUND_NAME("Strings.Search.Not-Found"),
    SHOP_BOUGHT("Strings.Shop.Already-Bought"),
    SHOP_BUY("Strings.Shop.Buy"),
    ADMIN_SHUTDOWN("Strings.Admin-Shutdown"),
    KIT_PRICE("Strings.Kit.Price"),
    KIT_BUY("Strings.Kit.Buy"),
    KIT_SELECT("Strings.Kit.Select"),
    KIT_CONTAINS("Strings.Kit.Contains"),
    KIT_ITEM("Strings.Kit.Items"),

    BUNGEECORD_FULL("Strings.BungeeCord.Full"),
    BUNGEECORD_NO_AVAILABLE_MAPS("Strings.BungeeCord.No-Available-Maps"),
    BUNGEECORD_KICK_MESSAGE("Strings.BungeeCord.Kick-Message"),
    BUNGEECORD_RESETTING_MESSAGE("Strings.BungeeCord.Resetting"),

    BUTTONS_NEXT_TITLE("Strings.Menus.Next-Button.Title"),
    BUTTONS_NEXT_DESC("Strings.Menus.Next-Button.Description"),
    BUTTONS_BACK_TITLE("Strings.Menus.Back-Button.Title"),
    BUTTONS_BACK_DESC("Strings.Menus.Back-Button.Description"),
    BUTTONS_FILTER_TITLE("Strings.Menus.Filter-Button.Title"),
    BUTTONS_FILTER_DESC("Strings.Menus.Filter-Button.Description"),
    BUTTONS_MENU_TITLE("Strings.Menus.Main-Menu-Button.Title"),
    BUTTONS_MENU_DESC("Strings.Menus.Main-Menu-Button.Description"),
    BOSSBAR_ARENA_DEATHMATCH("Strings.Boss-Bar.DeathMatch");

    private final String configPath;

    TranslatableLine(String configPath) {
        this.configPath = configPath;
    }

    public String get() {
        return Text.color(RSWLanguagesConfig.file().getString(this.configPath));
    }

    public void send(CommandSender sender) {
        Text.send(sender, this.get());
    }

    public void send(RSWPlayer sender) {
        if (sender.getPlayer() != null)
            send(sender.getPlayer());
    }
}
