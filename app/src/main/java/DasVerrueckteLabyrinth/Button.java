package DasVerrueckteLabyrinth;

import processing.core.PApplet;

public abstract class Button {
    private final PApplet pApplet;
    private final int posX, posY, width, height;

    Button(PApplet pApplet, int posX, int posY, int width, int height) {
        this.pApplet = pApplet;
        this.posX = posX;
        this.posY = posY;
        this.width = width;
        this.height = height;
    }

    public void checkClick() {
        // this method gets called every time a mouse button is clicked
        // if the current mouse position is over the button, clicked() and animateClick() are executed
        int mX = pApplet.mouseX;
        int mY = pApplet.mouseY;
        if (posX <= mX
                && mX <= posX + width
                && posY <= mY
                && mY <= posY + height) animateClick(clicked());
    }

    private void animateClick(boolean didWork) {
        if (didWork) pApplet.fill(0, 50);           // click effect is black when successful
        else pApplet.fill(255, 0, 0, 50);   // and red if not
        pApplet.rect(posX, posY, width, height);    // rectangle over button disappears after next draw loop
    }

    public abstract boolean clicked();                        // the executed tasks on click are specified when each button is created
}
