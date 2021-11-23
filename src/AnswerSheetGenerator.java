import java.awt.*;
import java.awt.image.BufferedImage;

public class AnswerSheetGenerator {
    private static final double MARK_ASPECT = 2;
    private static final double VERTICAL_SPACE = 0.5;
    private static final double HORIZONTAL_SPACE = 0.5;

    public static BufferedImage generateSheet(int questions, char[] choices, int height, int width, int minRows, int maxRows) {
        double aspect = (width) / (double) height;
        int low = minRows;
        int high = Math.min(Math.max(questions + 1, low),maxRows);
        while (high - low > 1) {
            int n = (low + high) / 2;
            int m = (questions - 1) / n + 1;
            double a = (m * (choices.length + 1) + MARK_ASPECT + m * HORIZONTAL_SPACE) / (n + (n - 1) * VERTICAL_SPACE) - aspect;
            if (a < 0) {
                high = n;
            } else {
                low = n;
            }
        }
        int n;
        int m;
        double whiteX;
        double whiteY;
        int lowm = (questions - 1) / low + 1;
        double lowx = whitespaceX((double) lowm * (choices.length + 1) + MARK_ASPECT + lowm * HORIZONTAL_SPACE, low + (low - 1) * VERTICAL_SPACE, width, height) + lowm* HORIZONTAL_SPACE;
        double lowy = whitespaceY((double) lowm * (choices.length + 1) + MARK_ASPECT + lowm * HORIZONTAL_SPACE, low + (low - 1) * VERTICAL_SPACE, width, height) + (low - 1) * VERTICAL_SPACE;
        int highm = (questions - 1) / high + 1;
        double highx = whitespaceX((double) highm * (choices.length + 1) + MARK_ASPECT + highm * HORIZONTAL_SPACE, high + (high - 1) * VERTICAL_SPACE, width, height) + highm * HORIZONTAL_SPACE;
        double highy = whitespaceY((double) highm * (choices.length + 1) + MARK_ASPECT + highm * HORIZONTAL_SPACE, high + (high - 1) * VERTICAL_SPACE, width, height) + (high - 1) * VERTICAL_SPACE;
        if (Math.max(lowx, lowy) < Math.max(highx, highy)) {
            n = low;
            m = lowm;
            whiteX = lowx;
            whiteY = lowy;
        } else {
            n = high;
            m = highm;
            whiteX = highx;
            whiteY = highy;
        }
        whiteX *= width / (m * (choices.length + 1) + MARK_ASPECT  + whiteX);
        whiteY *= height / (n + whiteY);
        System.out.println(n + "," + m + "," + whiteX + "," + whiteY);
        double sectionHeight = height - whiteY;
        double sectionStartX = sectionHeight / (double) n * (MARK_ASPECT);
        double sectionWidth = width - sectionStartX - whiteX;
        Box[] markBoxes = new Box[n];
        Box[] questionBoxes = new Box[questions];
        for (int i = 0; i < n; i++) {
            markBoxes[i] = new Box(0, sectionHeight / n * i + whiteY / (n - 1) * i , sectionStartX, sectionHeight / n * (i + 1) + whiteY / (n - 1) * i, 0, width - 1, 0, height - 1);
        }
        for (int i = 0; i < questions; i++) {
            int row = i % n;
            int column = i / n;
            questionBoxes[i] = new Box(sectionStartX + sectionWidth / m * column + whiteX / m * (column + 1), sectionHeight / n * row + whiteY / (n - 1) * row, sectionStartX + sectionWidth / m * (column + 1) + whiteX / m * (column+1), sectionHeight / n * (row + 1) + whiteY / (n - 1) * row, 0, width - 1, 0, height - 1);
        }
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
        Graphics2D gfx = (Graphics2D) img.getGraphics();
        gfx.setColor(Color.white);
        gfx.fillRect(0, 0, width, height);
        gfx.setColor(Color.black);
        for (Box box : markBoxes) {
            //System.out.println(box.width()/box.height());
            drawMark(gfx, box);
        }
        for (int i = 0; i < questions; i++) {
            //System.out.println(questionBoxes[i].width()/questionBoxes[i].height());
            drawQuestion(gfx, questionBoxes[i], i + 1, choices);
        }
        return img;
    }

    private static double whitespaceX(double w, double h, double wt, double ht) {
        return Math.max(h * wt / ht - w, 0);
    }

    private static double whitespaceY(double w, double h, double wt, double ht) {
        return Math.max(w * ht / wt - h, 0);
    }

    private static void drawMark(Graphics2D gfx, Box box) {
        Rect r = new Rect(box);
        gfx.fillRect(r.sx, r.sy, r.ex - r.sx + 1, r.ey - r.sy + 1);
        //gfx.drawRect(box.sx,box.sy,box.ex-box.sx, box.ey-box.sy);
    }

    private static void drawQuestion(Graphics2D gfx, Box box, int number, char[] choices) {
        Box num = new Box(box.sx, box.sy, box.sx+box.width()/(choices.length+1),box.ey,box.minX,box.maxX,box.minY,box.maxY);
        for (int i=0; i< choices.length; i++){
            Box bubble = new Box(box.sx + box.width()*(i+1)/(choices.length+1), box.sy, box.sx + box.width()*(i+2)/(choices.length+1),box.ey,box.minX,box.maxX,box.minY,box.maxY);
            drawBubble(gfx,bubble,choices[i]);
        }
    }

    private static void drawBubble(Graphics2D gfx, Box box, char symbol) {
        Rect r = new Rect(box.scale(0.9));
        gfx.fillRect(r.sx, r.sy, r.width() + 1, r.height() + 1);
    }
}
