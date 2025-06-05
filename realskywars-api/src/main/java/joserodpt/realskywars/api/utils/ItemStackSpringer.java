package joserodpt.realskywars.api.utils;

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
 * Wiki Reference: https://www.spigotmc.org/wiki/itemstack-serialization/
 */

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import joserodpt.realskywars.api.RealSkywarsAPI;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.block.banner.Pattern;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ItemStackSpringer {

    private static final Gson gson = new Gson();

    public static String getItemSerializedJSON(ItemStack i) {
        return gson.toJson(getItemSerialized(i));
    }

    public static ItemStack getItemDeSerializedJSON(String s) {
        return getItemDeSerialized(gson.fromJson(s, HashMap.class));
    }

    public static Map<String, Object> getItemSerialized(ItemStack i) {
        return getItemSerialized(-1, i);
    }

    public static Map<String, Object> getItemSerialized(int slot, ItemStack i) {
        Map<String, Object> singleItem = new HashMap<>();
        if (i == null) {
            //null items are empty items
            singleItem.put(ItemCategories.EMPTY.name(), ItemCategories.EMPTY.name());
            return singleItem;
        }

        if (slot != -1) {
            //save item slot
            singleItem.put(ItemCategories.SLOT.name(), slot);
        }

        //save item durability, if it has any
        if (i.getItemMeta() instanceof Damageable) {
            Damageable damageable = (Damageable) i.getItemMeta();
            if (damageable.hasDamage()) {
                int damage = damageable.getDamage();
                singleItem.put(ItemCategories.DAMAGE.name(), damage);
            }
        }

        //save display name
        if (i.hasItemMeta() && i.getItemMeta().hasDisplayName()) {
            singleItem.put(ItemCategories.NAME.name(), i.getItemMeta().getDisplayName());
        }

        //save item's lore
        if (i.hasItemMeta() && i.getItemMeta().hasLore()) {
            singleItem.put(ItemCategories.LORE.name(), i.getItemMeta().getLore());
        }

        //save item's enchants
        if (i.hasItemMeta() && i.getItemMeta().hasEnchants()) {
            singleItem.put(ItemCategories.ENCHANTMENTS.name(), i.getEnchantments().entrySet().stream()
                    .map(entry -> entry.getKey().getKey().getKey() + ":" + entry.getValue())
                    .collect(Collectors.joining(";")));
        }

        //save item's itemflags
        if (i.hasItemMeta()) {
            if (!i.getItemMeta().getItemFlags().isEmpty()) {
                singleItem.put(ItemCategories.ITEM_FLAGS.name(), i.getItemMeta().getItemFlags().stream()
                        .map(Enum::name)
                        .collect(Collectors.joining(";")));
            }

            if (i.getItemMeta().hasCustomModelData()) {
                singleItem.put(ItemCategories.CUSTOM_MODEL_DATA.name(), i.getItemMeta().getCustomModelData());
            }
        }

        //Leather Armor Items
        if (i.getType().name().contains("LEATHER_")) {
            LeatherArmorMeta leatherArmorMeta = (LeatherArmorMeta) i.getItemMeta();
            org.bukkit.Color armorColor = leatherArmorMeta.getColor();
            singleItem.put(ItemCategories.LEATHER_ARMOR_COLOR.name(), armorColor.asRGB());
        }

        //Banners
        if (i.getType().name().contains("_BANNER")) {
            BannerMeta bannerMeta = (BannerMeta) i.getItemMeta();

            if (!bannerMeta.getPatterns().isEmpty()) {
                List<Map<String, Object>> patternsSave = bannerMeta.getPatterns().stream()
                        .map(Pattern::serialize)
                        .collect(Collectors.toList());

                singleItem.put(ItemCategories.BANNER_PATTERNS.name(), patternsSave);
            }
        }

        //Writtable and Written Book
        if (i.getType() == Material.WRITABLE_BOOK || i.getType() == Material.WRITTEN_BOOK) {
            BookMeta bookMeta = (BookMeta) i.getItemMeta();
            if (bookMeta != null) {
                Map<String, Object> book_data = new HashMap<>();
                if (bookMeta.hasAuthor()) {
                    book_data.put("author", bookMeta.getAuthor());
                }
                if (bookMeta.hasTitle()) {
                    book_data.put("title", bookMeta.getTitle());
                }
                if (bookMeta.hasPages()) {
                    book_data.put("pages", bookMeta.getPages());
                }

                singleItem.put(ItemCategories.BOOK_DATA.name(), book_data);
            }
        }

        //Enchanted books
        if (i.getType() == Material.ENCHANTED_BOOK) {
            EnchantmentStorageMeta meta = (EnchantmentStorageMeta) i.getItemMeta();

            singleItem.put(ItemCategories.BOOK_ENCHANTMENTS.name(), meta.getStoredEnchants().entrySet().stream()
                    .map(entry -> entry.getKey().getKey().getKey() + ":" + entry.getValue())
                    .collect(Collectors.joining(";")));
        }

        //Firework
        if (i.getType() == Material.FIREWORK_ROCKET) {
            FireworkMeta fireworkMeta = (FireworkMeta) i.getItemMeta();

            if (fireworkMeta != null) {
                Map<String, Object> firework_data = new HashMap<>();

                firework_data.put("power", fireworkMeta.getPower());

                // Get the list of firework effects
                if (fireworkMeta.hasEffects()) {
                    List<Map<String, Object>> effects_serialized = fireworkMeta.getEffects().stream()
                            .map(ItemStackSpringer::serializeFirework)
                            .collect(Collectors.toList());

                    firework_data.put("effects", effects_serialized);
                }

                singleItem.put(ItemCategories.FIREWORK_DATA.name(), firework_data);
            }
        }

        //Potion
        if (i.getType() == Material.POTION || i.getType() == Material.LINGERING_POTION || i.getType() == Material.SPLASH_POTION) {
            PotionMeta potionMeta = (PotionMeta) i.getItemMeta();

            if (potionMeta != null) {
                singleItem.put(ItemCategories.POTION_DATA.name(), serializePotionData(potionMeta.getBasePotionData()));
            }
        }

        //save item material and amount
        singleItem.put(ItemCategories.MATERIAL.name(), i.getType().name());
        singleItem.put(ItemCategories.AMOUNT.name(), i.getAmount());

        return singleItem;
    }

    public static Map<String, Object> serializePotionData(PotionData potionData) {
        return ImmutableMap.of(
                "type", potionData.getType().name(),
                "extended", potionData.isExtended(),
                "upgraded", potionData.isUpgraded()
        );
    }

    public static PotionData deserializePotionData(Map<String, Object> data) {
        if (data.containsKey("type") && data.containsKey("extended") && data.containsKey("upgraded")) {
            PotionType type = PotionType.valueOf((String) data.get("type"));
            boolean extended = (boolean) data.get("extended");
            boolean upgraded = (boolean) data.get("upgraded");
            return new PotionData(type, extended, upgraded);
        }
        return null;
    }

    public static ItemStack getItemDeSerialized(Map<String, Object> data) {
        debugPrint(ItemStackSpringer.class, "Attempting to deserialize Item with Data " + data.toString());
        if (!data.containsKey(ItemCategories.MATERIAL.name())) {
            return null;
        }

        debugPrint(ItemStackSpringer.class, "Attempting to deserialize Item Data of Material " + data.get(ItemCategories.MATERIAL.name()));

        Material m = Material.valueOf((String) data.get(ItemCategories.MATERIAL.name()));

        int amount;
        try {
            amount = (int) data.get(ItemCategories.AMOUNT.name());
        } catch (Exception ignored) {
            amount = 1;
        }
        ItemStack i = new ItemStack(m, amount);

        for (Map.Entry<String, Object> par : data.entrySet()) {
            ItemMeta meta = i.getItemMeta();

            String key = par.getKey();
            Object value = par.getValue();

            if (ItemCategories.NAME.name().equals(key)) {
                meta.setDisplayName(Text.color((String) value));
                i.setItemMeta(meta);
            }
            if (ItemCategories.LORE.name().equals(key)) {
                meta.setLore(Text.color((List<String>) value));
                i.setItemMeta(meta);
            }
            if (ItemCategories.DAMAGE.name().equals(key)) {
                Damageable damageable = (Damageable) i.getItemMeta();
                int newDamageValue = (int) value;
                damageable.setDamage(newDamageValue);
                i.setItemMeta((ItemMeta) damageable);
            }
            if (ItemCategories.CUSTOM_MODEL_DATA.name().equals(key)) {
                meta.setCustomModelData((int) value);
                i.setItemMeta(meta);
            }
            if (ItemCategories.ENCHANTMENTS.name().equals(key)) {
                String[] enchantments = value.toString().split(";");
                for (String enchantmentEntry : enchantments) {
                    String[] parts = enchantmentEntry.split(":");
                    if (parts.length != 2) {
                        continue;
                    }

                    String enchantmentName = parts[0];
                    int enchantmentLevel;
                    try {
                        enchantmentLevel = Integer.parseInt(parts[1]);
                    } catch (NumberFormatException e) {
                        continue;
                    }

                    Enchantment enchantment = getEnchantmentByName(enchantmentName);
                    debugPrint(ItemStackSpringer.class, "Trying to apply " + enchantmentName + " - " + enchantmentLevel);
                    if (enchantment != null) {
                        i.addUnsafeEnchantment(enchantment, enchantmentLevel);
                    }
                }
            }
            if (ItemCategories.ITEM_FLAGS.name().equals(key)) {
                String[] flags = value.toString().split(";");
                for (String flagName : flags) {
                    try {
                        ItemFlag flag = ItemFlag.valueOf(flagName);
                        assert meta != null;
                        meta.addItemFlags(flag);
                        i.setItemMeta(meta);
                    } catch (IllegalArgumentException e) {
                        RealSkywarsAPI.getInstance().getPlugin().getLogger().severe(flagName + " isn't a known ItemFlag (is this a bug?) Skipping this flag.");
                    }
                }
            }
            if (ItemCategories.LEATHER_ARMOR_COLOR.name().equals(key)) {
                LeatherArmorMeta leatherArmorMeta = (LeatherArmorMeta) i.getItemMeta();
                leatherArmorMeta.setColor(Color.fromRGB((Integer) value));
                i.setItemMeta(leatherArmorMeta);
            }
            if (ItemCategories.BANNER_PATTERNS.name().equals(key)) {
                BannerMeta bannerMeta = (BannerMeta) i.getItemMeta();

                List<Map<String, Object>> patternData = (List<Map<String, Object>>) value;

                bannerMeta.setPatterns(patternData.stream()
                        .map(Pattern::new)
                        .collect(Collectors.toList()));

                i.setItemMeta(bannerMeta);
            }
            if (ItemCategories.BOOK_DATA.name().equals(key)) {
                BookMeta bookMeta = (BookMeta) i.getItemMeta();
                Map<String, Object> book_data = (Map<String, Object>) value;

                if (book_data.containsKey("author")) {
                    bookMeta.setAuthor((String) book_data.get("author"));
                }
                if (book_data.containsKey("title")) {
                    bookMeta.setTitle((String) book_data.get("title"));
                }
                if (book_data.containsKey("pages")) {
                    bookMeta.setPages((List<String>) book_data.get("pages"));
                }

                i.setItemMeta(bookMeta);
            }
            if (ItemCategories.BOOK_ENCHANTMENTS.name().equals(key)) {
                EnchantmentStorageMeta enchbookmeta = (EnchantmentStorageMeta) i.getItemMeta();

                String[] enchantments = value.toString().split(";");
                for (String enchantmentEntry : enchantments) {
                    String[] parts = enchantmentEntry.split(":");
                    if (parts.length != 2) {
                        continue;
                    }

                    String enchantmentName = parts[0];
                    int enchantmentLevel;
                    try {
                        enchantmentLevel = Integer.parseInt(parts[1]);
                    } catch (NumberFormatException e) {
                        continue;
                    }

                    Enchantment enchantment = getEnchantmentByName(enchantmentName);
                    debugPrint(ItemStackSpringer.class, "Trying to apply " + enchantmentName + " - " + enchantmentLevel);
                    if (enchantment != null) {
                        enchbookmeta.addStoredEnchant(enchantment, enchantmentLevel, true);
                    }
                }

                i.setItemMeta(enchbookmeta);
            }
            if (ItemCategories.FIREWORK_DATA.name().equals(key)) {
                FireworkMeta fireworkMeta = (FireworkMeta) i.getItemMeta();

                if (fireworkMeta != null) {
                    Map<String, Object> firework_data = (Map<String, Object>) value;

                    fireworkMeta.setPower((Integer) firework_data.get("power"));
                    fireworkMeta.addEffects(((List<HashMap<String, Object>>) firework_data.get("effects")).stream()
                            .map(ItemStackSpringer::deSerializeFirework)
                            .collect(Collectors.toList()));

                }

                i.setItemMeta(fireworkMeta);
            }
            if (ItemCategories.POTION_DATA.name().equals(key)) {
                PotionMeta potionMeta = (PotionMeta) i.getItemMeta();

                if (potionMeta != null) {
                    potionMeta.setBasePotionData(deserializePotionData((Map<String, Object>) value));
                }

                i.setItemMeta(potionMeta);
            }
        }

        debugPrint(ItemStackSpringer.class, "Item Deserialized: " + i);

        return i;
    }

    private static void debugPrint(Class<?> c, String s) {
        if (false)
            RealSkywarsAPI.getInstance().getPlugin().getLogger().warning(c.getName() + " -> " + s);
    }

    private static Map<String, Object> serializeFirework(FireworkEffect fireworkEffect) {
        //Reference from FireworkEffect.class
        //return ImmutableMap.of("flicker", flicker, "trail", trail, "colors", colors, "fade-colors", fadeColors, "type", type.name());
        return ImmutableMap.of(
                "flicker", fireworkEffect.hasFlicker(),
                "trail", fireworkEffect.hasTrail(),
                "colors", ImmutableList.copyOf(fireworkEffect.getColors()).stream().map(Color::asRGB).collect(Collectors.toList()),
                "fade-colors", ImmutableList.copyOf(fireworkEffect.getFadeColors()).stream().map(Color::asRGB).collect(Collectors.toList()),
                "type", fireworkEffect.getType().name()
        );
    }

    private static FireworkEffect deSerializeFirework(Map<String, Object> stringObjectEntry) {
        return FireworkEffect.builder()
                .trail((Boolean) stringObjectEntry.get("trail"))
                .flicker((Boolean) stringObjectEntry.get("flicker"))
                .with(FireworkEffect.Type.valueOf((String) stringObjectEntry.get("type")))
                .withFade(((List<Integer>) stringObjectEntry.get("fade-colors")).stream().map(Color::fromRGB).collect(Collectors.toList()))
                .withColor(((List<Integer>) stringObjectEntry.get("colors")).stream().map(Color::fromRGB).collect(Collectors.toList()))
                .build();
    }

    public static Enchantment getEnchantmentByName(String name) {
        switch (name.toLowerCase()) {
            case "protection":
                return Enchantment.PROTECTION_ENVIRONMENTAL;
            case "fire_protection":
                return Enchantment.PROTECTION_FIRE;
            case "feather_falling":
                return Enchantment.PROTECTION_FALL;
            case "blast_protection":
                return Enchantment.PROTECTION_EXPLOSIONS;
            case "projectile_protection":
                return Enchantment.PROTECTION_PROJECTILE;
            case "respiration":
                return Enchantment.OXYGEN;
            case "aqua_affinity":
                return Enchantment.WATER_WORKER;
            case "thorns":
                return Enchantment.THORNS;
            case "depth_strider":
                return Enchantment.DEPTH_STRIDER;
            case "frost_walker":
                return Enchantment.FROST_WALKER;
            case "binding_curse":
                return Enchantment.BINDING_CURSE;
            case "sharpness":
                return Enchantment.DAMAGE_ALL;
            case "smite":
                return Enchantment.DAMAGE_UNDEAD;
            case "bane_of_arthropods":
                return Enchantment.DAMAGE_ARTHROPODS;
            case "knockback":
                return Enchantment.KNOCKBACK;
            case "fire_aspect":
                return Enchantment.FIRE_ASPECT;
            case "looting":
                return Enchantment.LOOT_BONUS_MOBS;
            case "sweeping":
                return Enchantment.SWEEPING_EDGE;
            case "efficiency":
                return Enchantment.DIG_SPEED;
            case "silk_touch":
                return Enchantment.SILK_TOUCH;
            case "unbreaking":
                return Enchantment.DURABILITY;
            case "fortune":
                return Enchantment.LOOT_BONUS_BLOCKS;
            case "power":
                return Enchantment.ARROW_DAMAGE;
            case "punch":
                return Enchantment.ARROW_KNOCKBACK;
            case "flame":
                return Enchantment.ARROW_FIRE;
            case "infinity":
                return Enchantment.ARROW_INFINITE;
            case "luck_of_the_sea":
                return Enchantment.LUCK;
            case "lure":
                return Enchantment.LURE;
            case "loyalty":
                return Enchantment.LOYALTY;
            case "impaling":
                return Enchantment.IMPALING;
            case "riptide":
                return Enchantment.RIPTIDE;
            case "channeling":
                return Enchantment.CHANNELING;
            case "multishot":
                return Enchantment.MULTISHOT;
            case "quick_charge":
                return Enchantment.QUICK_CHARGE;
            case "piercing":
                return Enchantment.PIERCING;
            case "mending":
                return Enchantment.MENDING;
            case "vanishing_curse":
                return Enchantment.VANISHING_CURSE;
            default:
                RealSkywarsAPI.getInstance().getPlugin().getLogger().severe(name + " isn't a known Enchantment (is this a bug?) Skipping this enchant.");
                return null;
        }
    }

    public enum ItemCategories {
        SLOT, NAME, MATERIAL, AMOUNT, DAMAGE, LORE, ENCHANTMENTS, EMPTY, ITEM_FLAGS,
        LEATHER_ARMOR_COLOR, BANNER_PATTERNS, BOOK_DATA, BOOK_ENCHANTMENTS, FIREWORK_DATA, POTION_DATA, CUSTOM_MODEL_DATA
    }

    public static ItemStack[] getItemsDeSerialized(List<Map<String, Object>> l) {
        Optional<Map<String, Object>> maxSlotItem = l.stream()
                .max(Comparator.comparingInt(map -> map.containsKey("SLOT") ? ((int) map.get("SLOT")) : -1));

        return maxSlotItem.map(maxSLOT -> {
            ItemStack[] arr;

            int slot;
            if (maxSLOT.containsKey("SLOT")) {
                slot = (int) maxSLOT.get("SLOT");
            } else {
                slot = -1;
            }

            if (slot == -1) {
                arr = l.stream()
                        .map(ItemStackSpringer::getItemDeSerialized)
                        .toArray(ItemStack[]::new);
            } else {
                arr = new ItemStack[slot + 1];

                l.forEach(itemStackWrapper1 -> arr[(int) itemStackWrapper1.get("SLOT")] = getItemDeSerialized(itemStackWrapper1));
            }

            return arr;
        }).orElse(null);
    }

    public static List<Map<String, Object>> getItemsSerialized(ItemStack[] l) {
        return IntStream.range(0, l.length)
                .filter(i -> l[i] != null)
                .mapToObj(item -> getItemSerialized(l[item]))
                .collect(Collectors.toList());
    }
}