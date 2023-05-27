package dev.christmasbear;

public class Decay {
    public float a;
    public float b;
    public float c;
    public float d;

    public Decay(float a, float b, float c, float d) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
    }

    public float getY(float x) {
        if (x == 0) return 0;
        return (float) (isZero() ? 0 : a * Math.pow(x, b) + c * x + d);
    }

    public boolean isZero() {
        return a == 0 && b == 0 && c == 0 && d == 0;
    }
}
