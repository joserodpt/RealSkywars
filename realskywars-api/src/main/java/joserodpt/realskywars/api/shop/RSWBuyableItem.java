package joserodpt.realskywars.api.shop;

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
import joserodpt.realskywars.api.config.RSWShopsConfig;
import joserodpt.realskywars.api.config.TranslatableLine;
import joserodpt.realskywars.api.player.RSWPlayer;
import joserodpt.realskywars.api.utils.Itens;
import joserodpt.realskywars.api.utils.Pair;
import joserodpt.realskywars.api.utils.Text;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class RSWBuyableItem {

    private final String configKey;
    private final String displayName;
    private int amount = 1;
    private final Material material;
    private final Double price;
    private final String permission;
    private final ItemCategory category;
    private final Map<String, Object> extras;
    private boolean dummy = false;

    public RSWBuyableItem(String configKey, String displayName, Material material, Double price, String permission, ItemCategory category, Map<String, Object> extras) {
        this.configKey = configKey;
        this.displayName = displayName;
        this.material = material;
        this.price = price;
        this.permission = permission;
        this.category = category;
        this.extras = extras;
    }

    public RSWBuyableItem(String configKey, String displayName, Material material, Double price, String permission, ItemCategory category) {
        this(configKey, displayName, material, price, permission, category, new HashMap<>());
    }

    public RSWBuyableItem() {
        this("", TranslatableLine.SEARCH_NOTFOUND_NAME.getSingle(), Material.BUCKET, 999999999D, "", ItemCategory.NONE, new HashMap<>());
        this.dummy = true;
    }

    public void saveToConfig(Boolean save) {
        if (this.category == ItemCategory.NONE || this.category == ItemCategory.KIT) {
            return;
        }

        String configPath = "Shops." + getCategory().getCategoryConfigName() + "." + getConfigKey();
        RSWShopsConfig.file().set(configPath + ".Displayname", getDisplayName());
        RSWShopsConfig.file().set(configPath + ".Material", getMaterial().name());
        RSWShopsConfig.file().set(configPath + ".Price", getPrice());
        if (permission != null || permission.isEmpty()) {
            RSWShopsConfig.file().set(configPath + ".Permission", getPermission());
        }
        if (!extras.isEmpty()) {
            RSWShopsConfig.file().set(configPath + ".Extras", getExtrasMap());
        }
        if (save)
            RSWShopsConfig.save();
    }

    public String getConfigKey() {
        return this.configKey;
    }

    public String getName() {
        return this.configKey;
    }

    public String getPermission() {
        return this.permission;
    }

    public Map<String, Object> getExtrasMap() {
        return this.extras;
    }

    public Material getMaterial() {
        return this.material;
    }

    public ItemCategory getCategory() {
        return this.category;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public Double getPrice() {
        return this.getCategory() == RSWBuyableItem.ItemCategory.SPEC_SHOP ? this.price * this.amount : this.price;
    }

    public String getPriceFormatted() {
        return Text.formatDouble(this.getPrice());
    }

    public int getAmount() {
        return Math.max(1, Math.min(this.amount, 64));
    }

    public Pair<Boolean, String> isBought(RSWPlayer p) {
        return RealSkywarsAPI.getInstance().getDatabaseManagerAPI().didPlayerBoughtItem(p, this);
    }

    public void addAmount(int i) {
        this.amount = Math.min(this.getMaterial().getMaxStackSize(), Math.max(1, this.amount + i));
    }

    public ItemStack getIcon(RSWPlayer p) {
        if (this.dummy) {
            return Itens.createItem(this.getMaterial(), 1, this.getDisplayName());
        }

        if (this.getCategory() == ItemCategory.SPEC_SHOP) {
            return Itens.createItemLoreEnchanted(this.getMaterial(), this.getAmount(), "&b" + this.getDisplayName() + " &fx" + this.getAmount(), Arrays.asList("&a&nF (Swap hand)&r&f to increase the item amount.", "&c&nQ (Drop)&r&f to decrease the item amount.", TranslatableLine.SHOP_CLICK_2_BUY.get(p).replace("%coins%", this.getPriceFormatted())));
        }

        Pair<Boolean, String> res = this.isBought(p);
        return res.getKey() ? Itens.createItemLoreEnchanted(this.getMaterial(), this.getAmount(), "&b" + this.getDisplayName(), Collections.singletonList("&f" + TranslatableLine.SHOP_BOUGHT_ON.get(p) + res.getValue())) :
                Itens.createItem(this.getMaterial(), 1, "&b" + this.getDisplayName(), Collections.singletonList(TranslatableLine.SHOP_CLICK_2_BUY.get(p).replace("%coins%", this.getPriceFormatted())));
    }

    public void setDummy() {
        this.dummy = true;
    }

    public boolean isDummy() {
        return this.dummy;
    }

    public void setAmount(int i) {
        this.amount = i;
    }

    public enum ItemCategory {
        CAGE_BLOCK, BOW_PARTICLE, KIT, WIN_BLOCK, SPEC_SHOP, NONE;

        public static ItemCategory getCategoryByName(String shopCategory) {
            switch (shopCategory) {
                case "Cage-Blocks":
                    return CAGE_BLOCK;
                case "Win-Blocks":
                    return WIN_BLOCK;
                case "Bow-Particles":
                    return BOW_PARTICLE;
                case "Spectator-Shop":
                    return SPEC_SHOP;
                default:
                    return null;
            }
        }

        public String getCategoryConfigName() {
            switch (this) {
                case CAGE_BLOCK:
                    return "Cage-Blocks";
                case WIN_BLOCK:
                    return "Win-Blocks";
                case BOW_PARTICLE:
                    return "Bow-Particles";
                case SPEC_SHOP:
                    return "Spectator-Shop";
                default:
                    return "err?";
            }
        }

        public String getCategoryTitle(RSWPlayer p) {
            switch (this) {
                case KIT:
                    return TranslatableLine.KITS.get(p);
                case BOW_PARTICLE:
                    return TranslatableLine.BOWPARTICLE.get(p);
                case WIN_BLOCK:
                    return TranslatableLine.WINBLOCK.get(p);
                case CAGE_BLOCK:
                    return TranslatableLine.CAGEBLOCK.get(p);
                case SPEC_SHOP:
                    return TranslatableLine.MENU_SPECTATOR_SHOP_TITLE.get(p);
                default:
                    return "? not found";
            }
        }
    }

    @Override
    public String toString() {
        return "RSWBuyableItem{" +
                "configKey='" + configKey + '\'' +
                ", displayName='" + displayName + '\'' +
                ", amount=" + amount +
                ", material=" + material +
                ", price=" + price +
                ", permission='" + permission + '\'' +
                ", category=" + category +
                ", extras=" + extras +
                ", dummy=" + dummy +
                '}';
    }
}
