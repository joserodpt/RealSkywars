package josegamerpt.realskywars.commands;

import josegamerpt.realskywars.RealSkywars;
import josegamerpt.realskywars.managers.LanguageManager;
import josegamerpt.realskywars.player.RSWPlayer;
import me.mattstudios.mf.annotations.Alias;
import me.mattstudios.mf.annotations.Command;
import me.mattstudios.mf.annotations.Default;
import me.mattstudios.mf.annotations.Permission;
import me.mattstudios.mf.base.CommandBase;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@Command("leave")
@Alias({"sair", "ragequit"})
public class SairCMD extends CommandBase {

    public RealSkywars rs;

    public SairCMD(RealSkywars rs) {
        this.rs = rs;
    }

    @Default
    @Permission("RealSkywars.leave")
    public void defaultCommand(final CommandSender commandSender) {
        if (commandSender instanceof Player) {
            RSWPlayer p = RealSkywars.getPlayerManager().getPlayer((Player) commandSender);
            if (p.isInMatch()) {
                p.getMatch().removePlayer(p);
            } else {
                p.sendMessage(RealSkywars.getLanguageManager().getString(p, LanguageManager.TS.NO_MATCH, true));
            }
        } else {
            String onlyPlayer = "[RealSkywars] Only players can run this command.";
            commandSender.sendMessage(onlyPlayer);
        }
    }
}