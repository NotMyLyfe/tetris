// Tetris.java
// Gordon Lin
// Simple Tetris game (block stacking game with tetrominoes)
// Includes most features from the standard game, including wall kicking, hard dropping (drops the block immediately to the bottom), soft dropping (drops the block 1 square)

import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

// Main JFrame class
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

// Main JPanel Class
class TetrisPanel extends JPanel implements ActionListener, KeyListener{
    // Constant integers which store, in order:
    // - Width of JPanel by pixel
    // - Height of JPanel by pixel
    // - Width of the Tetris board by square size
    // - Height of the Tetris board by square size
    // - Pixel size of each block on the board
    // - Width/Height of the board that shows only a singular block by square size
    public static final int WIDTH = 750, HEIGHT=750, BOARD_WIDTH = 10, BOARD_HEIGHT = 24, SQUARE_SIZE = 25, SINGLE_BLOCK_BOARD_WIDTH_HEIGHT = 4;
    // Constant floats which store each respective font size
    public static final float SMALL_FONT = 12f, LARGE_FONT = 36f, VERY_LARGE_FONT = 72f;
    // Constant doubles which store how much the speed which the block drops increments by per level, and the maximum speed
    public static final double DROP_SPEED_INCREMENT = 0.05, MAX_SPEED = 1;

    // Boolean array that stores keys pressed upon first pressed as well as held keys
    private final boolean[][] keys;

    // Board object that stores the main board of the Tetris game, as well as the board that displays the held block and next block
    private final Board board, holdingBoard, nextBoard;
    // Tetrominoes object that store the current block being played, the next block that will be used, and the current block being held
    private Tetrominoes currentBlock, nextBlock, heldBlock;
    // Font object that stores the font of the game
    private final Font font;
    // Score object that keeps track of score, number of rows cleared, and current level the game is on
    private final Score score;
    // Timer object that stores the refresh rate of the game
    private final Timer myTimer;
    // Background image of the game
    private final Image back;

    // Integer that stores the final score of the user upon game over
    private int finalScore;
    // Double that stores the speed for which the current block being played drops per frame
    private double blockDropSpeed;
    // Booleans that store if you just held a block in the current block play, and if the game is over
    private boolean justHeld, gameOver;

    public TetrisPanel() throws IOException, FontFormatException, UnsupportedAudioFileException, LineUnavailableException {
        // Sets dimensions of the panel, adds keyListener, and gets focus
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        addKeyListener(this);
        setFocusable(true);
        requestFocus();

        // Imports the music and continuously loops it
        Clip themeClip = AudioSystem.getClip();
        themeClip.open(AudioSystem.getAudioInputStream(new File("./src/assets/music/theme.wav")));
        themeClip.loop(Clip.LOOP_CONTINUOUSLY);

        // Imports the font for the game
        font = Font.createFont(Font.TRUETYPE_FONT, new File("./src/assets/font/font.ttf"));

        // Imports the background image of the game
        back = new ImageIcon("./src/assets/images/background.png").getImage();

        // Creates a boolean array which stores the keys that are pressed and keys that are held
        keys = new boolean[KeyEvent.KEY_LAST + 1][2];

        // Initializes the Board with their respective positions and their respective widths and heights
        board = new Board(WIDTH/2, HEIGHT/2, SQUARE_SIZE, BOARD_WIDTH, BOARD_HEIGHT);
        holdingBoard = new Board(WIDTH - (SQUARE_SIZE * SINGLE_BLOCK_BOARD_WIDTH_HEIGHT), HEIGHT / 2 - (SQUARE_SIZE * SINGLE_BLOCK_BOARD_WIDTH_HEIGHT), SQUARE_SIZE, SINGLE_BLOCK_BOARD_WIDTH_HEIGHT, SINGLE_BLOCK_BOARD_WIDTH_HEIGHT);
        nextBoard = new Board(WIDTH - (SQUARE_SIZE * SINGLE_BLOCK_BOARD_WIDTH_HEIGHT), HEIGHT / 2 + (SQUARE_SIZE * SINGLE_BLOCK_BOARD_WIDTH_HEIGHT), SQUARE_SIZE, SINGLE_BLOCK_BOARD_WIDTH_HEIGHT, SINGLE_BLOCK_BOARD_WIDTH_HEIGHT);

        // Generates a new tetromino block for the currently played block, and the next block
        // Sets position of the current block to the top centre of the board, and the next block to the centre of the next block board
        // Sets the held block to null as not generated yet and stored until the user wants a held block
        currentBlock = new Tetrominoes();
        currentBlock.setTopCentrePos(board.getWidth() / 2, 0);
        nextBlock = new Tetrominoes();
        nextBlock.setCentrePos(nextBoard.getWidth() / 2, nextBoard.getHeight() / 2);
        heldBlock = null;

        // Initialises the speed which each block drops per frame
        blockDropSpeed = DROP_SPEED_INCREMENT;
        // Creates a new score object, and sets the position to 1 square size from the edge of the screen, and below the top of the board, using the font and font size of small
        score = new Score(SQUARE_SIZE, HEIGHT/2 - BOARD_HEIGHT * SQUARE_SIZE / 2, font, SMALL_FONT);

        // Sets justHeld to false and gameOver to true as no block has been held and game has yet to start
        justHeld = false;
        gameOver = true;

        // Sets finalScore to -1 as no score has been accomplished yet
        finalScore = -1;

        // Sets the timer to refresh every 60ms
        myTimer = new Timer(60, this);
    }

