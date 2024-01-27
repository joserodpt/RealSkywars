package joserodpt.realskywars.shop;

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

import joserodpt.realskywars.RealSkywars;
import joserodpt.realskywars.kits.SWKit;
import joserodpt.realskywars.managers.LanguageManager;
import joserodpt.realskywars.managers.ShopManager;
import joserodpt.realskywars.utils.Itens;
import joserodpt.realskywars.utils.Text;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShopDisplayItem {

    private final Map<String, Object> info = new HashMap<>();
    private String name, displayName;
    private Material m;
    private int amount = 1;
    private Double price;
    private Boolean bought = false;
    private String permission;
    private Boolean interactive = true;
    private ShopManager.Categories it;

    public ShopDisplayItem(String name, String displayName, Material ma, Double per, Boolean b, String perm, ShopManager.Categories t) {
        this.name = name;
        this.displayName = displayName;
        this.price = per;
        this.bought = b;
        this.permission = perm;
        this.it = t;
        this.m = ma;
    }

    public ShopDisplayItem(String name, String displayName, Material ma, Double price, String perm) {
        this.name = name;
        this.displayName = displayName;
        this.price = price;
        this.permission = perm;
        this.m = ma;
        this.it = ShopManager.Categories.SPEC_SHOP;
    }

    public ShopDisplayItem() {
        this.interactive = false;
    }

    public void addInfo(String a, Object b) {
        info.put(a, b);
    }


    private String formatName(String name) {
        String ret;
        try {
            Material m = Material.getMaterial(Text.strip(name));
            ret = "&b" + WordUtils.capitalizeFully(m.name().replace("_", " "));
        } catch (Exception e) {
            ret = name;
        }
        ret = "&r&f" + ret;
        return ret;
    }

    public Object getInfo(String s) {
        return info.get(s);
    }

    public boolean containsInfo(String randomBlock) {
        return info.containsKey(randomBlock);
    }

    public boolean isBought() {
        return this.bought;
    }

    public void setBought(boolean b) {
        this.bought = b;
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
        return this.it == ShopManager.Categories.SPEC_SHOP ? this.price * this.amount : this.price;
    }

    public int getAmount() {
        return this.amount;
    }

    public ItemStack getItemStack() {
        if (!this.interactive) {
            return Itens.createItem(Material.BUCKET, this.getAmount(), RealSkywars.getPlugin().getLanguageManager().getString(LanguageManager.TSsingle.SEARCH_NOTFOUND_NAME));
        }

        switch (this.it) {
            case KITS:
                SWKit k = RealSkywars.getPlugin().getKitManager().getKit(name);
                return this.bought ? Itens.createItemLoreEnchanted(m, this.getAmount(), "&r&f" + k.getDisplayName(), k.getDescription(false)) : Itens.createItemLore(m, 1, "&r&f" + k.getDisplayName(), k.getDescription(true));
            case SPEC_SHOP:
                return Itens.createItemLore(m, 1, "&f" + this.amount + "x " + this.getDisplayName(), makeSpecShopDescription());
            default:
                return this.bought ? Itens.createItemLoreEnchanted(m, this.getAmount(), formatName(this.getDisplayName()), Collections.singletonList(RealSkywars.getPlugin().getLanguageManager().getString(LanguageManager.TSsingle.SHOP_BOUGHT))) : Itens.createItemLore(m, 1, formatName(this.getDisplayName()), Collections.singletonList(RealSkywars.getPlugin().getLanguageManager().getString(LanguageManager.TSsingle.SHOP_BUY).replace("%price%", this.getPrice().toString())));
        }
    }

    private List<String> makeSpecShopDescription() {
        return Arrays.asList(RealSkywars.getPlugin().getLanguageManager().getString(LanguageManager.TSsingle.SHOP_BUY).replace("%price%", this.getPrice().toString()), "","&a&nF (Swap hand)&r&f to increase the item amount.", "&c&nQ (Drop)&r&f to decrease the item amount.");
    }

    public Material getMaterial() {
        return this.m;
    }

    public void addAmount(int i) {
        this.amount = Math.min(this.m.getMaxStackSize(), Math.max(1, this.amount + i));
    }
}
