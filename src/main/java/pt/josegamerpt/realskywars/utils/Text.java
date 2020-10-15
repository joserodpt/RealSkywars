package pt.josegamerpt.realskywars.utils;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Text {
    public static String addColor(String string) {
        return ChatColor.translateAlternateColorCodes('&', string);
    }

    public static void sendList(Player p, ArrayList<String> list) {
        for (String s : list) {
            p.sendMessage(addColor(s));
        }
    }

    public static void sendList(Player p, ArrayList<String> list, Object var) {
        for (String s : list) {
            p.sendMessage(addColor(s).replace("%CAGES%", var + ""));
        }
    }

    public static void sendList(Player p, List<String> list) {
        for (String s : list) {
            p.sendMessage(addColor(s));
        }
    }


    public static ArrayList<String> addColor(List<?> list) {
        ArrayList<String> color = new ArrayList<>();
        for (Object s : list) {
            color.add(Text.addColor((String) s));
        }
        return color;
    }

    public static CharSequence makeSpace() {
        return Text.addColor(randSp() + randSp() + randSp());
    }

    private static String randSp() {
        Random rand = new Random();
        List<String> sp = Arrays.asList("&6", "&7", "&8", "&9", "&5", "&f", "&e", "&a", "&b");
        return sp.get(rand.nextInt(sp.size()));
    }
}
