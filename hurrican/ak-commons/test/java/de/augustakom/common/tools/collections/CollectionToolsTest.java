package de.augustakom.common.tools.collections;

import static org.testng.Assert.*;

import java.util.*;
import org.testng.annotations.Test;

public class CollectionToolsTest {

    @Test
    public void testFormatCommaSeparated() throws Exception {
        assertEquals(CollectionTools.formatCommaSeparated(Arrays.asList("12", "13", "15")), "12, 13, 15");
        assertEquals(CollectionTools.formatCommaSeparated(Arrays.asList(12L, 13L, 15L)), "12, 13, 15");
        assertEquals(CollectionTools.formatCommaSeparated(null), "");
    }

    @Test
    public void testChunkBy2() {
        assertEquals(
                CollectionTools.chunkBy2(Collections.emptyList(), (Integer x, Integer y) -> true),
                Collections.emptyList());
        assertEquals(
                CollectionTools.chunkBy2(Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8), (j, i) -> i % 3 == 0),
                Arrays.asList(Arrays.asList(0, 1, 2), Arrays.asList(3, 4, 5), Arrays.asList(6, 7, 8)));
        assertEquals(
                CollectionTools.chunkBy2(Arrays.asList(0, 1, 2, -2, 4, 5, -6, 6, 8), (j, i) -> i + j == 0),
                Arrays.asList(Arrays.asList(0, 1, 2), Arrays.asList(-2, 4, 5, -6), Arrays.asList(6, 8)));
    }
}
