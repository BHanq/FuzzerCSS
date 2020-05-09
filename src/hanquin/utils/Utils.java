package hanquin.utils;

import java.lang.reflect.Array;
import java.util.Arrays;

public class Utils {
    //https://stackoverflow.com/questions/11208479/how-do-i-initialize-a-byte-array-in-java


    public static <T> T[] joinArrayGeneric(T[]... arrays) {
        int length = 0;
        for (T[] array : arrays) {
            length += array.length;
        }

        //T[] result = new T[length];
        final T[] result = (T[]) Array.newInstance(arrays[0].getClass().getComponentType(), length);

        int offset = 0;
        for (T[] array : arrays) {
            System.arraycopy(array, 0, result, offset, array.length);
            offset += array.length;
        }

        return result;
    }
}
