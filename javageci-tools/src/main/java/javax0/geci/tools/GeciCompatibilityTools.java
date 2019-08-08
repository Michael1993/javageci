package javax0.geci.tools;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax0.geci.api.SegmentSplitHelper;

public class GeciCompatibilityTools {

    public static <K, V> Map<K, V> createMap() {
        return Collections.unmodifiableMap(new HashMap<>());
    }

    public static <K, V> Map<K, V> createMap(K k, V v) {
        return createMapOfEntries(entry(k, v));
    }

    public static <K, V> Map<K, V> createMap(K k1, V v1, K k2, V v2) {
        return createMapOfEntries(
            entry(k1, v1),
            entry(k2, v2)
        );
    }

    public static <K, V> Map<K, V> createMap(K k1, V v1, K k2, V v2, K k3, V v3) {
        return createMapOfEntries(
            entry(k1, v1),
            entry(k2, v2),
            entry(k3, v3)
        );
    }

    public static <K, V> Map<K, V> createMap(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4) {
        return createMapOfEntries(
            entry(k1, v1),
            entry(k2, v2),
            entry(k3, v3),
            entry(k4, v4)
        );
    }

    public static <K, V> Map<K, V> createMap(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5) {
        return createMapOfEntries(
            entry(k1, v1),
            entry(k2, v2),
            entry(k3, v3),
            entry(k4, v4),
            entry(k5, v5)
        );
    }

    public static <K, V> Map<K, V> createMap(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5, K k6, V v6) {
        return createMapOfEntries(
            entry(k1, v1),
            entry(k2, v2),
            entry(k3, v3),
            entry(k4, v4),
            entry(k5, v5),
            entry(k6, v6)
        );
    }

    public static <K, V> Map<K, V> createMap(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5, K k6, V v6, K k7, V v7) {
        return createMapOfEntries(
            entry(k1, v1),
            entry(k2, v2),
            entry(k3, v3),
            entry(k4, v4),
            entry(k5, v5),
            entry(k6, v6),
            entry(k7, v7)
        );
    }

    public static <K, V> Map<K, V> createMap(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5, K k6, V v6, K k7, V v7, K k8, V v8) {
        return createMapOfEntries(
            entry(k1, v1),
            entry(k2, v2),
            entry(k3, v3),
            entry(k4, v4),
            entry(k5, v5),
            entry(k6, v6),
            entry(k7, v7),
            entry(k8, v8)
        );
    }

    public static <K, V> Map<K, V> createMap(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5, K k6, V v6, K k7, V v7, K k8, V v8, K k9, V v9) {
        return createMapOfEntries(
            entry(k1, v1),
            entry(k2, v2),
            entry(k3, v3),
            entry(k4, v4),
            entry(k5, v5),
            entry(k6, v6),
            entry(k7, v7),
            entry(k8, v8),
            entry(k9, v9)
        );
    }

    public static <K, V> Map<K, V> createMap(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5, K k6, V v6, K k7, V v7, K k8, V v8, K k9, V v9, K k10, V v10) {
        return createMapOfEntries(
            entry(k1, v1),
            entry(k2, v2),
            entry(k3, v3),
            entry(k4, v4),
            entry(k5, v5),
            entry(k6, v6),
            entry(k7, v7),
            entry(k8, v8),
            entry(k9, v9),
            entry(k10, v10)
        );
    }

    @SafeVarargs
    public static <K, V> Map<K, V> createMapOfEntries(Map.Entry<? extends K, ? extends V>... entries) {
        Map<K, V> result = new HashMap<>();
        for (Map.Entry<? extends K, ? extends V> entry : entries) {
            result.put(entry.getKey(), entry.getValue());
        }
        return Collections.unmodifiableMap(result);
    }

    public static <K, V> Map.Entry<K, V> entry(K key, V value) {
        return new KeyValueHolder<>(key, value);
    }

    public static <T> List<T> createList(T... items) {
        List<T> list = new ArrayList<>(Arrays.asList(items));
        return Collections.unmodifiableList(list);
    }

    public static <T> Set<T> createSet(T... items) {
        final HashSet<T> ts = new HashSet<>(Arrays.asList(items));
        return Collections.unmodifiableSet(ts);
    }

    public static byte[] readBytesFromInput(InputStream is) throws IOException {
        List<Integer> valueList = new ArrayList<>();
        int value = is.read();
        while (value != -1) {
            valueList.add(value);
            value = is.read();
        }
        byte[] result = new byte[valueList.size()];
        for (int i = 0; i < valueList.size(); i++) {
            result[i] = valueList.get(i).byteValue();
        }
        return result;
    }

    public static String repeat(String s, int to) {
        if(to < 0 || s == null) {
            throw new IllegalArgumentException("Can't repeat null, or repeat count can't be less than 0.");
        }
        StringBuilder stringBuilder = new StringBuilder();
        for(int i = 0; i < to; i++) {
            stringBuilder.append(s);
        }
        return stringBuilder.toString();
    }

    public static String stripLeading(String line) {
        String result = line;
        while (result.length() > 0 && Character.isWhitespace(result.charAt(0))) {
            result = result.substring(1);
        }
        return result;
    }

    public static String stripTrailing(String line) {
        String result = line;
        while (result.length() > 0 && Character.isWhitespace(result.charAt(line.length() - 1))) {
            result = result.substring(0, line.length() - 2);
        }
        return result;
    }

    public static Predicate<String> asMatchPredicate(Pattern pattern) {
        return s -> pattern.matcher(s).matches();
    }

    private static final class KeyValueHolder<K, V> implements Map.Entry<K, V> {
        final K key;
        final V value;

        private KeyValueHolder(K key, V value) {
            this.key = Objects.requireNonNull(key);
            this.value = Objects.requireNonNull(value);
        }

        @Override public K getKey() {
            return key;
        }

        @Override public V getValue() {
            return value;
        }

        @SuppressWarnings("unchecked")
        @Override public boolean equals(Object o) {
            if (o == null) return false;
            if (o.getClass() != this.getClass()) return false;

            KeyValueHolder<K, V> other = (KeyValueHolder) o;
            return other.key == this.key && other.value == this.value;
        }

        @Override public int hashCode() {
            return Objects.hash(key, value);
        }

        @Override public V setValue(V value) {
            throw new UnsupportedOperationException("Can't change immutable map element.");
        }
    }
}
