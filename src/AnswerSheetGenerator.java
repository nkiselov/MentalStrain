import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.image.BufferedImage;

public class AnswerSheetGenerator {

    public static AnswerSheet generateSheet(int questions, char[] choices, int height, int width, int minRows, int maxRows, Font font, Color color) {
        double aspect = (width) / (double) height;
        int low = minRows;
        int high = Math.min(Math.max(questions + 1, low),maxRows);
        while (high - low > 1) {
            int n = (low + high) / 2;
            int m = (questions - 1) / n + 1;
            double a = (m * (choices.length + 1) + 2*AnswerSheet.MARK_ASPECT + m * AnswerSheet.HORIZONTAL_SPACE) / (n + (n - 1) * AnswerSheet.VERTICAL_SPACE) - aspect;
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
        double lowx = whitespaceX((double) lowm * (choices.length + 1) + 2*AnswerSheet.MARK_ASPECT + lowm * AnswerSheet.HORIZONTAL_SPACE, low + (low - 1) * AnswerSheet.VERTICAL_SPACE, width, height) + lowm* AnswerSheet.HORIZONTAL_SPACE;
        double lowy = whitespaceY((double) lowm * (choices.length + 1) + 2*AnswerSheet.MARK_ASPECT + lowm * AnswerSheet.HORIZONTAL_SPACE, low + (low - 1) * AnswerSheet.VERTICAL_SPACE, width, height) + (low - 1) * AnswerSheet.VERTICAL_SPACE;
        int highm = (questions - 1) / high + 1;
        double highx = whitespaceX((double) highm * (choices.length + 1) + 2*AnswerSheet.MARK_ASPECT + highm * AnswerSheet.HORIZONTAL_SPACE, high + (high - 1) * AnswerSheet.VERTICAL_SPACE, width, height) + highm * AnswerSheet.HORIZONTAL_SPACE;
        double highy = whitespaceY((double) highm * (choices.length + 1) + 2*AnswerSheet.MARK_ASPECT + highm * AnswerSheet.HORIZONTAL_SPACE, high + (high - 1) * AnswerSheet.VERTICAL_SPACE, width, height) + (high - 1) * AnswerSheet.VERTICAL_SPACE;
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
        double scale = height / (n + whiteY);
        double whiteXScaled = whiteX*scale;
        double whiteYScaled = whiteY*scale;
        System.out.println(n + "," + m + "," + whiteXScaled + "," + whiteYScaled);
        double sectionHeight = height - whiteYScaled;
        double sectionStartX = sectionHeight / (double) n * (AnswerSheet.MARK_ASPECT);
        double sectionWidth = width - 2*sectionStartX - whiteXScaled;
        Box[] markBoxesLeft = new Box[n];
        Box[] markBoxesRight = new Box[n];
        Box[] questionBoxes = new Box[questions];
        for (int i = 0; i < n; i++) {
            markBoxesLeft[i] = new Box(0, sectionHeight / n * i + whiteYScaled / (n - 1) * i , sectionStartX, sectionHeight / n * (i + 1) + whiteYScaled / (n - 1) * i, 0, width - 1, 0, height - 1);
            markBoxesRight[i] = new Box(width-sectionStartX, sectionHeight / n * i + whiteYScaled / (n - 1) * i , width, sectionHeight / n * (i + 1) + whiteYScaled / (n - 1) * i, 0, width - 1, 0, height - 1);
        }
        for (int i = 0; i < questions; i++) {
            int row = i % n;
            int column = i / n;
            questionBoxes[i] = new Box(sectionStartX + sectionWidth / m * column + whiteXScaled / (m+1) * (column + 1), sectionHeight / n * row + whiteYScaled / (n - 1) * row, sectionStartX + sectionWidth / m * (column + 1) + whiteXScaled / (m+1) * (column+1), sectionHeight / n * (row + 1) + whiteYScaled / (n - 1) * row, 0, width - 1, 0, height - 1);
        }
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
        Graphics2D gfx = (Graphics2D) img.getGraphics();
        gfx.setColor(Color.white);
        gfx.fillRect(0, 0, width, height);
        gfx.setColor(color);
        gfx.setStroke(new BasicStroke((int)Math.floor(scale*AnswerSheet.STROKE_SCALE)));
        Font f = new Font(font.getName(),font.getStyle(),(int)Math.floor(scale*AnswerSheet.FONT_SCALE));
        gfx.setFont(f);
        int space = (int)Math.floor(scale*AnswerSheet.QUESTION_NUMBER_SPACE);
        for (Box box : markBoxesLeft) {
            //System.out.println(box.width()/box.height());
            drawMark(gfx, box);
        }
        for (Box box : markBoxesRight) {
            //System.out.println(box.width()/box.height());
            drawMark(gfx, box);
        }
        for (int i = 0; i < questions; i++) {
            //System.out.println(questionBoxes[i].width()/questionBoxes[i].height());
            drawQuestion(gfx, questionBoxes[i], i + 1, choices, space);
        }
        gfx.dispose();
        return new AnswerSheet(img,new AnswerSheetContext(questions, choices.length, n,m,whiteX,whiteY));
    }

    private static double whitespaceX(double w, double h, double wt, double ht) {
        return Math.max(h * wt / ht - w, 0);
    }

    private static double whitespaceY(double w, double h, double wt, double ht) {
        return Math.max(w * ht / wt - h, 0);
    }

    private static void drawMark(Graphics2D gfx, Box box) {
        Rect r = new Rect(box.scale(AnswerSheet.MARK_SCALE));
        gfx.fillRect(r.sx, r.sy, r.ex - r.sx + 1, r.ey - r.sy + 1);
        //gfx.drawRect(box.sx,box.sy,box.ex-box.sx, box.ey-box.sy);
    }

    private static void drawQuestion(Graphics2D gfx, Box box, int number, char[] choices, int space) {
        Rect num = new Rect(new Box(box.sx, box.sy, box.sx+box.width()/(choices.length+1),box.ey,box.minX,box.maxX,box.minY,box.maxY));
        drawRightString(gfx,String.valueOf(number),num,space);
        for (int i=0; i< choices.length; i++){
            Box bubble = new Box(box.sx + box.width()*(i+1)/(choices.length+1), box.sy, box.sx + box.width()*(i+2)/(choices.length+1),box.ey,box.minX,box.maxX,box.minY,box.maxY);
            drawBubble(gfx,bubble,choices[i]);
        }
    }

    private static void drawBubble(Graphics2D gfx, Box box, char symbol) {
        Rect rect = new Rect(box.scale(AnswerSheet.BUBBLE_SCALE));
        gfx.drawArc(rect.sx,rect.sy,rect.width(),rect.height(),0,360);
        drawCenteredString(gfx,String.valueOf(symbol),rect);
    }

//    private static void drawCenteredString(Graphics gfx, String s, Rect rect, FontMetrics fm) {
//        int x = (rect.width() - fm.stringWidth(s)) / 2;
//        System.out.println(s+":"+fm.getStringBounds(s,gfx));
//        int y = (fm.getMaxAscent() + (rect.height() - (fm.getMaxAscent()+ fm.getMaxDescent() )) / 2);
//        gfx.drawString(s, rect.sx+x, rect.sy+y);
//    }

    private static void drawRightString(Graphics2D gfx, String text, Rect rect, int space) {
        FontRenderContext frc = gfx.getFontRenderContext();
        GlyphVector gv = gfx.getFont().createGlyphVector(frc, text);
        Rectangle r2D = gv.getPixelBounds(null, 0, 0);
        int rWidth = (int) Math.round(r2D.getWidth());
        int rHeight = (int) Math.round(r2D.getHeight());
        int rX = (int) Math.round(r2D.getX());
        int rY = (int) Math.round(r2D.getY());

        int a = (rect.width()) - (rWidth) - rX - space;
        int b = (rect.height() / 2) - (rHeight / 2) - rY;
        gfx.drawString(text, rect.sx + a, rect.sy + b);
    }

    private static void drawCenteredString(Graphics2D gfx, String text, Rect rect) {
        FontRenderContext frc = gfx.getFontRenderContext();
        GlyphVector gv = gfx.getFont().createGlyphVector(frc, text);
        Rectangle r2D = gv.getPixelBounds(null, 0, 0);
        int rWidth = (int) Math.round(r2D.getWidth());
        int rHeight = (int) Math.round(r2D.getHeight());
        int rX = (int) Math.round(r2D.getX());
        int rY = (int) Math.round(r2D.getY());

        int a = (rect.width() / 2) - (rWidth / 2) - rX;
        int b = (rect.height() / 2) - (rHeight / 2) - rY;
        gfx.drawString(text, rect.sx + a, rect.sy + b);
    }
}
