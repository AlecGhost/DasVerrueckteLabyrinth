package DasVerrueckteLabyrinth;

public interface Labyrinth {
    public ImmutableTile[] getBoard();      // returns current board containing tiles with game state
    public ImmutableTile getSpareTile();    // returns current spare tile, the tile to be inserted next
    public int getPlayer();                 // returns current player number (0-3)
    public int[] getTreasureCount();        // returns treasure count as int array. The number of treasures collected by a player is on its index.
    public boolean isGameOver();            // returns if all conditions for the game to end are fulfilled
    public boolean move(int x, int y);      // tries to move current player to desired position. True if it worked, false if not.
    public boolean shift(int pos);          // tries to shift the tiles in the desired direction. True if it worked, false if not.
    public void rotateSpareTile();          // rotates spare tile
}



