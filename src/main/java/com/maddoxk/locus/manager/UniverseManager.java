package com.maddoxk.locus.manager;

import java.util.LinkedList;
import com.jme3.asset.AssetManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Sphere;
import com.maddoxk.locus.objects.CelestialBody;
import com.maddoxk.locus.objects.Plane3D;
import com.maddoxk.locus.states.UniverseState;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;

public class UniverseManager {
    
    private LinkedList<CelestialBody> bodies;
    private LinkedList<Geometry> stars;
    private Plane3D plane;
    private Node universe;
    private AssetManager assetManager;
    
    public UniverseManager(Node universe, AssetManager assetManager) {
        bodies = new LinkedList<CelestialBody>();
        stars = new LinkedList<Geometry>();
        this.universe = universe;
        this.assetManager = assetManager;
        plane = new Plane3D(assetManager);
        plane.attachTo(universe);
        createStars();
    }

    public void createBody(UniverseManager universeManager, String name, Float mass, ColorRGBA color, Vector3f velocity, Vector3f position) {
        // Sphere sun = new Sphere(64, 64, radius);
        // Geometry sunGeo = new Geometry("Sun", sun);
        // Material sunMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        // sunMat.setColor("Color", color);
        // sunMat.setColor("GlowColor", color);
        // sunGeo.setMaterial(sunMat);
        // CelestialBody sunBody = new CelestialBody(sunGeo, sunMat, velocity, position, sun);
        // universeManager.addBody(sunBody);
        float radius = FastMath.pow(mass, 1f/10f);
        Sphere body = new Sphere(64, 64, radius);
        Geometry bodyGeo = new Geometry(name, body);
        Material bodyMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        bodyMat.setColor("Color", color);
        bodyMat.setColor("GlowColor", color);
        bodyGeo.setMaterial(bodyMat);
        CelestialBody bodyBody = new CelestialBody(bodyGeo, bodyMat, velocity, position, body, mass);
        universeManager.addBody(bodyBody);
    }

    public void addBody(CelestialBody body) {
        bodies.add(body);

        System.out.println("Added body: " + body.getGeometry().getName());

        if (universe != null) {
            universe.attachChild(body.getGeometry());
        }
    }

    public void removeBody(CelestialBody body) {
        bodies.remove(body);

        System.out.println("Removed body: " + body.getGeometry().getName());

        if (universe != null) {
            universe.detachChild(body.getGeometry());
        }
    }

    public void removeBody(String name) {
        for (CelestialBody body : bodies) {
            if (body.getGeometry().getName().equals(name)) {
                removeBody(body);
                return;
            }
        }
    }

    public void removeBody(int index) {
        if (index < 0 || index >= bodies.size()) {
            return;
        }

        CelestialBody body = bodies.get(index);
        removeBody(body);
    }

    public void removeMostRecentBody() {
        if (bodies.size() == 0) {
            return;
        }

        CelestialBody body = bodies.get(bodies.size() - 1);
        removeBody(body);
    }

    public void FixedUpdate() {
        for (CelestialBody body : bodies) {
            body.UpdateVelocity(bodies, UniverseState.TimeStep);
        }

        for (CelestialBody body : bodies) {
            body.UpdatePosition(UniverseState.TimeStep);
        }

        for (CelestialBody body : bodies) {
            System.out.println("Body: " + body.getGeometry().getName() + " Position: " + body.getGeometry().getLocalTranslation() + " Velocity: " + body.getVelocity());
        }

    }

    public CelestialBody getBody(String name) {
        for (CelestialBody body : bodies) {
            if (body.getGeometry().getName().equals(name)) {
                return body;
            }
        }
        return null;
    }

    public Vector3f getMidPoint() {
        Vector3f midPoint = new Vector3f();
        for (CelestialBody body : bodies) {
            // if (body.getGeometry().getName().equals("Select body to focus")) {
            //     continue;
            // }
            midPoint.addLocal(body.getGeometry().getWorldTranslation());
        }
        midPoint.divideLocal(bodies.size());
        return midPoint;
    }
 
    public CelestialBody getBody(int index) {
        return bodies.get(index);
    }

