package DasVerrueckteLabyrinth;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Random;
import java.util.stream.IntStream;

public class MutableLabyrinthTest {
    private Labyrinth l;

    @BeforeEach
    void initLabyrinth() {
        this.l = new AL_Labyrinth(2, new Random(0));
    }

    @Test
    void testShiftingActive() { // tests if shifting is possible and moving is not, when in shifting mode
        assertFalse(l.move(0, 6), "Shifting is active.");
        assertFalse(l.move(0, 0), "Shifting is active.");
        assertTrue(l.shift(0), "Shifting is active.");
    }

    @Test
    void testShiftingInactive() { // tests if moving is possible and shifting is not, when not in shifting mode
        l.shift(0);
        assertFalse(l.shift(0), "Shifting is inactive.");
        assertTrue(l.move(0, 6), "Shifting is inactive.");
    }

    @Test
    void testShiftIndices() { // tests for which indices shift() is defined and for which not
        assertFalse(l.shift(-1), "Index must be invalid.");
        assertFalse(l.shift(12), "Index must be invalid.");
        assertTrue(l.shift(0), "Index must be valid.");
        initLabyrinth();
        assertTrue(l.shift(11), "Index must be valid.");
    }

    @Test
    void testShiftOutOfBoard() { // tests, if the player is placed on the other side, if shifted out of the board
        l.shift(0);
        l.move(0, 5);
        assertEquals(0, l.getBoard()[0 + 7 * 5].getPlayer(),
                "Player 0 stands on the edge of the board.");
        assertNotEquals(0, l.getBoard()[6 + 7 * 5].getPlayer(),
                "Player 0 is not already on the other side.");
        l.shift(6);
        assertNotEquals(0, l.getBoard()[6 + 7 * 5].getPlayer(),
                "Player 0 got shifted off the board and placed on the other side.");
        assertEquals(0, l.getBoard()[0 + 7 * 5].getPlayer(),
                "Player 0 is not anymore on the original side.");
    }

    @Test
    void testBackShiftingDisabled() { // tests, that it is not possible to directly shift in the opposite direction of the last shift
        assertTrue(l.shift(0), "Shifting in this direction is possible.");
        l.move(0, 6);
        assertFalse(l.shift(8), "Shifting the tiles right back is disabled.");
        assertTrue(l.shift(1), "Shifting in another direction is possible.");
        l.move(6, 6);
        assertTrue(l.shift(8),
                "After another shift it is possible again to shift in this direction.");
    }

    @Test
    void testMoveIndices() { // tests for which indices move() is defined and for which not
        l.shift(0);
        assertFalse(l.move(-1, 0), "Index must be invalid.");
        assertFalse(l.move(0, -1), "Index must be invalid.");
        assertFalse(l.move(-1, -1), "Index must be invalid.");
        assertFalse(l.move(7, 0), "Index must be invalid.");
        assertFalse(l.move(0, 7), "Index must be invalid.");
        assertFalse(l.move(7, 7), "Index must be invalid.");
        assertTrue(l.move(0, 6), "Index must be valid.");
    }

    @Test
    void testHasPath() { // tests if the hasPath-algorithm works
        int[] validFields = {21, 22, 28, 29, 30, 35, 36, 37, 42, 43, 44};
        Arrays.stream(validFields).forEach(i -> {
            initLabyrinth();
            l.shift(11);
            assertTrue(l.move(i % 7, i / 7), "Field " + i + "has a path.");
        });
        IntStream.range(0, 49).filter(i ->
                Arrays.stream(validFields).noneMatch(j -> i == j))
                .forEach(i -> {
                    initLabyrinth();
                    l.shift(11);
                    assertFalse(l.move(i % 7, i / 7), "Field " + i + "has no path.");
                });
    }

    @Test
    void testSpareTileRotation() { // tests if the open sides of the spare tile change correctly when rotated
        boolean[] b1 = {true, false, true, false};
        boolean[] b2 = {false, true, false, true};
        for (int i = 0; i < 4; i++) {
            l.rotateSpareTile();
            ImmutableTile st = l.getSpareTile();
            assertArrayEquals(i % 2 == 0 ? b1 : b2, st.getOpenDirections(),
                    "Spare tile did not rotate correctly");
        }
    }

    @Test
    void testShiftConstantDirections() { // tests if all open directions of the shifted tiles stay the same before and after shift
        Labyrinth lBefore = new AL_Labyrinth(1, new Random(0));
        Labyrinth lAfter = new AL_Labyrinth(1, new Random(0));
        lAfter.shift(0);
        IntStream.range(0, 49 - 7).filter(i -> i % 7 == 1)
                .forEach(i ->
                        assertArrayEquals(lBefore.getBoard()[i].getOpenDirections(),
                                lAfter.getBoard()[i + 7].getOpenDirections(),
                                "Tile " + i + " is not shifted correctly."));
        assertArrayEquals(lBefore.getSpareTile().getOpenDirections(),
                lAfter.getBoard()[1].getOpenDirections(),
                "Spare tile was not inserted correctly.");
        assertArrayEquals(lBefore.getBoard()[43].getOpenDirections(),
                lAfter.getSpareTile().getOpenDirections(),
                "Spare tile was not shifted out correctly.");
    }

    @Test
    void testIsGameOver() { // tests if isGameOver() works properly for different conditions
        assertFalse(l.isGameOver(), "Game is not over at start up.");
        l.shift(11);
        l.move(1, 4); // Player 0 collects first treasure
        assertEquals(1, l.getTreasureCount()[0], "Player 0 got first treasure.");
        assertFalse(l.isGameOver(), "Game is not over at first treasure.");
        l.shift(10);
        l.move(6, 6); // Player 1
        l.shift(10);
        l.move(6, 0); // Player 2
        l.shift(6);
        l.move(0, 0); // Player 3
        l.rotateSpareTile();
        l.shift(10);
        l.move(1, 3); // Player 0 collects second treasure
        assertEquals(2, l.getTreasureCount()[0], "Player 0 has got all (2) treasures.");
        assertNotEquals(0, l.getBoard()[42].getPlayer(), "Player 0 is not in base.");
        assertFalse(l.isGameOver(), "Game is not over, only the first condition is true.");
        l.shift(11);
        l.move(6, 6); // Player 1
        l.shift(11);
        l.move(6, 0); // Player 2
        l.shift(11);
        l.move(0, 0); // Player 3
        l.shift(11);
        assertArrayEquals(new int[]{2, 0, 0, 0}, l.getTreasureCount(),
                "Player 0 has got all (2) treasures.");
        assertNotEquals(0, l.getBoard()[42].getPlayer(), "Player 0 is not in base.");
        assertFalse(l.isGameOver(), "Game is not over, only the first condition is true.");
        l.move(0, 6); // Player 0 moves to base an wins
        assertArrayEquals(new int[]{2, 0, 0, 0}, l.getTreasureCount(),
                "Player 0 has got all (2) treasures.");
        assertEquals(0, l.getBoard()[42].getPlayer(),
                "Player 0 is in base.");
        assertTrue(l.isGameOver(), "Game is over because both conditions are true.");
    }
}
