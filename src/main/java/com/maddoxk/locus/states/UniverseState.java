package com.maddoxk.locus.states;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.input.ChaseCamera;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.BloomFilter;
import com.jme3.scene.Node;
import com.maddoxk.locus.manager.UniverseManager;
import com.maddoxk.locus.objects.CelestialBody;
import com.simsilica.lemur.Button;
import com.simsilica.lemur.Command;
import com.simsilica.lemur.Container;
import com.simsilica.lemur.GuiGlobals;
import com.simsilica.lemur.Label;
import com.simsilica.lemur.TextField;
import com.simsilica.lemur.style.BaseStyles;

public class UniverseState extends AbstractAppState {

    private final Node rootNode;
    public static Node followerNode;
    private final Node universe;
    private final AssetManager assetManager;
    private UniverseManager universeManager;
    public static float G = 600.667408f; // Gravitational constant
    public static float TimeStep = 0.001f; // Time step for physics
    public static float ZoomLevel = 100f;
    private Container myWindow;
    private Container universeStatsWindow;
    private Container targetBodyStatsWindow;
    private SimpleApplication simpleApp;
    private Node guiNode;
    private FilterPostProcessor fpp;
    private ChaseCamera chaseCam;
    private int targetBody;
    private String camMethod;

    public UniverseState(SimpleApplication app) {
        this.rootNode = app.getRootNode();
        this.assetManager = app.getAssetManager();
        this.universe = new Node("Universe");
        this.simpleApp = app;
        this.guiNode = app.getGuiNode();
        universeManager = new UniverseManager(universe, assetManager);
        camMethod = "midpoint";
        targetBody = 0;

        GuiGlobals.initialize(app);

        // Load the 'glass' style
        BaseStyles.loadGlassStyle();

        // Set 'glass' as the default style when not specified
        GuiGlobals.getInstance().getStyles().setDefaultStyle("glass");

    }

    public void simpleUpdate(float tpf) {
        universeStatsWindow.removeChild((Node) universeStatsWindow.getChildren().get(4));
        universeStatsWindow.removeChild((Node) universeStatsWindow.getChildren().get(3));
        universeStatsWindow.removeChild((Node) universeStatsWindow.getChildren().get(2));
        universeStatsWindow.removeChild((Node) universeStatsWindow.getChildren().get(1));
        universeStatsWindow.removeChild((Node) universeStatsWindow.getChildren().get(0));
        universeStatsWindow.addChild(new Label("Stats"));
        universeStatsWindow.addChild(new Label("Gravity: " + UniverseState.G));
        universeStatsWindow.addChild(new Label("TimeStep: " + UniverseState.TimeStep));
        universeStatsWindow.addChild(new Label("Zoom: " + UniverseState.ZoomLevel));
        universeStatsWindow.addChild(new Label("MidPoint: " + this.getUniverseManager().getMidPoint()));

        targetBodyStatsWindow.removeChild((Node) targetBodyStatsWindow.getChildren().get(4));
        targetBodyStatsWindow.removeChild((Node) targetBodyStatsWindow.getChildren().get(3));
        targetBodyStatsWindow.removeChild((Node) targetBodyStatsWindow.getChildren().get(2));
        targetBodyStatsWindow.removeChild((Node) targetBodyStatsWindow.getChildren().get(1));
        targetBodyStatsWindow.removeChild((Node) targetBodyStatsWindow.getChildren().get(0));
        targetBodyStatsWindow.addChild(new Label("Stats"));
        targetBodyStatsWindow.addChild(new Label("Mass: " + this.getUniverseManager().getBodies().get(targetBody).getMass()));
        targetBodyStatsWindow.addChild(new Label("Radius: " + this.getUniverseManager().getBodies().get(targetBody).getRadius()));
        targetBodyStatsWindow.addChild(new Label("Position: " + this.getUniverseManager().getBodies().get(targetBody).getPosition()));
        targetBodyStatsWindow.addChild(new Label("Velocity: " + this.getUniverseManager().getBodies().get(targetBody).getVelocity()));
    }

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        rootNode.attachChild(universe);

