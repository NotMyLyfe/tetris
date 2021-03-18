// Board.java
// Gordon Lin
// Board class that stores the squares and blocks of the game board, as well as other blocks

import java.awt.*;
import java.awt.image.*;

public class Board {
    // Constant to add on the width and height to include border of board
    public static int PLUS_BORDER = 2;
    // Constant that stores Color for a transparent black
    public static Color TRANSPARENT_BLACK = new Color(0, 0, 0, 200);
    // Constant that gets the Square object for the border
    public static Square BORDER_BLOCK = new Square(7);

    // Integers for x, y of the centre of board (by pixel on the screen), size of each square, top left X and Y value (by pixel on the screen), and width and height of the board
    private final int x, y, squareSize, topLeftX, topLeftY, width, height;
    // Array for each square of the board
    private Square[][] board;

    public Board(int x, int y, int squareSize, int width, int height){
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.squareSize = squareSize;

        // Sets the top left corner position, by subtracting the half the width * square size and half the height * square
        topLeftX = x - squareSize * width / 2;
        topLeftY = y - squareSize * height / 2;

        // Generates an array width x height full of null
        board = new Square[width][height];
    }

    // Getter methods for width and height
    public int getWidth(){ return width; }
    public int getHeight() { return height; }

    // Draw method which just draw the board itself
    public void draw(Graphics2D g2d, ImageObserver imageObserver){
        // Loops through each x position of the board (by square), including border, and draws the border block on the top and bottom
        for(int i = 0; i < width + PLUS_BORDER; i++){
            BORDER_BLOCK.draw(g2d, topLeftX - squareSize + squareSize * i, topLeftY - squareSize, squareSize, imageObserver);
            BORDER_BLOCK.draw(g2d, topLeftX - squareSize + squareSize * i, topLeftY + squareSize * height, squareSize, imageObserver);
        }
        // Loops through each y value of the board (by square), and draws the border block on the left and right
        for(int i = 0; i < height; i++){
            BORDER_BLOCK.draw(g2d, topLeftX - squareSize, topLeftY + squareSize * i, squareSize, imageObserver);
            BORDER_BLOCK.draw(g2d, topLeftX + squareSize * width, topLeftY + squareSize * i, squareSize, imageObserver);
        }
        // Draws a transparent black background on the whole board
        g2d.setColor(TRANSPARENT_BLACK);
        g2d.fillRect(topLeftX, topLeftY, squareSize * width, squareSize * height);

        // Loops through each square in the array and draws the respective square at the position on the board if it exists
        for(int rows = 0; rows < height; rows++){
            for(int columns = 0; columns < width; columns++){
                if(board[columns][rows] != null) board[columns][rows].draw(g2d, topLeftX + columns * squareSize, topLeftY + rows * squareSize, squareSize, imageObserver);
            }
        }
    }

    // Draws the board with a block on it
    public void draw(Graphics2D g2d, Tetrominoes currentTetromino, ImageObserver imageObserver){
        // Draws the grid itself using the method previously mentioned
        draw(g2d, imageObserver);
        // If the block isn't null
        if(currentTetromino != null)
            // Draws the block, with the position of the board on the screen
            currentTetromino.draw(g2d, topLeftX, topLeftY, squareSize, imageObserver);
    }

    // Method that clears the rows and returns the number of rows cleared
    public int clearRows(){
        // Stores number of rows cleared
        int rowsCleared = 0;
        // Loops through each row of the board, starting from the bottom
        for(int rows = height - 1; rows >= 0; rows--){
            // Sets clear to true
            boolean clearRow = true;
            // Loops through each column of the row, and checks if it's not null, if it is, sets clear to false and breaks out of the loop
            for(int columns = 0; columns < width; columns++){
                if(board[columns][rows] == null){
                    clearRow = false;
                    break;
                }
            }
            // If all the columns of the row aren't null
            if(clearRow){
                rowsCleared++;
                // Loops through all the previous rows and shifts all the rows down
                for(int previousRows = rows; previousRows > 0; previousRows--){
                    for(int columns = 0; columns < width; columns++) board[columns][previousRows] = board[columns][previousRows - 1];
                }
                // Increments one to rows, so that the loop rechecks the current row, after being cleared and shifted
                rows++;
            }
        }
        // Returns number of rows cleared
        return rowsCleared;
    }

    // Checks if a position is occupied, first by checking if it's out of bounds of the board, and the position in the array isn't null
    private boolean isOccupied(int x, int y){
        if(x >= width || x < 0 || y >= height || y < 0) return true;
        return board[x][y] != null;
    }

    // Checks if a block will collide with the board with changes of vx, vy, and rotate
    public boolean isCollide(Tetrominoes currentTetromino, int vx, double vy, int rotate){
        // Gets the position of the block on the board (by square) after the changes, and current rotation state after the changes
        int tetrominoX = currentTetromino.getX() + vx, tetrominoY = (int) (currentTetromino.getY() + vy),
                tetrominoRotate = (currentTetromino.getRotate() + rotate + Tetrominoes.NUM_ROTATIONS) % Tetrominoes.NUM_ROTATIONS;

        // Gets the squares of the block with offsets from the top left position of the block
        Square[] squares = Tetrominoes.TETROMINOES[currentTetromino.getTetrominoNum()][tetrominoRotate];

        // Loops through all the squares of the block
        for(Square square : squares){
            // Checks if the position of the square, if on the position on the board, including offset position from the block and the square, were to collide with anything on the board
            if(isOccupied(square.getX() + tetrominoX, square.getY() + tetrominoY)){
                // returns that a collision has occurred
                return true;
            }
        }
        // Returns false for no collision
        return false;
    }

    // Checks for collisions for rotating on the spot
    public boolean isCollide(Tetrominoes currentTetromino, int rotate){
        return isCollide(currentTetromino, 0, 0, rotate);
    }

    // Checks for collision for moving vertically and horizontally but no rotation
    public boolean isCollide(Tetrominoes currentTetromino, int vx, double vy){
        return isCollide(currentTetromino, vx, vy, 0);
    }

    // Checks for collision at the block's current location
    public boolean isCollide(Tetrominoes currentTetromino){
        return isCollide(currentTetromino, 0, 0, 0);
    }

    // Adds the squares of the block to the board
    public void addBlock(Tetrominoes currentTetromino){
        // Loops through all the squares of the block
        for(Square square : Tetrominoes.TETROMINOES[currentTetromino.getTetrominoNum()][currentTetromino.getRotate()]){
            // Sets each square where the block was on the board to the squares of the block
            board[square.getX() + currentTetromino.getX()][(int) (square.getY() + currentTetromino.getY())] = new Square(currentTetromino.getTetrominoNum());
        }
    }

    // Gets the distance for a block to hard drop
    public int drop(Tetrominoes currentTetromino){
        // Stores distance that's being dropped
        int dy = 0;
        // Checks while it doesn't collide if it moves down 1, increments to the distance being dropped
        while(!isCollide(currentTetromino, 0, dy + 1)){
            dy++;
        }
        // Returns distance dropped
        return dy;
    }


    // Resets the board by generating a new array
    public void reset(){
        board = new Square[width][height];
    }
}
