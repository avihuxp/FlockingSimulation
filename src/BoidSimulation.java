import processing.core.PApplet;
import processing.core.PVector;

import java.util.ArrayList;

public class BoidSimulation extends PApplet {


    private static final PVector backgroundColor = new PVector(22, 0, 56);
    private static final int NUM_OF_VEHICLES = 100;
    private static final int NUM_OF_FLOCKS = 6;
    private static final boolean SAVE_FRAMES = false;
    private static final boolean WITH_QUAD = false;
    private static QuadTree<Vehicle> quadTree;
    ArrayList<Vehicle> flocks;
    ArrayList<Obstacle> obstacles;
    PVector[] colorArray = new PVector[]{
            new PVector(237, 174, 7),
            new PVector(240, 41, 99),
            new PVector(43, 90, 237),
            new PVector(200, 200, 200),
            new PVector(174, 8, 250),
            new PVector(19, 92, 1)
    };

    public static void main(String[] args) {
        PApplet.main("BoidSimulation");
    }

    @Override
    public void settings() {
//        size(1080,9060);
        fullScreen();
    }


    @Override
    public void setup() {
        background(backgroundColor.x, backgroundColor.y, backgroundColor.z);
        flocks = new ArrayList<>();
        for (int i = 0; i < NUM_OF_FLOCKS; i++) {
            PVector coefficientsVector = new PVector(
                    lerp(0.8f, 1.5f, random(0, 1)),
                    lerp(0.8f, 1.5f, random(0, 1)),
                    lerp(1.1f, 1.6f, random(0, 1)));
            println("flock number " + i + " with:" + coefficientsVector);
            for (int j = 0; j < NUM_OF_VEHICLES; j++) {
                flocks.add(new Vehicle(
                        random(0,
                                width),
                        random(0,
                                height),
                        colorArray[j % NUM_OF_FLOCKS],
                        j % NUM_OF_FLOCKS,
                        coefficientsVector));
            }
        }
        obstacles = new ArrayList<>();
    }

    /**
     * thresholdAngle = arctan(height/width) - preprocess
     *
     * Give two point A,B, return min dist of them:
     * Angle = arctan(|A.y-B.y|/|A.x-B.x|)
     * if Angle < thresholdAngle:
     *      lineLength = width/sin(Angle)
     * else:
     *      lineLength = height/cos(Angle)
     * dist = euclideanDist(A,B)
     * return min(dist, LineLength - dist)
     *
     */

    @Override
    public void draw() {

//        background(255);
        fill(backgroundColor.x, backgroundColor.y, backgroundColor.z, 30f);
        rect(0, 0, width, height);
        if (WITH_QUAD) {
            quadTree = new QuadTree<>(new Square<>(width * 0.5f, height * 0.5f,
                    width * 0.5f + Vehicle.RENDER_BUFFER));
            for (Vehicle v :
                    flocks) {
                quadTree.insert(new Point<>(v.getX(), v.getY(), v));
            }
        }
        for (Vehicle v :
                flocks) {
            v.flock(flocks);
//            v.avoidEdges();
            v.update();

            v.edges();
            v.display();
        }
        if (mousePressed && mouseButton == LEFT) {
            obstacles.add(new Obstacle(mouseX, mouseY));
        }

        if (mousePressed && mouseButton == RIGHT) {
            for (int i = 0; i < obstacles.size(); i++) {
                Obstacle o = obstacles.get(i);
                if (dist(mouseX, mouseY, o.position.x, o.position.y) < o.obstacleSize) {
                    obstacles.remove(o);
                }
            }
        }

        for (Obstacle o :
                obstacles) {
            o.display();
        }

        if (frameCount < 45 * 90 && SAVE_FRAMES) {
            saveFrame("output/boids1_####.tif");
            println("curr frame rate:" + frameRate + ", curr frame: " + frameCount);
            println("seconds of video: " + (frameCount / 60));

        }
    }

