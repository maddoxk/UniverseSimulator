package com.maddoxk.locus;

import com.jme3.app.SimpleApplication;
import com.jme3.input.FlyByCamera;
import com.jme3.system.AppSettings;
import com.maddoxk.locus.states.MainMenu;
import com.maddoxk.locus.states.UniverseState;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;

public class App extends SimpleApplication {

    private static App app;
    private static UniverseState universeState;

    public static void main(String[] args) {
        run();
    }

    public static void run() {

        app = new App();

        GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        AppSettings setting = new AppSettings(true);
        setting.setFullscreen(true);
        setting.setFrameRate(60);
        setting.setResolution(2560, 1600);
        setting.setFrequency(device.getDisplayMode().getRefreshRate());
        app.setShowSettings(false);
        app.setSettings(setting);
        app.start();

    }

    @Override
    public void simpleInitApp() {
        setDisplayFps(true);
        setDisplayStatView(false);

        universeState = new UniverseState(this);
        stateManager.attach(universeState);
    }

    @Override
    public void restart() {
        stateManager.getClass().getClassLoader().getResourceAsStream("com/maddoxk/locus/states/UniverseState.class");
        System.out.println("Restarting " + stateManager.getClass().getClassLoader().getResourceAsStream("com/maddoxk/locus/states/UniverseState.class"));
        universeState.reset();
        stateManager.detach(universeState);
        universeState = new UniverseState(this);
        //universeState.updateFollower();
        stateManager.attach(universeState);
    }

}