// Score.java
// Gordon Lin
// Class which stores the score and information about the score of the user for the Tetris game

import java.awt.*;

public class Score {
    // Constants which store the multiplier points received for hard drop, and number of rows required to be cleared to level up
    public static final int DROP_MULTIPLIER = 2, ROWS_PER_LEVEL = 10;
    // Constant which stores the number of points received when clearing x number of rows
    public static final int[] ROW_SCORE = {
            0, 40, 100, 300, 1200
    };

    // Stores the total score, level, and number of rows cleared
    private int score, level, rowsCleared;

    // Stores the x and y position of the text on the screen
    private final int x, y;
    // Stores the font used to display the scores
    private final Font font;

    // Constructor which takes in the position of the text, font of the text, and font size
    public Score(int x, int y, Font font, float fontSize){
        // Sets all the values to 0
        score = 0;
        level = 0;
        rowsCleared = 0;

        // Sets the position
        this.x = x;
        this.y = y;
        // Sets the font to the font specified with the size specified
        this.font = font.deriveFont(fontSize);
    }

    // Gets the score
    public int getScore() { return score; }

    // Gets required num of rows cleared to level up
    public int getRequiredRowsCleared(){
        return ROWS_PER_LEVEL * (level + 1);
    }

    // Checks if the user can level up using the number of rows that have been cleared on the board
    public boolean rowClearLevelUp(int numCleared){
        // Adds the number of rows cleared
        rowsCleared += numCleared;
        // Adds the score from the points received from number of rows cleared multiplied by the level
        score += ROW_SCORE[numCleared] * (level + 1);

        // Checks if the number of rows cleared is equal to or greater than the number of rows needed to proceed to next level
        boolean nextLevel = rowsCleared >= getRequiredRowsCleared();

        // Increments a level if can proceed to next level
        if(nextLevel) level++;

        // Returns if can proceed to next level
        return nextLevel;
    }

    // Adds score if hard dropped, multiplies distance dropped by the drop multiplier
    public void hardDrop(int distance){
        score += distance * DROP_MULTIPLIER;
    }

    // Increments 1 to the score
    public void increment(){
        score++;
    }

    // Renders the text containing score, rows cleared, and level on the screen
    public void draw(Graphics2D g2d){
        // Gets the FontMetrics of the font
        FontMetrics fm = g2d.getFontMetrics();
        // Sets the colour to white and font to the specified font
        g2d.setColor(Color.WHITE);
        g2d.setFont(font);

        // Renders the score, rowsCleared and level on the screen vertically at the specified location
        g2d.drawString("Score: "+ score, x, y);
        g2d.drawString("Rows Cleared: " + rowsCleared, x, y + fm.getHeight() * 2);
        g2d.drawString("Level: " + level, x, y + fm.getHeight() * 4);
    }

    // Resets all the values back to 0
    public void reset(){
        score = 0;
        level = 0;
        rowsCleared = 0;
    }
}