        fpp = new FilterPostProcessor(assetManager);
        BloomFilter bloom = new BloomFilter(BloomFilter.GlowMode.Objects);
        fpp.addFilter(bloom);
        app.getViewPort().addProcessor(fpp);

        universeManager.createBody(universeManager, "Sun", 30000f, ColorRGBA.Yellow, new Vector3f(0, 0, 0), new Vector3f(0,0,0));

        updateFollower(camMethod);

        chaseCam = new ChaseCamera(app.getCamera(), followerNode, app.getInputManager());
        chaseCam.setDefaultDistance(ZoomLevel);
        chaseCam.setInvertVerticalAxis(true);
        chaseCam.setRotationSensitivity(5f);

        myWindow = new Container();

        // Create a simple container for our elements
        guiNode.attachChild(myWindow);

        myWindow.setLocalTranslation(1, 1400, 0);
        myWindow.setLocalScale(2f);

        myWindow.addChild(new Label("Locus"));
        myWindow.addChild(new Label(""));
        myWindow.addChild(new Label("TimeStep"));
        Button increaseTimeStepButton = myWindow.addChild(new Button("Increase TimeStep"));
        increaseTimeStepButton.addClickCommands(new Command<Button>() {
            @Override
            public void execute(Button source) {
                UniverseState.TimeStep *= 2f;
                myWindow.getChild(3).setName("TimeStep | " + UniverseState.TimeStep);
            }
        });
        Button decreaseTimeStepButton = myWindow.addChild(new Button("Decrease TimeStep"));
        decreaseTimeStepButton.addClickCommands(new Command<Button>() {
            @Override
            public void execute(Button source) {
                UniverseState.TimeStep *= 0.5f;
                myWindow.getChild(3).setName("TimeStep | " + UniverseState.TimeStep);
            }
        });
        myWindow.addChild(new Label(""));
        myWindow.addChild(new Label("Camera"));
        Button zoomInButton = myWindow.addChild(new Button("Zoom In"));
        zoomInButton.addClickCommands(new Command<Button>() {
            @Override
            public void execute(Button source) {
                chaseCam.setDefaultDistance(UniverseState.ZoomLevel *= 0.9f);
            }
        });
        Button zoomOutButton = myWindow.addChild(new Button("Zoom Out"));
        zoomOutButton.addClickCommands(new Command<Button>() {
            @Override
            public void execute(Button source) {
                chaseCam.setDefaultDistance(UniverseState.ZoomLevel *= 1.1f);
            }
        });
        Button resetZoomButton = myWindow.addChild(new Button("Reset Zoom"));
        resetZoomButton.addClickCommands(new Command<Button>() {
            @Override
            public void execute(Button source) {
                chaseCam.setDefaultDistance(UniverseState.ZoomLevel = 100f);
            }
        });
        Container bodyList = new Container();
        bodyList.setLocalTranslation(myWindow.getLocalTranslation().add(new Vector3f(300, -400, 0)));
        bodyList.setLocalScale(2f);
        guiNode.attachChild(bodyList);
        guiNode.detachChild(bodyList);
        
        Button TargetBody = myWindow.addChild(new Button("Target Body"));
        TargetBody.addClickCommands(new Command<Button>() {
            @Override
            public void execute(Button source) {
                guiNode.attachChild(bodyList);
                for (int i = 0; i < universeManager.getBodies().size(); i++) {
                    bodyList.addChild(new Button(universeManager.getBodies().get(i).getName()));
                    Button bodyButton = (Button) bodyList.getChild(i);
                    bodyButton.addClickCommands(new Command<Button>() {
                        @Override
                        public void execute(Button source) {
                            camMethod = "TargetBody";
                            if (universeManager.getIndexOfBody(universeManager.getBody(source.getText())) == 0) {
                                targetBody = 0;
                            } else {
                                targetBody = universeManager.getBodies().indexOf(universeManager.getBody(source.getText()));
                            }
                            targetBody = universeManager.getBodies().indexOf(universeManager.getBody(source.getText()));
                            bodyList.detachAllChildren();
                            guiNode.detachChild(bodyList);
                        }
                    });
                }
            }
        });
        Button TargetMidpointButton = myWindow.addChild(new Button("Target Midpoint"));
        TargetMidpointButton.addClickCommands(new Command<Button>() {
            @Override
            public void execute(Button source) {
                camMethod = "Midpoint";
                updateFollower(camMethod);
            }
        });

