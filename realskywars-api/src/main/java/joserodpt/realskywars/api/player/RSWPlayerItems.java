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
import joserodpt.realskywars.api.utils.ItemStackSpringer;
import joserodpt.realskywars.api.utils.Itens;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum RSWPlayerItems {
    LOBBY, CAGE, SETUP, SPECTATOR, ITEM_PROFILE, ITEM_SETCAGE, ITEM_MAPS, ITEM_SHOP, ITEM_LEAVE, ITEM_VOTE, ITEM_SPECTATE, ITEM_KITS, ITEM_PLAYAGAIN, ITEM_CHEST1, ITEM_CHEST2, ITEM_SETINGS, ITEM_SAVE;

    private void setItem(RSWPlayer p, int slot, ItemStack i) {
        if (slot < 0 || slot > 40) {
            return;
        }

        p.getInventory().setItem(slot, i);
    }

    public void giveSet(RSWPlayer p) {
        if (p.isBot() || p.getPlayer() == null) {
            return;
        }

        switch (this) {
            case CAGE:
                setItem(p, RSWConfig.file().getInt("Config.Item-Slots.Cage.Kit"), ITEM_KITS.get(p));
                setItem(p, RSWConfig.file().getInt("Config.Item-Slots.Cage.Vote"), ITEM_VOTE.get(p));
                setItem(p, RSWConfig.file().getInt("Config.Item-Slots.Cage.Leave"), ITEM_LEAVE.get(p));
                break;
            case LOBBY:
                if (RSWConfig.file().getBoolean("Config.Disable-Lobby-Items", false)) {
                    return;
                }
                setItem(p, RSWConfig.file().getInt("Config.Item-Slots.Lobby.Profile"), ITEM_PROFILE.get(p));
                setItem(p, RSWConfig.file().getInt("Config.Item-Slots.Lobby.Maps"), ITEM_MAPS.get(p));
                setItem(p, RSWConfig.file().getInt("Config.Item-Slots.Lobby.Shop"), ITEM_SHOP.get(p));
                break;
            case SPECTATOR:
                setItem(p, RSWConfig.file().getInt("Config.Item-Slots.Spectator.Spectate"), ITEM_SPECTATE.get(p));
                if (p.getState() != RSWPlayer.PlayerState.EXTERNAL_SPECTATOR) {
                    setItem(p, RSWConfig.file().getInt("Config.Item-Slots.Spectator.Play-Again"), ITEM_PLAYAGAIN.get(p));
                }
                if (RSWConfig.file().getBoolean("Config.Shops.Enable-Spectator-Shop")) {
                    setItem(p, RSWConfig.file().getInt("Config.Item-Slots.Spectator.Shop"), ITEM_SHOP.get(p));
                }
                setItem(p, RSWConfig.file().getInt("Config.Item-Slots.Spectator.Leave"), ITEM_LEAVE.get(p));
                break;
            case SETUP:
                setItem(p, RSWConfig.file().getInt("Config.Item-Slots.Setup.Cage"), ITEM_SETCAGE.get(p));
                setItem(p, RSWConfig.file().getInt("Config.Item-Slots.Setup.Chest1"), ITEM_CHEST1.get(p));
                setItem(p, RSWConfig.file().getInt("Config.Item-Slots.Setup.Chest2"), ITEM_CHEST2.get(p));
                setItem(p, RSWConfig.file().getInt("Config.Item-Slots.Setup.Settings"), ITEM_SETINGS.get(p));
                setItem(p, RSWConfig.file().getInt("Config.Item-Slots.Setup.Save"), ITEM_SAVE.get(p));
                break;
        }
    }

    private ItemStack getConfiguredItem(String itemKey, Material defaultMaterial, int defaultAmount, String defaultName) {
        String basePath = "Config.Items." + itemKey;

        if (!RSWConfig.file().contains(basePath)) {
            return Itens.createItem(defaultMaterial, defaultAmount, defaultName);
        }

        Map<String, Object> itemData = new HashMap<>();
        itemData.put(ItemStackSpringer.ItemCategories.MATERIAL.name(),
                RSWConfig.file().getString(basePath + ".MATERIAL", defaultMaterial.name()));
        itemData.put(ItemStackSpringer.ItemCategories.AMOUNT.name(),
                RSWConfig.file().getInt(basePath + ".AMOUNT", defaultAmount));

        String configuredName = null;
        if (RSWConfig.file().contains(basePath + ".NAME")) {
            configuredName = RSWConfig.file().getString(basePath + ".NAME");
        }
        if (configuredName == null || configuredName.isBlank()) {
            configuredName = defaultName;
        }
        itemData.put(ItemStackSpringer.ItemCategories.NAME.name(), configuredName);
        List<String> lore = RSWConfig.file().getStringList(basePath + ".LORE");
        if (lore != null && !lore.isEmpty()) {
            itemData.put(ItemStackSpringer.ItemCategories.LORE.name(), lore);
        }

        if (RSWConfig.file().contains(basePath + ".CUSTOM_MODEL_DATA")) {
            itemData.put(ItemStackSpringer.ItemCategories.CUSTOM_MODEL_DATA.name(),
                    RSWConfig.file().getInt(basePath + ".CUSTOM_MODEL_DATA"));
        }

        String enchantments = RSWConfig.file().getString(basePath + ".ENCHANTMENTS");
        if (enchantments != null && !enchantments.isBlank()) {
            itemData.put(ItemStackSpringer.ItemCategories.ENCHANTMENTS.name(), enchantments);
        }

        String itemFlags = RSWConfig.file().getString(basePath + ".ITEM_FLAGS");
        if (itemFlags != null && !itemFlags.isBlank()) {
            itemData.put(ItemStackSpringer.ItemCategories.ITEM_FLAGS.name(), itemFlags);
        }

        ItemStack configuredItem = ItemStackSpringer.getItemDeSerialized(itemData);
        return configuredItem != null ? configuredItem : Itens.createItem(defaultMaterial, defaultAmount, defaultName);
    }

    public ItemStack get(RSWPlayer p) {
        switch (this) {
            case ITEM_KITS:
                return getConfiguredItem("Kit", Material.BOW, 1, TranslatableLine.ITEM_KIT_NAME.get(p));
            case ITEM_PROFILE:
                return getConfiguredItem("Profile", Material.BOOK, 1, TranslatableLine.ITEM_PROFILE_NAME.get(p));
            case ITEM_SETCAGE:
                return getConfiguredItem("Cage", Material.BEACON, 1, TranslatableLine.ITEM_CAGESET_NAME.get(p));
            case ITEM_MAPS:
                return getConfiguredItem("Maps", Material.NETHER_STAR, 1, TranslatableLine.ITEM_MAPS_NAME.get(p));
            case ITEM_SHOP:
                return getConfiguredItem("Shop", Material.EMERALD, 1, TranslatableLine.ITEM_SHOP_NAME.get(p));
            case ITEM_LEAVE:
                return getConfiguredItem("Leave", Material.MINECART, 1, TranslatableLine.ITEM_LEAVE_NAME.get(p));
            case ITEM_VOTE:
                return getConfiguredItem("Vote", Material.HOPPER, 1, TranslatableLine.ITEM_VOTE_NAME.get(p));
            case ITEM_SPECTATE:
                return getConfiguredItem("Spectate", Material.MAP, 1, TranslatableLine.ITEM_SPECTATE_NAME.get(p));
            case ITEM_PLAYAGAIN:
                return getConfiguredItem("Play-Again", Material.TOTEM_OF_UNDYING, 1, TranslatableLine.ITEM_PLAYAGAIN_NAME.get(p));
            case ITEM_CHEST1:
                return getConfiguredItem("Chest1", Material.CHEST, 1, TranslatableLine.ITEM_CHEST1_NAME.get(p));
            case ITEM_CHEST2:
                return getConfiguredItem("Chest2", Material.CHEST, 1, TranslatableLine.ITEM_CHEST2_NAME.get(p));
            case ITEM_SETINGS:
                return getConfiguredItem("Settings", Material.COMPARATOR, 1, TranslatableLine.ITEM_SETTINGS_NAME.get(p));
            case ITEM_SAVE:
                return getConfiguredItem("Save", Material.CHEST_MINECART, 1, TranslatableLine.ITEM_SAVE_NAME.get(p));
        }
        return new ItemStack(Material.DEAD_BUSH);
    }
}
