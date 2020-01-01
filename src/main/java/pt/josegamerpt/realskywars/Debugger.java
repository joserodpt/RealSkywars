package pt.josegamerpt.realskywars;

public class Debugger {

	public static int debug = 1;

	public static void print(String b) {
		if (debug == 1) {
			System.out.print(RealSkywars.getPrefix() + "[DEBUG] " + b);
		}
	}
}
