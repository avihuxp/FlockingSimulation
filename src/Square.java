/**
 * represents a square with a center and radius
 */
class Square<T> {

    // instance fields
    private final float x;
    private final float y;
    private final float r;


    /**
     * constructor
     *
     * @param x - the x coordinate of the center of the square
     * @param y - the y coordinate of the center of the square
     * @param r - the half-length of each edge of the Square
     */
    Square(float x, float y, float r) {
        this.x = x;
        this.y = y;
        this.r = r;
    }


    public float getY() {
        return y;
    }

    public float getX() {
        return x;
    }

    public float getR() {
        return r;
    }

    /**
     * checks if a given point is inside the square
     *
     * @param p - the point to be checks
     * @return true if the point is inside the square, false otherwise
     */
    boolean containsPoint(Point<T> p) {
        return !(p.getX() >= this.x + this.r ||
                p.getX() <= this.x - this.r ||
                p.getY() >= this.y + this.r ||
                p.getY() <= this.y - this.r);
    }

    /**
     * checks if another square is intersecting (overlapping) with this one
     *
     * @param other - the other square
     * @return true if the squares intersect, false otherwise
     */
    boolean intersects(Square<T> other) {
        return !(this.x + this.r < other.x - other.r ||
                this.x - this.r > other.x + other.r ||
                this.y + this.r < other.y - other.r ||
                this.y - this.r > other.y + other.r);
    }


}