package josegamerpt.realskywars.commands;

import josegamerpt.realskywars.RealSkywars;
import josegamerpt.realskywars.chests.ChestManager;
import josegamerpt.realskywars.configuration.Config;
import josegamerpt.realskywars.game.modes.SWGameMode;
import josegamerpt.realskywars.gui.*;
import josegamerpt.realskywars.kits.Kit;
import josegamerpt.realskywars.managers.CurrencyManager;
import josegamerpt.realskywars.managers.LanguageManager;
import josegamerpt.realskywars.managers.ShopManager;
import josegamerpt.realskywars.misc.Selections;
import josegamerpt.realskywars.player.RSWPlayer;
import josegamerpt.realskywars.utils.Itens;
import josegamerpt.realskywars.utils.Text;
import me.mattstudios.mf.annotations.*;
import me.mattstudios.mf.base.CommandBase;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Command("leave")
@Alias({"sair", "ragequit"})
public class SairCMD extends CommandBase {

    public RealSkywars rs;
    private String onlyPlayer = "[RealSkywars] Only players can run this command.";
    public enum KIT { create, delete, give }

    public SairCMD(RealSkywars rs) {
        this.rs = rs;
    }

    @Default
    public void defaultCommand(final CommandSender commandSender) {
        if (commandSender instanceof Player) {
            RSWPlayer p = RealSkywars.getPlayerManager().getPlayer((Player) commandSender);
            if (p.isInMatch()) {
                p.getMatch().removePlayer(p);
            } else {
                p.sendMessage(RealSkywars.getLanguageManager().getString(p, LanguageManager.TS.NO_MATCH, true));
            }
        } else {
            commandSender.sendMessage(onlyPlayer);
        }    }

}