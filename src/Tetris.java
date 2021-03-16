import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class Tetris extends JFrame{
    TetrisPanel game;

    public Tetris() throws IOException, FontFormatException, UnsupportedAudioFileException, LineUnavailableException {
        super("Tetris");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        game = new TetrisPanel();
        add(game);
        pack();

        setVisible(true);
        setResizable(false);
    }

    public static void main(String[] args) throws IOException, FontFormatException, UnsupportedAudioFileException, LineUnavailableException {
        new Tetris();
    }

}

class TetrisPanel extends JPanel implements ActionListener, KeyListener{
    public static final int WIDTH = 750, HEIGHT=750, BOARD_WIDTH = 10, BOARD_HEIGHT = 24, BLOCK_SIZE = 25, SINGLE_BLOCK_BOARD_WIDTH_HEIGHT = 4;
    public static final float SMALL_FONT = 12f, LARGE_FONT = 36f, VERY_LARGE_FONT = 72f;
    public static final double DROP_SPEED_INCREMENT = 0.05, MAX_SPEED = 1;

    private final boolean[][] keys;

    private final Board board, holdingBoard, nextBoard;
    private Tetrominoes currentBlock, nextBlock, heldBlock;
    private final Font font;
    private final Score score;
    private final Clip themeClip;
    private final AudioInputStream audioInputStream;

    private int finalScore;
    private double blockDropSpeed;
    private boolean justHeld, gameOver;


    Timer myTimer;
    Image back;

    public TetrisPanel() throws IOException, FontFormatException, UnsupportedAudioFileException, LineUnavailableException {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        addKeyListener(this);
        setFocusable(true);
        requestFocus();

        audioInputStream = AudioSystem.getAudioInputStream(new File("./src/assets/music/theme.wav"));
        themeClip = AudioSystem.getClip();
        themeClip.open(audioInputStream);
        themeClip.loop(Clip.LOOP_CONTINUOUSLY);

        font = Font.createFont(Font.TRUETYPE_FONT, new File("./src/assets/font/font.ttf"));

        back = new ImageIcon("./src/assets/images/background.png").getImage();

        keys = new boolean[KeyEvent.KEY_LAST + 1][2];

        board = new Board(WIDTH/2, HEIGHT/2, BLOCK_SIZE, BOARD_WIDTH, BOARD_HEIGHT);
        holdingBoard = new Board(WIDTH - (BLOCK_SIZE * SINGLE_BLOCK_BOARD_WIDTH_HEIGHT), HEIGHT / 2 - (BLOCK_SIZE * SINGLE_BLOCK_BOARD_WIDTH_HEIGHT), BLOCK_SIZE, SINGLE_BLOCK_BOARD_WIDTH_HEIGHT, SINGLE_BLOCK_BOARD_WIDTH_HEIGHT);
        nextBoard = new Board(WIDTH - (BLOCK_SIZE * SINGLE_BLOCK_BOARD_WIDTH_HEIGHT), HEIGHT / 2 + (BLOCK_SIZE * SINGLE_BLOCK_BOARD_WIDTH_HEIGHT), BLOCK_SIZE, SINGLE_BLOCK_BOARD_WIDTH_HEIGHT, SINGLE_BLOCK_BOARD_WIDTH_HEIGHT);

        currentBlock = new Tetrominoes();
        currentBlock.setTopCentrePos(board.getWidth() / 2, 0);
        nextBlock = new Tetrominoes();
        nextBlock.setCentrePos(nextBoard.getWidth() / 2, nextBoard.getHeight() / 2);

        blockDropSpeed = DROP_SPEED_INCREMENT;
        score = new Score(BLOCK_SIZE, HEIGHT/2 - BOARD_HEIGHT * BLOCK_SIZE / 2, font, SMALL_FONT);

        justHeld = false;
        heldBlock = null;
        gameOver = true;

        finalScore = -1;

        myTimer = new Timer(60, this);
    }

