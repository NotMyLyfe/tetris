// Tetrominoes.java
// Gordon Lin
// Class which possess each block (tetromino) of the tetris game

import java.awt.*;
import java.awt.image.*;
import java.util.Random;

public class Tetrominoes {
    // Random object for random generation
    public static final Random random = new Random();

    // Constant which stores the Squares for each block (Tetromino), with rotation of each block, offset from the top left of the boundary that holds the block
    public static final Square[][][] TETROMINOES = new Square[][][]{
            { // I block
                    { new Square(0, 1, 0), new Square(1, 1, 0), new Square(2, 1, 0), new Square(3, 1, 0) },
                    { new Square(2, 0, 0), new Square(2, 1, 0), new Square(2, 2, 0), new Square(2, 3, 0) },
                    { new Square(0, 2, 0), new Square(1, 2, 0), new Square(2, 2, 0), new Square(3, 2, 0) },
                    { new Square(1, 0, 0), new Square(1, 1, 0), new Square(1, 2, 0), new Square(1, 3, 0) }
            },
            { // J Block
                    { new Square(0, 0, 1), new Square(0, 1, 1), new Square(1, 1, 1), new Square(2, 1, 1) },
                    { new Square(2, 0, 1), new Square(1, 0, 1), new Square(1, 1, 1), new Square(1, 2, 1) },
                    { new Square(0, 1, 1), new Square(1, 1, 1), new Square(2, 1, 1), new Square(2, 2, 1) },
                    { new Square(0, 2, 1), new Square(1, 2, 1), new Square(1, 1, 1), new Square(1, 0, 1) }
            },
            { // L Block
                    { new Square(0, 1, 2), new Square(1, 1, 2), new Square(2, 1, 2), new Square(2, 0, 2) },
                    { new Square(1, 0, 2), new Square(1, 1, 2), new Square(1, 2, 2), new Square(2, 2, 2) },
                    { new Square(0, 2, 2), new Square(0, 1, 2), new Square(1, 1, 2), new Square(2, 1, 2) },
                    { new Square(0, 0, 2), new Square(1, 0, 2), new Square(1, 1, 2), new Square(1, 2, 2) }
            },
            { // O Block
                    { new Square(1, 0, 3), new Square(2, 0, 3), new Square(1, 1, 3), new Square(2, 1, 3) },
                    { new Square(1, 0, 3), new Square(2, 0, 3), new Square(1, 1, 3), new Square(2, 1, 3) },
                    { new Square(1, 0, 3), new Square(2, 0, 3), new Square(1, 1, 3), new Square(2, 1, 3) },
                    { new Square(1, 0, 3), new Square(2, 0, 3), new Square(1, 1, 3), new Square(2, 1, 3) }
            },
            { // S Block
                    { new Square(0, 1, 4), new Square(1, 1, 4), new Square(1, 0, 4), new Square(2, 0, 4) },
                    { new Square(1, 0, 4), new Square(1, 1, 4), new Square(2, 1, 4), new Square(2, 2, 4) },
                    { new Square(0, 2, 4), new Square(1, 2, 4), new Square(1, 1, 4), new Square(2, 1, 4) },
                    { new Square(0, 0, 4), new Square(0, 1, 4), new Square(1, 1, 4), new Square(1, 2, 4) }
            },
            { // T block
                    { new Square(0, 1, 5), new Square(1, 1, 5), new Square(1, 0, 5), new Square(2, 1, 5) },
                    { new Square(1, 0, 5), new Square(1, 1, 5), new Square(2, 1, 5), new Square(1, 2, 5) },
                    { new Square(0, 1, 5), new Square(1, 1, 5), new Square(1, 2, 5), new Square(2, 1, 5) },
                    { new Square(1, 0, 5), new Square(1, 1, 5), new Square(0, 1, 5), new Square(1, 2, 5) },
            },
            { // Z Block
                    { new Square(0, 0, 6), new Square(1, 0, 6), new Square(1, 1, 6), new Square(2, 1, 6) },
                    { new Square(2, 0, 6), new Square(2, 1, 6), new Square(1, 1, 6), new Square(1, 2, 6) },
                    { new Square(0, 1, 6), new Square(1, 1, 6), new Square(1, 2, 6), new Square(2, 2, 6) },
                    { new Square(1, 0, 6), new Square(1, 1, 6), new Square(0, 1, 6), new Square(0, 2, 6) }
            }
    };

    // Constant which store the boundary size of each tetromino
    public static final int[] TETROMINO_BOUNDARY_WIDTH_HEIGHT = new int[]{
            4, 3, 3, 4, 3, 3, 3
    };

