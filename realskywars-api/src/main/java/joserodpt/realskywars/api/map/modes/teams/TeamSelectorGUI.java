package joserodpt.realskywars.api.map.modes.teams;

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

import joserodpt.realskywars.api.config.TranslatableLine;
import joserodpt.realskywars.api.player.RSWPlayer;
import joserodpt.realskywars.api.utils.GUIBuilder;
import joserodpt.realskywars.api.utils.Itens;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * The team picker shown on maps with manual team selection enabled. Built on {@link GUIBuilder},
 * whose listener is already registered by the plugin, so this needs no registration of its own and
 * can be opened straight from {@link TeamsMode}.
 */
public class TeamSelectorGUI {

    private TeamSelectorGUI() {
    }

    public static void open(RSWPlayer p, TeamsMode map) {
        if (p == null || p.getPlayer() == null || map == null) {
            return;
        }

        //getTeams() is backed by a HashMap keyed on cage location, so its iteration order has
        //nothing to do with the team numbers: the picker showed them shuffled, and re-rendering
        //after every pick could deal them out in a different order again. Sort by id, which is
        //also what the name, colour and wool are derived from, so the menu reads 1, 2, 3...
        List<RSWTeam> teams = new ArrayList<>(map.getTeams());
        teams.sort(Comparator.comparingInt(RSWTeam::getID));

        int size = Math.max(9, (int) (Math.ceil((teams.size() + 1) / 9D) * 9));
        size = Math.min(size, 54);

        GUIBuilder inventory = new GUIBuilder(TranslatableLine.MENU_TEAMSELECT_TITLE.get(p), size, p.getUUID(),
                Itens.createItem(Material.BLACK_STAINED_GLASS_PANE, 1, ""));

        int slot = 0;
        for (RSWTeam team : teams) {
            if (slot >= size - 1) {
                break;
            }

            final RSWTeam t = team;
            inventory.addItem(e -> {
                if (map.selectTeam(p, t)) {
                    //re-render so the counts and the "your team" marker are up to date
                    open(p, map);
                }
            }, buildIcon(p, t), slot);
            ++slot;
        }

        inventory.addItem(e -> p.closeInventory(),
                Itens.createItem(Material.OAK_DOOR, 1, "&cClose",
                        Collections.singletonList("&fClick here to close this menu.")), size - 1);

        inventory.openInventory(p.getPlayer());
    }

    private static ItemStack buildIcon(RSWPlayer p, RSWTeam team) {
        List<String> lore = new ArrayList<>();

        if (team.getMembers().isEmpty()) {
            lore.add(TranslatableLine.MENU_TEAMSELECT_EMPTY.get(p));
        } else {
            for (RSWPlayer member : team.getMembers()) {
                lore.add("&7- &f" + member.getDisplayName());
            }
        }

        lore.add("");
        if (p.getTeam() == team) {
            lore.add(TranslatableLine.MENU_TEAMSELECT_YOURS.get(p));
        } else if (team.isTeamFull()) {
            lore.add(TranslatableLine.TEAM_SELECT_FULL.get(p));
        } else {
            lore.add(TranslatableLine.MENU_TEAMSELECT_CLICK.get(p));
        }

        String name = team.getColoredName() + " &7(" + team.getMemberCount() + "/" + team.getMaxMembers() + ")";
        return Itens.createItem(team.getIconMaterial(), Math.max(1, team.getMemberCount()), name, lore);
    }
}
