package com.maddoxk.locus.states;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.simsilica.lemur.Button;
import com.simsilica.lemur.Command;
import com.simsilica.lemur.Container;
import com.simsilica.lemur.GuiGlobals;
import com.simsilica.lemur.Label;
import com.simsilica.lemur.style.BaseStyles;

public class MainMenu extends AbstractAppState {

    private final Node rootNode;
    private final SimpleApplication simpleApp;
    private final Node guiNode;

    public MainMenu(SimpleApplication app) {
        this.simpleApp = app;
        this.rootNode = app.getRootNode();
        this.guiNode = app.getGuiNode();
    }
    
    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);

        GuiGlobals.initialize(app);

        // Load the 'glass' style
        BaseStyles.loadGlassStyle();

        // Set 'glass' as the default style when not specified
        GuiGlobals.getInstance().getStyles().setDefaultStyle("glass");

        Container myWindow = new Container("myWindow");

        // Create a simple container for our elements
        guiNode.attachChild(myWindow);

        //Set window to center of screen
        myWindow.setLocalTranslation(new Vector3f(app.getCamera().getWidth() / 2 - 300, app.getCamera().getHeight() / 2 + 300, 0));

        // Calculate a standard scale and position from the app's camera
        // height`
        int height = app.getCamera().getHeight();
        Vector3f pref = myWindow.getPreferredSize().clone();

        float standardScale = getStandardScale();
        pref.multLocal(standardScale);

        // With a slight bias toward the top
        float y = height * 0.9f;

        myWindow.setLocalTranslation(100 * standardScale, y, 0);
        myWindow.setLocalScale(standardScale);

        myWindow.setLocalScale(new Vector3f(10f, 10f, 10f));

        myWindow.addChild(new Label("Locus"));
        myWindow.addChild(new Label("By MaddoxK"));
        Button playButton = myWindow.addChild(new Button("Play"));
        playButton.addClickCommands(new Command<Button>() {
            @Override
            public void execute(Button source) {
                guiNode.detachAllChildren();
                app.getStateManager().detach(MainMenu.this);
                app.restart();
                app.getStateManager().attach(new UniverseState(getSimpleApp()));
            }
        });
    }

    public float getStandardScale() {
        int height = getSimpleApp().getCamera().getHeight();
        return height / 720f;
    }

    public SimpleApplication getSimpleApp() {
        return simpleApp;
    }

}
