import java.awt.*;
import java.awt.image.*;

public class Board {
    public static int PLUS_BORDER = 2;
    public static Color TRANSPARENT_BLACK = new Color(0, 0, 0, 200);
    public static Square BORDER_BLOCK = new Square(7);

    private final int x, y, blockSize, topLeftX, topLeftY, width, height;
    private Square[][] board;

    public Board(int x, int y, int blockSize, int width, int height){
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.blockSize = blockSize;

        topLeftX = x - blockSize * width / 2;
        topLeftY = y - blockSize * height / 2;
        board = new Square[width][height];
    }

    public int getWidth(){ return width; }
    public int getHeight() { return height; }

    public void draw(Graphics g, ImageObserver imageObserver){
        for(int i = 0; i < width + PLUS_BORDER; i++){
            BORDER_BLOCK.draw(g, topLeftX - blockSize + blockSize * i, topLeftY - blockSize, blockSize, imageObserver);
            BORDER_BLOCK.draw(g, topLeftX - blockSize + blockSize * i, topLeftY + blockSize * height, blockSize, imageObserver);
        }
        for(int i = 0; i < height; i++){
            BORDER_BLOCK.draw(g, topLeftX - blockSize, topLeftY + blockSize * i, blockSize, imageObserver);
            BORDER_BLOCK.draw(g, topLeftX + blockSize * width, topLeftY + blockSize * i, blockSize, imageObserver);
        }

        g.setColor(TRANSPARENT_BLACK);
        g.fillRect(topLeftX, topLeftY, blockSize * width, blockSize * height);
        for(int rows = 0; rows < height; rows++){
            for(int columns = 0; columns < width; columns++){
                if(board[columns][rows] != null) board[columns][rows].draw(g, topLeftX + columns * blockSize, topLeftY + rows * blockSize, blockSize, imageObserver);
            }
        }
    }

    public int clearRows(){
        int rowsCleared = 0;
        for(int rows = height - 1; rows >= 0; rows--){
            boolean clearRow = true;
            for(int columns = 0; columns < width; columns++){
                if(board[columns][rows] == null){
                    clearRow = false;
                    break;
                }
            }
            if(clearRow){
                rowsCleared++;
                for(int previousRows = rows; previousRows > 0; previousRows--){
                    for(int columns = 0; columns < width; columns++) board[columns][previousRows] = board[columns][previousRows - 1];
                }
                rows++;
            }
        }
        return rowsCleared;
    }

    private boolean isOccupied(int x, int y){
        if(x >= width || x < 0 || y >= height || y < 0) return true;
        return board[x][y] != null;
    }

    public boolean isCollide(Tetrominoes currentTetromino, int vx, double vy, int rotate){
        int tetrominoX = currentTetromino.getX() + vx, tetrominoY = (int) (currentTetromino.getY() + vy),
                tetrominoRotate = (currentTetromino.getRotate() + rotate + Tetrominoes.NUM_ROTATIONS) % Tetrominoes.NUM_ROTATIONS;
        Square[] squares = Tetrominoes.TETROMINOES[currentTetromino.getTetrominoNum()][tetrominoRotate];

        for(Square square : squares){
            if(isOccupied(square.getX() + tetrominoX, square.getY() + tetrominoY)){
                return true;
            }
        }
        return false;
    }

    public boolean isCollide(Tetrominoes currentTetromino, int rotate){
        return isCollide(currentTetromino, 0, 0, rotate);
    }

    public boolean isCollide(Tetrominoes currentTetromino, int vx, double vy){
        return isCollide(currentTetromino, vx, vy, 0);
    }

    public boolean isCollide(Tetrominoes currentTetromino){
        return isCollide(currentTetromino, 0, 0, 0);
    }

    public void addBlock(Tetrominoes currentTetromino){
        for(Square square : Tetrominoes.TETROMINOES[currentTetromino.getTetrominoNum()][currentTetromino.getRotate()]){
            board[square.getX() + currentTetromino.getX()][(int) (square.getY() + currentTetromino.getY())] = new Square(currentTetromino.getTetrominoNum());
        }
    }

    public int drop(Tetrominoes currentTetromino){
        int dy = 0;
        while(!isCollide(currentTetromino, 0, dy + 1)){
            dy++;
        }
        return dy;
    }

    public void draw(Graphics g, Tetrominoes currentTetromino, ImageObserver imageObserver){
        draw(g, imageObserver);
        if(currentTetromino != null)
            currentTetromino.draw(g, topLeftX, topLeftY, blockSize, imageObserver);
    }

    public void reset(){
        board = new Square[width][height];
    }
}
