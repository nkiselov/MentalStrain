public class Box {
    int sx;
    int sy;
    int ex;
    int ey;

    public Box(float sx, float sy, float ex, float ey, int minX, int maxX, int minY, int maxY) {
        this.sx = clamp((int)Math.round(sx-0.5),minX,maxX);
        this.sy = clamp((int)Math.round(sy-0.5),minY,maxY);
        this.ex = clamp((int)Math.round(ex-0.5),minX,maxX);
        this.ey = clamp((int)Math.round(ey-0.5),minY,maxY);
    }

    private int clamp(int x, int min, int max){
        return Math.min(Math.max(x,min),max);
    }
}
