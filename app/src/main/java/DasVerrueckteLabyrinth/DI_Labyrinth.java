package DasVerrueckteLabyrinth;

import processing.core.PApplet;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

public class DI_Labyrinth extends PApplet {
    // basic graphical size constants
    private final int w = 700;                                  // width and height of the labyrinth && edit only this to change the size
    private final int rows = 7;                                 // number of rows, must be 7
    private final int menuSize = w / 4;                         // height of the menu
    private final int borderSize = w / 25;                      // width of the border around the labyrinth
    private final int labyrinthPartSize = 2 * borderSize + w;   // width and height of the upper/labyrinth part
    private final int tileSize = w / rows;                      // width of each individual tile
    private final int iconSize = w / 30;                        // radius of player circle and width/height of treasure square
    private final int textSize = w / 60;                        // size of normal text fields
    private final int buttonHeight = borderSize / 2;            // height of each button
    private final int buttonMargin = borderSize / 4;            // amount of space around the button

    // constant color values
    private final int backgroundColor = color(34, 141, 46);
    private final int menuColor = 150;
    private final int pathColor = 255;
    private final int textColor = 0;
    private final int tileColor = color(215, 152, 66);
    private final int shiftButtonColor = color(141, 106, 34);
    private final int menuButtonColor = color(100);
    private final Map<Integer, Integer> playerColors = Map.of(
            0, color(241, 196, 15),     //Player 0
            1, color(52, 152, 219),     //Player 1
            2, color(231, 76, 60),      //Player 2
            3, color(39, 174, 96)       //Player 3
    );

    // constant array for conversion of shifting button index to button coordinates
    private final int[][] buttonPoss = {
            // top side
            {borderSize + 1 * tileSize, buttonMargin},
            {borderSize + 3 * tileSize, buttonMargin},
            {borderSize + 5 * tileSize, buttonMargin},
            // right side
            {borderSize + w + buttonMargin, borderSize + 1 * tileSize},
            {borderSize + w + buttonMargin, borderSize + 3 * tileSize},
            {borderSize + w + buttonMargin, borderSize + 5 * tileSize},
            // bottom side
            {borderSize + 5 * tileSize, borderSize + w + buttonMargin},
            {borderSize + 3 * tileSize, borderSize + w + buttonMargin},
            {borderSize + 1 * tileSize, borderSize + w + buttonMargin},
            // left side
            {buttonMargin, borderSize + 5 * tileSize},
            {buttonMargin, borderSize + 3 * tileSize},
            {buttonMargin, borderSize + 1 * tileSize}
    };

    // mutable variables
    private Labyrinth labyrinth = new AL_Labyrinth(2); // optional: put Random with seed in constructor for constant outcome. Seed 0 is used in tests.
    private List<Button> buttons = new ArrayList<>();

    public static void main(String[] args) { // Starting point, starts Processing engine
        PApplet.runSketch(new String[]{"Das verrückte Labyrinth"}, new DI_Labyrinth());
    }

    public void settings() { // sets window size
        size(labyrinthPartSize, borderSize + w + menuSize);
    }

    public void setup() { // creates all buttons to detect mouse clicks
        frameRate(5);   // sets redraw speed
        textAlign(CENTER);  // sets text alignment
        strokeCap(SQUARE);  // sets shape of the end of a stroke
        /// Setup Labyrinth part
        // Add buttons for tiles
        IntStream.range(0, labyrinth.getBoard().length)
                .forEach(n ->
                        buttons.add(
                                new Button(this,
                                        borderSize + tileSize * (n % rows),
                                        borderSize + tileSize * (n / rows),
                                        tileSize, tileSize) {
                                    @Override
                                    public boolean clicked() { // executes move() for the clicked button tile
                                        return labyrinth.move(n % rows, n / rows);
                                    }
                                }));

        // Add shifting buttons
        IntStream.range(0, buttonPoss.length)
                .forEach(i -> {
                    boolean isHorizontal = (i / 3) % 2 == 0;
                    buttons.add(new Button(this,
                            buttonPoss[i][0], buttonPoss[i][1],
                            isHorizontal ? tileSize : buttonHeight,
                            isHorizontal ? buttonHeight : tileSize) {
                        @Override
                        public boolean clicked() { // shifts in the desired row/column
                            return labyrinth.shift(i);
                        }
                    });
                });

        /// Setup Menu part
        // Add button for spare tile
        buttons.add(
                new Button(this,
                        borderSize + 3 * tileSize,
                        labyrinthPartSize + menuSize / 7,
                        tileSize, tileSize) {
                    @Override
                    public boolean clicked() { // if clicked, the spare tile is rotated
                        labyrinth.rotateSpareTile();
                        return true;
                    }
                });

        // Add restart button
        buttons.add(
                new Button(this, borderSize, labyrinthPartSize + borderSize, tileSize, buttonHeight) {
                    @Override
                    public boolean clicked() { // overwrites labyrinth therefore game is started again
                        labyrinth = new AL_Labyrinth(2); // Overrides current labyrinth
                        frameRate(15);  // resets the frame rate in case it was lowered by checkGameOver()
                        draw();             // restarts draw() in case it was stopped by checkGameOver()
                        return true;
                    }
                });
    }

