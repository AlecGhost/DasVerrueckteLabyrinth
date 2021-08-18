package DasVerrueckteLabyrinth;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Random;
import java.util.stream.IntStream;

public class TreasureTest {
    private Labyrinth l;

    @BeforeEach
    void initLabyrinth() {
        this.l = new AL_Labyrinth(2, new Random(0));
    }

    @Test
    void testTreasureStartingPos() { // tests, whether all treasures are on the correct position at startup and whether there are no treasures elsewhere
        ImmutableTile[] b = l.getBoard();
        int[] treasures = {29, 9, 34, 24};
        assertTrue(IntStream.range(0, treasures.length)
                        .noneMatch(i -> i != b[treasures[i]].getTreasure()),
                "The treasures are not placed correctly.");
        assertTrue(IntStream.range(0, 49)
                        .filter(i -> Arrays.stream(treasures).noneMatch(j -> i == j))
                        .noneMatch(n -> b[n].getTreasure() != -1),
                "Every treasure should be on his starting position.");
    }

    @Test
    void testInitialTreasureCount() { // tests, whether the treasureCount-Array is initialized correctly
        assertArrayEquals(new int[]{0, 0, 0, 0}, l.getTreasureCount(),
                "All players start with 0 treasures.");
    }

    @Test
    void testTreasureCapture() { // tests, whether the game behaves correctly if a player collects his treasure
        l.shift(11);
        assertEquals(0, l.getBoard()[1 + 4 * 7].getTreasure(),
                "Here lies Treasure 0.");
        l.move(1, 4);
        assertEquals(-1, l.getBoard()[1 + 4 * 7].getTreasure(),
                "Here is no treasure anymore.");
        assertEquals(0, l.getBoard()[27].getTreasure(),
                "Treasure 0 treasure spawned here.");
        assertTrue(IntStream.range(0, 49)
                        .filter(i -> i != 27)
                        .noneMatch(i -> l.getBoard()[i].getTreasure() == 0),
                "There is no second treasure 0.");
        assertArrayEquals(new int[]{1, 0, 0, 0}, l.getTreasureCount(),
                "Only Player 1 found a treasure.");
    }

    @Test
    void testStepOnOtherTreasure() { // tests, that the treasure is not picked up, when the wrong player walks on the field.
        l.shift(11);
        l.move(0, 6);
        l.shift(11);
        assertEquals(1, l.getPlayer(), "Current player is 1.");
        assertEquals(2, l.getBoard()[6 + 4 * 7].getTreasure(), "Target treasure is 2.");
        l.move(6, 4);
        assertEquals(1, l.getBoard()[6 + 4 * 7].getPlayer(),
                "Player 1 is on this field.");
        assertEquals(2, l.getBoard()[6 + 4 * 7].getTreasure(),
                "Treasure 2 did not get collected.");
        assertArrayEquals(new int[]{0, 0, 0, 0}, l.getTreasureCount(),
                "TreasureCount did not get updated.");
    }

    @Test
    void testTreasureShiftedOut() { // tests if the treasure stays on the tile, if it gets shifted out and becomes the spare tile
        l.shift(11);
        l.move(1, 4);
        assertEquals(0, l.getBoard()[27].getTreasure(),
                "Treasure is on the edge of the board.");
        l.shift(10);
        assertEquals(0, l.getSpareTile().getTreasure(),
                "Treasure was shifted on spare tile.");
        ImmutableTile[] b = l.getBoard();
        assertTrue(IntStream.range(0, 49).noneMatch(i -> b[i].getTreasure() == 0),
                "Treasure is no longer on the board.");
    }

    @Test
    void testNoTreasureSpawnsAfterMax() { // tests, that no new treasure spawns, if limit is reached
        l.shift(11);
        l.move(1, 4); // Player 0 collects first treasure
        assertEquals(1, l.getTreasureCount()[0], "Player 0 collected first treasure.");
        assertEquals(0, l.getBoard()[27].getTreasure(), "A new treasure spawned.");
        l.shift(10);
        l.move(6, 6); // Player 1
        l.shift(10);
        l.move(6, 0); // Player 2
        l.shift(6);
        l.move(0, 0); // Player 3
        l.rotateSpareTile();
        l.shift(10);
        l.move(1, 3); // Player 0 collects second treasure
        assertEquals(2, l.getTreasureCount()[0], "Player 0 collected second treasure.");
        assertTrue(IntStream.range(0, 49).noneMatch(i -> l.getBoard()[i].getTreasure() == 0),
                "No further treasure spawns for player 0 because the maximum of treasures is reached.");
    }
}
