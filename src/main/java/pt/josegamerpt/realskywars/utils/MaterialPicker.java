package pt.josegamerpt.realskywars.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
import pt.josegamerpt.realskywars.classes.Enum;
import pt.josegamerpt.realskywars.classes.Trail;
import pt.josegamerpt.realskywars.configuration.Shops;
import pt.josegamerpt.realskywars.gui.TrailEditor;

public class MaterialPicker {

    static ItemStack placeholder = Itens.createItem(Material.BLACK_STAINED_GLASS_PANE, 1, "");
    static ItemStack next = Itens.createItemLore(Material.GREEN_STAINED_GLASS, 1, "&aNext",
            Arrays.asList("&fClick here to go to the next page."));
    static ItemStack back = Itens.createItemLore(Material.YELLOW_STAINED_GLASS, 1, "&6Back",
            Arrays.asList("&fClick here to go back to the next page."));
    static ItemStack close = Itens.createItemLore(Material.ACACIA_DOOR, 1, "&cGo Back",
            Arrays.asList("&fClick here to go back."));
    static ItemStack search = Itens.createItemLore(Material.OAK_SIGN, 1, "&9Search",
            Arrays.asList("&fClick here to search for a material."));
    private static Map<UUID, MaterialPicker> inventories = new HashMap<>();
    public int trailID;
    int pageNumber = 0;
    Pagination<Material> p;
    private PickCategory pc;
    private Inventory inv;
    private UUID uuid;
    private ArrayList<Material> items;
    private HashMap<Integer, Material> display = new HashMap<Integer, Material>();
    private PickType pt;
    private Enum.Categories cat;
    private String invNam;

    public MaterialPicker(int id, Player pl, PickType block, PickCategory c, Enum.Categories ca, String i) {
        this.uuid = pl.getUniqueId();
        this.trailID = id;
        this.pt = block;
        this.pc = c;
        this.cat = ca;
        this.invNam = i;

        inv = Bukkit.getServer().createInventory(null, 54, Text.addColor("Pick a new Material"));

        items = getIcons();

        p = new Pagination<Material>(28, items);
        fillChest(p.getPage(pageNumber));

        this.register();
    }

