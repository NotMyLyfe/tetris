import java.awt.*;
import java.awt.image.*;
import java.util.Random;

public class Tetrominoes {
    public static final Random random = new Random();

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

    public static final int[] TETROMINO_BOUNDARY_WIDTH_HEIGHT = new int[]{
            4, 3, 3, 4, 3, 3, 3
    };

    public static final Point[][][] TETROMINO_CLOCKWISE_ROTATION_TRANSLATION = new Point[][][]{
            {
                    { new Point(-2, 0), new Point(1, 0), new Point(-2, 1), new Point(1, -2) },
                    { new Point(-1, 0), new Point(2, 0), new Point(-1, -2), new Point(2, 1) },
                    { new Point(2, 0), new Point(-1, 0), new Point(2 , -1), new Point(-1, 2) },
                    { new Point(1, 0), new Point(-2, 0), new Point(1, 2), new Point(-2, -1) }
            },
            {
                    { new Point(-1, 0), new Point(-1, -1), new Point(0, 2), new Point(-1, 2) },
                    { new Point(1, 0), new Point(1, 1), new Point(0, -2), new Point(1, -2) },
                    { new Point(1, 0), new Point(1, -1), new Point(0, 2), new Point(1, 2) },
                    { new Point(-1, 0), new Point(-1, 1), new Point(0, -2), new Point(-1, -2) }
            }
    };

    public static final Point[][][] TETROMINO_COUNTERCLOCKWISE_ROTATION_TRANSLATION = new Point[][][]{
            {
                    { new Point(-1, 0), new Point(2, 0), new Point(-1, -2), new Point(2, 1) },
                    { new Point(2, 0), new Point(-1, 0), new Point(2, -1), new Point(-1, 2) },
                    { new Point(1, 0), new Point(-2, 0), new Point(1, 2), new Point(-2, -1) },
                    { new Point(-2, 0), new Point(1, 0), new Point(-2, 1), new Point(1, -2) }
            },
            {
                    { new Point(1, 0), new Point(1, -1), new Point(0, 2), new Point(1, 2) },
                    { new Point(1, 0), new Point(1, 1), new Point(0, -2), new Point(1, -2) },
                    { new Point(-1, 0), new Point(-1, -1), new Point(0, 2), new Point(-1, 2) },
                    { new Point(-1, 0), new Point(-1, 1), new Point(0, -2), new Point(-1, -2) }
            }
    };

    public static final int TOTAL_NUM_OF_TETROMINOES = 7;
    public static final int NUM_ROTATIONS = 4;
    public static final int I_BLOCK_VALUE = 0;
    public static final int O_BLOCK_VALUE = 3;
    public static final int NON_I_BLOCK_ROTATION_TRANSLATION_INDEX = 1;

    private int rotate;
    private int x, tetrominoNum;
    private double y;

    public Tetrominoes(int x, double y){
        this.tetrominoNum = random.nextInt(TOTAL_NUM_OF_TETROMINOES);
        this.x = x;
        this.y = y;
        this.rotate = 0;
    }

    public Tetrominoes(){
        this.tetrominoNum = random.nextInt(TOTAL_NUM_OF_TETROMINOES);
        this.x = 0;
        this.y = 0;
        this.rotate = 0;
    }

    public int getX(){ return this.x; }
    public double getY(){ return this.y; }
    public int getTetrominoNum() { return this.tetrominoNum; }
    public int getRotate() { return this.rotate; }

    public void setX(int x) { this.x = x; }
    public void setY(int y) { this.y = y; }

    public void setLeftCornerPos(int x, int y){
        setX(x);
        setY(y);
    }

    public void setTopCentrePos(int x, int y){
        setLeftCornerPos(x - TETROMINO_BOUNDARY_WIDTH_HEIGHT[tetrominoNum] / 2, y);
    }

    public void setCentrePos(int x, int y){
        setLeftCornerPos(x - TETROMINO_BOUNDARY_WIDTH_HEIGHT[tetrominoNum] / 2, y - TETROMINO_BOUNDARY_WIDTH_HEIGHT[tetrominoNum] / 2);
    }
    
    public void resetRotate(){ rotate = 0; }

    public void truncateY(){
        y = (int) y;
    }

    public void rotate(int rotate){
        this.rotate = (this.rotate + rotate + NUM_ROTATIONS) % NUM_ROTATIONS;
    }

    public void move(int vx, double vy){
        x += vx;
        y += vy;
    }

    public void draw(Graphics g, int x, int y, int size, ImageObserver imageObserver){
        for(Square i : TETROMINOES[tetrominoNum][rotate]){
            i.draw(g,x + this.x * size,  y + (int)(this.y) * size, size, imageObserver);
        }
    }

}
