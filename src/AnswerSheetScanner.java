import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class AnswerSheetScanner {
    private static final Vector2Int[] directions = new Vector2Int[]{
            new Vector2Int(1, 0),
            new Vector2Int(0, 1),
            new Vector2Int(-1, 0),
            new Vector2Int(0, -1)
    };
    private static final int PIXEL_ARTIFACT_TOLERANCE = 500;
    private static final int DARK_LIGHTNESS_CUTOFF = 25;
    private static final double MARK_ASPECT_RATIO_TOLERANCE = 0.075;
    private static final double MIN_MARK_FILL_TOLERANCE = 0.95;
    private static final double MIN_BUBBLE_FILL_TOLERANCE = 0.6;
    private static final int BUBBLE_LIGHTNESS_CUTOFF = 210;

    public static int[] scanSheet(AnswerSheet sheet) throws MalformedScanException {
        int w = sheet.img.getWidth();
        int h = sheet.img.getHeight();
        BufferedImage sh = new BufferedImage(w, h,
                BufferedImage.TYPE_BYTE_GRAY);
        Graphics2D gfx = (Graphics2D) sh.getGraphics();
        gfx.drawImage(sheet.img, 0, 0, sh.getWidth(), sh.getHeight(), null);
        gfx.dispose();
        try {
            ImageIO.write(sh, "png", new File("process1.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] pixels = ((DataBufferByte) sh.getRaster().getDataBuffer()).getData();
        byte[] newPixels = new byte[pixels.length];
        boolean[][] px = new boolean[h][w];
        for (int i = 0; i < pixels.length; i++) {
            if ((pixels[i] & 0xFF) < DARK_LIGHTNESS_CUTOFF) {
                newPixels[i] = (byte) 0;
                px[i / w][i % w] = true;
            } else {
                newPixels[i] = (byte) 255;
                px[i / w][i % w] = false;
            }
        }
        BufferedImage sh2 = new BufferedImage(w, h,
                BufferedImage.TYPE_BYTE_GRAY);
        byte[] pixels2 = ((DataBufferByte) sh2.getRaster().getDataBuffer()).getData();
        System.arraycopy(newPixels, 0, pixels2, 0, newPixels.length);
        try {
            ImageIO.write(sh2, "png", new File("process2.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<List<Vector2Int>> islands = new ArrayList<>();
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                if (px[y][x]) {
                    List<Vector2Int> closed = new ArrayList<>();
                    Queue<Vector2Int> open = new LinkedList<>();
                    open.add(new Vector2Int(x, y));
                    px[y][x] = false;
                    Vector2Int p;
                    while ((p = open.poll()) != null) {
                        for (Vector2Int dir : directions) {
                            int nx = p.x + dir.x;
                            int ny = p.y + dir.y;
                            if (nx >= 0 && nx < w && ny >= 0 && ny < h) {
                                if (px[ny][nx]) {
                                    open.add(new Vector2Int(nx, ny));
                                    px[ny][nx] = false;
                                }
                            }
                        }
                        closed.add(p);
                    }
                    if (closed.size() > PIXEL_ARTIFACT_TOLERANCE) {
                        islands.add(closed);
                    }
                }
            }
        }
        Graphics2D g = (Graphics2D) sheet.img.getGraphics();
        g.setColor(Color.blue);
        g.setStroke(new BasicStroke(4));
        List<Rectangle> marks = new ArrayList<>();
        for (List<Vector2Int> island : islands) {
            Rectangle r = minAreaBound(island);
            double fill = island.size() / (r.size.x * r.size.y);
            double aspect = r.size.x / r.size.y;
            if ((Math.abs(aspect / AnswerSheet.MARK_ASPECT - 1) <= MARK_ASPECT_RATIO_TOLERANCE || Math.abs(1 / aspect / AnswerSheet.MARK_ASPECT - 1) <= MARK_ASPECT_RATIO_TOLERANCE) && fill >= MIN_MARK_FILL_TOLERANCE) {
                if (Math.abs(1 / aspect / AnswerSheet.MARK_ASPECT - 1) <= MARK_ASPECT_RATIO_TOLERANCE) {
                    r.rotation += Math.PI / 2;
                    double temp = r.size.x;
                    r.size.x = r.size.y;
                    r.size.y = temp;
                }
                r.rotation = mod(r.rotation,Math.PI);
                marks.add(r);
                Vector2 wv = new Vector2(r.size.x / 2 * Math.cos(r.rotation), r.size.x / 2 * Math.sin(r.rotation));
                Vector2 hv = new Vector2(-r.size.y / 2 * Math.sin(r.rotation), r.size.y / 2 * Math.cos(r.rotation));
                double[] xmultipliers = new double[]{-1, -1, 1, 1};
                double[] ymultipliers = new double[]{-1, 1, 1, -1};
                for (int i = 0; i < 4; i++) {
                    int ni = (i + 1) % 4;
                    g.drawLine((int) (r.centroid.x + wv.x * xmultipliers[i] + hv.x * ymultipliers[i]), (int) (r.centroid.y + wv.y * xmultipliers[i] + hv.y * ymultipliers[i]), (int) (r.centroid.x + wv.x * xmultipliers[ni] + hv.x * ymultipliers[ni]), (int) (r.centroid.y + wv.y * xmultipliers[ni] + hv.y * ymultipliers[ni]));
                }
            }else {
                System.out.println(r + ":" + fill + ":" + aspect);
            }
        }
        try {
            ImageIO.write(sheet.img, "png", new File("process3.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(marks.size() != sheet.ctx.n*2){
            throw new MalformedScanException(marks.size(), sheet.ctx.n);
        }
        int q = sheet.ctx.questions;
        int choices = sheet.ctx.choices;
        int n = sheet.ctx.n;
        int m = sheet.ctx.m;
        double whiteX = sheet.ctx.whiteX;

        for(int i = 0; i< n; i++){
            if(marks.get(2*i).centroid.x>marks.get(2*i+1).centroid.x){
                Rectangle temp = marks.get(2*i);
                marks.set(2*i,marks.get(2*i+1));
                marks.set(2*i+1,temp);
            }
        }
        for(int i=0; i<n; i++){
            g.drawLine((int)(marks.get(2*i).centroid.x),(int)(marks.get(2*i).centroid.y),(int)(marks.get(2*i+1).centroid.x),(int)(marks.get(2*i+1).centroid.y));
        }
        g.setColor(Color.green);
        g.setStroke(new BasicStroke(4));
        int[] responses = new int[q];
        double step = 1/(AnswerSheet.MARK_ASPECT+whiteX+(choices+1)*m);
        for(int i=0; i<q; i++){
            int row = i%n;
            int column = i/n;
            Vector2 markLeft = marks.get(2*row).centroid;
            Vector2 markRight = marks.get(2*row+1).centroid;
            double scale = Math.sqrt((markRight.x-markLeft.x)*(markRight.x-markLeft.x)+(markRight.y-markLeft.y)*(markRight.y-markLeft.y))/(AnswerSheet.MARK_ASPECT+whiteX+(choices+1)*m);
            double r = scale*AnswerSheet.BUBBLE_SCALE;
            double fract = (0.5 + AnswerSheet.MARK_ASPECT/2+whiteX/(m+1)*(column+1)+(choices+1)*column)/(AnswerSheet.MARK_ASPECT+whiteX+(choices+1)*m);
            int choice = 0;
            int chosen = 0;
            for(int j=0; j<choices; j++){
                fract+=step;
                double cx = markLeft.x*(1-fract)+markRight.x*fract;
                double cy = markLeft.y*(1-fract)+markRight.y*fract;
                double fill = 0;
                double total = 0;
                for(int x = -(int)(r/2); x<=(int)(r/2); x++){
                    for(int y = -(int)(r/2); y<=(int)(r/2); y++){
                        if(x*x+y*y<=r*r){
                            total += 1;
                            if ((pixels[x + (int) (cx) + (y + (int) cy) * w] & 0XFF) < BUBBLE_LIGHTNESS_CUTOFF) {
                                fill++;
                            }
                        }
                    }
                }
                fill/=total;
                if(fill>MIN_BUBBLE_FILL_TOLERANCE) {
                    choice = j;
                    chosen+=1;
                    g.drawArc((int) (cx - r / 2), (int) (cy - r / 2), (int) (r), (int) (r), 0, 360);
                    System.out.println(i+","+j+":"+fill);
                }
            }
            if(chosen == 0){
                responses[i] = -1;
            }
            if(chosen == 1){
                responses[i] = choice;
            }
            if(chosen > 1){
                responses[i] = -2;
            }
        }
        try {
            ImageIO.write(sheet.img, "png", new File("process4.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return responses;
    }

    private static double mod(double x, double m){
        return x-Math.floor(x/m)*m;
    }

    private static class Vector2Int {
        int x;
        int y;

        public Vector2Int(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public String toString() {
            return "(" + x + ", " + y + ')';
        }
    }

    private static class Vector2 {
        double x;
        double y;

        public Vector2(double x, double y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public String toString() {
            return "(" + x + ", " + y + ')';
        }
    }

    private static class Rectangle {
        Vector2 centroid;
        Vector2 size;
        double rotation;

        public Rectangle(Vector2 centroid, Vector2 size, double rotation) {
            this.centroid = centroid;
            this.size = size;
            this.rotation = rotation;
        }

        @Override
        public String toString() {
            return "{" +
                    "c=" + centroid +
                    ", s=" + size +
                    ", r=" + rotation +
                    '}';
        }
    }

    private static ArrayList<Vector2Int> convexHull(List<Vector2Int> points) {
        int minY = Integer.MAX_VALUE;
        int minX = Integer.MAX_VALUE;
        Vector2Int min = null;
        for (Vector2Int v : points) {
            if (v.y < minY) {
                min = v;
                minY = v.y;
                minX = v.x;
            } else if (minY == v.y) {
                if (v.x < minX) {
                    min = v;
                    minX = v.x;
                }
            }
        }
        Vector2Int fmin = min;
        points.sort((lhs, rhs) -> -ccw(lhs, fmin, rhs));
        Stack<Vector2Int> stack = new Stack<>();
        for (Vector2Int v : points) {
            if (stack.size() > 1) {
                Vector2Int top = null;
                Vector2Int next = null;
                while (stack.size() > 1) {
                    top = stack.pop();
                    next = stack.peek();
                    if (ccw(v, top, next) > 0) {
                        break;
                    }
                }
                if (top != null) {
                    stack.push(top);
                }
            }
            stack.push(v);
        }
        return new ArrayList<>(stack);
    }

    private static Rectangle minAreaBound(List<Vector2Int> points) {
        List<Vector2Int> hull = convexHull(points);
        double minArea = Double.MAX_VALUE;
        Rectangle minRectangle = null;
        for (int i = 0; i < hull.size(); i++) {
            Vector2Int st = hull.get(i);
            Vector2Int en = hull.get((i + 1) % hull.size());
            double theta = Math.atan2(en.y - st.y, en.x - st.x);
            double c = Math.cos(theta);
            double s = Math.sin(theta);
            Vector2 mins = new Vector2(0, 0);
            Vector2 maxs = new Vector2(Math.sqrt((en.x - st.x) * (en.x - st.x) + (en.y - st.y) * (en.y - st.y)), 0);
            for (int j = 0; j < hull.size(); j++) {
                if (j != i && j != (i + 1) % hull.size()) {
                    double x = hull.get(j).x - st.x;
                    double y = hull.get(j).y - st.y;
                    double x0 = c * x + s * y;
                    double y0 = c * y - s * x;
                    mins.x = Math.min(x0, mins.x);
                    mins.y = Math.min(y0, mins.y);
                    maxs.x = Math.max(x0, maxs.x);
                    maxs.y = Math.max(y0, maxs.y);
                }
            }
            double width = maxs.x - mins.x;
            double height = maxs.y - mins.y;
            double area = width * height;
            if (area < minArea) {
                double midX0 = (maxs.x + mins.x) / 2;
                double midY0 = (maxs.y + mins.y) / 2;
                double midX = c * midX0 - s * midY0;
                double midY = s * midX0 + c * midY0;
                minRectangle = new Rectangle(new Vector2(midX + st.x, midY + st.y), new Vector2(width, height), theta);
                minArea = area;
            }
        }
        return minRectangle;
    }

//    private static double cos(Vector2 a, Vector2 b){
//        return (a.x*b.x+a.y*b.y)/Math.sqrt(a.x*a.x+a.y*a.y)/Math.sqrt(b.x*b.x+b.y*b.y);
//    }

    private static int ccw(Vector2Int a, Vector2Int b, Vector2Int c) {
        Vector2Int vab = new Vector2Int(a.x - b.x, a.y - b.y);
        Vector2Int vcb = new Vector2Int(c.x - b.x, c.y - b.y);
        return (int) Math.signum(vab.x * vcb.y - vab.y * vcb.x);
    }

    private static double slope(List<Rectangle> ps) {
        float sum_x = 0, sum_y = 0, sum_xy = 0, sum_x2 = 0;
        for (int i = 0; i < ps.size(); i++) {
            sum_x += ps.get(i).centroid.x;
            sum_y += ps.get(i).centroid.y;
            sum_xy += ps.get(i).centroid.x * ps.get(i).centroid.y;
            sum_x2 += Math.pow(ps.get(i).centroid.x, 2);
        }
        return (ps.size() * sum_xy - sum_x * sum_y) / (ps.size() * sum_x2 - Math.pow(sum_x, 2));
    }
}
