package DasVerrueckteLabyrinth;

import com.google.common.primitives.Booleans;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

public class TileTest {
    @Test
    void testShapeCreation() { // tests if the shape-parameter works correctly
        Tile straight = new Tile(Shape.STRAIGHT);
        Tile edge = new Tile(Shape.EDGE);
        Tile t = new Tile(Shape.T);
        assertArrayEquals(new boolean[]{true, false, true, false}, straight.getOpenDirections(),
                "Shape STRAIGHT not created correctly.");
        assertArrayEquals(new boolean[]{true, true, false, false}, edge.getOpenDirections(),
                "Shape EDGE not created correctly.");
        assertArrayEquals(new boolean[]{true, true, true, false}, t.getOpenDirections(),
                "Shape T not created correctly.");
    }

    @Test
    void testInitialValues() { // tests that initially there is not player and no treasure on a new tile
        Tile t1 = new Tile(Shape.STRAIGHT);
        Tile t2 = new Tile(Shape.EDGE);
        Tile t3 = new Tile(Shape.T);
        assertEquals(-1, t1.getPlayer(), "Wrong initial player.");
        assertEquals(-1, t2.getPlayer(), "Wrong initial player.");
        assertEquals(-1, t3.getPlayer(), "Wrong initial player.");
        assertEquals(-1, t1.getTreasure(), "Wrong initial treasure.");
        assertEquals(-1, t2.getTreasure(), "Wrong initial treasure.");
        assertEquals(-1, t3.getTreasure(), "Wrong initial treasure.");
    }

    @Test
    void testRandomOf() { // test if the random tile gets created correctly (with a specific seed)
        Tile t = Tile.randomOf(new Random(0));
        assertArrayEquals(new boolean[]{false, true, false, true}, t.getOpenDirections(),
                "Random Tile not created correctly.");
        assertEquals(-1, t.getPlayer());
        assertEquals(-1, t.getTreasure());
    }

    @Test
    void testCopyOf() { // tests if copy of a tile does not give access to the instace variables of the original tile
        Tile t = new Tile(Shape.STRAIGHT);
        assertEquals(t.getClass(), Tile.copyOf(t).getClass(),
                "Copy must be of the same class as original.");
        Tile copyOft = Tile.copyOf(t);
        copyOft.rotate(1).setPlayer(2).setTreasure(3);
        assertArrayEquals(new boolean[]{true, false, true, false}, t.getOpenDirections(),
                "Copy must not have access to the original object.");
        assertEquals(-1, t.getPlayer(),
                "Copy must not have access to the original object.");
        assertEquals(-1, t.getTreasure(),
                "Copy must not have access to the original object.");
    }

    @Test
    void testRotate() { // tests if the rotate()-method works correctly
        Arrays.stream(Shape.values()).forEach(s -> {
            Tile t = new Tile(s);
            boolean[] expected = t.getOpenDirections();
            Collections.rotate(Booleans.asList(expected), -9);
            t.rotate(1).rotate(2).rotate(-3).rotate(4).rotate(5);
            assertArrayEquals(expected, t.getOpenDirections(),
                    "Rotation didn't work properly.");
        });
    }

    @Test
    void testGetNSetPlayer() { // tests the getters and setters for player
        Tile t = Tile.randomOf(new Random());
        assertEquals(-1, t.getPlayer(), "Wrong initial player.");
        t.setPlayer(0);
        assertEquals(0, t.getPlayer(), "Got wrong player.");
        t.setPlayer(1);
        assertEquals(1, t.getPlayer(), "Got wrong player.");
        t.setPlayer(2);
        assertEquals(2, t.getPlayer(), "Got wrong player.");
        t.setPlayer(3);
        assertEquals(3, t.getPlayer(), "Got wrong player.");
        t.setPlayer(4);
        assertEquals(3, t.getPlayer(), "Got wrong player.");
        t.setPlayer(-2);
        assertEquals(3, t.getPlayer(), "Got wrong player.");
    }

    @Test
    void testGetNSetTreasure() { // tests the getters and setters for treasure
        Tile t = Tile.randomOf(new Random());
        assertEquals(-1, t.getTreasure(), "Wrong initial treasure.");
        t.setTreasure(0);
        assertEquals(0, t.getTreasure(), "Got wrong treasure.");
        t.setTreasure(1);
        assertEquals(1, t.getTreasure(), "Got wrong treasure.");
        t.setTreasure(2);
        assertEquals(2, t.getTreasure(), "Got wrong treasure.");
        t.setTreasure(3);
        assertEquals(3, t.getTreasure(), "Got wrong treasure.");
        t.setTreasure(4);
        assertEquals(3, t.getTreasure(), "Got wrong treasure.");
        t.setTreasure(-2);
        assertEquals(3, t.getTreasure(), "Got wrong treasure.");
    }
}