        myWindow.addChild(new Label(""));
        myWindow.addChild(new Label("Universe"));
        Button addBodyButton = myWindow.addChild(new Button("Add Celestial Body"));
        addBodyButton.addClickCommands(new Command<Button>() {
            @Override
            public void execute(Button source) {
                Container bodyCreator = new Container();
                bodyCreator.setLocalTranslation(myWindow.getLocalTranslation().add(new Vector3f(500, 0, 1)));
                bodyCreator.setLocalScale(2f);
                guiNode.attachChild(bodyCreator);
                bodyCreator.addChild(new Label("Name"));
                TextField nameField = bodyCreator.addChild(new TextField("Name"));
                bodyCreator.addChild(new Label("Mass"));
                TextField massField = bodyCreator.addChild(new TextField("Mass"));
                bodyCreator.addChild(new Label("Color"));
                TextField colorField = bodyCreator.addChild(new TextField("Color"));
                bodyCreator.addChild(new Label("Veloctiy"));
                TextField velocityFieldX = bodyCreator.addChild(new TextField("X"));
                TextField velocityFieldY = bodyCreator.addChild(new TextField("Y"));
                TextField velocityFieldZ = bodyCreator.addChild(new TextField("Z"));
                bodyCreator.addChild(new Label("Position"));
                TextField positionFieldX = bodyCreator.addChild(new TextField("X"));
                TextField positionFieldY = bodyCreator.addChild(new TextField("Y"));
                TextField positionFieldZ = bodyCreator.addChild(new TextField("Z"));

                Button createBodyButton = bodyCreator.addChild(new Button("Create"));
                createBodyButton.addClickCommands(new Command<Button>() {
                    @Override
                    public void execute(Button source) {
                        ColorRGBA color;
                        switch (colorField.getText().toLowerCase()) {
                            case "red":
                                color = ColorRGBA.Red;
                                break;
                            case "green":
                                color = ColorRGBA.Green;
                                break;
                            case "blue":
                                color = ColorRGBA.Blue;
                                break;
                            case "yellow":
                                color = ColorRGBA.Yellow;
                                break;
                            case "cyan":
                                color = ColorRGBA.Cyan;
                                break;
                            case "magenta":
                                color = ColorRGBA.Magenta;
                                break;
                            case "white":
                                color = ColorRGBA.White;
                                break;
                            case "black":
                                color = ColorRGBA.Black;
                                break;
                            default:
                                color = ColorRGBA.White;
                                break;
                        }
                        try {
                            getUniverseManager().createBody(
                                    getUniverseManager(),
                                    nameField.getText(),
                                    Float.parseFloat(massField.getText()),
                                    color,
                                    new Vector3f(
                                            Float.parseFloat(velocityFieldX.getText()),
                                            Float.parseFloat(velocityFieldY.getText()),
                                            Float.parseFloat(velocityFieldZ.getText())),
                                    new Vector3f(
                                            Float.parseFloat(positionFieldX.getText()),
                                            Float.parseFloat(positionFieldY.getText()),
                                            Float.parseFloat(positionFieldZ.getText())));
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid number format");
                        } catch (Exception e) {
                            System.out.println("Error creating body");
                        }
                        getUniverseManager().resetBodiesScale();
                        // Leave!
                        try {
                            bodyCreator.removeFromParent();
                        } catch (Exception e) {
                            System.out.println("Error removing body creator *Possibly null?*");
                        }
                    }
                });
            }
        });
        Button removeBodyButton = myWindow.addChild(new Button("Remove Celestial Body"));
        removeBodyButton.addClickCommands(new Command<Button>() {
            @Override
            public void execute(Button source) {
                Container bodyRemover = new Container();
                bodyRemover.setLocalTranslation(myWindow.getLocalTranslation().add(new Vector3f(500, 0, 1)));
                bodyRemover.setLocalScale(2f);
                guiNode.attachChild(bodyRemover);
                bodyRemover.addChild(new Label("Name"));
                TextField nameField = bodyRemover.addChild(new TextField("Name"));
                bodyRemover.addChild(new Label(""));
                Button removeBodyButton = bodyRemover.addChild(new Button("Remove"));
                removeBodyButton.addClickCommands(new Command<Button>() {
                    @Override
                    public void execute(Button source) {
                        if (universeManager.getBodies().size() > 1) {
                            try {
                                getUniverseManager().removeBody(nameField.getText());
                            } catch (Exception e) {
                                System.out.println("Error removing body");
                            }
                            getUniverseManager().resetBodiesScale();
                        }
                        // Leave!
                        try {
                            bodyRemover.removeFromParent();
                        } catch (Exception e) {
                            System.out.println("Error removing body remover *Possibly null?*");
                        }
                    }
                });
            }
        });
        Button showPlane = myWindow.addChild(new Button("Toggle Plane"));
        showPlane.addClickCommands(new Command<Button>() {
            @Override
            public void execute(Button source) {
                if (getUniverseManager().getPlane().isVisible()) {
                        getUniverseManager().getPlane().hide();
                } else {
                    getUniverseManager().getPlane().show();
                }
            }
        });
        Button increaseScaleButton = myWindow.addChild(new Button("Increase Scale"));
        increaseScaleButton.addClickCommands(new Command<Button>() {
            @Override
            public void execute(Button source) {
                getUniverseManager().increaseBodiesScale(1.1f);
            }
        });
        Button decreaseScaleButton = myWindow.addChild(new Button("Decrease Scale"));
        decreaseScaleButton.addClickCommands(new Command<Button>() {
            @Override
            public void execute(Button source) {
                getUniverseManager().increaseBodiesScale(0.9f);
            }
        });
        Button resetScaleButton = myWindow.addChild(new Button("Reset Scale"));
        resetScaleButton.addClickCommands(new Command<Button>() {
            @Override
            public void execute(Button source) {
                getUniverseManager().resetBodiesScale();
            }
        });
        Button addRandomBodyButton = myWindow.addChild(new Button("Add Random Body"));
        addRandomBodyButton.addClickCommands(new Command<Button>() {
            @Override
            public void execute(Button source) {
                getUniverseManager().addRandomBody(getUniverseManager(), followerNode);
            }
        });
        Button increaseGravityButton = myWindow.addChild(new Button("Increase Gravity"));
        increaseGravityButton.addClickCommands(new Command<Button>() {
            @Override
            public void execute(Button source) {
                UniverseState.G = UniverseState.G * 1.1f;
            }
        });
        Button decreaseGravityButton = myWindow.addChild(new Button("Decrease Gravity"));
        decreaseGravityButton.addClickCommands(new Command<Button>() {
            @Override
            public void execute(Button source) {
                UniverseState.G = UniverseState.G * 0.9f;
            }
        });
        Button restartGameButton = myWindow.addChild(new Button("Restart Game"));
        restartGameButton.addClickCommands(new Command<Button>() {
            @Override
            public void execute(Button source) {
                simpleApp.restart();
            }
        });
        Button exitButton = myWindow.addChild(new Button("Exit"));
        exitButton.addClickCommands(new Command<Button>() {
            @Override
            public void execute(Button source) {
                simpleApp.stop();
            }
        });

