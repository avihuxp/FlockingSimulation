import processing.core.PConstants;
import processing.core.PVector;

/***
 * Obstacle class - represents an obstacle in the simulation for flocks to avoid
 */
class Obstacle implements Locatable {
    private final BoidSimulation boidSimulation;
    private final PVector obstacleColor = new PVector(23, 128, 237);
    private final int obstacleSize = 11;
    PVector position;

    /***
     * Constructor for the obstacle class
     * @param boidSimulation - reference to the boidSimulation for access to Processing methods
     * @param x - x position of the obstacle
     * @param y - y position of the obstacle
     */
    Obstacle(BoidSimulation boidSimulation, int x, int y) {
        this.boidSimulation = boidSimulation;
        this.position = new PVector(x, y);
    }

    /***
     * Method to display the obstacle
     */
    void display() {
        boidSimulation.noStroke();
        boidSimulation.fill(obstacleColor.x, obstacleColor.y, obstacleColor.z);
        boidSimulation.ellipseMode(PConstants.RADIUS);
        boidSimulation.ellipse(position.x, position.y, obstacleSize, obstacleSize);
    }

    @Override
    public float getX() {
        return position.x;
    }

    @Override
    public float getY() {
        return position.y;
    }

    public int getObstacleSize() {
        return obstacleSize;
    }
}
