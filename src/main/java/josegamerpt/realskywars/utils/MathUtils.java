package josegamerpt.realskywars.utils;

import java.util.Arrays;

public class MathUtils {

    public static int bigger(int[] arr) {
        Arrays.sort(arr);

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

        if (curr_count > max_count) {
            res = arr[arr.length - 1];
        }

        return res;
    }

}