    public void draw() { // draws what's on the screen
        background(backgroundColor);

        /// Draw labyrinth (upper) part
        // Draw the tiles
        ImmutableTile[] board = labyrinth.getBoard();
        IntStream.range(0, board.length).forEach(i ->
                drawTile(board[i], borderSize + tileSize * (i % rows), borderSize + tileSize * (i / rows)));

        // Draw the buttons for shifting
        fill(shiftButtonColor);
        IntStream.range(0, buttonPoss.length).forEach(i -> {
            boolean isHorizontal = (i / 3) % 2 == 0;
            rect(buttonPoss[i][0], buttonPoss[i][1],
                    isHorizontal ? tileSize : buttonHeight,
                    isHorizontal ? buttonHeight : tileSize);
        });

        /// Draw menu (lower) part
        // Draw menu background
        fill(menuColor);
        rect(0, labyrinthPartSize, labyrinthPartSize, labyrinthPartSize + menuSize);

        // Draw Spare Tile
        ImmutableTile spareTile = labyrinth.getSpareTile();
        stroke(0);
        drawTile(spareTile, borderSize + 3 * tileSize, labyrinthPartSize + menuSize / 7);
        fill(textColor);
        textSize(textSize);
        text("Click the spare tile to rotate it.",
                borderSize + 3.5f * tileSize, labyrinthPartSize + menuSize / 7 - textSize / 2);

        // Draw restart button
        fill(menuButtonColor);
        rect(borderSize, labyrinthPartSize + borderSize, tileSize, buttonHeight);
        fill(textColor);
        text("Restart", borderSize + tileSize / 2, labyrinthPartSize + borderSize + textSize);

        // Draw treasure count / player indicator
        for (int i = 0; i < 4; i++) {
            int playerColor = playerColors.get(i);
            float xOffset = borderSize + (i + 0.5f) * tileSize / 4;
            float yOffset = labyrinthPartSize + 2.25f * borderSize;
            boolean isCurrentPlayer = labyrinth.getPlayer() == i;
            if (!isCurrentPlayer) noStroke();
            fill(playerColor);
            circle(xOffset, yOffset, iconSize);
            if (!isCurrentPlayer) { // other players get grayed out
                fill(120, 100);
                circle(xOffset, yOffset, iconSize);
            }
            stroke(0);
            fill(textColor);
            text(labyrinth.getTreasureCount()[i], xOffset, yOffset + w / 150, iconSize);
        }
    }

    private void drawTile(ImmutableTile t, int posX, int posY) { // draws the given tile on its position
        // draw background square
        fill(tileColor);
        square(posX, posY, tileSize);
        // convert openDirections to usable booleans for each direction
        boolean[] openDirections = t.getOpenDirections();
        boolean up = openDirections[0];
        boolean right = openDirections[1];
        boolean down = openDirections[2];
        boolean left = openDirections[3];
        // evaluate middle of tile
        int midX = posX + tileSize / 2;
        int midY = posY + tileSize / 2;
        // draw the paths
        stroke(pathColor);
        strokeWeight(2);
        if (up) line(midX, midY, midX, midY - tileSize / 2);
        if (right) line(midX, midY, midX + tileSize / 2, midY);
        if (down) line(midX, midY, midX, midY + tileSize / 2);
        if (left) line(midX, midY, midX - tileSize / 2, midY);
        stroke(0);
        strokeWeight(1);
        // draw the treasure if there
        if (t.getTreasure() != -1) {
            int treasureColor = playerColors.get(t.getTreasure());
            fill(treasureColor, 200);
            float offset = iconSize / 2f;
            square(midX - offset, midY - offset, iconSize);
        }
        // draw the player if there
        if (t.getPlayer() != -1) {
            int playerColor = playerColors.get(t.getPlayer());
            fill(playerColor);
            noStroke();
            circle(midX, midY, iconSize);
            stroke(0);
        }
    }

    private void checkGameOver() { // if the game is over, draws a banner over the screen
        if (labyrinth.isGameOver()) {
            draw();
            fill(255, 0, 0);
            rect(0, borderSize + 3 * tileSize, labyrinthPartSize, tileSize);
            noStroke();
            fill(textColor);
            textSize(w / 12);
            text("Game Over", labyrinthPartSize / 2, labyrinthPartSize / 2 + tileSize / 6);
            frameRate(0.2f); // drop frame rate to show the banner for longer,
            // therefore restart will take longer to show, if clicked
        }
    }

    @Override
    public void mousePressed() { // checks if a button was pressed, if a mouse button is pressed
        buttons.forEach(Button::checkClick);
        checkGameOver();
    }
}