    class Vehicle implements Locatable {
        // the rendering buffer from screen edges
        private static final int RENDER_BUFFER = 10;
        private static final float VISION_RADIUS = 150;
        private static final float OBSTACLE_AVOIDANCE_COEFFICIENT = 2f;


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
        private static final float minDistanceFromObstacle = 150;
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
        Vehicle(float x,
                float y,
                PVector color,
                int flockNumber,
                PVector coefficientVector) {
            this.color = color;
            this.flockNumber = flockNumber;
            this.coefficientVector = coefficientVector;
            acceleration = new PVector(0, 0);
            velocity = new PVector(random(-10, 10), random(-10, 10));
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
                    quadTree.queryRange(new Square<>(this.getX(), this.getY(),
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

        private boolean inView(Vehicle other) {
            PVector vecBetweenVehicles = PVector.sub(other.position,
                    this.position);
            float angle = PVector.angleBetween(this.velocity,
                    vecBetweenVehicles);
            return !(minVisionAngle <= angle && angle <= maxVisionAngle);
        }

        /**
         * calculates and returns the desired steering vector for this
         * vehicle to align with its neighbors direction
         *
         * @param neighbors - the flock of this vehicle
         * @return the desired velocity vector for alignment for this vehicle
         */
        PVector align(ArrayList<Vehicle> neighbors) {
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

        PVector obstacleAvoidance(ArrayList<Obstacle> obstacles) {
            PVector steering = new PVector();

            for (Obstacle o :
                    obstacles) {
                float dist = PVector.dist(this.position, o.position);
                if (dist <= (visionRadius + o.obstacleSize) * 1.25f) {
                    PVector steerAwayVector = PVector.sub(this.position, o.position);
                    steerAwayVector.setMag(maxSpeed);
                    steerAwayVector.div((dist + o.obstacleSize) * 1.2f);
                    steering.add(steerAwayVector);
                }
            }
            return steering;
        }

        void avoidEdges() {
            PVector desired = velocity.copy();
            if (position.x < minDistanceFromEdge) {
                desired.x = maxSpeed;
            }
            if (position.x > width - minDistanceFromEdge) {
                desired.x = -maxSpeed;
            }
            if (position.y < minDistanceFromEdge) {
                desired.y = maxSpeed;
            }
            if (position.y > height - minDistanceFromEdge) {
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
            if (WITH_QUAD) {
                neighbours = this.getNeighboursQuad();
            } else {
                neighbours = this.getNeighbours(flocks);
            }
//
            if (neighbours.size() != 0) {
                PVector alignment = align(neighbours);
                acceleration.add(alignment.mult(coefficientVector.x));
                PVector cohesion = cohesion(neighbours);
                acceleration.add(cohesion.mult(coefficientVector.y));
                PVector separation = separation(neighbours);
                acceleration.add(separation.mult(coefficientVector.z));
                PVector obstacleAvoidance = obstacleAvoidance(obstacles);
                acceleration.add(obstacleAvoidance.mult(OBSTACLE_AVOIDANCE_COEFFICIENT));
            }
        }

        /**
         * if the vehicle exits the screen, moves it to the other side of
         * the screen
         */
        void edges() {
            if (position.x < -RENDER_BUFFER)
                position.x = width + RENDER_BUFFER;
            if (position.x > width + RENDER_BUFFER)
                position.x = -RENDER_BUFFER;
            if (position.y < -RENDER_BUFFER)
                position.y = height + RENDER_BUFFER;
            if (position.y > height + RENDER_BUFFER)
                position.y = -RENDER_BUFFER;
        }

        /**
         * displays the vehicle
         */
        void display() {
            // Draw a triangle rotated in the direction of velocity
            float theta = velocity.heading() + PI / 2;
            fill(color.x, color.y, color.z);
            noStroke();
            pushMatrix();
            translate(position.x, position.y);
            rotate(theta);
            beginShape();
            vertex(0, -r * 2);
            vertex(-r, r * 2);
            vertex(r, r * 2);
            endShape(CLOSE);
            popMatrix();
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

    class Obstacle {
        private final PVector obstacleColor = new PVector(23, 128, 237);
        private final int obstacleSize = 11;
        PVector position;

        Obstacle(int x, int y) {
            this.position = new PVector(x, y);
        }

        void display() {
            noStroke();
            fill(obstacleColor.x, obstacleColor.y, obstacleColor.z);
            ellipseMode(RADIUS);
            ellipse(position.x, position.y, obstacleSize, obstacleSize);
        }
    }
}


