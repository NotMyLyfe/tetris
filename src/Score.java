import java.awt.*;

public class Score {
    public static final int DROP_MULTIPLIER = 2;
    public static final int[] ROW_SCORE_MULTIPLIER = {
            0, 40, 100, 300, 1200
    };

    private int score, level, rowsCleared;
    private final int x, y;
    private final Font font;
    public Score(int x, int y, Font font, float fontSize){
        score = 0;
        level = 0;
        rowsCleared = 0;
        this.x = x;
        this.y = y;
        this.font = font.deriveFont(fontSize);
    }

    public int getScore() { return score; }

    public int getMaxRowsCleared(){
        return 5 * ((level + 1) * (level + 1) + 3 * level + 3) / 2;
    }

    public boolean rowClear(int numCleared){
        rowsCleared += numCleared;
        score += ROW_SCORE_MULTIPLIER[numCleared] * (level + 1);
        boolean nextLevel = rowsCleared >= getMaxRowsCleared();

        if(nextLevel) level++;
        return nextLevel;
    }

    public void hardDrop(int distance){
        score += distance * DROP_MULTIPLIER;
    }

    public void increment(){
        score++;
    }

    public void draw(Graphics g){
        Graphics2D g2d = (Graphics2D)g.create();
        FontMetrics fm = g2d.getFontMetrics();
        g2d.setColor(Color.WHITE);
        g2d.setFont(font);
        g2d.drawString("Score: "+ score, x, y);
        g2d.drawString("Rows Cleared: " + rowsCleared, x, y + fm.getHeight() * 2);
        g2d.drawString("Level: " + level, x, y + fm.getHeight() * 4);
    }

    public void reset(){
        score = 0;
        level = 0;
        rowsCleared = 0;
    }
}
