import processing.core.PConstants;
import processing.core.PVector;

import java.util.ArrayList;

/**
 * Vehicle class - represents a vehicle agent in the simulation
 */
class Vehicle implements Locatable {
    // the rendering buffer from screen edges
    static final int RENDER_BUFFER = 10;
    private static final float VISION_RADIUS = 150;
    private static final float OBSTACLE_AVOIDANCE_COEFFICIENT = 2f;


    private final BoidSimulation boidSimulation;
    //vehicle's rendering constants
    float r;
    private final PVector color;


    // the vehicle's awareness constants
    private final float maxSpeed;
    private final float maxForce;
    private final float visionRadius;
    private final float minVisionAngle;
    private final float maxVisionAngle;
    private static final float MIN_VISION_ANGLE = 3f / 4f;
    private static final float MAX_VISION_ANGLE = 5f / 4f;

    // vehicle fields
    private final PVector position;
    private final PVector acceleration;
    private final PVector velocity;

    // flock fields
    private final int flockNumber;
    private final PVector coefficientVector;
    private static final int minDistanceFromEdge = 150;


    /**
     * constructor. creates a vehicle in the given x,y coordinates
     *
     * @param x                 - the vehicle's x coordinate
     * @param y                 - the vehicle's y coordinate
     * @param color             - rgb value of vehicle
     * @param flockNumber       - the number of the flock of this vehicle
     * @param coefficientVector - a vector that holds the alignment,
     *                          cohesion, and separation coefficients in
     *                          this order
     */
    Vehicle(BoidSimulation boidSimulation, float x,
            float y,
            PVector color,
            int flockNumber,
            PVector coefficientVector) {
        this.boidSimulation = boidSimulation;
        this.color = color;
        this.flockNumber = flockNumber;
        this.coefficientVector = coefficientVector;
        acceleration = new PVector(0, 0);
        velocity = new PVector(boidSimulation.random(-10, 10), boidSimulation.random(-10, 10));
        position = new PVector(x, y);
        r = 6 + flockNumber % 2;
        maxSpeed = 4;
        maxForce = 0.2f;
        visionRadius = VISION_RADIUS;
        minVisionAngle = MIN_VISION_ANGLE;
        maxVisionAngle = MAX_VISION_ANGLE;

    }

    /**
     * a method to updates the vehicle's acceleration
     */
    void update() {
        // Update velocity
        velocity.add(acceleration);
        // Limit speed
        velocity.limit(maxSpeed);
        position.add(velocity);
        // Reset acceleration to 0 each cycle
        acceleration.mult(0);
    }

    /**
     * applies a force to the vehicle's acceleration
     *
     * @param force - the force to be applied
     */
    void applyForce(PVector force) {
        // We could add mass here if we want A = F / M
        acceleration.add(force);
    }


    /**
     * returns all neighbours within the visionRadius of the current
     * vehicle
     *
     * @return all neighbours of the current vehicle
     */
    ArrayList<Vehicle> getNeighboursQuad() {
        ArrayList<Point<Vehicle>> possibleNeighbours =
                BoidSimulation.quadTree.queryRange(new Square<>(this.getX(), this.getY(),
                        Vehicle.RENDER_BUFFER + this.visionRadius));
        ArrayList<Vehicle> neighbours = new ArrayList<>();
        for (Point<Vehicle> other :
                possibleNeighbours) {
            if (other.getUserData() != this && inView(other.getUserData())) {
                neighbours.add(other.getUserData());
            }
        }
        return neighbours;
    }

    /**
     * returns all neighbours within the visionRadius of the current
     * vehicle
     *
     * @param flock - all vehicles in the flock
     * @return all neighbours of the current vehicle
     */
    ArrayList<Vehicle> getNeighbours(ArrayList<Vehicle> flock) {
        ArrayList<Vehicle> neighbours = new ArrayList<>();
        for (Vehicle other :
                flock) {
            if (other != this && PVector.dist(this.position, other.position)
                    <= visionRadius && inView(other)) {
                neighbours.add(other);
            }
        }
        return neighbours;
    }

    /**
     * calculates whether the given vehicle is in the vision of this
     *
     * @param other - the other vehicle
     * @return true if the other vehicle is in the vision of this, false otherwise
     */
    private boolean inView(Vehicle other) {
        PVector vecBetweenVehicles = PVector.sub(other.position,
                this.position);
        float angle = PVector.angleBetween(this.velocity,
                vecBetweenVehicles);
        return !(minVisionAngle <= angle && angle <= maxVisionAngle);
    }

    /**
     * calculates and returns the desired steering vector for this
     * vehicle to alignment with its neighbors direction
     *
     * @param neighbors - the flock of this vehicle
     * @return the desired velocity vector for alignment for this vehicle
     */
    PVector alignment(ArrayList<Vehicle> neighbors) {
        // desired steering velocity vector
        PVector steering = new PVector();
        int total = 0;
        for (Vehicle v :
                neighbors) {
            if (v.flockNumber == this.flockNumber) {
                steering.add(v.velocity);
                total++;
            }
        }
        if (total != 0) {
            steering.setMag(maxSpeed);
            steering.sub(this.velocity);
            steering.limit(maxForce);
        }
        return steering;
    }

