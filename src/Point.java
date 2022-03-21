/**
 * a generic point class
 * @param <T>
 */
class Point<T> {
    private static final int SIZE = 2;
    // instance fields
    private final float x;
    private final float y;
    private final T userData;

    /**
     * constructor
     *
     * @param x        - the x coordinate of the point
     * @param y        - the y coordinate of the point
     * @param userData - user data of generic type
     */
    Point(float x, float y, T userData) {
        this.x = x;
        this.y = y;
        this.userData = userData;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public T getUserData() {
        return userData;
    }

}