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
 * @author José Rodrigues
 * @link https://github.com/joserodpt/RealSkywars
 */

import joserodpt.realskywars.api.RealSkywarsAPI;
import joserodpt.realskywars.api.config.TranslatableLine;
import joserodpt.realskywars.api.kits.RSWKit;
import joserodpt.realskywars.api.managers.ShopManagerAPI;
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

public class RSWShopDisplayItem {

    private final Map<String, Object> info = new HashMap<>();
    private String name, displayName;
    private Material m;
    private int amount = 1;
    private Double price;
    private Pair<Boolean, String> boughtPair = new Pair<>(false, "");
    private String permission;
    private Boolean interactive = true;
    private ShopManagerAPI.ShopCategory it;

    public RSWShopDisplayItem(String name, String displayName, Material ma, Double per, Pair<Boolean, String> boughtPair, String perm, ShopManagerAPI.ShopCategory t) {
        this.name = name;
        this.displayName = displayName;
        this.price = per;
        this.boughtPair = boughtPair;
        this.permission = perm;
        this.it = t;
        this.m = ma;
    }

    public RSWShopDisplayItem(String name, String displayName, Material ma, Double price, String perm) {
        this.name = name;
        this.displayName = displayName;
        this.price = price;
        this.permission = perm;
        this.m = ma;
        this.it = ShopManagerAPI.ShopCategory.SPEC_SHOP;
    }

    public RSWShopDisplayItem() {
        this.interactive = false;
    }

    public void addInfo(String a, Object b) {
        info.put(a, b);
    }

    public Object getInfo(String s) {
        return info.get(s);
    }

    public boolean containsInfo(String randomBlock) {
        return info.containsKey(randomBlock);
    }

    public boolean isBought() {
        return this.boughtPair.getKey();
    }

    public void setBought(boolean b) {
        this.boughtPair.setKey(b);
    }

    public boolean isInteractive() {
        return this.interactive;
    }

    public void setInteractive(boolean b) {
        this.interactive = b;
    }

    public String getPermission() {
        return this.permission;
    }

    public String getName() {
        return this.name;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public void setName(String s) {
        this.name = s;
    }

    public Double getPrice() {
        return this.it == ShopManagerAPI.ShopCategory.SPEC_SHOP ? this.price * this.amount : this.price;
    }

    public int getAmount() {
        return Math.max(1, Math.min(this.amount, 64));
    }

    public ItemStack getItemStack(RSWPlayer p) {
        if (!this.interactive) {
            return Itens.createItem(Material.BUCKET, this.getAmount(), TranslatableLine.SEARCH_NOTFOUND_NAME.getSingle());
        }

        switch (this.it) {
            case KITS:
                RSWKit k = RealSkywarsAPI.getInstance().getKitManagerAPI().getKit(name);

                if (this.isBought()) {
                    return Itens.createItemLoreEnchanted(this.m, this.getAmount(), "&r&f" + k.getDisplayName(), k.getDescription(p, this.boughtPair));
                } else {
                    return Itens.createItem(this.m, this.getAmount(), "&r&f" + k.getDisplayName(), k.getDescription(p, this.boughtPair));
                }
            case SPEC_SHOP:
                return Itens.createItem(this.getMaterial(), this.getAmount(), "&f" + this.amount + "x " + this.getDisplayName(), Arrays.asList(TranslatableLine.SHOP_CLICK_2_BUY.get(p).replace("%price%", this.getPrice().toString()), "", "&a&nF (Swap hand)&r&f to increase the item amount.", "&c&nQ (Drop)&r&f to decrease the item amount."));
            default:
                return this.isBought() ? Itens.createItemLoreEnchanted(this.m, this.getAmount(), formatName(p, this.name), Arrays.asList("&f" + TranslatableLine.SHOP_BOUGHT_ON.get(p) + this.boughtPair.getValue(), TranslatableLine.SHOP_CLICK_2_SELECT.get(p))) : Itens.createItem(m, 1, formatName(p, this.getDisplayName()), Collections.singletonList(TranslatableLine.SHOP_CLICK_2_BUY.get(p).replace("%coins%", this.getPrice().toString())));
        }
    }

    private String formatName(RSWPlayer p, String name) {
        try {
            Material m = Material.getMaterial(Text.strip(name));
            return "&b" + RealSkywarsAPI.getInstance().getLanguageManagerAPI().getMaterialName(p, m);
        } catch (Exception e) {
            return "&r&f" + name;
        }
    }

    public Material getMaterial() {
        return this.m;
    }

    public void addAmount(int i) {
        this.amount = Math.min(this.m.getMaxStackSize(), Math.max(1, this.amount + i));
    }
}
