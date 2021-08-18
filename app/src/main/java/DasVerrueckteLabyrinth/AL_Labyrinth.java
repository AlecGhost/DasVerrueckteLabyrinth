package DasVerrueckteLabyrinth;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

public class AL_Labyrinth implements Labyrinth {
    private final int players = 4;          // must be 4 for the game to work
    // constant arrays for conversion
    private final int[] startPoss = new int[]{42, 48, 6, 0};
    private final int[] pos2Opposite = {8, 7, 6, 11, 10, 9, 2, 1, 0, 5, 4, 3};
    private final int[] pos2Rotations = {0, 0, 0, 3, 3, 3, 2, 2, 2, 1, 1, 1};
    private final int[] relNeighbors = {-7, +1, +7, -1};
    private final int[] matrixRotation = IntStream.range(0, 49).parallel()
            .map(n -> n / 7 + 42 - (7 * (n % 7))).toArray(); // equals a 90° tilted 7 x 7 matrix
    // variables initialized by constructor
    private final Random random;
    private final int maxTreasures;
    private Tile[] board;
    private Tile spareTile;
    // mutable values resembling the current game state
    private boolean shifting = true;
    private int lastShift = -1;
    private int currentPlayer = 0;
    private int[] playerPoss = Arrays.copyOf(startPoss, startPoss.length);
    private int[] treasureCount = {0, 0, 0, 0};

    AL_Labyrinth(int maxTreasures, Random random) {
        this.maxTreasures = maxTreasures;
        this.random = random;
        this.spareTile = Tile.randomOf(random);
        board = IntStream.range(0, 49).mapToObj(i -> Tile.randomOf(random)).toArray(Tile[]::new); // init board with random tiles
        IntStream.range(0, players).forEach(i -> {
            board[startPoss[i]] = new Tile(Shape.EDGE).rotate(i).setPlayer(i); // init starting positions
            createNewTreasure(i); // init first treasures
        });
    }

    AL_Labyrinth(int maxTreasures) {
        this(maxTreasures, new Random());
    }

    private void createNewTreasure(int player) { // searches valid random position to place the next treasure
        random.ints(0, board.length)
                .filter(i -> Arrays.stream(startPoss).noneMatch(j -> j == i)) // not on the edges
                .mapToObj(n -> board[n])
                .filter(tile -> tile.getPlayer() == -1 && tile.getTreasure() == -1) // not on a tile that already has a player or a treasure
                .findAny().orElse(board[1])
                .setTreasure(player);
    }

    private void updateTreasures() { // if the right player stands on the right treasure,
        // his treasure count is updated and a new treasure spawns, if he hasn't found all already
        Arrays.stream(board).parallel()
                .filter(tile -> tile.getTreasure() != -1 && tile.getPlayer() == tile.getTreasure())
                .forEach(tile -> {
                    int player = tile.getPlayer();
                    tile.setTreasure(-1);
                    treasureCount[player]++;
                    if (treasureCount[player] < maxTreasures) createNewTreasure(player);
                });
    }

    private int[] getNeighbors(int pos) { // returns the index of neighboring tiles, if they exist
        // order of neighbors in array is up, right, down, left
        int[] neighbors = {-1, -1, -1, -1}; // -1 means no neighbor
        for (int i = 0; i < relNeighbors.length; i++) {
            int newPos = pos + relNeighbors[i]; // converts relative neighboring position to absolute
            // looks if newPos is still on the board and if it is in the same row for +1 und -1
            if (0 <= newPos && newPos < 49 && (relNeighbors[i] % 7 == 0 || pos / 7 == newPos / 7)) {
                neighbors[i] = newPos;
            }
        }
        return neighbors;
    }

    private boolean hasPath(int start, int end, List<Integer> checked) { // evaluates recursively if there is a path from the current tile to the targeted one
        /* exit condition: if start was set to -1 this option was already determined as not possible;
         *  if start-tile was already checked by a recursive branch before this, it does not need to be checked again.*/
        if (start == -1 || checked.stream().parallel().anyMatch(n -> n == start)) return false;
        // exit condition: if the recursive start field is equal to the original target field, a path exists.
        if (start == end) return true;
        boolean[] openDirs = board[start].getOpenDirections();
        int[] neighbors = getNeighbors(start); // evaluates the indices of neighboring tiles
        for (int i = 0; i < openDirs.length; i++) {
            if (neighbors[i] == -1) continue; // no neighboring tile on this side
            boolean[] openDirsNeighbor = board[neighbors[i]].getOpenDirections();
            /* if the current tile and the neighboring tile are both open (true) on the facing sides, this neighbor stays valid.
             *  if not, this neighbor is set to -1 and therefore not checked in future calls. */
            if ((openDirs[i] && openDirsNeighbor[i + 2 * (i / 2 * -2 + 1)])) { // f(0) = 2, f(1) = 3, f(2) = 0, f(3) = 1
                checked.add(start); // add current position to checked list
                // recursive call of hasPath for each neighboring tiles
                if (hasPath(neighbors[i], end, checked)) return true;
            }
        }
        return false; // if every possible path was checked and none returned true, there is no path.
    }

