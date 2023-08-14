package com.github.andreiruse;

import java.util.*;
import java.util.function.BiFunction;

public class MappyMcMapFace<T,U,V> extends AbstractMap<T, Map<U, V>> {

    // Internal map to store the data
    private final Map<T, Map<U, V>> internalMap = new HashMap<>();

    /**
     * Retrieves a value with the given keys.
     *
     * @param key1 the first key
     * @param key2 the second key
     * @return the value, or null if not found
     */
    public V get(T key1, U key2) {
        Map<U, V> secondaryMap = internalMap.get(key1);
        return (secondaryMap == null) ? null : secondaryMap.get(key2);
    }

    /**
     * Inserts a value with the given keys.
     *
     * @param key1  the first key
     * @param key2  the second key
     * @param value the value to insert
     * @return the value that was inserted
     * @throws NullPointerException if either key1 or key2 is null
     */
    public V put(T key1, U key2, V value) {
        Objects.requireNonNull(key1);
        Objects.requireNonNull(key2);
        internalMap
                .computeIfAbsent(key1, k -> new HashMap<>())
                .put(key2, value);
        return value;
    }

    /**
     * Modifies a value with the given keys using a remapping function.
     *
     * @param key1             the first key
     * @param key2             the second key
     * @param remappingFunction the function to compute a value
     * @return the new value after remapping
     */
    public V modify(T key1, U key2, BiFunction<U, V, V> remappingFunction) {
        Map<U, V> secondaryMap = internalMap.get(key1);
        if (secondaryMap == null) {
            return null;
        }
        return secondaryMap.compute(key2, remappingFunction);
    }

    /**
     * Retrieves the nested map associated with the given key.
     *
     * @param key1 the first key
     * @return the nested map, or an empty map if not found
     */
    public Map<U, V> getNestedMap(T key1) {
        return internalMap.getOrDefault(key1, Collections.emptyMap());
    }

    /**
     * Flattens the nested structure of the map using the provided combiner.
     *
     * @param combiner function to combine the two keys
     * @return a flattened map with combined keys and values
     */
    public <R> Map<R, V> flatten(BiFunction<T, U, R> combiner) {
        Map<R, V> resultMap = new HashMap<>();
        internalMap.forEach((key1, nestedMap) ->
                nestedMap.forEach((key2, value) ->
                        resultMap.put(combiner.apply(key1, key2), value)
                )
        );
        return resultMap;
    }

    /**
     * Retrieves the size of the nested map associated with the given key.
     *
     * @param key1 the first key
     * @return the size of the nested map, or -1 if not found
     */
    public int size(T key1) {
        Map<U, V> secondaryMap = internalMap.get(key1);
        return (secondaryMap != null) ? secondaryMap.size() : -1;
    }

    @Override
    public Set<Entry<T, Map<U, V>>> entrySet() {
        return internalMap.entrySet();
    }
}