        // Create a new window to display all the CelestialBodies and their stats (pos,
        // mass, velo)
        universeStatsWindow = new Container("Stats");
        universeStatsWindow.setLocalTranslation(myWindow.getLocalTranslation().add(new Vector3f(0, 200, 1)));
        universeStatsWindow.setLocalScale(2f);
        guiNode.attachChild(universeStatsWindow);
        universeStatsWindow.addChild(new Label("Stats"));
        universeStatsWindow.addChild(new Label("Gravity: " + UniverseState.G));
        universeStatsWindow.addChild(new Label("TimeStep: " + UniverseState.TimeStep));
        universeStatsWindow.addChild(new Label("Zoom: " + UniverseState.ZoomLevel));
        universeStatsWindow.addChild(new Label("MidPoint: " + this.getUniverseManager().getMidPoint()));

        targetBodyStatsWindow = new Container("Target Body Stats");
        targetBodyStatsWindow.setLocalTranslation(myWindow.getLocalTranslation().add(new Vector3f(800, 200, 1)));
        targetBodyStatsWindow.setLocalScale(2f);
        guiNode.attachChild(targetBodyStatsWindow);
        targetBodyStatsWindow.addChild(new Label("Target Body Stats"));
        targetBodyStatsWindow.addChild(new Label("Name: "));
        targetBodyStatsWindow.addChild(new Label("Position: "));
        targetBodyStatsWindow.addChild(new Label("Velocity: "));
        targetBodyStatsWindow.addChild(new Label("Mass: "));
    }

    @Override
    public void cleanup() {
        rootNode.detachChild(universe);
        guiNode.detachAllChildren();
        super.cleanup();
    }

    @Override
    public void update(float tpf) {
        super.update(tpf);
        universeManager.FixedUpdate();
        universeManager.updateStars(followerNode);
        if (universeManager.getBodies().size() > 0) {
            updateFollower(camMethod);
        }
        universeManager.getPlane().move(followerNode.getLocalTranslation());
        // for (CelestialBody body : universeManager.getBodies()) {
        //     System.out.println(body.getGeometry().getName() + ": " + body.getGeometry().getLocalTranslation());
        // } Debug
        simpleUpdate(tpf);
    }

    public void updateFollower(String method) {

        switch (method.toLowerCase()) {
            case "midpoint":
                if (followerNode == null) {
                followerNode = new Node("Follower");
                followerNode.setLocalTranslation(universeManager.getMidPoint());
                rootNode.attachChild(followerNode);
                } else {
                followerNode.setLocalTranslation(universeManager.getMidPoint());
                }
                break;
            case "targetbody":
                if (followerNode == null) {
                    followerNode = new Node("Follower");
                    rootNode.attachChild(followerNode);
                }
                if (targetBody == 0) {
                    followerNode.setLocalTranslation(universeManager.getMidPoint());
                }
                if (universeManager.getBodies().size() > 0) {
                    followerNode.setLocalTranslation(
                            universeManager.getBodies().get(targetBody).getGeometry().getLocalTranslation());
                } else {
                    followerNode.setLocalTranslation(universeManager.getMidPoint());
                }
        }
        System.out.println("Follower: " + followerNode.getLocalTranslation());

    }

    public void updateFollower(Vector3f position) {
        //Follower Node is a node that the camera will follow. is used to follow the midpoint of all celestial bodies
        if (followerNode == null) {
            followerNode = new Node("Follower");
            followerNode.setLocalTranslation(position);
            rootNode.attachChild(followerNode);
        } else {
            followerNode.setLocalTranslation(position);
        }
        System.out.println("Follower: " + followerNode.getLocalTranslation());
    }

    public void setTimeStep(float timeStep) {
        TimeStep = timeStep;
    }

    public UniverseManager getUniverseManager() {
        return universeManager;
    }

    public void reset() {
        TimeStep = 0.001f;
        G = 60.667408f;
        ZoomLevel = 100f;
        universeManager.reset();
        followerNode.setLocalTranslation(new Vector3f(0,0,0));
        simpleApp.getViewPort().removeProcessor(fpp);
        //reset chaseCam
        simpleApp.getCamera().setLocation(new Vector3f(0, 0, 0));
        simpleApp.getCamera().lookAt(new Vector3f(0, 0, -1), Vector3f.UNIT_Y);
        camMethod = "midpoint";
    }

}
