package pt.josegamerpt.realskywars.utils;

import java.util.Arrays;

public class MathUtils {
	public static double calculateDistanceBetweenPoints(double x1, double y1, double x2, double y2) {
		return java.lang.Math.sqrt((y2 - y1) * (y2 - y1) + (x2 - x1) * (x2 - x1));
	}

	public static int bigger(int[] arr) {
		Arrays.sort(arr);

		// find the max frequency using linear
		// traversal
		int max_count = 1, res = arr[0];
		int curr_count = 1;

		for (int i = 1; i < arr.length; i++) {
			if (arr[i] == arr[i - 1])
				curr_count++;
			else {
				if (curr_count > max_count) {
					max_count = curr_count;
					res = arr[i - 1];
				}
				curr_count = 1;
			}
		}

		// If last element is most frequent
		if (curr_count > max_count) {
			res = arr[arr.length - 1];
		}

		return res;
	}

}
