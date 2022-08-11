package com.maddoxk.locus.objects;

import java.util.LinkedList;

import javax.swing.text.GlyphView;

import org.checkerframework.checker.units.qual.radians;
import org.checkerframework.common.returnsreceiver.qual.This;

import com.google.common.util.concurrent.Service.State;
import com.jme3.material.Material;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Sphere;
import com.maddoxk.locus.states.UniverseState;
import com.simsilica.lemur.Container;
import com.simsilica.lemur.Label;

public class CelestialBody {
    
    private float mass;
    private Vector3f initialVelocity;
    private Vector3f currentVelocity;
    private Vector3f position;
    private Geometry geometry;
    private Sphere sphere; // Posibly change to Mesh to allow for more complex shapes*
    private Material material;

    // public CelestialBody(Geometry geometry, Material material, Vector3f initialVelocity, Vector3f position, Sphere sphere) {
    //     this.sphere = sphere;
    //     this.geometry = geometry;
    //     this.material = material;
    //     this.mass = FastMath.pow(sphere.radius, 3) + 1f;
    //     this.initialVelocity = initialVelocity;
    //     this.currentVelocity = initialVelocity;
    //     this.position = position;
    //     geometry.setMaterial(material);
    //     this.geometry.setLocalTranslation(position);
    // }

    public CelestialBody(Geometry geometry, Material material, Vector3f initialVelocity, Vector3f position, Sphere sphere, float mass) {
        this.sphere = sphere;
        this.geometry = geometry;
        this.material = material;
        this.mass = mass;
        this.initialVelocity = initialVelocity;
        this.currentVelocity = initialVelocity;
        this.position = position;
        geometry.setMaterial(material);
        this.geometry.setLocalTranslation(position);
    }

    public void UpdateVelocity(LinkedList<CelestialBody> allBodies, float timeStep) {
        for (CelestialBody other : allBodies) {
            if (other != this) {

                // Vector3f direction = other.getPosition().subtract(this.getPosition());
                // float distance = direction.length();
                // float force = UniverseState.G * this.getMass() * other.getMass() / (distance * distance);
                // direction.normalizeLocal();
                // direction.multLocal(force);
                // this.getCurrentVelocity().addLocal(direction.mult(timeStep));

                Vector3f direction = other.getGeometry().getLocalTranslation().subtract(this.getGeometry().getLocalTranslation());
                Vector3f force = direction.mult(UniverseState.G * other.getMass() / direction.lengthSquared());
                Vector3f acceleration = force.divide(this.getMass());
                this.currentVelocity = this.currentVelocity.add(acceleration.mult(timeStep));
                
                // float sqrDst = (other.getGeometry().getWorldTranslation().subtract(this.getGeometry().getWorldTranslation())).lengthSquared();
                // Vector3f forceDirection = other.getGeometry().getWorldTranslation().subtract(this.getGeometry().getWorldTranslation()).normalize();
                // Vector3f force = forceDirection.mult(UniverseState.G * other.getMass() / sqrDst);
                // Vector3f acceleration = force.divide(this.mass);
                // this.currentVelocity.addLocal(acceleration.mult(timeStep));
            }
        }
    }

    public void UpdatePosition(float timeStep) {
        this.position = this.position.add(this.currentVelocity.mult(timeStep));
        this.geometry.setLocalTranslation(this.position);
    }

    public float getMass() {
        return mass;
    }

    public void setMass(float mass) {
        this.mass = mass;
    }


    public Vector3f getInitialVelocity() {
        return initialVelocity;
    }

    public void setInitialVelocity(Vector3f initialVelocity) {
        this.initialVelocity = initialVelocity;
    }

    public Vector3f getCurrentVelocity() {
        return currentVelocity;
    }

    public void setCurrentVelocity(Vector3f currentVelocity) {
        this.currentVelocity = currentVelocity;
    }

    public Geometry getGeometry() {
        return geometry;
    }

    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }

    public Sphere getSphere() {
        return sphere;
    }

    public void setSphere(Sphere sphere) {
        this.sphere = sphere;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
        this.geometry.setMaterial(material);
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public void move(Vector3f vector) {
        this.position = this.position.add(vector);
        this.geometry.setLocalTranslation(this.position);
    }

    public String getName() {
        return this.geometry.getName();
    }

    public Vector3f getVelocity() {
        return this.currentVelocity;
    }

    public float getRadius() {
        return this.sphere.getRadius();
    }

    @Override
    public String toString() {
        return "CelestialBody [mass=" + mass + ", initialVelocity=" + initialVelocity + ", currentVelocity=" + currentVelocity + ", geometry=" + geometry + ", sphere=" + sphere + ", material=" + material + "]";
    }

}
