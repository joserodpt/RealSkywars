package josegamerpt.realskywars.utils;

/*
 * Copyright (c) José Rodrigues 2023. All rights reserved.
 * Wiki Reference: https://www.spigotmc.org/wiki/itemstack-serialization/
 */

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import josegamerpt.realskywars.Debugger;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.block.banner.Pattern;
import org.bukkit.enchantments.Enchantment;
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
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

class ItemStackWrapper {

    private int slot = -1;
    private ItemStack i;
    private HashMap<String, Object> data;

    //wrap item, save item without slot
    public ItemStackWrapper(ItemStack i) {
        this.i = i;
    }

    //wrap item, save item in slot
    public ItemStackWrapper(int slot, ItemStack i) {
        this.slot = slot;
        this.i = i;
    }

    //unwrap item
    public ItemStackWrapper(HashMap<String, Object> stringObjectHashMap) {
        this.data = stringObjectHashMap;
        if (this.data.containsKey(ItemCategories.SLOT.name())) {
            this.slot = (int) this.data.get(ItemCategories.SLOT.name());
        }
    }

    public Map<String, Object> getItemSerialized() {
        Map<String, Object> singleItem = new HashMap<>();
        if (this.i == null) {
            //null items are empty items
            singleItem.put(ItemStackWrapper.ItemCategories.EMPTY.name(), ItemStackWrapper.ItemCategories.EMPTY.name());
            return singleItem;
        }

        if (this.slot != -1) {
            //save item slot
            singleItem.put(ItemStackWrapper.ItemCategories.SLOT.name(), this.slot);
        }

        //save item durability, if it has any
        if (this.i.getItemMeta() instanceof Damageable) {
            Damageable damageable = (Damageable) this.i.getItemMeta();
            if (damageable.hasDamage()) {
                int damage = damageable.getDamage();
                singleItem.put(ItemStackWrapper.ItemCategories.DAMAGE.name(), damage);
            }
        }

        //save display name
        if (this.i.hasItemMeta() && this.i.getItemMeta().hasDisplayName()) {
            singleItem.put(ItemStackWrapper.ItemCategories.NAME.name(), this.i.getItemMeta().getDisplayName());
        }

        //save item's lore
        if (this.i.hasItemMeta() && this.i.getItemMeta().hasLore()) {
            singleItem.put(ItemStackWrapper.ItemCategories.LORE.name(), this.i.getItemMeta().getLore());
        }

        //save item's enchants
        if (this.i.hasItemMeta() && this.i.getItemMeta().hasEnchants()) {
            singleItem.put(ItemStackWrapper.ItemCategories.ENCHANTMENTS.name(), this.i.getEnchantments().entrySet().stream()
                    .map(entry -> entry.getKey().getKey().getKey() + ":" + entry.getValue())
                    .collect(Collectors.joining(";")));
        }

        //Leather Armor Items
        if (this.i.getType().name().contains("LEATHER_")) {
            LeatherArmorMeta leatherArmorMeta = (LeatherArmorMeta) this.i.getItemMeta();
            org.bukkit.Color armorColor = leatherArmorMeta.getColor();
            singleItem.put(ItemStackWrapper.ItemCategories.LEATHER_ARMOR_COLOR.name(), armorColor.asRGB());
        }

        //Banners
        if (this.i.getType().name().contains("_BANNER")) {
            BannerMeta bannerMeta = (BannerMeta) this.i.getItemMeta();

            if (!bannerMeta.getPatterns().isEmpty()) {
                List<Map<String, Object>> patternsSave = bannerMeta.getPatterns().stream()
                        .map(Pattern::serialize)
                        .collect(Collectors.toList());

                singleItem.put(ItemStackWrapper.ItemCategories.BANNER_PATTERNS.name(), patternsSave);
            }
        }

        //Writtable and Written Book
        if (this.i.getType() == Material.WRITABLE_BOOK || this.i.getType() == Material.WRITTEN_BOOK) {
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

                singleItem.put(ItemStackWrapper.ItemCategories.BOOK_DATA.name(), book_data);
            }
        }

