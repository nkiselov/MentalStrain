package Sheet;

public class Box {
    double sx;
    double sy;
    double ex;
    double ey;
    int minX;
    int maxX;
    int minY;
    int maxY;

    public Box(double sx, double sy, double ex, double ey, int minX, int maxX, int minY, int maxY) {
        this.sx = sx;
        this.sy = sy;
        this.ex = ex;
        this.ey = ey;
        this.minX = minX;
        this.maxX = maxX;
        this.minY = minY;
        this.maxY = maxY;
    }

    double width() {
        return ex - sx;
    }

    double height() {
        return ey - sy;
    }

    Box scale(double s) {
        double midX = (ex + sx) / 2;
        double midY = (ey + sy) / 2;
        double ex = (this.ex - midX) * s + midX;
        double sx = (this.sx - midX) * s + midX;
        double ey = (this.ey - midY) * s + midY;
        double sy = (this.sy - midY) * s + midY;
        return new Box(sx, sy, ex, ey, minX, maxX, minY, maxY);
    }
}
