import java.awt.*;
import java.awt.image.BufferedImage;

public class AnswerSheetGenerator {
    private static final float MARK_ASPECT = 2;
    public static BufferedImage generateSheet(String title, int questions, char[] choices, int height,int width, int aimTitleHeight, int minRows){
        float aspect = (height-aimTitleHeight)/(float)width;
        int low = minRows;
        int high = questions+1;
        while(high-low>1){
            int n = (low+high)/2;
            int m = (questions-1)/n+1;
            float a = n/(m*(choices.length+1)+MARK_ASPECT)-aspect;
            if(a<0) {
                low = n;
            }else if(a>0){
                high = n;
            }
        }
        int n;
        int m;
        float a;
        int lowm = (questions-1)/low+1;
        float lowa = low/(lowm*(choices.length+1)+MARK_ASPECT)-aspect;
        int highm = (questions-1)/high+1;
        float higha = high/(highm*(choices.length+1)+MARK_ASPECT)-aspect;
        if(Math.abs(lowa)<Math.abs(higha)){
            n = low;m = lowm;a = lowa;
        }else{
            n = high;m = highm;a = higha;
        }
        System.out.println(n+","+m+","+a*width);
        float sectionStartY = aimTitleHeight-a;
        float sectionHeight = height-aimTitleHeight+a;
        float sectionStartX = sectionHeight/n*MARK_ASPECT;
        float sectionWidth = width-sectionStartX;
        Box titleBox = new Box(0,0,width,(int)(sectionStartY),0,width-1,0,height-1);
        Box[] markBoxes = new Box[n];
        Box[] questionBoxes = new Box[questions];
        for (int i=0; i<n; i++){
            markBoxes[i] = new Box(0,sectionStartY+sectionHeight/n*i,sectionStartX,sectionStartY+sectionHeight/n*(i+1),0,width-1,0,height-1);
        }
        for(int i=0; i<questions; i++){
            int row = i%n;
            int column = i/n;
            questionBoxes[i] = new Box(sectionStartX+sectionWidth/m*column,sectionStartY+sectionHeight/n*row,sectionStartX+sectionWidth/m*(column+1),sectionStartY+sectionHeight/n*(row+1),0,width-1,0,height-1);
        }
        BufferedImage img = new BufferedImage(width,height,BufferedImage.TYPE_3BYTE_BGR);
        Graphics2D gfx = (Graphics2D)img.getGraphics();
        drawBox(gfx,titleBox);
        for (Box box : markBoxes) {
            drawBox(gfx, box);
        }
        for (Box box : questionBoxes) {
            drawBox(gfx, box);
        }
        return img;
    }

    private static void drawBox(Graphics2D gfx, Box box){
        gfx.drawRect(box.sx,box.sy,box.ex-box.sx, box.ey-box.sy);
    }

    private static void bubble(BufferedImage img){
    }
}
