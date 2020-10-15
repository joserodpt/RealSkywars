package pt.josegamerpt.realskywars.gui;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import pt.josegamerpt.realskywars.RealSkywars;
import pt.josegamerpt.realskywars.classes.DisplayItem;
import pt.josegamerpt.realskywars.classes.Enum;
import pt.josegamerpt.realskywars.configuration.Shops;
import pt.josegamerpt.realskywars.managers.LanguageManager;
import pt.josegamerpt.realskywars.managers.PlayerManager;
import pt.josegamerpt.realskywars.managers.ShopManager;
import pt.josegamerpt.realskywars.player.GamePlayer;
import pt.josegamerpt.realskywars.utils.*;

import java.util.*;

public class TrailEditor {

    private static final Map<UUID, TrailEditor> inventories = new HashMap<>();
    static ItemStack placeholder = Itens.createItem(Material.BLACK_STAINED_GLASS_PANE, 1, "");
    static ItemStack menu = Itens.createItemLore(Material.CHEST, 1, "&9Menu",
            Arrays.asList("&fClick here to go back to the main menu."));
    static ItemStack next = Itens.createItemLore(Material.GREEN_STAINED_GLASS, 1, "&aNext",
            Arrays.asList("&fClick here to go to the next page."));
    static ItemStack back = Itens.createItemLore(Material.YELLOW_STAINED_GLASS, 1, "&6Back",
            Arrays.asList("&fClick here to go back to the next page."));
    private final Inventory inv;
    private final UUID uuid;
    private final List<DisplayItem> items;
    private final HashMap<Integer, DisplayItem> display = new HashMap<Integer, DisplayItem>();
    private final Boolean disableMenu = false;
    private final String in;
    private final Enum.Categories cat;
    public Player pl;
    int pageNumber = 0;
    Pagination<DisplayItem> p;

    public TrailEditor(Player as, Enum.Categories t, String invName) {
        this.uuid = as.getUniqueId();
        this.pl = as;
        this.cat = t;
        this.in = Text.addColor(invName);
        inv = Bukkit.getServer().createInventory(null, 54, Text.addColor(invName));

        items = ShopManager.getCategoryContents(PlayerManager.getPlayer(as), t);

        p = new Pagination<DisplayItem>(28, items);
        fillChest(p.getPage(pageNumber), false);

        this.register();
    }

