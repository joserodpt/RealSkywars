package pt.josegamerpt.realskywars.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import pt.josegamerpt.realskywars.classes.Enum.GameState;

public class Text {
	private static int i;
	private static String texto;

	static {
		Text.i = 1;
		Text.texto = "";
	}

	public static String addColor(String string) {
		return ChatColor.translateAlternateColorCodes('&', string);
	}

	public static ArrayList<String> entryToList(List<Entry> e) {
		ArrayList<String> r = new ArrayList<>();
		e.forEach(entry -> r.add(entry.getName()));
		return r;
	}

	public static String rainbow(String original) {
		final char[] chars = {'c', '6', 'e', 'a', 'b', '3', 'd'};
		int index = 0;
		String returnValue = "";
		char[] charArray;
		for (int length = (charArray = original.toCharArray()).length, i = 0; i < length; ++i) {
			final char c = charArray[i];
			returnValue = returnValue + "&" + chars[index] + c;
			if (++index == chars.length) {
				index = 0;
			}
		}
		return ChatColor.translateAlternateColorCodes('&', returnValue);
	}

	public static void startAnimation() {
		final List<String> lista = Arrays.asList("&c", "&6", "&e", "&a", "&b", "&3", "&d");
		final int s = lista.size();
		try {
			if (Text.i >= s) {
				Text.i = 0;
			}
			Text.texto = lista.get(Text.i).replaceAll("&", "&");
			++Text.i;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static String getRainbow() {
		return Text.texto;
	}

	public static String getStateText(GameState game) {
		if (game == GameState.AVAILABLE) {
			return addColor("&aAvailable");
		} else if (game == GameState.FINISHING) {
			return addColor("&7Finishing");
		} else if (game == GameState.PLAYING) {
			return addColor("&4Playing");
		} else if (game == GameState.RESETTING) {
			return addColor("&5Resetting");
		} else if (game == GameState.STARTING) {
			return addColor("&6Starting");
		} else if (game == GameState.WAITING) {
			return addColor("&3Waiting");
		} else {
			return addColor("&eError");
		}
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
	
	private static String randSp() 
    { 
        Random rand = new Random(); 
        List<String> sp = Arrays.asList("&6", "&7", "&8", "&9", "&5", "&f", "&e", "&a", "&b");
        return sp.get(rand.nextInt(sp.size())); 
    } 
}
