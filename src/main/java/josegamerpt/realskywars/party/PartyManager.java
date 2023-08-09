package josegamerpt.realskywars.party;

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
import josegamerpt.realskywars.game.modes.SWGameMode;
import josegamerpt.realskywars.managers.LanguageManager;
import josegamerpt.realskywars.player.RSWPlayer;
import org.bukkit.Bukkit;

import java.util.HashMap;

public class PartyManager {
    private RealSkywars rs;
    public PartyManager(RealSkywars rs) {
        this.rs = rs;
    }

    public HashMap<RSWPlayer, RSWPlayer> invites = new HashMap<>();

    public Boolean hasInvite(RSWPlayer p) {
        return invites.containsKey(p);
    }

    public RSWPlayer getInvite(RSWPlayer p) {
        return invites.get(p);
    }

    public void sendInvite(RSWPlayer emissor, RSWPlayer recetor) {
        if (emissor != recetor) {
            if (emissor.hasParty()) {
                if (emissor.getParty().isOwner(emissor) && !emissor.getParty().getMembers().contains(recetor)) {
                    invites.put(recetor, emissor);
                    emissor.sendMessage(rs.getLanguageManager().getString(emissor, LanguageManager.TS.PARTY_INVITE_SENT, true).replace("%player%", recetor.getDisplayName()));
                    recetor.sendMessage(rs.getLanguageManager().getString(recetor, LanguageManager.TS.PARTY_INVITE_RECIEVED, true).replace("%player%", emissor.getDisplayName()));
                } else {
                    emissor.sendMessage(rs.getLanguageManager().getString(emissor, LanguageManager.TS.PARTY_NOT_OWNER, true));
                }
            } else {
                emissor.sendMessage(rs.getLanguageManager().getString(emissor, LanguageManager.TS.PARTY_NOTINPARTY, true));
            }
        } else {
            emissor.sendMessage(rs.getLanguageManager().getString(emissor, LanguageManager.TS.PARTY_CANTINVITEYOURSELF, true));
        }
    }

    public void acceptInvite(RSWPlayer p) {
        RSWPlayer inviteOwner = this.getInvite(p);
        p.sendMessage(rs.getLanguageManager().getString(p, LanguageManager.TS.PARTY_ACCEPTEDINVITE, true).replace("%player%", inviteOwner.getDisplayName()));
        inviteOwner.getParty().playerJoin(p);
    }

    public boolean checkForParties(RSWPlayer p, SWGameMode swgm) {
        int current, max, toAdd;

        boolean result;
        if (p.hasParty()) {
            if (p.getParty().isOwner(p)) {
                current = swgm.getPlayerCount();
                max = swgm.getMaxPlayers();

                toAdd = current + p.getParty().getMembers().size() + 1; //1 = owner of the party

                if (toAdd > max) {
                    p.sendMessage(rs.getLanguageManager().getString(p, LanguageManager.TS.PARTY_INSUFICIENT_ROOMSPACE, true));
                    result = false;
                } else {
                    p.getParty().setAllowJoin(true);
                    p.getParty().getMembers().forEach(swgm::addPlayer);
                    result = true;

                    Bukkit.getScheduler().scheduleSyncDelayedTask(RealSkywars.getPlugin(), () -> p.getParty().setAllowJoin(false), 20L);

                }
            } else {
                if (p.getParty().allowJoin()) {
                    if (p.isInMatch()) {
                        if (p.getMatch().equals(swgm)) {
                            result = false;
                        } else {
                            p.getMatch().removePlayer(p);
                            result = true;
                        }
                    } else {
                        result = true;
                    }
                } else {
                    p.sendMessage(rs.getLanguageManager().getString(p, LanguageManager.TS.PARTY_NOT_OWNER, true));
                    result = false;
                }
            }
        } else {
            result = true;
        }

        return result;
    }
}
