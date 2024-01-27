package joserodpt.realskywars.plugin.commands;

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
 * @author José Rodrigues
 * @link https://github.com/joserodpt/RealSkywars
 */

import joserodpt.realskywars.api.RealSkywarsAPI;
import joserodpt.realskywars.api.managers.LanguageManagerAPI;
import joserodpt.realskywars.api.player.RSWPlayer;
import joserodpt.realskywars.api.utils.Text;
import me.mattstudios.mf.annotations.*;
import me.mattstudios.mf.base.CommandBase;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

@Command("party")
@Alias({"p", "festa", "f", "swp", "rswparty"})
public class PartyCMD extends CommandBase {

    public RealSkywarsAPI rs;
    private final String onlyPlayer = "[RealSkywars] Only players can run this command.";

    public PartyCMD(RealSkywarsAPI rs) {
        this.rs = rs;
    }

    @Default
    public void defaultCommand(final CommandSender commandSender) {
        if (commandSender instanceof Player) {
            Text.sendList(commandSender, Arrays.asList(rs.getLanguageManagerAPI().getPrefix(), " &3/party create", " &3/party disband", " &3/party invite <player>", " &3/party accept <player>", " &3/party kick <player>", " &3/party leave"));
        } else {
            commandSender.sendMessage(onlyPlayer);
        }
    }

    @SubCommand("create")
    @Permission("rsw.party.owner")
    public void createcmd(final CommandSender commandSender) {
        if (commandSender instanceof Player) {
            RSWPlayer p = rs.getPlayerManagerAPI().getPlayer(((Player) commandSender));
            if (!p.hasParty()) {
                p.createParty();
                p.sendMessage(rs.getLanguageManagerAPI().getString(p, LanguageManagerAPI.TS.PARTY_CREATED, true));
            } else {
                p.sendMessage(rs.getLanguageManagerAPI().getString(p, LanguageManagerAPI.TS.PARTY_ALREADYCREATED, true));
            }
        } else {
            commandSender.sendMessage(onlyPlayer);
        }
    }

    @SubCommand("disband")
    @Permission("rsw.party.owner")
    public void disbandcmd(final CommandSender commandSender) {
        if (commandSender instanceof Player) {
            RSWPlayer p = rs.getPlayerManagerAPI().getPlayer(((Player) commandSender));
            if (p.hasParty()) {
                if (p.getParty().isOwner(p)) {
                    p.disbandParty();
                } else {
                    p.sendMessage(rs.getLanguageManagerAPI().getString(p, LanguageManagerAPI.TS.PARTY_NOT_OWNER, true));
                }
            } else {
                p.sendMessage(rs.getLanguageManagerAPI().getString(p, LanguageManagerAPI.TS.PARTY_NOTINPARTY, true));
            }
        } else {
            commandSender.sendMessage(onlyPlayer);
        }
    }

    @SubCommand("invite")
    @Permission("rsw.party.invite")
    public void invitecmd(final CommandSender commandSender, final Player player) {
        if (commandSender instanceof Player) {
            RSWPlayer p = rs.getPlayerManagerAPI().getPlayer(((Player) commandSender));
            if (player != null) {
                rs.getPartiesManagerAPI().sendInvite(p, rs.getPlayerManagerAPI().getPlayer(player));
            } else {
                p.sendMessage(rs.getLanguageManagerAPI().getString(p, LanguageManagerAPI.TS.NO_PLAYER_FOUND, true));
            }
        } else {
            commandSender.sendMessage(onlyPlayer);
        }
    }

    @SubCommand("accept")
    @Permission("rsw.party.accept")
    public void acceptcmd(final CommandSender commandSender) {
        if (commandSender instanceof Player) {
            RSWPlayer p = rs.getPlayerManagerAPI().getPlayer(((Player) commandSender));
            if (!p.hasParty()) {
                if (rs.getPartiesManagerAPI().hasInvite(p)) {
                    rs.getPartiesManagerAPI().acceptInvite(p);
                } else {
                    p.sendMessage(rs.getLanguageManagerAPI().getString(p, LanguageManagerAPI.TS.PARTY_INVITENOTFOUND, true));
                }
            } else {
                p.sendMessage(rs.getLanguageManagerAPI().getString(p, LanguageManagerAPI.TS.PARTY_ALREADYIN, true));
            }
        } else {
            commandSender.sendMessage(onlyPlayer);
        }
    }

    @SubCommand("kick")
    @Permission("rsw.party.owner")
    public void kickcmd(final CommandSender commandSender, final Player player) {
        if (commandSender instanceof Player) {
            RSWPlayer p = rs.getPlayerManagerAPI().getPlayer(((Player) commandSender));
            if (p.hasParty()) {
                if (p.getParty().isOwner(p)) {
                    if (player != null && p.getPlayer() != player) {
                        p.getParty().kick(rs.getPlayerManagerAPI().getPlayer(player));
                    } else {
                        p.sendMessage(rs.getLanguageManagerAPI().getString(p, LanguageManagerAPI.TS.NO_PLAYER_FOUND, true));
                    }
                } else {
                    p.sendMessage(rs.getLanguageManagerAPI().getString(p, LanguageManagerAPI.TS.PARTY_NOT_OWNER, true));
                }
            } else {
                p.sendMessage(rs.getLanguageManagerAPI().getString(p, LanguageManagerAPI.TS.PARTY_NOTINPARTY, true));
            }
        } else {
            commandSender.sendMessage(onlyPlayer);
        }
    }

    @SubCommand("leave")
    @Permission("rsw.party.leave")
    public void leavecmd(final CommandSender commandSender) {
        if (commandSender instanceof Player) {
            RSWPlayer p = rs.getPlayerManagerAPI().getPlayer(((Player) commandSender));
            if (p.hasParty()) {
                if (p.getParty().isOwner(p)) {
                    p.disbandParty();
                } else {
                    p.getParty().playerLeave(p);
                }
            } else {
                p.sendMessage(rs.getLanguageManagerAPI().getString(p, LanguageManagerAPI.TS.PARTY_NOTINPARTY, true));
            }
        } else {
            commandSender.sendMessage(onlyPlayer);
        }
    }
}