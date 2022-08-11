package com.maddoxk.locus.objects;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;

public class Plane3D {
    
    private AssetManager assetManager;
    private Box planeX;
    private Box planeY;
    private Box planeZ;
    private Geometry planeXGeo;
    private Geometry planeYGeo;
    private Geometry planeZGeo;

    public Plane3D(AssetManager assetManager) {
        this.assetManager = assetManager;
        init();
    }

    public void init() {
        planeX = new Box(100000f, 0.05f, 0.05f);
        planeY = new Box(0.05f, 100000f, 0.05f);
        planeZ = new Box(0.05f, 0.05f, 100000f);
        Material matX = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        matX.setColor("Color", ColorRGBA.Red);
        Material matY = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        matY.setColor("Color", ColorRGBA.Green);
        Material matZ = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        matZ.setColor("Color", ColorRGBA.Blue);
        planeXGeo = new Geometry("planeX", planeX);
        planeYGeo = new Geometry("planeY", planeY);
        planeZGeo = new Geometry("planeZ", planeZ);
        planeXGeo.setMaterial(matX);
        planeYGeo.setMaterial(matY);
        planeZGeo.setMaterial(matZ);
        planeXGeo.setLocalTranslation(0, 0, 0);
        planeYGeo.setLocalTranslation(0, 0, 0);
        planeZGeo.setLocalTranslation(0, 0, 0);
    }

    public void attachTo(Node node) {
        node.attachChild(planeXGeo);
        node.attachChild(planeYGeo);
        node.attachChild(planeZGeo);
    }

    public void move(Vector3f position) {
        planeXGeo.setLocalTranslation(position);
        planeYGeo.setLocalTranslation(position);
        planeZGeo.setLocalTranslation(position);
    }

    public void show() {
        planeXGeo.setCullHint(Node.CullHint.Never);
        planeYGeo.setCullHint(Node.CullHint.Never);
        planeZGeo.setCullHint(Node.CullHint.Never);
    }

    public void hide() {
        planeXGeo.setCullHint(Node.CullHint.Always);
        planeYGeo.setCullHint(Node.CullHint.Always);
        planeZGeo.setCullHint(Node.CullHint.Always);
    }

    public boolean isVisible() {
        return planeXGeo.getCullHint() == Node.CullHint.Never;
    }

}