    public static Listener getListener() {
        return new Listener() {
            @EventHandler
            public void onClick(InventoryClickEvent e) {
                HumanEntity clicker = e.getWhoClicked();
                if (clicker instanceof Player) {
                    if (e.getCurrentItem() == null) {
                        return;
                    }
                    UUID uuid = clicker.getUniqueId();
                    if (inventories.containsKey(uuid)) {
                        TrailEditor current = inventories.get(uuid);
                        if (e.getInventory().getHolder() != current.getInventory().getHolder()) {
                            return;
                        }

                        e.setCancelled(true);
                        GamePlayer gp = PlayerManager.getPlayer((Player) clicker);

                        if (e.getRawSlot() == 49) {
                            if (e.getCurrentItem().equals(menu)) {
                                clicker.closeInventory();
                                if (inventories.containsKey(uuid)) {
                                    inventories.get(uuid).unregister();
                                }
                                GUIManager.openTrailEditor(gp);
                            }
                        }

                        if (e.getRawSlot() == 26 || e.getRawSlot() == 35) {
                            nextPage(current);
                            gp.p.playSound(gp.p.getLocation(), Sound.ITEM_BOOK_PAGE_TURN, 50, 50);
                        }
                        if (e.getRawSlot() == 18 || e.getRawSlot() == 27) {
                            backPage(current);
                            gp.p.playSound(gp.p.getLocation(), Sound.ITEM_BOOK_PAGE_TURN, 50, 50);
                        }

                        //Debugger.print((e.getRawSlot() * (current.pageNumber + 1)) - 10 + "");
                        if (current.display.containsKey(e.getRawSlot())) {

                            DisplayItem a = current.display.get(e.getRawSlot());

                            switch (e.getClick()) {
                                case DROP:
                                    current.deleteTrail(gp.p, a);
                                    break;
                                case LEFT:
                                    if (current.cat == Enum.Categories.BOWPARTICLE) {
                                        current.changeIcon(gp.p, a);
                                    } else {
                                        gp.sendMessage(LanguageManager.getPrefix() + "&fThis trail isnt compatible with changing the icon.");
                                    }
                                    break;
                                case RIGHT:
                                    current.changeName(gp.p, a);
                                    break;
                                case SHIFT_RIGHT:
                                    current.changePermission(gp.p, a);
                                    break;
                                case SHIFT_LEFT:
                                    current.changePrice(gp.p, a);
                                    break;
                                default:
                                    gp.p.playSound(gp.p.getLocation(), Sound.ENTITY_VILLAGER_HURT, 50, 50);
                                    break;
                            }
                        }
                    }
                }
            }

            private void backPage(TrailEditor asd) {
                if (asd.p.exists(asd.pageNumber - 1)) {
                    asd.pageNumber--;
                }

                asd.fillChest(asd.p.getPage(asd.pageNumber), asd.disableMenu);
            }

            private void nextPage(TrailEditor asd) {
                if (asd.p.exists(asd.pageNumber + 1)) {
                    asd.pageNumber++;
                }

                asd.fillChest(asd.p.getPage(asd.pageNumber), asd.disableMenu);
            }

            @EventHandler
            public void onClose(InventoryCloseEvent e) {
                if (e.getPlayer() instanceof Player) {
                    if (e.getInventory() == null) {
                        return;
                    }
                    Player p = (Player) e.getPlayer();
                    UUID uuid = p.getUniqueId();
                    if (inventories.containsKey(uuid)) {
                        inventories.get(uuid).unregister();

                    }
                }
            }
        };
    }

    public void fillChest(List<DisplayItem> items, Boolean b) {

        inv.clear();

        for (int i = 0; i < 9; i++) {
            inv.setItem(i, placeholder);
        }

        display.clear();

        inv.setItem(45, placeholder);
        inv.setItem(46, placeholder);
        inv.setItem(47, placeholder);
        inv.setItem(48, placeholder);
        inv.setItem(49, placeholder);
        inv.setItem(50, placeholder);
        inv.setItem(51, placeholder);
        inv.setItem(52, placeholder);
        inv.setItem(53, placeholder);
        inv.setItem(36, placeholder);
        inv.setItem(44, placeholder);
        inv.setItem(9, placeholder);
        inv.setItem(17, placeholder);

        inv.setItem(18, back);
        inv.setItem(27, back);
        inv.setItem(26, next);
        inv.setItem(35, next);

        if (!b) {
            inv.setItem(49, menu);
        } else {
            inv.setItem(49, placeholder);
        }

        int slot = 0;
        for (ItemStack i : inv.getContents()) {
            if (i == null) {
                if (items.size() != 0) {
                    DisplayItem s = items.get(0);
                    inv.setItem(slot, Itens.createItemLore(s.i.getType(), 1, s.name + Text.addColor(" &7| &b" + s.price + " &fcoins"), Arrays.asList("&fPermission: &b" + s.permission, "&fParticle: &b" + s.getInfo("Particle"), "", "&fPress(Q) to Delete this Trail", "&fLeft Click to Change the Trail ICON", "&fRight Click to Change the Trail Name", "&fShift-Click Right to Change the Trail Permission", "&fShift-Click Left to Change the Trail Price.")));
                    display.put(slot, s);
                    items.remove(0);
                }
            }
            slot++;
        }
    }

