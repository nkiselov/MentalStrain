import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        BufferedImage img = AnswerSheetGenerator.generateSheet("TEST",120,new char[]{'a','b'}, 1000,500,100,30);
        ImageIO.write(img,"png",new File("sheet.png"));
    }
}
