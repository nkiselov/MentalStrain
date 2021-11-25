import java.awt.image.BufferedImage;

public class AnswerSheet {
    protected static final double MARK_ASPECT = 2;
    protected static final double MARK_SCALE = 0.5;
    protected static final double BUBBLE_SCALE = 0.75;
    protected static final double VERTICAL_SPACE = 0.5;
    protected static final double HORIZONTAL_SPACE = 0.5;
    protected static final double FONT_SCALE = 0.5;
    protected static final double STROKE_SCALE = 0.05;
    protected static final double QUESTION_NUMBER_SPACE = 0.1;

    public BufferedImage img;
    public AnswerSheetContext ctx;

    public AnswerSheet(BufferedImage img, AnswerSheetContext ctx) {
        this.img = img;
        this.ctx = ctx;
    }
}
