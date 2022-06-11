package com.finalproject.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.finalproject.game.Base;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		// configure title, height, and width of the game window
		config.title = "Lucid Dreamer";
		config.width = 1280;
		config.height = 720;
		new LwjglApplication(new Base(), config);
	}
}
