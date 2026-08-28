package joserodpt.realskywars.plugin.listeners;

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
 * @author José Rodrigues © 2019-2026
 * @link https://github.com/joserodpt/RealSkywars
 */

import joserodpt.realskywars.api.RealSkywarsAPI;
import joserodpt.realskywars.api.config.RSWConfig;
import joserodpt.realskywars.api.player.RSWPlayer;
import joserodpt.realskywars.api.utils.ItemStackSpringer;
import joserodpt.realskywars.api.utils.Text;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Turns configured blocks into lucky blocks. There is no hard dependency on any
 * LuckyBlock plugin: the trigger blocks can be vanilla materials, ItemsAdder
 * blocks ("ITEMSADDER:namespace:id"), or a mix of both.
 * <p>
 * Rewards are configured as "WEIGHT=n|KIND:payload", where KIND is one of
 * ITEM, COMMAND, EFFECT or EXPLOSION.
 */
public class LuckyBlockListener implements Listener {

    private static final String ITEMSADDER_PREFIX = "ITEMSADDER:";

    private final RealSkywarsAPI rs;

    public LuckyBlockListener(RealSkywarsAPI rs) {
        this.rs = rs;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBreak(BlockBreakEvent e) {
        if (!RSWConfig.file().getBoolean("Config.LuckyBlock.Enabled", false) || !isLuckyBlock(e.getBlock())) {
            return;
        }

        Player player = e.getPlayer();
        RSWPlayer gamePlayer = this.rs.getPlayerManagerAPI().getPlayer(player);
        if (RSWConfig.file().getBoolean("Config.LuckyBlock.Break-In-Match-Only", true)
                && (gamePlayer == null || !gamePlayer.isInMatch())) {
            return;
        }

        List<Reward> rewards = new ArrayList<>();
        for (String raw : RSWConfig.file().getStringList("Config.LuckyBlock.Rewards")) {
            rewards.add(Reward.parse(raw));
        }
        if (rewards.isEmpty()) {
            return;
        }

        //the listener consumes the block, so another plugin cannot also drop it
        e.setDropItems(false);

        int amount = RSWConfig.file().getInt("Config.LuckyBlock.Rewards-Per-Break", 1);
        if (amount <= 0 || amount > rewards.size()) {
            amount = rewards.size();
        }

        Set<Integer> given = new HashSet<>();
        for (int i = 0; i < amount; i++) {
            int index = pickWeighted(rewards, given);
            if (index < 0) {
                break;
            }
            given.add(index);
            applyReward(player, rewards.get(index).value);
        }

        String broadcast = RSWConfig.file().getString("Config.LuckyBlock.Broadcast", "");
        if (broadcast != null && !broadcast.isEmpty()) {
            Bukkit.broadcastMessage(Text.color(broadcast
                    .replace("%player%", player.getName())
                    .replace("%amount%", String.valueOf(given.size()))));
        }

        playFeedback(e.getBlock().getLocation());
    }

    /** Picks a reward by weight, never handing out the same one twice. */
    private int pickWeighted(List<Reward> rewards, Set<Integer> excluded) {
        double total = 0;
        for (int i = 0; i < rewards.size(); i++) {
            if (!excluded.contains(i)) {
                total += rewards.get(i).weight;
            }
        }
        if (total <= 0) {
            return -1;
        }

        double chosen = ThreadLocalRandom.current().nextDouble(total);
        for (int i = 0; i < rewards.size(); i++) {
            if (excluded.contains(i)) {
                continue;
            }
            chosen -= rewards.get(i).weight;
            if (chosen <= 0) {
                return i;
            }
        }
        return -1;
    }

    private boolean isLuckyBlock(Block block) {
        List<String> configured = RSWConfig.file().getStringList("Config.LuckyBlock.Blocks");
        if (configured.isEmpty()) {
            configured = List.of(RSWConfig.file().getString("Config.LuckyBlock.Material", "SPONGE"));
        }

        for (String raw : configured) {
            String type = raw.trim();
            if (type.equalsIgnoreCase(block.getType().name())) {
                return true;
            }
            if (type.regionMatches(true, 0, ITEMSADDER_PREFIX, 0, ITEMSADDER_PREFIX.length())
                    && isItemsAdderBlock(block, type.substring(ITEMSADDER_PREFIX.length()))) {
                return true;
            }
        }
        return false;
    }

    private boolean isItemsAdderBlock(Block block, String expected) {
        try {
            Class<?> customBlock = Class.forName("dev.lone.itemsadder.api.CustomBlock");
            Object placed = customBlock.getMethod("byAlreadyPlaced", Block.class).invoke(null, block);
            if (placed == null) {
                return false;
            }

            for (String methodName : new String[]{"getNamespacedID", "getNamespacedId", "getId"}) {
                try {
                    Method method = placed.getClass().getMethod(methodName);
                    Object id = method.invoke(placed);
                    if (id != null && expected.equalsIgnoreCase(String.valueOf(id))) {
                        return true;
                    }
                } catch (ReflectiveOperationException ignored) {
                    //try the next accessor: the name changed between ItemsAdder versions
                }
            }
        } catch (ReflectiveOperationException | LinkageError ignored) {
            //ItemsAdder is not installed
        }
        return false;
    }

    private void applyReward(Player player, String reward) {
        if (reward.regionMatches(true, 0, "COMMAND:", 0, 8)) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), reward.substring(8).replace("%player%", player.getName()));
            return;
        }

        if (reward.regionMatches(true, 0, "EFFECT:", 0, 7)) {
            String[] parts = reward.substring(7).split(";");
            PotionEffectType type = PotionEffectType.getByName(parts[0].trim().toUpperCase());
            if (type == null) {
                this.rs.getLogger().warning(parts[0] + " isn't a valid PotionEffectType [LUCKYBLOCK]");
                return;
            }
            int duration = intValue(parts, "DURATION", 100);
            int amplifier = Math.max(0, intValue(parts, "AMPLIFIER", 1) - 1);
            player.addPotionEffect(new PotionEffect(type, duration, amplifier));
            return;
        }

        if (reward.regionMatches(true, 0, "EXPLOSION:", 0, 10)) {
            player.getWorld().createExplosion(player.getLocation(), (float) doubleValue(reward.substring(10), 2.0), false, false);
            return;
        }

        if (reward.regionMatches(true, 0, "ITEM:", 0, 5)) {
            Map<String, Object> data = new HashMap<>();
            for (String pair : reward.substring(5).split(";")) {
                String[] split = pair.split("=", 2);
                if (split.length == 2) {
                    data.put(split[0].trim().toUpperCase(), split[1]);
                }
            }

            data.putIfAbsent(ItemStackSpringer.ItemCategories.AMOUNT.name(), "1");
            try {
                data.put(ItemStackSpringer.ItemCategories.AMOUNT.name(),
                        Integer.parseInt(String.valueOf(data.get(ItemStackSpringer.ItemCategories.AMOUNT.name())).trim()));
            } catch (NumberFormatException ignored) {
                data.put(ItemStackSpringer.ItemCategories.AMOUNT.name(), 1);
            }

            ItemStack item = ItemStackSpringer.getItemDeSerialized(data);
            if (item != null) {
                player.getInventory().addItem(item);
            }
            return;
        }

        this.rs.getLogger().warning("Unknown LuckyBlock reward: " + reward);
    }

    private void playFeedback(Location location) {
        String sound = RSWConfig.file().getString("Config.LuckyBlock.Sound", "ENTITY_PLAYER_LEVELUP");
        try {
            location.getWorld().playSound(location, Sound.valueOf(sound.toUpperCase()), 1f, 1.2f);
        } catch (IllegalArgumentException ignored) {
            //an unknown sound just means no sound
        }
        location.getWorld().spawnParticle(Particle.TOTEM, location.clone().add(.5, .5, .5), 18, .35, .35, .35, .1);
    }

    private int intValue(String[] parts, String key, int fallback) {
        for (String part : parts) {
            String[] split = part.split("=", 2);
            if (split.length == 2 && key.equalsIgnoreCase(split[0].trim())) {
                try {
                    return Integer.parseInt(split[1].trim());
                } catch (NumberFormatException ignored) {
                    return fallback;
                }
            }
        }
        return fallback;
    }

    private double doubleValue(String value, double fallback) {
        try {
            return Double.parseDouble(value.trim());
        } catch (NumberFormatException ignored) {
            return fallback;
        }
    }

    /** One entry of the configured reward table. */
    private static final class Reward {
        private final double weight;
        private final String value;

        private Reward(double weight, String value) {
            this.weight = Math.max(0, weight);
            this.value = value;
        }

        private static Reward parse(String raw) {
            String[] split = raw.split("\\|", 2);
            if (split.length == 2 && split[0].toUpperCase().startsWith("WEIGHT=")) {
                try {
                    return new Reward(Double.parseDouble(split[0].substring(7).trim()), split[1]);
                } catch (NumberFormatException ignored) {
                    //fall through to the default weight
                }
            }
            return new Reward(1, raw);
        }
    }
}
