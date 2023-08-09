package josegamerpt.realskywars.commands;

/*
 *  _____            _  _____ _
 * |  __ \          | |/ ____| |
 * | |__) |___  __ _| | (___ | | ___   ___      ____ _ _ __ ___
 * |  _  // _ \/ _` | |\___ \| |/ / | | \ \ /\ / / _` | '__/ __|
 * | | \ \  __/ (_| | |____) |   <| |_| |\ V  V / (_| | |  \__ \
 * |_|  \_\___|\__,_|_|_____/|_|\_\\__, | \_/\_/ \__,_|_|  |___/
 *                                 __/ |
 *                                |___/
 *
 * Licensed under the MIT License
 * @author José Rodrigues
 * @link https://github.com/joserodpt/RealSkywars
 *
 */

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
            RSWPlayer p = rs.getPlayerManager().getPlayer((Player) commandSender);
            if (p.isInMatch()) {
                p.getMatch().removePlayer(p);
            } else {
                p.sendMessage(rs.getLanguageManager().getString(p, LanguageManager.TS.NO_MATCH, true));
            }
        } else {
            commandSender.sendMessage("[RealSkywars] Only players can run this command.");
        }
    }
}