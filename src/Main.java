import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException, MalformedScanException {
//        AnswerSheet sh = AnswerSheetGenerator.generateSheet(81,new char[]{'A','B','C','D','E','F'}, 2200,1700,0,100, new Font("Helvetica", Font.PLAIN, 10),Color.black);
//        ImageIO.write(sh.img,"png",new File("sheet.png"));
//        AnswerSheetContext.writeToFile(sh.ctx,new File("context.txt"));
        BufferedImage img = ImageIO.read(new File("scan.jpg"));
        AnswerSheetContext ctx = AnswerSheetContext.readFromFile(new File("context.txt"));
        int[] choices = AnswerSheetScanner.scanSheet(new AnswerSheet(img,ctx));
//        for(int i = 0; i<choices.length; i++){
//            String add = "";
//            switch(choices[i]){
//                case -2:
//                    add = "multiple";
//                    break;
//                case -1:
//                    add = "none";
//                    break;
//                case 0:
//                    add = "A";
//                    break;
//                case 1:
//                    add = "B";
//                    break;
//                case 2:
//                    add = "C";
//                    break;
//                case 3:
//                    add = "D";
//                    break;
//                case 4:
//                    add = "E";
//                    break;
//                case 5:
//                    add = "F";
//                    break;
//            }
//            System.out.println((i+1)+":"+add);
//        }
    }
}