    public void reset(){
        board.reset();
        currentBlock = new Tetrominoes();
        currentBlock.setTopCentrePos(board.getWidth() / 2, 0);
        nextBlock = new Tetrominoes();
        nextBlock.setCentrePos(nextBoard.getWidth() / 2, nextBoard.getHeight() / 2);
        heldBlock = null;

        justHeld = false;
        score.reset();
        blockDropSpeed = DROP_SPEED_INCREMENT;
    }

    public void isGameOver(){
        if(!board.isCollide(currentBlock)) return;

        if(!board.isCollide(currentBlock, 0, -1)) {
            currentBlock.move(0, -1);
            return;
        }

        gameOver = true;
        finalScore = score.getScore();
        reset();
        myTimer.stop();

    }

    public void fallenBlock(){
        board.addBlock(currentBlock);
        int rowsCleared = board.clearRows();

        if(score.rowClear(rowsCleared)){
            blockDropSpeed += DROP_SPEED_INCREMENT;
            blockDropSpeed = Math.min(blockDropSpeed, MAX_SPEED);
        }

        currentBlock = nextBlock;
        currentBlock.setTopCentrePos(board.getWidth() / 2, 0);

        nextBlock = new Tetrominoes();
        nextBlock.setCentrePos(nextBoard.getWidth() / 2, nextBoard.getHeight() / 2);

        isGameOver();
    }

    public void move(){
        int dx = 0;

        if(keys[KeyEvent.VK_RIGHT][1]) dx = 1;
        else if(keys[KeyEvent.VK_LEFT][1]) dx = -1;

        if(keys[KeyEvent.VK_SPACE][0]){
            justHeld = false;

            int dy = board.drop(currentBlock);
            score.hardDrop(dy);

            currentBlock.move(0, dy);

            fallenBlock();
            return;
        }

        double dy = blockDropSpeed;

        if(keys[KeyEvent.VK_DOWN][1]) {
            dy = 1;
            score.increment();
        }

        if(!board.isCollide(currentBlock, 0, dy)) {
            currentBlock.move(0, dy);
        }

        if (dx != 0 && !board.isCollide(currentBlock, dx, 0)){
            currentBlock.move(dx, 0);
            if(board.isCollide(currentBlock, 0, 1)) {
                currentBlock.truncateY();
            }
        }

        if (keys[KeyEvent.VK_UP][0] && currentBlock.getTetrominoNum() != Tetrominoes.O_BLOCK_VALUE){
            if(!board.isCollide(currentBlock, 1)){
                currentBlock.rotate(1);
            }
            else if(!board.isCollide(currentBlock, -1)){
                currentBlock.rotate(-1);
            }
            else{
                boolean hasRotated = false;
                if(currentBlock.getTetrominoNum() == Tetrominoes.I_BLOCK_VALUE) {
                    for (Point i : Tetrominoes.TETROMINO_CLOCKWISE_ROTATION_TRANSLATION[currentBlock.getTetrominoNum()][currentBlock.getRotate()]) {
                        if(!board.isCollide(currentBlock, (int) i.getX(), i.getY(), 1)){
                            currentBlock.rotate(1);
                            currentBlock.move((int) i.getX(), i.getY());
                            hasRotated = true;
                            break;
                        }
                    }
                    if(!hasRotated){
                        for (Point i : Tetrominoes.TETROMINO_COUNTERCLOCKWISE_ROTATION_TRANSLATION[currentBlock.getTetrominoNum()][currentBlock.getRotate()]) {
                            if(!board.isCollide(currentBlock, (int) i.getX(), i.getY(), -1)){
                                currentBlock.rotate(-1);
                                currentBlock.move((int) i.getX(), i.getY());
                                break;
                            }
                        }
                    }
                }
                else{
                    for (Point i : Tetrominoes.TETROMINO_CLOCKWISE_ROTATION_TRANSLATION[Tetrominoes.NON_I_BLOCK_ROTATION_TRANSLATION_INDEX][currentBlock.getRotate()]) {
                        if(!board.isCollide(currentBlock, (int) i.getX(), i.getY(), 1)){
                            currentBlock.rotate(1);
                            currentBlock.move((int) i.getX(), i.getY());
                            hasRotated = true;
                            break;
                        }
                    }
                    if(!hasRotated){
                        for (Point i : Tetrominoes.TETROMINO_COUNTERCLOCKWISE_ROTATION_TRANSLATION[Tetrominoes.NON_I_BLOCK_ROTATION_TRANSLATION_INDEX][currentBlock.getRotate()]) {
                            if(!board.isCollide(currentBlock, (int) i.getX(), i.getY(), -1)){
                                currentBlock.rotate(-1);
                                currentBlock.move((int) i.getX(), i.getY());
                                break;
                            }
                        }
                    }
                }
            }
        }

        if(board.isCollide(currentBlock, 0, dy)){
            justHeld = false;

            fallenBlock();

            isGameOver();
        }
    }

