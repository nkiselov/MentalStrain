import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        BufferedImage img = AnswerSheetGenerator.generateSheet(120,new char[]{'a','b','c','d','e','f'}, 4000,2000,0,100);
        ImageIO.write(img,"png",new File("sheet.png"));
    }
}
