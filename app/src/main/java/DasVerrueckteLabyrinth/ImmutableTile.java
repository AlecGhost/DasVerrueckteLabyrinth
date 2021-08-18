package DasVerrueckteLabyrinth;

public interface ImmutableTile {
    public int getPlayer();                 // returns player on tile
    public int getTreasure();               // returns treasure on tile
    public boolean[] getOpenDirections();   // returns open directions of tile
}
