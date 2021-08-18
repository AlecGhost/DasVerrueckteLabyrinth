package DasVerrueckteLabyrinth;

import org.junit.jupiter.api.Test;

import java.util.Random;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

public class ImmutableLabyrinthTest {
    private final Labyrinth l = new AL_Labyrinth(1, new Random(0));

    @Test
    void testBoardLength() { // tests if board-array has the correct length of 49
        assertEquals(49, l.getBoard().length, "Incorrect board length.");
    }

    @Test
    void testStartPlayer() { // test if player 0 starts the game
        assertEquals(0, l.getPlayer(), "Incorrect starting player.");
    }

    @Test
    void testInitialGameNotOver() { // tests that the game is not over at the very beginning
        assertFalse(l.isGameOver(), "The Game is not over right at the start.");
    }

    @Test
    void testStartingPositions() { // tests, whether all players are on the correct starting spot
        assertTrue(IntStream.range(0, 49)
                        .filter(n -> n != 0 && n != 6 && n != 42 && n != 48)
                        .noneMatch(n -> l.getBoard()[n].getPlayer() != -1),
                "Every player should be on his starting position.");
    }

    @Test
    void testFirstSpareTile() { // tests if the first spare tile is open at the expected sides and there is no treasure or player on it
        ImmutableTile st = l.getSpareTile();
        assertArrayEquals(new boolean[]{false, true, false, true}, st.getOpenDirections(),
                "The first spare tile is not aligned correctly.");
        assertEquals(-1, st.getPlayer(),
                "There is no player on the first spare tile.");
        assertEquals(-1, st.getTreasure(),
                "There is no treasure on the first spare tile.");
    }

    @Test
    void testStartTileAlignment() { // tests if the tiles in the edges of the board are correctly set
        assertArrayEquals(new boolean[]{false, true, true, false}, l.getBoard()[0].getOpenDirections(),
                "Invalid directions of starting tile.");
        assertArrayEquals(new boolean[]{false, false, true, true}, l.getBoard()[6].getOpenDirections(),
                "Invalid directions of starting tile.");
        assertArrayEquals(new boolean[]{true, true, false, false}, l.getBoard()[42].getOpenDirections(),
                "Invalid directions of starting tile.");
        assertArrayEquals(new boolean[]{true, false, false, true}, l.getBoard()[48].getOpenDirections(),
                "Invalid directions of starting tile.");
    }

    @Test
    void testGetPlayerImmutable() { // tests, that it is not possible to change the player from the outside
        int p = l.getPlayer();
        p = 5;
        assertEquals(0, l.getPlayer(),
                "TreasureCount is not mutable from the outside.");
    }

    @Test
    void testGetTreasureCountImmutable() { // tests, that it is not possible to change the treasure from the outside
        int[] tc = l.getTreasureCount();
        tc[0] = 1;
        tc[3] = 5;
        assertArrayEquals(new int[]{0, 0, 0, 0}, l.getTreasureCount(),
                "TreasureCount is not mutable from the outside.");
    }

    @Test
    void testGetSpareTileImmutable() { // tests, that it is not possible to change the spare tile from the outside
        Tile t0 = (Tile) l.getSpareTile();
        Tile t = (Tile) l.getSpareTile();
        t.rotate(1).setPlayer(3).setTreasure(2);
        assertArrayEquals(new boolean[]{false, true, false, true}, t0.getOpenDirections(),
                "SpareTile is not mutable from the outside.");
        assertEquals(-1, t0.getPlayer(), "SpareTile is not mutable from the outside.");
        assertEquals(-1, t0.getTreasure(), "SpareTile is not mutable from the outside.");
    }

    @Test
    void testGetBoardImmutable() { // tests, that it is not possible to change the board from the outside
        Tile[] b0 = (Tile[]) l.getBoard();
        Tile[] b = (Tile[]) l.getBoard();
        b[0].rotate(1).setPlayer(3).setTreasure(2);
        assertArrayEquals(new boolean[]{false, true, true, false}, b0[0].getOpenDirections(),
                "Board is not mutable from the outside.");
        assertEquals(3, b0[0].getPlayer(), "Board is not mutable from the outside.");
        assertEquals(-1, b0[0].getTreasure(), "Board is not mutable from the outside.");
        b = new Tile[]{Tile.randomOf(new Random()), Tile.randomOf(new Random())};
        assertEquals(49, b0.length, "Board is not mutable from the outside.");
    }
}