    private void nextPlayer() { // updates current player to the next
        currentPlayer = (currentPlayer + 1) % players;
    }

    private void updatePlayers() { // updates the array "playerPoss" to show the current position of each player
        IntStream.range(0, board.length).parallel()
                .filter(n -> board[n].getPlayer() != -1)
                .forEach(n -> playerPoss[board[n].getPlayer()] = n);
    }

    @Override
    public boolean move(int x, int y) {
        int newPos = x + 7 * y; // conversion from coordinates to 1D-array index
        // returns false if shifting mode is active or the index is invalid
        if (shifting || newPos < 0 || board.length <= newPos) return false;
        int currentPos = playerPoss[currentPlayer];
        Tile target = board[newPos];
        Tile current = board[currentPos];
        // returns true and ends turn if the field is the one the player currently stands on
        if (newPos == currentPos) {
            nextPlayer();
            shifting = true;
            return true;
        } else if (target.getPlayer() == -1 && hasPath(currentPos, newPos, new ArrayList<>())) { // moves the player if there is no other player and a path
            current.setPlayer(-1);              // removes player from current field
            target.setPlayer(currentPlayer);    // and puts him on the new one
            // updating the game state
            updatePlayers();
            updateTreasures();
            nextPlayer();
            shifting = true;
            return true;
        } else return false; // no valid move if this statement is reached
    }

    private Tile[] rotateBoard(Tile[] b, int times) { // calls other rotateBoard as often as specified in times.
        while (times < 0) times += 4; // because % of a negative number is still negative
        for (int i = 0; i < times % 4; i++) b = rotateBoard(b);
        return b;
    }

    private Tile[] rotateBoard(Tile[] b) { // returns a rotated version of board b, actual board is not touched
        /* matrixRotation looks like this, a 90° tilted 7 x 7 matrix:
        *   int[] matrixRotation = new int[]{
        *       42, 35, 28, 21, 14, 7, 0,
        *       43, 36, 29, 22, 15, 8, 1,
        *       44, 37, 30, 23, 16, 9, 2,
        *       45, 38, 31, 24, 17, 10, 3,
        *       46, 39, 32, 25, 18, 11, 4,
        *       47, 40, 33, 26, 19, 12, 5,
        *       48, 41, 34, 27, 20, 13, 6
        };*/
        return IntStream.range(0, board.length).parallel()
                .mapToObj(i -> b[matrixRotation[i]]).map(Tile::copyOf).toArray(Tile[]::new);
    }

    @Override
    public boolean shift(int pos) {
        // shifting must be active and index in range and it is not allowed to just shift back the last shift
        if (!shifting || pos < 0 || 11 < pos || pos2Opposite[pos] == lastShift) return false;
        int rotations = pos2Rotations[pos]; // conversion from row index to number of rotations needed
        Tile[] tempBoard = rotateBoard(board, rotations); // temporary board, to make the shift without rotating the actual board
        int firstIndexOfColumn = 2 * (pos % 3) + 1; // numbers 1, 3 and 5
        Tile newSpareTile = tempBoard[firstIndexOfColumn + 6 * 7]; // = tile on "lastIndexOfColumn"
        if (newSpareTile.getPlayer() != -1) { // repositions a player, if one stands on the tile shifted out of the labyrinth
            spareTile.setPlayer(newSpareTile.getPlayer()); // player is set on former (shifted in) spare tile
            newSpareTile.setPlayer(-1);
        }
        // each tile is shifted by one in the selected direction
        for (int i = 6; i > 0; i--)
            tempBoard[firstIndexOfColumn + i * 7] = tempBoard[firstIndexOfColumn + (i - 1) * 7];
        tempBoard[firstIndexOfColumn] = spareTile;
        spareTile = newSpareTile; // update the spare tile
        board = rotateBoard(tempBoard, 4 - rotations); // sets the board to the rotated-back and shifted temporary board
        // updating rest of the game state
        updatePlayers();
        updateTreasures();
        lastShift = pos;
        shifting = false;
        return true;
    }

    @Override
    public void rotateSpareTile() { // rotates the spare tile by one
        spareTile.rotate(1);
    }

    @Override
    public int getPlayer() {
        return currentPlayer;
    }

    @Override
    public boolean isGameOver() { // checks if a player got all necessary treasures and stands on his starting position again
        return IntStream.range(0, players)
                .anyMatch(i -> treasureCount[i] == maxTreasures && board[startPoss[i]].getPlayer() == i);
    }

    @Override
    public ImmutableTile[] getBoard() { // returns a deep copy of the board
        return Arrays.stream(board).parallel().map(Tile::copyOf).toArray(Tile[]::new);
    }

    @Override
    public ImmutableTile getSpareTile() { // returns copy of the spare tile
        return Tile.copyOf(spareTile);
    }

    @Override
    public int[] getTreasureCount() { // returns copy of the treasure count
        return Arrays.copyOf(treasureCount, treasureCount.length);
    }
}