    // Method which resets the game back to default values
    public void reset(){
        // Resets the board
        board.reset();

        // Creates new Tetromino and moves it to the top centre of the board
        currentBlock = new Tetrominoes();
        currentBlock.setTopCentrePos(board.getWidth() / 2, 0);
        // Creates new Tetromino for next block in play and moves it to the centre of the nextBoard
        nextBlock = new Tetrominoes();
        nextBlock.setCentrePos(nextBoard.getWidth() / 2, nextBoard.getHeight() / 2);
        // Resets heldBlock back to null
        heldBlock = null;

        // Resets justHeld back to false
        justHeld = false;
        // Resets the score
        score.reset();
        // Resets the blockDropSpeed back to it's initial value
        blockDropSpeed = DROP_SPEED_INCREMENT;
    }

    // Method which checks if the game is over, after each row clear and the current block is added to the top
    public void isGameOver(){
        // Checks if the current block does not collide at the top of the board
        if(!board.isCollide(currentBlock)) return;

        // Checks if translating the block 1 unit up doesn't collide
        if(!board.isCollide(currentBlock, 0, -1)) {
            // Translates the block 1 unit up
            currentBlock.move(0, -1);
            return;
        }

        // Sets the game to be over
        gameOver = true;
        // Gets the final score of the user
        finalScore = score.getScore();
        // Calls reset method to reset the game
        reset();
        // Stops the timer
        myTimer.stop();
    }

    // Method called when a block has fallen and rested
    public void fallenBlock(){
        // Sets justHeld to false as currentBlock being used is now out of play
        justHeld = false;

        // Adds the block to the board
        board.addBlock(currentBlock);
        // Gets the number of rows
        int rowsCleared = board.clearRows();

        // Checks if adding a new row will result in a level up
        if(score.rowClearLevelUp(rowsCleared)){
            // Increments the speed each block is dropped by, but makes sure it does not cap out at 1 square/frame
            blockDropSpeed += DROP_SPEED_INCREMENT;
            blockDropSpeed = Math.min(blockDropSpeed, MAX_SPEED);
        }

        // Changes the nextBlock to the currentBlock and moves it to the top centre of the board
        currentBlock = nextBlock;
        currentBlock.setTopCentrePos(board.getWidth() / 2, 0);

        // Generates a new Tetromino for the nextBlock and sets it centre of the nextBoard
        nextBlock = new Tetrominoes();
        nextBlock.setCentrePos(nextBoard.getWidth() / 2, nextBoard.getHeight() / 2);

        // Checks if the game is over with the new block
        isGameOver();
    }

