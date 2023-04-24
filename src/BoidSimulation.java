import processing.core.PApplet;
import processing.core.PVector;

import java.util.ArrayList;

public class BoidSimulation extends PApplet {
    private static final PVector backgroundColor = new PVector(22, 0, 56);
    private static final int NUM_OF_VEHICLES = 100;
    private static final int NUM_OF_FLOCKS = 6;
    private static final boolean SAVE_FRAMES = false;
    public static final boolean WITH_QUAD = false;
    public static QuadTree<Vehicle> quadTree;
    ArrayList<Vehicle> flocks;
    ArrayList<Obstacle> obstacles;
    static PVector[] colorArray = getFlockColors();

    /**
     * Method to get the flock colors
     *
     * @return - array of PVector objects representing the flock colors as RGB values
     */
    private static PVector[] getFlockColors() {
        return new PVector[]{
                new PVector(237, 174, 7),
                new PVector(240, 41, 99),
                new PVector(43, 90, 237),
                new PVector(200, 200, 200),
                new PVector(174, 8, 250),
                new PVector(19, 92, 1)};
    }

    public static void main(String[] args) {
        PApplet.main("BoidSimulation");
    }

    @Override
    public void settings() {
        fullScreen();
    }


    @Override
    public void setup() {
        background(backgroundColor.x, backgroundColor.y, backgroundColor.z);
        flocks = new ArrayList<>();
        for (int i = 0; i < NUM_OF_FLOCKS; i++) {
            PVector coefficientsVector = new PVector(lerp(0.8f, 1.5f, random(0, 1)), lerp(0.8f, 1.5f, random(0, 1)), lerp(1.1f, 1.6f, random(0, 1)));
            for (int j = 0; j < NUM_OF_VEHICLES; j++) {
                flocks.add(new Vehicle(this, random(0, width), random(0, height), colorArray[j % NUM_OF_FLOCKS], j % NUM_OF_FLOCKS, coefficientsVector));
            }
        }
        obstacles = new ArrayList<>();
    }

    @Override
    public void draw() {

        fill(backgroundColor.x, backgroundColor.y, backgroundColor.z, 30f);
        rect(0, 0, width, height);
        if (WITH_QUAD) {
            restartQuad();
        }
        for (Vehicle v : flocks) {
            v.flock(flocks);
//            v.avoidEdges(); // uncomment to enable edge avoidance by vehicles
            v.update();

            v.edges(); // comment to disable infinite screen wrap
            v.display();
        }


        if (mousePressed && mouseButton == LEFT) {
            obstacles.add(new Obstacle(this, mouseX, mouseY));
        }

        if (mousePressed && mouseButton == RIGHT) {
            removeObstacle(mouseX, mouseY);
        }

        for (Obstacle o : obstacles) {
            o.display();
        }

        if (frameCount < 45 * 90 && SAVE_FRAMES) {
            saveFrame("output/boids1_####.tif");
            println("curr frame rate:" + frameRate + ", curr frame: " + frameCount);
            println("seconds of video: " + (frameCount / 60));
        }
    }

    /**
     * Method to restart the quad tree and insert all the vehicles into it
     */
    private void restartQuad() {
        quadTree = new QuadTree<>(new Square<>(width * 0.5f, height * 0.5f, width * 0.5f + Vehicle.RENDER_BUFFER));
        for (Vehicle v : flocks) {
            quadTree.insert(new Point<>(v.getX(), v.getY(), v));
        }
    }

    /**
     * Method to remove an obstacle from the obstacles list
     *
     * @param posX - x position of the obstacle to remove
     * @param posY - y position of the obstacle to remove
     */
    private void removeObstacle(int posX, int posY) {
        for (int i = 0; i < obstacles.size(); i++) {
            Obstacle o = obstacles.get(i);
            if (dist(posX, posY, o.position.x, o.position.y) < o.getObstacleSize()) {
                obstacles.remove(o);
            }
        }
    }
}


