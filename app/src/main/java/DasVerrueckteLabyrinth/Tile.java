package DasVerrueckteLabyrinth;

import com.google.common.primitives.Booleans;

import java.util.*;
import java.util.stream.IntStream;

public class Tile implements ImmutableTile {
    private int player = -1;   // -1 is no player, 0 to 3 are the four players
    private int treasure = -1; // -1 is no treasure, the player's number is his treasure
    private boolean[] openDirections = new boolean[4];   // 0: up, 1: right, 2: down, 3: left

    Tile(Shape s) {
        setShape(s);
    }

    private Tile(int treasure, int player, boolean[] openDirections) { // necessary to create deep copies of tiles
        this.treasure = treasure;
        this.player = player;
        this.openDirections = Arrays.copyOf(openDirections, 4);
    }

    static Tile randomOf(Random random) { // random is selected outside of the class to make it testable
        Shape[] shapes = Shape.values();
        return new Tile(shapes[random.nextInt(shapes.length)]).rotate(random.nextInt(4)); // creates new random tile
    }

    static public Tile copyOf(Tile t) { // returns copy of tile, necessary for deep copies
        return new Tile(t.treasure, t.player, t.openDirections);
    }

    private void setShape(Shape s) { // sets the boolean values of openDirections associated with the shape
        IntStream.range(0, openDirections.length).parallel().forEach(n -> {
            switch (s) {
                case STRAIGHT -> openDirections[n] = n % 2 == 0; // the two opposing directions
                case EDGE -> openDirections[n] = n < 2;          // two directions on one half
                case T -> openDirections[n] = n < 3;             // all but one
            }
        });
    }

    public Tile rotate(int times) { // rotates the tile by shifting the values of openDirections
        while (times < 0) times += 4; // because % of a negative number is still negative
        IntStream.range(0, times % 4).forEach(n -> Collections.rotate(Booleans.asList(openDirections), -1));
        return this;
    }

    @Override
    public int getTreasure() { // returns the stored treasure
        return treasure;
    }

    public Tile setTreasure(int treasure) { // edits treasure field to valid value
        if (-1 <= treasure && treasure <= 3) this.treasure = treasure;
        return this;
    }

    @Override
    public int getPlayer() { // returns the stored player
        return player;
    }

    public Tile setPlayer(int player) { // edits player field to valid value
        if (-1 <= player && player <= 3) this.player = player;
        return this;
    }

    @Override

    public boolean[] getOpenDirections() { // returns openDirections boolean array
        return Arrays.copyOf(openDirections, openDirections.length);
    }
}