    public LinkedList<CelestialBody> getBodies() {
        return bodies;
    }

    public void increaseBodiesScale(float scale) {
        for (CelestialBody body : bodies) {
            body.getGeometry().setLocalScale(body.getGeometry().getLocalScale().mult(scale));
        }
    }

    public void resetBodiesScale() {
        for (CelestialBody body : bodies) {
            body.getGeometry().setLocalScale(1);
        }
    }

    public void addRandomBody(UniverseManager universeManager, Node followerNode) {
        /* Method 1
        float radius = (float) (Math.random() * 50 + 1);
        Vector3f velocity = new Vector3f((float) (Math.random() * 10 - 5), (float) (Math.random() * 10 - 5), (float) (Math.random() * 10 - 5));
        Vector3f position = getMidPoint().add(new Vector3f((float) (Math.random() * 500 - 5), (float) (Math.random() * 500 - 5), (float) (Math.random() * 500 - 5)));
        ColorRGBA color = new ColorRGBA((float) (Math.random() * 1), (float) (Math.random() * 1), (float) (Math.random() * 1), 1);
        String name = "Body" + bodies.size();
        createBody(universeManager, name, radius, color, velocity, position);
        */
        /* Method 2 */
        float mass = (float) (Math.random() * 1000 + 1);
        Vector3f velocity = new Vector3f((float) (Math.random() * 20 - 5), (float) (Math.random() * 100 - 5), (float) (Math.random() * 100 - 5));
        Vector3f position = getMidPoint().add(new Vector3f((float) (Math.random() * 500 - 5), (float) (Math.random() * 500 - 5), (float) (Math.random() * 500 - 5)));
        ColorRGBA color = new ColorRGBA((float) (Math.random() * 1), (float) (Math.random() * 1), (float) (Math.random() * 1), 1);
        String name = "Body" + bodies.size();
        createBody(universeManager, name, mass, color, velocity, position);
        resetBodiesScale();
    }

    public void addStar(Geometry star) {
        stars.add(star);
    }

    public void updateStars(Node followerNode) {

        Vector3f starMidpoint = new Vector3f();
        for (Geometry star : stars) {
            starMidpoint.addLocal(star.getWorldTranslation());
        }
        starMidpoint.divideLocal(stars.size());

        for (Geometry star : stars) {
            Vector3f starPos = star.getWorldTranslation();
            Vector3f newPos = starPos.subtract(starMidpoint).add(followerNode.getLocalTranslation());
            star.setLocalTranslation(newPos);
        }

        // Vector3f starMidpoint = new Vector3f();
        // for (Geometry star : stars) {
        //     starMidpoint.addLocal(star.getWorldTranslation());
        // }
        // starMidpoint.divideLocal(stars.size());

        // Vector3f midPoint = getMidPoint();

        // for (Geometry star : stars) {
        //     Vector3f starPos = star.getWorldTranslation();
        //     Vector3f newPos = starPos.subtract(starMidpoint).add(midPoint);
        //     star.setLocalTranslation(newPos);
        // }

    }

    private void createStars() {
        for (int i = 0; i < 100; i++) {
            Sphere star = new Sphere(4, 4, 0.1f);
            Geometry starGeo = new Geometry("Star", star);
            Material starMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            starMat.setColor("Color", ColorRGBA.White);
            starMat.setColor("GlowColor", ColorRGBA.White);
            starGeo.setMaterial(starMat);
            Vector3f pos = new Vector3f(FastMath.nextRandomFloat() * 500 - 5, FastMath.nextRandomFloat() * 500 - 5, FastMath.nextRandomFloat() * 500 - 5);
            starGeo.setLocalTranslation(pos);
            this.stars.add(starGeo);
            universe.attachChild(starGeo);
        }
    }

    public int getIndexOfBody(CelestialBody body) {
        return bodies.indexOf(body);
    }

    public Plane3D getPlane() {
        return plane;
    }

    public int getBodyCount() {
        return bodies.size();
    }

    public void reset() {
        bodies.clear();
        universe = null;
        stars.clear();
        plane = null;
    }

}
