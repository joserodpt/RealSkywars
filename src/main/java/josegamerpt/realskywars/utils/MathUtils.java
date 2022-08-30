package josegamerpt.realskywars.utils;

import java.util.HashMap;
import java.util.Map;

public class MathUtils {

    public static <E> E mostFrequentElement(Iterable<E> iterable) {
        Map<E, Integer> freqMap = new HashMap<>();
        E mostFreq = null;
        int mostFreqCount = -1;
        for (E e : iterable) {
            Integer count = freqMap.get(e);
            freqMap.put(e, count = (count == null ? 1 : count+1));

            if (count > mostFreqCount) {
                mostFreq = e;
                mostFreqCount = count;
            }
        }
        return mostFreq;
    }

}
