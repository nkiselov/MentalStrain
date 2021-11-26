package Sheet;

public class Rect {
    int sx;
    int sy;
    int ex;
    int ey;

    public Rect(Box box) {
        this.sx = clamp((int) Math.round(box.sx - 0.5), box.minX, box.maxX);
        this.sy = clamp((int) Math.round(box.sy - 0.5), box.minY, box.maxY);
        this.ex = clamp((int) Math.round(box.ex - 0.5), box.minX, box.maxX);
        this.ey = clamp((int) Math.round(box.ey - 0.5), box.minY, box.maxY);
    }

    private int clamp(int x, int min, int max) {
        return Math.min(Math.max(x, min), max);
    }

    int width() {
        return ex - sx;
    }

    int height() {
        return ey - sy;
    }
}