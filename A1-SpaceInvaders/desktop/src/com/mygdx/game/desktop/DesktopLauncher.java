package com.mygdx.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.mygdx.game.Base;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		// configure title, height, and width of the game window
		config.title = "Gemini's Space Invaders";
		config.width = 900;
		config.height = 600;
		new LwjglApplication(new Base(), config);
	}
}
