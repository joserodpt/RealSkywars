package joserodpt.realskywars.plugin.managers;

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
import joserodpt.realskywars.api.map.RSWMap;
import joserodpt.realskywars.api.managers.LanguageManagerAPI;
import joserodpt.realskywars.api.managers.PartiesManagerAPI;
import joserodpt.realskywars.api.player.RSWPlayer;
import org.bukkit.Bukkit;

public class PartiesManager extends PartiesManagerAPI {
    private final RealSkywarsAPI rs;

    public PartiesManager(RealSkywarsAPI rs) {
        this.rs = rs;
    }

    @Override
    public void sendInvite(RSWPlayer emissor, RSWPlayer recetor) {
        if (emissor != recetor) {
            if (emissor.hasParty()) {
                if (emissor.getParty().isOwner(emissor) && !emissor.getParty().getMembers().contains(recetor)) {
                    invites.put(recetor, emissor);
                    emissor.sendMessage(rs.getLanguageManagerAPI().getString(emissor, LanguageManagerAPI.TS.PARTY_INVITE_SENT, true).replace("%player%", recetor.getDisplayName()));
                    recetor.sendMessage(rs.getLanguageManagerAPI().getString(recetor, LanguageManagerAPI.TS.PARTY_INVITE_RECIEVED, true).replace("%player%", emissor.getDisplayName()));
                } else {
                    emissor.sendMessage(rs.getLanguageManagerAPI().getString(emissor, LanguageManagerAPI.TS.PARTY_NOT_OWNER, true));
                }
            } else {
                emissor.sendMessage(rs.getLanguageManagerAPI().getString(emissor, LanguageManagerAPI.TS.PARTY_NOTINPARTY, true));
            }
        } else {
            emissor.sendMessage(rs.getLanguageManagerAPI().getString(emissor, LanguageManagerAPI.TS.PARTY_CANTINVITEYOURSELF, true));
        }
    }

    @Override
    public void acceptInvite(RSWPlayer p) {
        RSWPlayer inviteOwner = this.getInvite(p);
        p.sendMessage(rs.getLanguageManagerAPI().getString(p, LanguageManagerAPI.TS.PARTY_ACCEPTEDINVITE, true).replace("%player%", inviteOwner.getDisplayName()));
        inviteOwner.getParty().playerJoin(p);
    }

    @Override
    public boolean checkForParties(RSWPlayer p, RSWMap swgm) {
        int current, max, toAdd;

        boolean result;
        if (p.hasParty()) {
            if (p.getParty().isOwner(p)) {
                current = swgm.getPlayerCount();
                max = swgm.getMaxPlayers();

                toAdd = current + p.getParty().getMembers().size() + 1; //1 = owner of the party

                if (toAdd > max) {
                    p.sendMessage(rs.getLanguageManagerAPI().getString(p, LanguageManagerAPI.TS.PARTY_INSUFICIENT_ROOMSPACE, true));
                    result = false;
                } else {
                    p.getParty().setAllowJoin(true);
                    p.getParty().getMembers().forEach(swgm::addPlayer);
                    result = true;

                    Bukkit.getScheduler().scheduleSyncDelayedTask(RealSkywarsAPI.getInstance().getPlugin(), () -> p.getParty().setAllowJoin(false), 20L);
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
                    p.sendMessage(rs.getLanguageManagerAPI().getString(p, LanguageManagerAPI.TS.PARTY_NOT_OWNER, true));
                    result = false;
                }
            }
        } else {
            result = true;
        }

        return result;
    }
}
