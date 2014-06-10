package de.fau.cs.mad.fly.res;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Model;

/**
 * Loads and stores the AssetManager who cares about the Assets.
 * 
 * @author Lukas Hahmann 
 */
public class Assets {
	public static AssetManager manager;

	public static final AssetDescriptor<Model> space = new AssetDescriptor<Model>(
			"spacesphere.obj", Model.class);
	public static final AssetDescriptor<Model> torus = new AssetDescriptor<Model>(
			"torus.obj", Model.class);
	public static final AssetDescriptor<Model> arrow = new AssetDescriptor<Model>(
			"3dArrow/arrow.obj", Model.class);
	public static final AssetDescriptor<Texture> flyTextureLoadingScreen = new AssetDescriptor<Texture>(
			"Fly.png", Texture.class);

	public static void init() {
		manager = new AssetManager();
		manager.load(flyTextureLoadingScreen);
		manager.finishLoading();
	}


	public static void load() {
		manager.load(space);
		manager.load(torus);
		manager.finishLoading();
	}

	public static void dispose() {
		manager.dispose();
	}
	
	public static void loadArrow() {
		manager.load(arrow);
		manager.finishLoading();
	}
}