    public void openInventory(Player player) {
        Inventory inv = getInventory();
        InventoryView openInv = player.getOpenInventory();
        if (openInv != null) {
            Inventory openTop = player.getOpenInventory().getTopInventory();
            if (openTop != null && openTop.getType().name().equalsIgnoreCase(inv.getType().name())) {
                openTop.setContents(inv.getContents());
            } else {
                player.openInventory(inv);
            }
        }
    }

    private void changePrice(Player p, DisplayItem id) {
        switch (cat) {
            case BOWPARTICLE:
                String acbw = "Main-Shop.Bow-Particles";
                new PlayerInput(p, input -> {
                    int pric;
                    try {
                        pric = Integer.parseInt(input);
                    } catch (NumberFormatException ex) {
                        p.sendMessage(LanguageManager.getPrefix() + Text.addColor("&cThats not a number. &fType in a number."));
                        changePrice(p, id);
                        return;
                    }

                    List<String> list = Shops.file().getStringList(acbw);
                    String trail = list.get(id.id);
                    String[] str = trail.split(">");


                    String newTrail = str[0] + ">" + pric + ">" + str[2] + ">" + str[3] + ">" + str[4];
                    list.remove(id.id);
                    list.add(id.id, newTrail);
                    Shops.file().set(acbw, list);
                    Shops.save();
                    p.sendMessage(LanguageManager.getPrefix() + "Price changed to §a" + pric);

                    TrailEditor v = new TrailEditor(p, cat, in);
                    v.openInventory(p);
                }, input -> {
                    TrailEditor v = new TrailEditor(p, cat, in);
                    v.openInventory(p);
                });
                break;
            case WINBLOCKS:
                String acwb = "Main-Shop.Bow-Particles";
                new PlayerInput(p, input -> {
                    int pric;
                    try {
                        pric = Integer.parseInt(input);
                    } catch (NumberFormatException ex) {
                        p.sendMessage(LanguageManager.getPrefix() + Text.addColor("&cThats not a number. &fType in a number."));
                        changePrice(p, id);
                        return;
                    }

                    List<String> list = Shops.file().getStringList(acwb);
                    String trail = list.get(id.id);
                    String[] str = trail.split(">");


                    String newTrail = str[0] + ">" + pric + ">" + str[2] + ">" + str[3] + ">" + str[4];
                    list.remove(id.id);
                    list.add(id.id, newTrail);
                    Shops.file().set(acwb, list);
                    Shops.save();
                    p.sendMessage(LanguageManager.getPrefix() + "Price changed to §a" + pric);

                    TrailEditor v = new TrailEditor(p, cat, in);
                    v.openInventory(p);
                }, input -> {
                    TrailEditor v = new TrailEditor(p, cat, in);
                    v.openInventory(p);
                });
                break;
        }
    }

    private void changePermission(Player p, DisplayItem id) {
        switch (cat) {
            case BOWPARTICLE:
                String acessb = "Main-Shop.Bow-Particles";

                new PlayerInput(p, input -> {
                    List<String> list = Shops.file().getStringList(acessb);
                    String trail = list.get(id.id);
                    String[] str = trail.split(">");
                    String newTrail = str[0] + ">" + str[1] + ">" + str[2] + ">" + input + ">" + str[4];
                    list.remove(id.id);
                    list.add(id.id, newTrail);
                    Shops.file().set(acessb, list);
                    Shops.save();
                    p.sendMessage(LanguageManager.getPrefix() + "Permission changed to §a" + input);

                    TrailEditor v = new TrailEditor(p, cat, in);
                    v.openInventory(p);
                }, input -> {
                    TrailEditor v = new TrailEditor(p, cat, in);
                    v.openInventory(p);
                });
                break;
            case WINBLOCKS:
                String acessw = "Main-Shop.Win-Blocks";

                new PlayerInput(p, input -> {
                    List<String> list = Shops.file().getStringList(acessw);
                    String trail = list.get(id.id);
                    String[] str = trail.split(">");
                    String newTrail = str[0] + ">" + str[1] + ">" + str[2] + ">" + input;
                    list.remove(id.id);
                    list.add(id.id, newTrail);
                    Shops.file().set(acessw, list);
                    Shops.save();
                    p.sendMessage(LanguageManager.getPrefix() + "Permission changed to §a" + input);

                    TrailEditor v = new TrailEditor(p, cat, in);
                    v.openInventory(p);
                }, input -> {
                    TrailEditor v = new TrailEditor(p, cat, in);
                    v.openInventory(p);
                });
                break;
        }
    }