    // Main move method that checks if each move is possible and moves the block accordingly
    public void move(){
        // Checks if the move being played by the user is switching to a held block
        if(keys[KeyEvent.VK_SHIFT][0] && !justHeld){
            // Sets justHeld to true signifying that a switch to a held block has occurred in this play of the game
            justHeld = true;
            // Checks if there isn't a held block available
            if(heldBlock == null){
                // Sets the heldBlock to the currentBlock, and sets it's position to the centre of the holdingBoard and rotation to default
                heldBlock = currentBlock;
                heldBlock.setCentrePos(holdingBoard.getWidth() / 2, holdingBoard.getHeight() / 2);
                heldBlock.resetRotate();

                // Sets the currentBlock to the nextBlock available and sets it's position to the top centre of the playing board
                currentBlock = nextBlock;
                currentBlock.setTopCentrePos(board.getWidth() / 2,  0);

                // Generates a new nextBlock and sets it's position to the centre of the nextBoard
                nextBlock = new Tetrominoes();
                nextBlock.setCentrePos(nextBoard.getWidth() / 2, nextBoard.getHeight() / 2);
            }
            // Else if there is a heldBlock available
            else{
                // Temporary storage for the heldBlock
                Tetrominoes temp = heldBlock;

                // Sets the heldBlock to the currentBlock being played, and sets it's position to the centre of the holdingBoard and rotation to default
                heldBlock = currentBlock;
                heldBlock.setCentrePos(holdingBoard.getWidth() / 2, holdingBoard.getHeight() / 2);
                heldBlock.resetRotate();

                // currentBlock becomes the previous heldBlock and sets it's position to the top centre of the playing board
                currentBlock = temp;
                currentBlock.setTopCentrePos(board.getWidth() / 2, 0);
            }
            // Checks if the game is over and escapes the move method
            isGameOver();
            return;
        }

        // Stores the horizontal change of the block
        int dx = 0;

        // If the user is holding the key right button, then it changes the horizontal speed to 1, else if the user is holding the key left, it changes it to -1
        if(keys[KeyEvent.VK_RIGHT][1]) dx = 1;
        else if(keys[KeyEvent.VK_LEFT][1]) dx = -1;

        // If the user pressed the space key - Hard drops the block
        if(keys[KeyEvent.VK_SPACE][0]){
            // Gets the distance the block gets hard dropped
            int dy = board.drop(currentBlock);
            // Adds distance hard dropped into score multiplied by hard drop multiplier
            score.hardDrop(dy);

            // Moves the block down the distance hard dropped
            currentBlock.move(0, dy);

            // Calls the method fallenBlock as the block has rested and fallen
            fallenBlock();
            return;
        }

        // Gets the speed that each block will drop
        double dy = blockDropSpeed;

        // Checks if the user is holding down the down arrow, which softly drops the block, changing the speed of drop to 1 square/frame, and incrementing the score
        if(keys[KeyEvent.VK_DOWN][1]) {
            dy = 1;
            score.increment();
        }

        // Checks if the current block doesn't collide with anything if it moves down at the speed specified, and if so, moves the block down dy units
        if(!board.isCollide(currentBlock, 0, dy)) {
            currentBlock.move(0, dy);
        }

        // Checks if the user specified a left/right movement, and moving left/right doesn't cause a collision
        if (dx != 0 && !board.isCollide(currentBlock, dx, 0)){
            // Moves the block to the left or right (depending on dx from the user's input)
            currentBlock.move(dx, 0);
            // Checks if the block is resting on another block or bottom, and will collide if it moves one square down
            if(board.isCollide(currentBlock, 0, 1)) {
                // Resets the amount of movement the block needs to move down in order to rest and clear a row by truncating it's Y value
                currentBlock.truncateY();
            }
        }

        // Checks if the block isn't an O block, as O blocks cannot rotate
        if (currentBlock.getTetrominoNum() != Tetrominoes.O_BLOCK_VALUE){
            // Checks if the key being pressed is the up arrow for right (clockwise) rotation
            if(keys[KeyEvent.VK_UP][0]){
                // Checks if rotating on the spot won't cause a collision, and if so, rotates it normally
                if(!board.isCollide(currentBlock, 1)){
                    currentBlock.rotate(1);
                }
                // Else if rotating on the spot will cause a collision, which results in a wall kick
                else {
                    // Checks if the block is an I block value, as I blocks have different set of possible translations for wall kicking
                    if(currentBlock.getTetrominoNum() == Tetrominoes.I_BLOCK_VALUE) {
                        // Loops through all possible translations for wall kicks
                        for (Point i : Tetrominoes.TETROMINO_CLOCKWISE_ROTATION_TRANSLATION[currentBlock.getTetrominoNum()][currentBlock.getRotate()]) {
                            // Checks if translating and rotating the block does not result in a collision
                            if (!board.isCollide(currentBlock, (int) i.getX(), i.getY(), 1)) {
                                // Translates the block accordingly and rotates the block clockwise
                                currentBlock.rotate(1);
                                currentBlock.move((int) i.getX(), i.getY());
                                break;
                            }
                        }
                    }
                    else{
                        // Block is not an I block, and loops through all the possible translations for wall kicking
                        for (Point i : Tetrominoes.TETROMINO_CLOCKWISE_ROTATION_TRANSLATION[Tetrominoes.NON_I_BLOCK_ROTATION_TRANSLATION_INDEX][currentBlock.getRotate()]) {
                            // Checks if translating and rotating the block does not result in a collision
                            if(!board.isCollide(currentBlock, (int) i.getX(), i.getY(), 1)){
                                // Translates the block accordingly and rotates the block clockwise
                                currentBlock.rotate(1);
                                currentBlock.move((int) i.getX(), i.getY());
                                break;
                            }
                        }
                    }
                }
            }
            // Checks if the key being pressed is the Z for left (counterclockwise) rotation
            else if(keys[KeyEvent.VK_Z][0]){
                // Checks if rotating on the spot won't cause a collision, and if so, rotates it normally
                if(!board.isCollide(currentBlock, -1)){
                    currentBlock.rotate(-1);
                }
                // Else if rotating on the spot will cause a rotation, which results in a wall kick
                else{
                    // Checks if the block is an I block value, as I blocks have different set of possible translations for wall kicking
                    if(currentBlock.getTetrominoNum() == Tetrominoes.I_BLOCK_VALUE) {
                        // Loops through all possible translations for wall kicks
                        for (Point i : Tetrominoes.TETROMINO_COUNTERCLOCKWISE_ROTATION_TRANSLATION[currentBlock.getTetrominoNum()][currentBlock.getRotate()]) {
                            // Checks if translating and rotating the block does not result in a collision
                            if(!board.isCollide(currentBlock, (int) i.getX(), i.getY(), -1)){
                                // Translates the block accordingly and rotates the block counterclockwise
                                currentBlock.rotate(-1);
                                currentBlock.move((int) i.getX(), i.getY());
                                break;
                            }
                        }
                    }
                    else{
                        // Block is not an I block, and loops through all the possible translations for wall kicking
                        for (Point i : Tetrominoes.TETROMINO_COUNTERCLOCKWISE_ROTATION_TRANSLATION[Tetrominoes.NON_I_BLOCK_ROTATION_TRANSLATION_INDEX][currentBlock.getRotate()]) {
                            // Checks if translating and rotating the block does not result in a collision
                            if(!board.isCollide(currentBlock, (int) i.getX(), i.getY(), -1)){
                                // Translates the block accordingly and rotates the block counterclockwise
                                currentBlock.rotate(-1);
                                currentBlock.move((int) i.getX(), i.getY());
                                break;
                            }
                        }
                    }
                }
            }
        }

        // Checks if moving down at the speed vertical speed will result in a collision
        if(board.isCollide(currentBlock, 0, dy)){
            // Calls the method fallenBlock as the block has rested and fallen
            fallenBlock();
        }
    }

