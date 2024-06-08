package joserodpt.realskywars.api.player;

import joserodpt.realskywars.api.config.RSWConfig;
import joserodpt.realskywars.api.config.TranslatableLine;
import joserodpt.realskywars.api.utils.Itens;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public enum RSWPlayerItems {
    LOBBY, CAGE, SETUP, SPECTATOR, PROFILE, CAGESET, MAPS, SHOP, LEAVE, VOTE, SPECTATE, KIT, PLAYAGAIN, CHEST1, CHEST2;

    public void giveSet(RSWPlayer p) {
        switch (this) {
            case CAGE:
                p.getInventory().setItem(RSWConfig.file().getInt("Config.Item-Slots.Cage.Kit"), KIT.get(p));
                p.getInventory().setItem(RSWConfig.file().getInt("Config.Item-Slots.Cage.Vote"), VOTE.get(p));
                p.getInventory().setItem(RSWConfig.file().getInt("Config.Item-Slots.Cage.Leave"), LEAVE.get(p));
                break;
            case LOBBY:
                p.getInventory().setItem(RSWConfig.file().getInt("Config.Item-Slots.Lobby.Profile"), PROFILE.get(p));
                p.getInventory().setItem(RSWConfig.file().getInt("Config.Item-Slots.Lobby.Maps"), MAPS.get(p));
                p.getInventory().setItem(RSWConfig.file().getInt("Config.Item-Slots.Lobby.Shop"), SHOP.get(p));
                break;
            case SPECTATOR:
                p.getInventory().setItem(RSWConfig.file().getInt("Config.Item-Slots.Spectator.Spectate"), SPECTATE.get(p));
                if (p.getState() != RSWPlayer.PlayerState.EXTERNAL_SPECTATOR) {
                    p.getInventory().setItem(RSWConfig.file().getInt("Config.Item-Slots.Spectator.Play-Again"), PLAYAGAIN.get(p));
                }
                if (RSWConfig.file().getBoolean("Config.Spectator-Shop")) {
                    p.getInventory().setItem(RSWConfig.file().getInt("Config.Item-Slots.Spectator.Shop"), SHOP.get(p));
                }
                p.getInventory().setItem(RSWConfig.file().getInt("Config.Item-Slots.Spectator.Leave"), LEAVE.get(p));
                break;
            case SETUP:
                p.getInventory().setItem(RSWConfig.file().getInt("Config.Item-Slots.Setup.Chest1"), CHEST1.get(p));
                p.getInventory().setItem(RSWConfig.file().getInt("Config.Item-Slots.Setup.Cage"), CAGESET.get(p));
                p.getInventory().setItem(RSWConfig.file().getInt("Config.Item-Slots.Setup.Chest2"), CHEST2.get(p));
                break;
        }
    }

    public ItemStack get(RSWPlayer p) {
        switch (this) {
            case KIT:
                return Itens.createItem(Material.BOW, 1, TranslatableLine.ITEM_KIT_NAME.get(p, false));
            case PROFILE:
                return Itens.createItem(Material.BOOK, 1, TranslatableLine.ITEM_PROFILE_NAME.get(p, false));
            case CAGESET:
                return Itens.createItem(Material.BEACON, 1, TranslatableLine.ITEM_CAGESET_NAME.get(p, false));
            case MAPS:
                return Itens.createItem(Material.NETHER_STAR, 1, TranslatableLine.ITEM_MAPS_NAME.get(p, false));
            case SHOP:
                return Itens.createItem(Material.EMERALD, 1, TranslatableLine.ITEM_SHOP_NAME.get(p, false));
            case LEAVE:
                return Itens.createItem(Material.MINECART, 1, TranslatableLine.ITEM_LEAVE_NAME.get(p, false));
            case VOTE:
                return Itens.createItem(Material.HOPPER, 1, TranslatableLine.ITEM_VOTE_NAME.get(p, false));
            case SPECTATE:
                return Itens.createItem(Material.MAP, 1, TranslatableLine.ITEM_SPECTATE_NAME.get(p, false));
            case PLAYAGAIN:
                return Itens.createItem(Material.TOTEM_OF_UNDYING, 1, TranslatableLine.ITEM_PLAYAGAIN_NAME.get(p, false));
            case CHEST1:
                return Itens.createItem(Material.CHEST, 1, TranslatableLine.ITEM_CHEST1_NAME.get(p, false));
            case CHEST2:
                return Itens.createItem(Material.CHEST, 1, TranslatableLine.ITEM_CHEST2_NAME.get(p, false));
        }
        return new ItemStack(Material.DEAD_BUSH);
    }
}