    private void changeIcon(Player p, DisplayItem a) {
        p.closeInventory();
        Bukkit.getScheduler().scheduleSyncDelayedTask(RealSkywars.pl, () -> {
            MaterialPicker mp = new MaterialPicker(a.id, p, MaterialPicker.PickType.ALL, MaterialPicker.PickCategory.TRAIL_ICON, cat, in);
            mp.openInventory(p);
        }, 2);
    }

    private void deleteTrail(Player p, DisplayItem id) {
        switch (cat) {
            case BOWPARTICLE:
                new PlayerInput(p, input -> {
                    if (input.equalsIgnoreCase("confirm")) {
                        List<String> list = Shops.file().getStringList("Main-Shop.Bow-Particles");
                        list.remove(id.id);
                        Shops.file().set("Main-Shop.Bow-Particles", list);
                        Shops.save();
                        p.sendMessage(LanguageManager.getPrefix() + Text.addColor("&fTrail " + id.name + " &adeleted."));
                        TrailEditor v = new TrailEditor(p, cat, in);
                        v.openInventory(p);
                    } else {
                        deleteTrail(p, id);
                        p.sendMessage(LanguageManager.getPrefix() + Text.addColor("&fType &aConfirm &for &cCancel"));
                    }
                }, input -> {
                    TrailEditor v = new TrailEditor(p, cat, in);
                    v.openInventory(p);
                }, "&l&9Type &aConfirm &9to delete.", "&fType &4Cancel &fto cancel");
                break;
            case WINBLOCKS:
                new PlayerInput(p, input -> {
                    if (input.equalsIgnoreCase("confirm")) {
                        List<String> list = Shops.file().getStringList("Main-Shop.Win-Blocks");
                        list.remove(id.id);
                        Shops.file().set("Main-Shop.Win-Blocks", list);
                        Shops.save();
                        p.sendMessage(LanguageManager.getPrefix() + Text.addColor("&fTrail " + id.name + " &adeleted."));
                        TrailEditor v = new TrailEditor(p, cat, in);
                        v.openInventory(p);
                    } else {
                        deleteTrail(p, id);
                        p.sendMessage(LanguageManager.getPrefix() + Text.addColor("&fType &aConfirm &for &cCancel"));
                    }
                }, input -> {
                    TrailEditor v = new TrailEditor(p, cat, in);
                    v.openInventory(p);
                }, "&l&9Type &aConfirm &9to delete.", "&fType &4Cancel &fto cancel");
                break;
        }
    }

    private void changeName(Player p, DisplayItem id) {
        new PlayerInput(p, input -> {
            List<String> list = Shops.file().getStringList("Main-Shop.Bow-Particles");
            String trail = list.get(id.id);
            String[] str = trail.split(">");
            String newTrail = str[0] + ">" + str[1] + ">" + input + ">" + str[3] + ">" + str[4];
            list.remove(id.id);
            list.add(id.id, newTrail);
            Shops.file().set("Main-Shop.Bow-Particles", list);
            Shops.save();
            p.sendMessage(LanguageManager.getPrefix() + "Name changed to §a" + input);

            TrailEditor v = new TrailEditor(p, cat, in);
            v.openInventory(p);
        }, input -> {
            TrailEditor v = new TrailEditor(p, cat, in);
            v.openInventory(p);
        });
    }

    public Inventory getInventory() {
        return inv;
    }

    private void register() {
        inventories.put(this.uuid, this);
    }

    private void unregister() {
        inventories.remove(this.uuid);
    }
}
