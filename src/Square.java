// Square.java
// Gordon Lin
// Class which stores each square object that'll be displayed on the board

import javax.swing.*;
import java.awt.*;
import java.awt.image.*;

public class Square{
    // Constant Image array which imports and stores each colour of the square images from the assets folder
    public static final Image[] SQUARES ={
            new ImageIcon("./src/assets/images/cyan_block.png").getImage(),
            new ImageIcon("./src/assets/images/blue_block.png").getImage(),
            new ImageIcon("./src/assets/images/orange_block.png").getImage(),
            new ImageIcon("./src/assets/images/yellow_block.png").getImage(),
            new ImageIcon("./src/assets/images/green_block.png").getImage(),
            new ImageIcon("./src/assets/images/purple_block.png").getImage(),
            new ImageIcon("./src/assets/images/red_block.png").getImage(),
            new ImageIcon("./src/assets/images/gray_block.png").getImage()
    };

    // x and y offset (offset by square size, not pixel) and colour of the square
    private final int x, y, colour;

    // Constructor that takes in x and y offset and colour
    public Square(int x, int y, int colour){
        this.x = x;
        this.y = y;
        this.colour = colour;
    }

    // Constructor that only takes in colour, no offset
    public Square(int colour){
        this.x = 0;
        this.y = 0;
        this.colour = colour;
    }

    // Getter methods that get the x and y offset
    public int getX(){ return this.x; }
    public int getY(){ return this.y; }

    // Draw method which draws each individual square, requiring the Graphics2D object, position on the screen, size of each square, and ImageObserver object
    // Draws the image corresponding to the Square colour
    public void draw(Graphics2D g2d, int x, int y, int size, ImageObserver imageObserver){
        g2d.drawImage(SQUARES[colour], x + this.x * size, y + this.y*size, size, size, imageObserver);
    }
}
