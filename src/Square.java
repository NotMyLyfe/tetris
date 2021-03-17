import javax.swing.*;
import java.awt.*;
import java.awt.image.*;

public class Square{
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

    private final int x;
    private final int y;
    private final int colour;

    public Square(int x, int y, int colour){
        this.x = x;
        this.y = y;
        this.colour = colour;
    }

    public Square(int colour){
        this.x = 0;
        this.y = 0;
        this.colour = colour;
    }

    public int getX(){ return this.x; }
    public int getY(){ return this.y; }

    public void draw(Graphics g, int x, int y, int size, ImageObserver imageObserver){
        g.drawImage(SQUARES[colour], x + this.x * size, y + this.y*size, size, size, imageObserver);
    }
}