    // Constant which stores the translations when rotating clockwise, corresponding to the current rotation state of the block, in order of cases and translation that occur first
    public static final Point[][][] TETROMINO_CLOCKWISE_ROTATION_TRANSLATION = new Point[][][]{
            { // I block translations
                    { new Point(-2, 0), new Point(1, 0), new Point(-2, 1), new Point(1, -2) },
                    { new Point(-1, 0), new Point(2, 0), new Point(-1, -2), new Point(2, 1) },
                    { new Point(2, 0), new Point(-1, 0), new Point(2 , -1), new Point(-1, 2) },
                    { new Point(1, 0), new Point(-2, 0), new Point(1, 2), new Point(-2, -1) }
            },
            { // Non-I block translations
                    { new Point(-1, 0), new Point(-1, -1), new Point(0, 2), new Point(-1, 2) },
                    { new Point(1, 0), new Point(1, 1), new Point(0, -2), new Point(1, -2) },
                    { new Point(1, 0), new Point(1, -1), new Point(0, 2), new Point(1, 2) },
                    { new Point(-1, 0), new Point(-1, 1), new Point(0, -2), new Point(-1, -2) }
            }
    };

    // Constant which stores the translations when rotating counterclockwise, corresponding to the current rotation state of the block, in order of cases and translation that occur first
    public static final Point[][][] TETROMINO_COUNTERCLOCKWISE_ROTATION_TRANSLATION = new Point[][][]{
            { // I block translations
                    { new Point(-1, 0), new Point(2, 0), new Point(-1, -2), new Point(2, 1) },
                    { new Point(2, 0), new Point(-1, 0), new Point(2, -1), new Point(-1, 2) },
                    { new Point(1, 0), new Point(-2, 0), new Point(1, 2), new Point(-2, -1) },
                    { new Point(-2, 0), new Point(1, 0), new Point(-2, 1), new Point(1, -2) }
            },
            { // Non-I block translations
                    { new Point(1, 0), new Point(1, -1), new Point(0, 2), new Point(1, 2) },
                    { new Point(1, 0), new Point(1, 1), new Point(0, -2), new Point(1, -2) },
                    { new Point(-1, 0), new Point(-1, -1), new Point(0, 2), new Point(-1, 2) },
                    { new Point(-1, 0), new Point(-1, 1), new Point(0, -2), new Point(-1, -2) }
            }
    };

    // Total number of available of blocks/tetrominoes
    public static final int TOTAL_NUM_OF_TETROMINOES = 7;
    // Total number of available rotations
    public static final int NUM_ROTATIONS = 4;
    // Value for I block
    public static final int I_BLOCK_VALUE = 0;
    // Value for O block
    public static final int O_BLOCK_VALUE = 3;
    // Index for rotation of translation
    public static final int NON_I_BLOCK_ROTATION_TRANSLATION_INDEX = 1;

    // Stores the current state of rotation of the block
    private int rotate;
    // Stores the x position on the game board
    private int x;
    // Stores the number of the block/tetromino
    private final int tetrominoNum;
    // Stores the y position on the game board
    private double y;


    // Constructor which sets the tetrominoNum to a random number from 0 to TOTAL_NUM_OF_TETROMINOES, and sets x, y, rotate to 0
    public Tetrominoes(){
        this.tetrominoNum = random.nextInt(TOTAL_NUM_OF_TETROMINOES);
        this.x = 0;
        this.y = 0;
        this.rotate = 0;
    }


    // Getter methods which gets each value of the object
    public int getX(){ return this.x; }
    public double getY(){ return this.y; }
    public int getTetrominoNum() { return this.tetrominoNum; }
    public int getRotate() { return this.rotate; }

    // Setter method which sets the X and Y values
    public void setX(int x) { this.x = x; }
    public void setY(double y) { this.y = y; }

    // Sets the position of the top left corner of the block relative to the game board
    public void setLeftCornerPos(int x, double y){
        setX(x);
        setY(y);
    }

    // Sets the position of the top centre of the block relative to the game board
    public void setTopCentrePos(int x, int y){
        setLeftCornerPos(x - TETROMINO_BOUNDARY_WIDTH_HEIGHT[tetrominoNum] / 2, y);
    }

    // Sets the position of the centre of the block relative to the game board
    public void setCentrePos(int x, int y){
        setLeftCornerPos(x - TETROMINO_BOUNDARY_WIDTH_HEIGHT[tetrominoNum] / 2, y - TETROMINO_BOUNDARY_WIDTH_HEIGHT[tetrominoNum] / 2);
    }

    // Resets rotate to 0
    public void resetRotate(){ rotate = 0; }

    // Truncates the y value
    public void truncateY(){
        y = (int) y;
    }

    // Rotates the block, by add rotate by number of times rotating clockwise (negative results in counterclockwise rotation)
    public void rotate(int rotate){
        this.rotate = (this.rotate + rotate + NUM_ROTATIONS) % NUM_ROTATIONS;
    }

    // Moves the block by vx value and vy value
    public void move(int vx, double vy){
        x += vx;
        y += vy;
    }

    // Draws the block, by getting the pixel position of the block relative to the screen on the graph
    public void draw(Graphics2D g2d, int x, int y, int size, ImageObserver imageObserver){
        // Loops through all the squares of the block
        for(Square i : TETROMINOES[tetrominoNum][rotate]){
            // Draws the square, offset by the position of the block on the screen (position on the grid * size of the block)
            i.draw(g2d,x + this.x * size,  y + (int)(this.y) * size, size, imageObserver);
        }
    }

}
