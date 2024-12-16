package joserodpt.realskywars.api.player;

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

import joserodpt.realskywars.api.config.RSWConfig;
import joserodpt.realskywars.api.config.TranslatableLine;
import joserodpt.realskywars.api.utils.Itens;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public enum RSWPlayerItems {
    LOBBY, CAGE, SETUP, SPECTATOR, ITEM_PROFILE, ITEM_SETCAGE, ITEM_MAPS, ITEM_SHOP, ITEM_LEAVE, ITEM_VOTE, ITEM_SPECTATE, ITEM_KITS, ITEM_PLAYAGAIN, ITEM_CHEST1, ITEM_CHEST2, ITEM_SETINGS, ITEM_SAVE;

    public void giveSet(RSWPlayer p) {
        if (p.isBot() || p.getPlayer() == null) {
            return;
        }

        switch (this) {
            case CAGE:
                p.getInventory().clear();
                p.getInventory().setItem(RSWConfig.file().getInt("Config.Item-Slots.Cage.Kit"), ITEM_KITS.get(p));
                p.getInventory().setItem(RSWConfig.file().getInt("Config.Item-Slots.Cage.Vote"), ITEM_VOTE.get(p));
                p.getInventory().setItem(RSWConfig.file().getInt("Config.Item-Slots.Cage.Leave"), ITEM_LEAVE.get(p));
                break;
            case LOBBY:
                if (RSWConfig.file().getBoolean("Config.Disable-Lobby-Items", false)) {
                    return;
                }
                p.getInventory().clear();
                p.getInventory().setItem(RSWConfig.file().getInt("Config.Item-Slots.Lobby.Profile"), ITEM_PROFILE.get(p));
                p.getInventory().setItem(RSWConfig.file().getInt("Config.Item-Slots.Lobby.Maps"), ITEM_MAPS.get(p));
                p.getInventory().setItem(RSWConfig.file().getInt("Config.Item-Slots.Lobby.Shop"), ITEM_SHOP.get(p));
                break;
            case SPECTATOR:
                p.getInventory().clear();
                p.getInventory().setItem(RSWConfig.file().getInt("Config.Item-Slots.Spectator.Spectate"), ITEM_SPECTATE.get(p));
                if (p.getState() != RSWPlayer.PlayerState.EXTERNAL_SPECTATOR) {
                    p.getInventory().setItem(RSWConfig.file().getInt("Config.Item-Slots.Spectator.Play-Again"), ITEM_PLAYAGAIN.get(p));
                }
                if (RSWConfig.file().getBoolean("Config.Shops.Enable-Spectator-Shop")) {
                    p.getInventory().setItem(RSWConfig.file().getInt("Config.Item-Slots.Spectator.Shop"), ITEM_SHOP.get(p));
                }
                p.getInventory().setItem(RSWConfig.file().getInt("Config.Item-Slots.Spectator.Leave"), ITEM_LEAVE.get(p));
                break;
            case SETUP:
                p.getInventory().clear();
                p.getInventory().setItem(RSWConfig.file().getInt("Config.Item-Slots.Setup.Cage"), ITEM_SETCAGE.get(p));
                p.getInventory().setItem(RSWConfig.file().getInt("Config.Item-Slots.Setup.Chest1"), ITEM_CHEST1.get(p));
                p.getInventory().setItem(RSWConfig.file().getInt("Config.Item-Slots.Setup.Chest2"), ITEM_CHEST2.get(p));
                p.getInventory().setItem(RSWConfig.file().getInt("Config.Item-Slots.Setup.Settings"), ITEM_SETINGS.get(p));
                p.getInventory().setItem(RSWConfig.file().getInt("Config.Item-Slots.Setup.Save"), ITEM_SAVE.get(p));
                break;
        }
    }

    public ItemStack get(RSWPlayer p) {
        switch (this) {
            case ITEM_KITS:
                return Itens.createItem(Material.BOW, 1, TranslatableLine.ITEM_KIT_NAME.get(p));
            case ITEM_PROFILE:
                return Itens.createItem(Material.BOOK, 1, TranslatableLine.ITEM_PROFILE_NAME.get(p));
            case ITEM_SETCAGE:
                return Itens.createItem(Material.BEACON, 1, TranslatableLine.ITEM_CAGESET_NAME.get(p));
            case ITEM_MAPS:
                return Itens.createItem(Material.NETHER_STAR, 1, TranslatableLine.ITEM_MAPS_NAME.get(p));
            case ITEM_SHOP:
                return Itens.createItem(Material.EMERALD, 1, TranslatableLine.ITEM_SHOP_NAME.get(p));
            case ITEM_LEAVE:
                return Itens.createItem(Material.MINECART, 1, TranslatableLine.ITEM_LEAVE_NAME.get(p));
            case ITEM_VOTE:
                return Itens.createItem(Material.HOPPER, 1, TranslatableLine.ITEM_VOTE_NAME.get(p));
            case ITEM_SPECTATE:
                return Itens.createItem(Material.MAP, 1, TranslatableLine.ITEM_SPECTATE_NAME.get(p));
            case ITEM_PLAYAGAIN:
                return Itens.createItem(Material.TOTEM_OF_UNDYING, 1, TranslatableLine.ITEM_PLAYAGAIN_NAME.get(p));
            case ITEM_CHEST1:
                return Itens.createItem(Material.CHEST, 1, TranslatableLine.ITEM_CHEST1_NAME.get(p));
            case ITEM_CHEST2:
                return Itens.createItem(Material.CHEST, 1, TranslatableLine.ITEM_CHEST2_NAME.get(p));
            case ITEM_SETINGS:
                return Itens.createItem(Material.COMPARATOR, 1, TranslatableLine.ITEM_SETTINGS_NAME.get(p));
            case ITEM_SAVE:
                return Itens.createItem(Material.CHEST_MINECART, 1, TranslatableLine.ITEM_SAVE_NAME.get(p));
        }
        return new ItemStack(Material.DEAD_BUSH);
    }
}
