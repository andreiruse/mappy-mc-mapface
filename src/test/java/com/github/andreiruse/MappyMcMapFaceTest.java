package com.github.andreiruse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.AbstractMap;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;

import static org.junit.jupiter.api.Assertions.*;

class MappyMcMapFaceTest {
    private MappyMcMapFace<String, Integer, String> map;

    @BeforeEach
    public void setup() {
        map = new MappyMcMapFace<>();
    }

    @Test
    public void testPutAndGet() {
        String insertedValue = map.put("Hello", 1, "World");
        assertEquals("World", insertedValue);
        assertEquals("World", map.get("Hello", 1));
        assertEquals(Map.of(1, "World"), map.getNestedMap("Hello"));
    }

    @Test
    public void testNonExistentKey() {
        assertNull(map.get("NonExistent", 123));
        assertEquals(-1, map.size("NonExistent"));
    }

    @Test
    public void testNestedMapSize() {
        map.put("Hello", 1, "World");
        map.put("Hello", 2, "Everyone");
        assertEquals(2, map.size("Hello"));
    }

    @Test
    public void testModifyExistingValue() {
        map.put("Hello", 1, "World");
        String modifiedValue = map.modify("Hello", 1, (k, v) -> "Modified");
        assertEquals("Modified", modifiedValue);
        assertEquals("Modified", map.get("Hello", 1));
    }

    @Test
    public void testModifyNonExistentValue() {
        assertNull(map.modify("Hello", 1, (k, v) -> "Modified"));
    }

    @Test
    public void testPutWithNulls() {
        assertThrows(NullPointerException.class, () -> map.put(null, null, "Value"));
    }

    @Test
    public void testGetWithNulls() {
        assertNull(map.get(null, null));
    }

    @Test
    public void testGetEmptyNestedMap() {
        assertTrue(map.getNestedMap("Empty").isEmpty());
    }

    @Test
    public void testModifyWithNulls() {
        assertNull(map.modify(null, null, (k, v) -> "Modified"));
    }

    @Test
    public void testEntrySet() {
        map.put("Hello", 1, "World");
        Map.Entry<String, Map<Integer, String>> expectedEntry = new AbstractMap.SimpleEntry<>("Hello", Map.of(1, "World"));
        assertEquals(Set.of(expectedEntry), map.entrySet());
    }

    @Test
    public void testFlattenWithValidValues() {
        map.put("Hello", 1, "World");
        map.put("Hi", 2, "Everyone");

        BiFunction<String, Integer, String> combiner = (key1, key2) -> key1 + "-" + key2;
        Map<String, String> flattened = map.flatten(combiner);

        assertEquals("World", flattened.get("Hello-1"));
        assertEquals("Everyone", flattened.get("Hi-2"));
    }

    @Test
    public void testFlattenWithNullCombiner() {
        map.put("Hello", 1, "World");

        assertThrows(NullPointerException.class, () -> map.flatten(null));
    }

    @Test
    public void testFlattenWithEmptyNestedMap() {
        map.put("Hello", 1, null);

        BiFunction<String, Integer, String> combiner = (key1, key2) -> key1 + "-" + key2;
        Map<String, String> flattened = map.flatten(combiner);

        assertFalse(flattened.isEmpty());
        assertTrue(flattened.containsKey("Hello-1"));
        assertNull(flattened.get("Hello-1"));
    }

    @Test
    public void testFlattenWithNestedValueMap() {
        MappyMcMapFace<String, Integer, Map<String, String>> complexMap = new MappyMcMapFace<>();
        Map<String, String> nestedValue = Map.of("nestedKey", "nestedValue");
        complexMap.put("Hello", 1, nestedValue);

        BiFunction<String, Integer, String> combiner = (key1, key2) -> key1 + "-" + key2;
        Map<String, Map<String, String>> flattened = complexMap.flatten(combiner);

        assertEquals(nestedValue, flattened.get("Hello-1"));
    }
}