    @Override
    public void paint(Graphics g){
        g.drawImage(back, 0, 0, this);

        if(!gameOver){
            board.draw(g, currentBlock, this);
            holdingBoard.draw(g, heldBlock, this);
            nextBoard.draw(g, nextBlock, this);
            score.draw(g);
        }
        else{
            Graphics2D g2d = (Graphics2D)g.create();
            FontMetrics fm;
            g2d.setColor(Color.WHITE);
            g2d.setFont(font.deriveFont(VERY_LARGE_FONT));
            fm = g2d.getFontMetrics();
            g2d.drawString("TETRIS", (WIDTH - fm.stringWidth("TETRIS"))/2, HEIGHT/2 - BOARD_HEIGHT * BLOCK_SIZE / 2 + fm.getHeight());
            if(finalScore >= 0) {
                g2d.setFont(font.deriveFont(LARGE_FONT));
                fm = g2d.getFontMetrics();
                g2d.drawString("Game Over", (WIDTH - fm.stringWidth("Game Over")) / 2, HEIGHT / 2 - fm.getHeight());
                g2d.drawString("Score: " + finalScore, (WIDTH - fm.stringWidth("Score: " + finalScore))/2, HEIGHT / 2 + fm.getHeight());
            }
            g2d.setFont(font.deriveFont(SMALL_FONT));
            fm = g2d.getFontMetrics();
            g2d.drawString("Press any button to play", (WIDTH - fm.stringWidth("Press any button to play")) / 2, HEIGHT/2 + BOARD_HEIGHT * BLOCK_SIZE / 2 - fm.getHeight());
        }

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(keys[KeyEvent.VK_SHIFT][0] && !justHeld){
            justHeld = true;
            if(heldBlock == null){
                heldBlock = currentBlock;
                heldBlock.setCentrePos(holdingBoard.getWidth() / 2, holdingBoard.getHeight() / 2);
                heldBlock.resetRotate();

                currentBlock = nextBlock;
                currentBlock.setTopCentrePos(board.getWidth() / 2,  0);

                nextBlock = new Tetrominoes();
                nextBlock.setCentrePos(nextBoard.getWidth() / 2, nextBoard.getHeight() / 2);
            }
            else{
                Tetrominoes temp = heldBlock;

                heldBlock = currentBlock;
                heldBlock.setCentrePos(holdingBoard.getWidth() / 2, holdingBoard.getHeight() / 2);
                heldBlock.resetRotate();

                currentBlock = temp;
                currentBlock.setTopCentrePos(board.getWidth() / 2, 0);
            }
            isGameOver();
        }
        move();
        repaint();
        for(int i = 0; i <= KeyEvent.KEY_LAST; i++){
            if(keys[i][1] && keys[i][0]) keys[i][0] = false;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        if(gameOver){
            gameOver = !gameOver;
            myTimer.start();
            return;
        }
        if(e.getKeyCode() > KeyEvent.KEY_LAST) return;
        keys[e.getKeyCode()][0] = !keys[e.getKeyCode()][1];
        keys[e.getKeyCode()][1] = true;
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if(e.getKeyCode() > KeyEvent.KEY_LAST) return;
        keys[e.getKeyCode()][0] = false;
        keys[e.getKeyCode()][1] = false;
    }
}