        //Enchanted books
        if (this.i.getType() == Material.ENCHANTED_BOOK) {
            EnchantmentStorageMeta meta = (EnchantmentStorageMeta) i.getItemMeta();

            singleItem.put(ItemStackWrapper.ItemCategories.BOOK_ENCHANTMENTS.name(), meta.getStoredEnchants().entrySet().stream()
                    .map(entry -> entry.getKey().getKey().getKey() + ":" + entry.getValue())
                    .collect(Collectors.joining(";")));
        }

        //Firework
        if (this.i.getType() == Material.FIREWORK_ROCKET) {
            FireworkMeta fireworkMeta = (FireworkMeta) i.getItemMeta();

            if (fireworkMeta != null) {
                Map<String, Object> firework_data = new HashMap<>();

                firework_data.put("power",fireworkMeta.getPower());

                // Get the list of firework effects
                if (fireworkMeta.hasEffects()) {
                    List<Map<String, Object>> effects_serialized = fireworkMeta.getEffects().stream()
                            .map(this::serializeFirework)
                            .collect(Collectors.toList());

                    firework_data.put("effects", effects_serialized);
                }

                singleItem.put(ItemStackWrapper.ItemCategories.FIREWORK_DATA.name(),firework_data);
            }
        }

        //Potion
        if (this.i.getType() == Material.POTION || this.i.getType() == Material.LINGERING_POTION || this.i.getType() == Material.SPLASH_POTION) {
            PotionMeta potionMeta = (PotionMeta) i.getItemMeta();

            if (potionMeta != null) {
                singleItem.put(ItemStackWrapper.ItemCategories.POTION_DATA.name(), serializePotionData(potionMeta.getBasePotionData()));
            }
        }

        //save item material and amount
        singleItem.put(ItemStackWrapper.ItemCategories.MATERIAL.name(), this.i.getType().name());
        singleItem.put(ItemStackWrapper.ItemCategories.AMOUNT.name(), this.i.getAmount());

