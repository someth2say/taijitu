package org.someth2say.taijitu.ui.util;

import java.util.ArrayList;
import java.util.List;

public class ListUtil {

    @SafeVarargs
	public static <T> List<T> safeUnion(List<T>... lists) {
        List<T> result = new ArrayList<>();
        for (List<T> list : lists) {
            if (list != null) {
                result.addAll(list);
            }
        }
        return result;
    }
}