    @Override
    public void paint(Graphics g){
        // Renders the background image
        g.drawImage(back, 0, 0, this);
        // Graphics2D object from Graphics object
        Graphics2D g2d = (Graphics2D)g.create();
        // FontMetrics object used for getting dimensions of font
        FontMetrics fm;

        // If the game is not over (when the game is being played)
        if(!gameOver){
            // Draws the board with the currentBlock, the holdingBoard with heldBlock, and nextBoard with the next block
            board.draw(g2d, currentBlock, this);
            holdingBoard.draw(g2d, heldBlock, this);
            nextBoard.draw(g2d, nextBlock, this);

            // Draws the score (with level and number of rows cleared)
            score.draw(g2d);

            // Sets the font to small font and colour to Color.WHITE, and gets the FontMetrics of the font
            g2d.setFont(font.deriveFont(SMALL_FONT));
            g2d.setColor(Color.WHITE);
            fm = g2d.getFontMetrics();

            // Displays "Held Block" directly above the holdingBoard, centred to the holdingBoard
            g2d.drawString("Held Block", WIDTH - (SQUARE_SIZE * SINGLE_BLOCK_BOARD_WIDTH_HEIGHT) - fm.stringWidth("Held Block") / 2, HEIGHT / 2 - (SQUARE_SIZE * SINGLE_BLOCK_BOARD_WIDTH_HEIGHT) * 2);
            // Displays "Next Block" directly below the nextBoard, centred to the nextBoard
            g2d.drawString("Next Block", WIDTH - (SQUARE_SIZE * SINGLE_BLOCK_BOARD_WIDTH_HEIGHT) - fm.stringWidth("Next Block") / 2, HEIGHT / 2 + (SQUARE_SIZE * SINGLE_BLOCK_BOARD_WIDTH_HEIGHT) * 2);
        }
        else{
            // Else if the game is not being played, the main menu screen
            // Sets the colour of the next drawn graphics to white, and set to the very large font
            g2d.setColor(Color.WHITE);
            g2d.setFont(font.deriveFont(VERY_LARGE_FONT));
            // Gets FontMetrics of the very large font
            fm = g2d.getFontMetrics();
            // Displays TETRIS centred to the screen, on the top of the screen
            g2d.drawString("TETRIS", (WIDTH - fm.stringWidth("TETRIS"))/2, HEIGHT/2 - BOARD_HEIGHT * SQUARE_SIZE / 2 + fm.getHeight());

            // Checks if there was last played game with a final score
            if(finalScore >= 0) {
                // Sets font to large font and gets FontMetrics of the font
                g2d.setFont(font.deriveFont(LARGE_FONT));
                fm = g2d.getFontMetrics();

                // Displays Game Over on the centre of the screen with the score of the user
                g2d.drawString("Game Over", (WIDTH - fm.stringWidth("Game Over")) / 2, HEIGHT / 2 - fm.getHeight());
                g2d.drawString("Score: " + finalScore, (WIDTH - fm.stringWidth("Score: " + finalScore))/2, HEIGHT / 2 + fm.getHeight());
            }

            // Sets font to small font and gets FontMetrics of the font
            g2d.setFont(font.deriveFont(SMALL_FONT));
            fm = g2d.getFontMetrics();

            // Displays information to get started playing, centred to the bottom of the screen
            g2d.drawString("Press any button to play", (WIDTH - fm.stringWidth("Press any button to play")) / 2, HEIGHT/2 + BOARD_HEIGHT * SQUARE_SIZE / 2 - fm.getHeight());

            // Displays the controls of the game centred to the bottom of the screen below the instructions to get started playing
            g2d.drawString("Left/Right Arrows - Left/Right Movement", (WIDTH - fm.stringWidth("Left/Right Arrows - Left/Right Movement"))/2, HEIGHT - fm.getHeight() * 5);
            g2d.drawString("Z - Rotate Left, Up Arrow - Rotate Right", (WIDTH - fm.stringWidth("Z - Rotate Left, Up Arrow - Rotate Right"))/2, HEIGHT - fm.getHeight() * 3);
            g2d.drawString("Down Arrow - Soft Drop, Space - Hard Drop, Shift - Hold", (WIDTH - fm.stringWidth("Down Arrow - Soft Drop, Space - Hard Drop, Shift - Hold"))/2, HEIGHT - fm.getHeight());
        }

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // Calls the move method to move the blocks
        move();
        // Repaints the board to refresh graphics
        repaint();
        // Loops through all the keys, and sets if the key is being held, and the key has been pressed to be false as the button was no longer pressed, but held now
        for(int i = 0; i <= KeyEvent.KEY_LAST; i++){
            if(keys[i][1] && keys[i][0]) keys[i][0] = false;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        // If the game is over, sets gameOver to false, as the game is no longer over, and starts the refresh timer
        if(gameOver){
            gameOver = false;
            myTimer.start();
            return;
        }
        // Checks if the key being pressed is outside the range for the keys array and returns
        if(e.getKeyCode() > KeyEvent.KEY_LAST) return;
        // Sets key pressed to not a held value, and sets the held value to true
        keys[e.getKeyCode()][0] = !keys[e.getKeyCode()][1];
        keys[e.getKeyCode()][1] = true;
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // Checks if the key being pressed is outside the range for the keys array and returns
        if(e.getKeyCode() > KeyEvent.KEY_LAST) return;
        // Sets key to false for both values
        keys[e.getKeyCode()][0] = false;
        keys[e.getKeyCode()][1] = false;
    }
}
