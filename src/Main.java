import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        BufferedImage img = AnswerSheetGenerator.generateSheet(120,new char[]{'A','B','C','D','E','F'}, 8000,4000,0,100, new Font("Helvetica", Font.PLAIN, 10),Color.black);
        ImageIO.write(img,"png",new File("sheet.png"));
    }
}
