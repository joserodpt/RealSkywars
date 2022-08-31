package josegamerpt.realskywars.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.*;
import java.util.stream.IntStream;

public class Itens {

    public static void shrink(ArrayList<ItemStack> list, int newSize) {
        int size = list.size();
        if (newSize >= size)
            return;
        for (int i = newSize; i < size; ++i) {
            list.remove(list.size() - 1);
        }
    }

    public static List<ItemStack> getInventory(Player p)
    {
        return Arrays.asList(IntStream.range(9, 35).boxed().map(p.getInventory()::getItem).filter(Objects::nonNull).toArray(ItemStack[]::new));
    }

    public static ItemStack getHead(Player player, int quantidade, String name) {
        ItemStack item = new ItemStack(Material.PLAYER_HEAD, quantidade);
        SkullMeta skull = (SkullMeta) item.getItemMeta();
        skull.setDisplayName(Text.color(name));
        ArrayList<String> lore = new ArrayList<>();
        skull.setLore(lore);
        skull.setOwningPlayer(Bukkit.getServer().getPlayer(player.getName()));
        item.setItemMeta(skull);
        return item;
    }

    public static ItemStack addLore(ItemStack i, List<String> lor) {
        if (i != null) {
            ItemStack is = i.clone();
            ItemMeta meta;
            if (!is.hasItemMeta()) {
                meta = Bukkit.getItemFactory().getItemMeta(is.getType());
            } else {
                meta = is.getItemMeta();
            }

            List<String> lore;
            if (!meta.hasLore()) {
                lore = new ArrayList<>();
            } else {
                lore = meta.getLore();
            }
            lore.add("§9");
            lore.addAll(Text.color(lor));
            meta.setLore(lore);
            is.setItemMeta(meta);
            return is;
        } else {
            return null;
        }
    }

    public static ItemStack setLore(ItemStack i, List<String> lor) {
        if (i != null) {
            ItemStack is = i.clone();
            ItemMeta meta;
            if (!is.hasItemMeta()) {
                meta = Bukkit.getItemFactory().getItemMeta(is.getType());
            } else {
                meta = is.getItemMeta();
            }

            List<String> lore;
            lore = new ArrayList<>();

            lore.add("§9");
            lore.addAll(Text.color(lor));
            assert meta != null;
            meta.setLore(lore);
            is.setItemMeta(meta);
            return is;
        } else {
            return null;
        }
    }

    public static ItemStack createItem(Material material, int quantidade, String nome) {
        ItemStack item = new ItemStack(material, quantidade);
        ItemMeta meta = item.getItemMeta();
        if (nome != null) {
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', nome));
        }
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack createItemLore(Material material, int quantidade, String nome, List<String> desc) {
        ItemStack item = new ItemStack(material, quantidade);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', nome));
        meta.setLore(Text.color(desc));
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack removeLore(ItemStack item, String string) {
        ItemStack i = item.clone();
        ItemMeta meta = i.getItemMeta();
        meta.setLore(Collections.emptyList());
        return i;
    }

    public static ItemStack createItemLoreEnchanted(Material m, int i, String name, List<String> desc) {
        ItemStack item = new ItemStack(m, i);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
        meta.setLore(Text.color(desc));
        meta.addEnchant(Enchantment.LUCK, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        item.setItemMeta(meta);
        return item;
    }
}
