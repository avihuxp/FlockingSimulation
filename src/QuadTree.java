import java.util.ArrayList;

/**
 * a generic QuadTree implementation
 *
 * @param <T> the type of data for the QuadTree points to hold
 */
public class QuadTree<T> {

    //max capacity of points that each QuadTree node can hold
    public static final int QT_NODE_CAPACITY = 4;

    //the boundary of this QuadTree node
    private final Square<T> boundary;

    //a flag for weather the node is subdivided
    private boolean isDivided = false;

    //children
    private QuadTree<T> tr;
    private QuadTree<T> tl;
    private QuadTree<T> br;
    private QuadTree<T> bl;

    //the array of points held by this node
    private final ArrayList<Point<T>> points;


    /**
     * constructor
     *
     * @param boundary - the boundary square of this node
     */
    public QuadTree(Square<T> boundary) {
        this.boundary = boundary;
        this.points = new ArrayList<>();
    }

    /**
     * insert a new point into the node, subdivide if needed
     *
     * @param p - the point to be inserted
     * @return true if the point was inserted into this node, false otherwise
     */
    public boolean insert(Point<T> p) {
        //if the point does not intersect this node
        if (!boundary.containsPoint(p)) {
            return false;
        }
        // if there is enough space for another point and the node is not
        // divided
        if (this.points.size() < QT_NODE_CAPACITY && !isDivided) {
            this.points.add(p);
            return true;
        }

        // if not already subdivided
        if (!isDivided) subDivide();

        // try to insert into children
        if (tr.insert(p)) return true;
        if (br.insert(p)) return true;
        if (bl.insert(p)) return true;
        return tl.insert(p);
    }

    private void subDivide() {
        //create local variables for readability
        float x = this.boundary.getX();
        float y = this.boundary.getY();
        float r = this.boundary.getR();

        //subdivide this node into 4 new, equally sized nodes
        tr = new QuadTree<>(new Square<>(x + r / 2, y - r / 2, r / 2));
        tl = new QuadTree<>(new Square<>(x - r / 2, y - r / 2, r / 2));
        br = new QuadTree<>(new Square<>(x + r / 2, y + r / 2, r / 2));
        bl = new QuadTree<>(new Square<>(x - r / 2, y + r / 2, r / 2));

        //set isDivided to true
        this.isDivided = true;
    }

    /**
     * returns all points in this node that are in the given range
     *
     * @param range - a Square that is the range to look for points in
     * @return an ArrayList of the points from this node that are in the range
     */
    ArrayList<Point<T>> queryRange(Square<T> range) {
        ArrayList<Point<T>> result = new ArrayList<>();

        if (!boundary.intersects(range)) {
            return result;
        }

        for (Point<T> point :
                this.points) {
            if (range.containsPoint(point)) result.add(point);
        }

        if (!isDivided) return result;

        result.addAll(tr.queryRange(range));
        result.addAll(br.queryRange(range));
        result.addAll(bl.queryRange(range));
        result.addAll(tl.queryRange(range));

        return result;
    }

}