    public MaterialPicker(int id, Player pl, PickType block, PickCategory c, Enum.Categories ca, String i, String search) {
        this.uuid = pl.getUniqueId();
        this.trailID = id;
        this.pt = block;
        this.pc = c;
        this.cat = ca;
        this.invNam = i;

        inv = Bukkit.getServer().createInventory(null, 54, Text.addColor("Pick a new Material"));

        items = searchMaterial(search);

        p = new Pagination<Material>(28, items);
        fillChest(p.getPage(pageNumber));

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
                        MaterialPicker current = inventories.get(uuid);
                        if (e.getInventory().getHolder() != current.getInventory().getHolder()) {
                            return;
                        }

                        e.setCancelled(true);

                        Player p = (Player) clicker;
                        if (e.getRawSlot() == 4) {
                            new PlayerInput(p, new PlayerInput.InputRunnable() {
                                public void run(String input) {
                                    if (current.searchMaterial(input).size() == 0) {
                                        p.sendMessage("Nothing found for your results.");

                                        current.exit(p);
                                        return;
                                    } else {
                                        MaterialPicker df = new MaterialPicker(current.trailID, p, current.pt, current.pc, current.cat, current.invNam, input);
                                        df.openInventory(p);
                                    }
                                }
                            }, new PlayerInput.InputRunnable() {
                                public void run(String input) {
                                    MaterialPicker df = new MaterialPicker(current.trailID, p, current.pt, current.pc, current.cat, current.invNam);
                                    df.openInventory(p);
                                }
                            });
                        }

                        if (e.getRawSlot() == 49) {
                            current.exit(p);
                        }

                        if (e.getRawSlot() == 26 || e.getRawSlot() == 35) {
                            nextPage(current);
                            p.playSound(p.getLocation(), Sound.ITEM_BOOK_PAGE_TURN, 50, 50);
                        }
                        if (e.getRawSlot() == 18 || e.getRawSlot() == 27) {
                            backPage(current);
                            p.playSound(p.getLocation(), Sound.ITEM_BOOK_PAGE_TURN, 50, 50);
                        }

                        if (current.display.containsKey(e.getRawSlot())) {
                            Material a = current.display.get(e.getRawSlot());

                            switch (current.pc) {
                                case TRAIL_ICON:
                                    List<String> list = Shops.file().getStringList("Main-Shop.Bow-Particles");
                                    String trail = list.get(current.trailID);
                                    String[] str = trail.split(">");
                                    String newTrail = str[0] + ">" + str[1] + ">" + str[2] + ">" + str[3] + ">" + a.name();
                                    list.remove(current.trailID);
                                    list.add(current.trailID, newTrail);
                                    Shops.file().set("Main-Shop.Bow-Particles", list);
                                    Shops.save();
                                    p.sendMessage("Changed to §a" + a.name());
                                    p.closeInventory();

                                    Bukkit.getScheduler().scheduleSyncDelayedTask(RealSkywars.pl, new Runnable() {
                                        public void run() {
                                            TrailEditor v = new TrailEditor(p, current.cat, current.invNam);
                                            v.openInventory(p);
                                        }
                                    }, 10);

                                    break;
                            }
                        }
                    }
                }
            }

            private void backPage(MaterialPicker asd) {
                if (asd.p.exists(asd.pageNumber - 1)) {
                    asd.pageNumber--;
                }

                asd.fillChest(asd.p.getPage(asd.pageNumber));
            }

            private void nextPage(MaterialPicker asd) {
                if (asd.p.exists(asd.pageNumber + 1)) {
                    asd.pageNumber++;
                }

                asd.fillChest(asd.p.getPage(asd.pageNumber));
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

    private ArrayList<Material> getIcons() {
        ArrayList<Material> ms = new ArrayList<Material>();
        switch (pt) {
            case ALL:
                for (Material m : Material.values()) {
                    if (!m.equals(Material.AIR)) {
                        ms.add(m);
                    }
                }
                break;
            case ITEM:
                for (Material m : Material.values()) {
                    if (!m.equals(Material.AIR) && m.isSolid() && m.isBlock() && m.isItem()) {
                        ms.add(m);
                    }
                }
                break;
            case BLOCK:
                for (Material m : Material.values()) {
                    if (!m.equals(Material.AIR) && m.isSolid() && m.isBlock() && m.isItem()) {
                        ms.add(m);
                    }
                }
                break;
            default:
                break;
        }
        return ms;
    }

    private ArrayList<Material> searchMaterial(String s) {
        ArrayList<Material> ms = new ArrayList<Material>();
        for (Material m : getIcons()) {
            if (m.name().toLowerCase().contains(s.toLowerCase())) {
                ms.add(m);
            }
        }
        return ms;
    }

    public void fillChest(List<Material> items) {

        inv.clear();
        display.clear();

        for (int i = 0; i < 9; i++) {
            inv.setItem(i, placeholder);
        }

        inv.setItem(4, search);

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

        int slot = 0;
        for (ItemStack i : inv.getContents()) {
            if (i == null) {
                if (items.size() != 0) {
                    Material s = items.get(0);
                    inv.setItem(slot,
                            Itens.createItemLore(s, 1, "§9" + s.name(), Arrays.asList("&fClick to pick this.")));
                    display.put(slot, s);
                    items.remove(0);
                }
            }
            slot++;
        }

        inv.setItem(49, close);
    }

    public void openInventory(Player target) {
        Inventory inv = getInventory();
        InventoryView openInv = target.getOpenInventory();
        if (openInv != null) {
            Inventory openTop = target.getOpenInventory().getTopInventory();
            if (openTop != null && openTop.getType().name().equalsIgnoreCase(inv.getType().name())) {
                openTop.setContents(inv.getContents());
            } else {
                target.openInventory(inv);
            }
        }
    }

    private void exit(Player p) {
        switch (this.pc) {
            case TRAIL_ICON:
                p.closeInventory();
                TrailEditor t = new TrailEditor(p, this.cat, this.invNam);
                t.openInventory(p);
                break;

        }
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

    public enum PickType {
        BLOCK, ITEM, ALL;
    }

    public enum PickCategory {
        TRAIL_ICON
    }
}