        return singleItem;
    }

    public Map<String, Object> serializePotionData(PotionData potionData) {
        return ImmutableMap.of(
                "type", potionData.getType().name(),
                "extended", potionData.isExtended(),
                "upgraded", potionData.isUpgraded()
        );
    }

    public PotionData deserializePotionData(Map<String, Object> data) {
        if (data.containsKey("type") && data.containsKey("extended") && data.containsKey("upgraded")) {
            PotionType type = PotionType.valueOf((String) data.get("type"));
            boolean extended = (boolean) data.get("extended");
            boolean upgraded = (boolean) data.get("upgraded");
            return new PotionData(type, extended, upgraded);
        }
        return null;
    }

    public ItemStack getItemDeSerialized() {
        if (!data.containsKey(ItemCategories.MATERIAL.name())) {
            return null;
        }

        Debugger.print(ItemStackSpringer.class, "Attempting to deserialize Item Data of Material " + this.data.get(ItemStackWrapper.ItemCategories.MATERIAL.name()));

        Material m = Material.valueOf((String) popKey(this.data, ItemStackWrapper.ItemCategories.MATERIAL.name()));

        int amount;
        try {
            amount = (int) popKey(this.data, ItemStackWrapper.ItemCategories.AMOUNT.name());
        } catch (Exception ignored) {
            amount = 1;
        }
        ItemStack i = new ItemStack(m, amount);

        for (Map.Entry<String, Object> par : this.data.entrySet()) {
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
                i.setItemMeta(damageable);
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
                    Debugger.print(ItemStackSpringer.class, "Trying to apply " + enchantmentName + " - " + enchantmentLevel);
                    if (enchantment != null) {
                        i.addUnsafeEnchantment(enchantment, enchantmentLevel);
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
                    Debugger.print(ItemStackSpringer.class, "Trying to apply " + enchantmentName + " - " + enchantmentLevel);
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
                            .map(this::deSerializeFirework)
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

        Debugger.print(ItemStackSpringer.class, "Item Deserialized: " + i + " in slot: " + this.slot);

        return i;
    }

    public int getSlot() {
        return this.slot;
    }

    private Map<String, Object> serializeFirework(FireworkEffect fireworkEffect) {
        //Reference from FireworkEffect.class
        //return ImmutableMap.of("flicker", this.flicker, "trail", this.trail, "colors", this.colors, "fade-colors", this.fadeColors, "type", this.type.name());
        return ImmutableMap.of(
                "flicker", fireworkEffect.hasFlicker(),
                "trail", fireworkEffect.hasTrail(),
                "colors", ImmutableList.copyOf(fireworkEffect.getColors()).stream().map(Color::asRGB).collect(Collectors.toList()),
                "fade-colors", ImmutableList.copyOf(fireworkEffect.getFadeColors()).stream().map(Color::asRGB).collect(Collectors.toList()),
                "type", fireworkEffect.getType().name()
        );
    }

    private FireworkEffect deSerializeFirework(Map<String, Object> stringObjectEntry) {
        return FireworkEffect.builder()
                .trail((Boolean) stringObjectEntry.get("trail"))
                .flicker((Boolean) stringObjectEntry.get("flicker"))
                .with(FireworkEffect.Type.valueOf((String) stringObjectEntry.get("type")))
                .withFade(((List<Integer>) stringObjectEntry.get("fade-colors")).stream().map(Color::fromRGB).collect(Collectors.toList()))
                .withColor(((List<Integer>) stringObjectEntry.get("colors")).stream().map(Color::fromRGB).collect(Collectors.toList()))
                .build();
    }

    public Enchantment getEnchantmentByName(String name) {
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
            case "soul_speed":
                return Enchantment.SOUL_SPEED;
            default:
                Bukkit.getLogger().severe(name + " isn't a known Enchantment (is this a bug?) Skipping this enchant.");
                return null;
        }
    }

    public static <K, V> V popKey(HashMap<K, V> map, K key) {
        V value = map.get(key);
        if (value != null) {
            map.remove(key);
        }
        return value;
    }

    @Override
    public String toString() {
        return "ItemStackWrapper{" +
                "slot=" + slot +
                ", i=" + i +
                '}';
    }

    public enum ItemCategories { SLOT, NAME, MATERIAL, AMOUNT, DAMAGE, LORE, ENCHANTMENTS, EMPTY,
        LEATHER_ARMOR_COLOR, BANNER_PATTERNS, BOOK_DATA, BOOK_ENCHANTMENTS, FIREWORK_DATA, POTION_DATA }
}

public class ItemStackSpringer {

    //disable slot saving with -1
    public static ItemStack[] getItemsDeSerialized(List<HashMap<String, Object>> l) {
        List<ItemStackWrapper> wrapped = l.stream()
                .filter(Objects::nonNull)
                .map(ItemStackWrapper::new)
                .collect(Collectors.toList());

        Optional<ItemStackWrapper> maxSlotItem = wrapped.stream()
                .max(Comparator.comparingInt(ItemStackWrapper::getSlot));

        return maxSlotItem.map(maxSLOT -> {
            ItemStack[] arr;

            if (maxSLOT.getSlot() == -1) {
                arr = wrapped.stream()
                        .map(ItemStackWrapper::getItemDeSerialized)
                        .toArray(ItemStack[]::new);
            } else {
                arr = new ItemStack[maxSLOT.getSlot() + 1];

                wrapped.forEach(itemStackWrapper1 -> arr[itemStackWrapper1.getSlot()] = itemStackWrapper1.getItemDeSerialized());
            }

            return arr;
        }).orElse(null);
    }

    public static List<Map<String, Object>> getItemsSerialized(ItemStack[] l) {
        return IntStream.range(0, l.length)
                .filter(i -> l[i] != null)
                .mapToObj(i -> new ItemStackWrapper(i, l[i]))
                .map(ItemStackWrapper::getItemSerialized)
                .collect(Collectors.toList());
    }
}