    /**
     * calculates and returns the desired steering vector for this
     * vehicle to move towards the center of its local neighborhood
     *
     * @param neighbors - the flock of this vehicle
     * @return the desired velocity vector for cohesion for this vehicle
     */
    PVector cohesion(ArrayList<Vehicle> neighbors) {
        PVector steering = new PVector();
        int total = 0;
        for (Vehicle v :
                neighbors) {
            if (v.flockNumber == this.flockNumber) {
                total++;
                steering.add(v.position);
            }
        }
        if (total != 0) {
            steering.div(total);
            steering.sub(position);
            steering.setMag(maxSpeed);
            steering.sub(velocity);
            steering.limit(maxForce);
        }
        return steering;
    }

    /**
     * calculates and returns the desired steering vector for this
     * vehicle to move away from its neighbors, weighted by the adjacency of other vehicles
     *
     * @param neighbours - the local flock of this vehicle
     * @return the desired steering vector for separation for this vehicle
     */

    PVector separation(ArrayList<Vehicle> neighbours) {
        PVector steering = new PVector();
        for (Vehicle v :
                neighbours) {
            float dist = PVector.dist(this.position, v.position);
            if (dist != 0) {
                PVector distVector = PVector.sub(this.position, v.position);
                distVector.div(dist);
                steering.add(distVector);
            }
        }
        steering.div(neighbours.size());
        steering.setMag(maxSpeed);
        steering.sub(velocity);
        steering.limit(maxForce);
        return steering;
    }


    /**
     * calculates and returns the desired steering vector for this
     * vehicle to move away from obstacles
     *
     * @param obstacles - the obstacles in the vicinity of this vehicle
     * @return the desired steering vector for obstacle avoidance for this vehicle
     */

    PVector obstacleAvoidance(ArrayList<Obstacle> obstacles) {
        PVector steering = new PVector();

        for (Obstacle o :
                obstacles) {
            float dist = PVector.dist(this.position, o.position);
            if (dist <= (visionRadius + o.getObstacleSize()) * 1.25f) {
                PVector steerAwayVector = PVector.sub(this.position, o.position);
                steerAwayVector.setMag(maxSpeed);
                steerAwayVector.div((dist + o.getObstacleSize()) * 1.2f);
                steering.add(steerAwayVector);
            }
        }
        return steering;
    }

    /**
     * calculates and returns the desired steering vector for this
     * vehicle to move away from the edges of the screen
     */
    void avoidEdges() {
        PVector desired = velocity.copy();
        if (position.x < minDistanceFromEdge) {
            desired.x = maxSpeed;
        }
        if (position.x > boidSimulation.width - minDistanceFromEdge) {
            desired.x = -maxSpeed;
        }
        if (position.y < minDistanceFromEdge) {
            desired.y = maxSpeed;
        }
        if (position.y > boidSimulation.height - minDistanceFromEdge) {
            desired.y = -maxSpeed;
        }
        PVector steering = PVector.sub(desired, velocity);
        steering.setMag(maxSpeed);
        steering.limit(2 * maxForce);
        applyForce(steering);
    }

    /**
     * makes the vehicle flock with all other vehicles in its flock
     *
     * @param boids - the ArrayList of vehicles in this vehicles flock
     */
    void flock(ArrayList<Vehicle> boids) {
        // loop through the boids and find neighbours
        ArrayList<Vehicle> neighbours;
        if (BoidSimulation.WITH_QUAD) {
            neighbours = this.getNeighboursQuad();
        } else {
            neighbours = this.getNeighbours(boids);
        }
        if (neighbours.size() != 0) {
            PVector alignment = alignment(neighbours);
            acceleration.add(alignment.mult(coefficientVector.x));
            PVector cohesion = cohesion(neighbours);
            acceleration.add(cohesion.mult(coefficientVector.y));
            PVector separation = separation(neighbours);
            acceleration.add(separation.mult(coefficientVector.z));
            PVector obstacleAvoidance = obstacleAvoidance(boidSimulation.obstacles);
            acceleration.add(obstacleAvoidance.mult(OBSTACLE_AVOIDANCE_COEFFICIENT));
        }
    }

    /**
     * if the vehicle exits the screen, moves it to the other side of
     * the screen
     */
    void edges() {
        if (position.x < -RENDER_BUFFER)
            position.x = boidSimulation.width + RENDER_BUFFER;
        if (position.x > boidSimulation.width + RENDER_BUFFER)
            position.x = -RENDER_BUFFER;
        if (position.y < -RENDER_BUFFER)
            position.y = boidSimulation.height + RENDER_BUFFER;
        if (position.y > boidSimulation.height + RENDER_BUFFER)
            position.y = -RENDER_BUFFER;
    }

    /**
     * displays the vehicle
     */
    void display() {
        // Draw a triangle rotated in the direction of velocity
        float theta = velocity.heading() + PConstants.PI / 2;
        boidSimulation.fill(color.x, color.y, color.z);
        boidSimulation.noStroke();
        boidSimulation.pushMatrix();
        boidSimulation.translate(position.x, position.y);
        boidSimulation.rotate(theta);
        boidSimulation.beginShape();
        boidSimulation.vertex(0, -r * 2);
        boidSimulation.vertex(-r, r * 2);
        boidSimulation.vertex(r, r * 2);
        boidSimulation.endShape(PConstants.CLOSE);
        boidSimulation.popMatrix();
    }

    @Override
    public float getX() {
        return position.x;
    }

    @Override
    public float getY() {
        return position.y;
    }
}
