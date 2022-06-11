package com.mygdx.guardgame.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.mygdx.guardgame.GuardGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		// configure title, height, and width of the game window
		config.title = "Gemini's Guard Game";
		config.width = 1280;
		config.height = 720;
		new LwjglApplication(new GuardGame(), config);
	}
}
