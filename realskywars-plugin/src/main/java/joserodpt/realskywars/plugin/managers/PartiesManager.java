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
 * @author José Rodrigues © 2019-2025
 * @link https://github.com/joserodpt/RealSkywars
 */

import joserodpt.realskywars.api.RealSkywarsAPI;
import joserodpt.realskywars.api.config.TranslatableLine;
import joserodpt.realskywars.api.managers.PartiesManagerAPI;
import joserodpt.realskywars.api.map.RSWMap;
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
                    emissor.sendMessage(TranslatableLine.PARTY_INVITE_SENT.get(emissor, true).replace("%player%", recetor.getDisplayName()));
                    recetor.sendMessage(TranslatableLine.PARTY_INVITE_RECIEVED.get(recetor, true).replace("%player%", emissor.getDisplayName()));
                } else {
                    TranslatableLine.PARTY_NOT_OWNER.send(emissor, true);
                }
            } else {
                TranslatableLine.PARTY_NOTINPARTY.send(emissor, true);
            }
        } else {
            TranslatableLine.PARTY_CANTINVITEYOURSELF.send(emissor, true);
        }
    }

    @Override
    public void acceptInvite(RSWPlayer p) {
        RSWPlayer inviteOwner = this.getInvite(p);
        p.sendMessage(TranslatableLine.PARTY_ACCEPTEDINVITE.get(p, true).replace("%player%", inviteOwner.getDisplayName()));
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
                    TranslatableLine.PARTY_INSUFICIENT_ROOMSPACE.send(p, true);
                    result = false;
                } else {
                    p.getParty().setAllowJoin(true);
                    p.getParty().getMembers().forEach(swgm::addPlayer);
                    result = true;

                    Bukkit.getScheduler().scheduleSyncDelayedTask(rs.getPlugin(), () -> p.getParty().setAllowJoin(false), 20L);
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
                    TranslatableLine.PARTY_NOT_OWNER.send(p, true);
                    result = false;
                }
            }
        } else {
            result = true;
        }

        return result;
    }
}
