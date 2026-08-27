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
 */

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import joserodpt.realskywars.api.RealSkywarsAPI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class Itens {

    private static final String MOJANG_PROFILE_URL = "https://sessionserver.mojang.com/session/minecraft/profile/";
    private static final Map<UUID, String> TEXTURE_CACHE = new ConcurrentHashMap<>();

    /** Resolves an ItemsAdder item without a hard dependency on its API. */
    public static ItemStack itemsAdder(String id, int amount) {
        if (id == null || !Bukkit.getPluginManager().isPluginEnabled("ItemsAdder")) {
            return null;
        }

        try {
            Class<?> customStack = Class.forName("dev.lone.itemsadder.api.CustomStack");
            Object stack = customStack.getMethod("getInstance", String.class).invoke(null, id);
            if (stack == null) {
                return null;
            }

            ItemStack result = ((ItemStack) customStack.getMethod("getItemStack").invoke(stack)).clone();
            result.setAmount(Math.max(1, Math.min(result.getMaxStackSize(), amount)));
            return result;
        } catch (ReflectiveOperationException | LinkageError | ClassCastException e) {
            return null;
        }
    }

    /**
     * Applies a player's real Mojang skin to a head. setOwningPlayer alone does
     * not resolve a texture on offline-mode or proxied servers.
     * <p>
     * Only the cached profile can be applied here: Bukkit copies an ItemStack
     * when it is put into an inventory, so mutating this one after an async
     * fetch would change nothing. {@link #cachePlayerTexture(Player)} is called
     * on join so the profile is already cached by the time items are built.
     */
    public static void applyPlayerTexture(ItemStack item, Player player) {
        if (item == null || player == null || !(item.getItemMeta() instanceof SkullMeta)) {
            return;
        }

        String cached = TEXTURE_CACHE.get(player.getUniqueId());
        if (cached != null) {
            applyTexture(item, cached);
        } else {
            //not cached yet: warm it so the next head this player sees is correct
            cachePlayerTexture(player);
        }
    }

    /** Fetches and caches a player's Mojang profile off the main thread. */
    public static void cachePlayerTexture(Player player) {
        if (player == null || TEXTURE_CACHE.containsKey(player.getUniqueId())) {
            return;
        }

        UUID uuid = player.getUniqueId();
        Bukkit.getScheduler().runTaskAsynchronously(RealSkywarsAPI.getInstance().getPlugin(), () -> {
            String fetched = fetchTexture(uuid);
            if (fetched != null) {
                TEXTURE_CACHE.put(uuid, fetched);
            }
        });
    }

    /** Drops a cached profile, so a skin change is picked up on the next join. */
    public static void forgetPlayerTexture(UUID uuid) {
        TEXTURE_CACHE.remove(uuid);
    }

    /** Returns the base64 "textures" property of a profile, or null. */
    private static String fetchTexture(UUID uuid) {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(MOJANG_PROFILE_URL + uuid.toString().replace("-", "") + "?unsigned=false");
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(3000);
            connection.setReadTimeout(3000);

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return null;
            }

            String body = new String(readAll(connection), StandardCharsets.UTF_8);
            JsonObject profile = JsonParser.parseString(body).getAsJsonObject();
            if (!profile.has("properties")) {
                return null;
            }

            for (JsonElement element : profile.getAsJsonArray("properties")) {
                JsonObject property = element.getAsJsonObject();
                if ("textures".equals(property.get("name").getAsString())) {
                    return property.get("value").getAsString();
                }
            }
        } catch (Exception e) {
            //the Bukkit profile stays as the fallback when Mojang is unreachable
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return null;
    }

    private static byte[] readAll(HttpURLConnection connection) throws java.io.IOException {
        try (java.io.InputStream in = connection.getInputStream();
             java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream()) {
            byte[] buffer = new byte[8192];
            int read;
            while ((read = in.read(buffer)) > 0) {
                out.write(buffer, 0, read);
            }
            return out.toByteArray();
        }
    }

    private static void applyTexture(ItemStack item, String encodedTexture) {
        if (!(item.getItemMeta() instanceof SkullMeta)) {
            return;
        }

        SkullMeta skull = (SkullMeta) item.getItemMeta();

        //preferred path: the Bukkit profile API, available since 1.18.1
        String skinUrl = skinUrlOf(encodedTexture);
        if (skinUrl != null && applyWithProfileApi(item, skull, skinUrl)) {
            return;
        }

        //fallback for older servers: write the GameProfile into CraftMetaSkull
        try {
            Class<?> gameProfileClass = Class.forName("com.mojang.authlib.GameProfile");
            Class<?> propertyClass = Class.forName("com.mojang.authlib.properties.Property");

            Object profile = gameProfileClass.getConstructor(UUID.class, String.class).newInstance(UUID.randomUUID(), null);
            Object property = propertyClass.getConstructor(String.class, String.class).newInstance("textures", encodedTexture);
            Object properties = gameProfileClass.getMethod("getProperties").invoke(profile);
            properties.getClass().getMethod("put", Object.class, Object.class).invoke(properties, "textures", property);

            Field profileField = findField(skull.getClass(), "profile");
            profileField.setAccessible(true);
            profileField.set(skull, profile);
            item.setItemMeta(skull);
        } catch (ReflectiveOperationException | LinkageError | RuntimeException e) {
            //leave the head as-is rather than failing the item
        }
    }

    private static boolean applyWithProfileApi(ItemStack item, SkullMeta skull, String skinUrl) {
        try {
            Object profile = Bukkit.class.getMethod("createPlayerProfile", UUID.class, String.class)
                    .invoke(null, UUID.randomUUID(), "RSW");
            Object textures = profile.getClass().getMethod("getTextures").invoke(profile);
            textures.getClass().getMethod("setSkin", URL.class).invoke(textures, new URL(skinUrl));
            profile.getClass().getMethod("setTextures", Class.forName("org.bukkit.profile.PlayerTextures"))
                    .invoke(profile, textures);

            Method setOwnerProfile = skull.getClass().getMethod("setOwnerProfile", Class.forName("org.bukkit.profile.PlayerProfile"));
            setOwnerProfile.invoke(skull, profile);
            item.setItemMeta(skull);
            return true;
        } catch (ReflectiveOperationException | LinkageError | RuntimeException | java.io.IOException e) {
            return false;
        }
    }

    /** Decodes the base64 texture payload and pulls the skin URL out of it. */
    private static String skinUrlOf(String encodedTexture) {
        try {
            String json = new String(Base64.getDecoder().decode(encodedTexture), StandardCharsets.UTF_8);
            JsonObject textures = JsonParser.parseString(json).getAsJsonObject().getAsJsonObject("textures");
            if (textures == null || !textures.has("SKIN")) {
                return null;
            }
            return textures.getAsJsonObject("SKIN").get("url").getAsString();
        } catch (Exception e) {
            return null;
        }
    }

    private static Field findField(Class<?> type, String name) throws NoSuchFieldException {
        Class<?> current = type;
        while (current != null) {
            try {
                return current.getDeclaredField(name);
            } catch (NoSuchFieldException ignored) {
                current = current.getSuperclass();
            }
        }
        throw new NoSuchFieldException(name);
    }

    public static ItemStack createHead(Player player, int quantidade, String name, List<String> lore) {
        ItemStack item = new ItemStack(Material.PLAYER_HEAD, quantidade);
        SkullMeta skull = (SkullMeta) item.getItemMeta();
        skull.setDisplayName(Text.color(name));
        skull.setLore(Text.color(lore));
        skull.setOwningPlayer(player);
        item.setItemMeta(skull);

        applyPlayerTexture(item, player);
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

    public static ItemStack renameItem(ItemStack item, String name, List<String> lore) {
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(Text.color(name));
        meta.setLore(Text.color(lore));
        meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack createItem(Material material, int quantidade, String nome) {
        ItemStack item = new ItemStack(material, quantidade);
        ItemMeta meta = item.getItemMeta();
        if (nome != null) {
            meta.setDisplayName(Text.color(nome));
        }
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack createItem(Material material, int quantidade, String nome, List<String> desc) {
        ItemStack item = new ItemStack(material, quantidade);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(Text.color(nome));
        meta.setLore(Text.color(desc));
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack createItemLoreEnchanted(Material m, int i, String name, List<String> desc) {
        ItemStack item = new ItemStack(m, i);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(Text.color(name));
        meta.setLore(Text.color(desc));
        meta.addEnchant(Enchantment.LUCK, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        item.setItemMeta(meta);
        return item;
    }
}
