package test.main.game;

import java.awt.Color;
import java.io.File;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import main.java.VolatiliaOGL.entities.Camera;
import main.java.VolatiliaOGL.entities.Player;
import main.java.VolatiliaOGL.game.Terrain;
import main.java.VolatiliaOGL.game.World;
import main.java.VolatiliaOGL.gui.GuiComponent;
import main.java.VolatiliaOGL.gui.ShapeRectangle;
import main.java.VolatiliaOGL.gui.text.FontType;
import main.java.VolatiliaOGL.gui.text.GuiText;
import main.java.VolatiliaOGL.models.TexturedModel;
import main.java.VolatiliaOGL.screen.Screen;
import main.java.VolatiliaOGL.textures.ModelTexture;
import main.java.VolatiliaOGL.textures.TerrainTexture;
import main.java.VolatiliaOGL.textures.TerrainTexturePack;
import main.java.VolatiliaOGL.util.Loader;

public class Game
{
	private Player player;
	private Camera camera;

	private Screen screen;
	private FontType font = new FontType(Loader.INSTANCE.loadTexture("font/TestFont"), new File("res/font/TestFont.fnt"));
	private GuiText text = new GuiText("This is a test >:)", 3, font, new Vector2f(0.0f, 0.0f), 1f, true);

	private float color = 0, boarder = 0;
	private boolean up = true;

	private World world = new World(0);

	public Game(Screen screen)
	{
		this.screen = screen;
		this.screen.addText(text);
		ModelTexture texture = new ModelTexture(Loader.INSTANCE.loadTexture("textures/gui/health"));
		this.screen.addGuiComponent(new GuiComponent("test", texture.getID(), new Vector2f(0.5f, 0.5f), new Vector2f(.1f, .1f)));
		this.screen.addShape(new ShapeRectangle(new Vector4f(0.25f, 0.6f, 0.4f, 1f), new Vector2f(0.5f, 0.5f), new Vector2f(0.1f, 0.1f)));

		TexturedModel test = new TexturedModel(Loader.quad, texture);
		player = new Player(test, new Vector2f(.2f, .2f), 0, new Vector2f(.1f, .1f));
		camera = new Camera(player);
		
		TerrainTexturePack texturePack = new TerrainTexturePack("textures/terrain/grassy2", "textures/terrain/mud", "textures/terrain/flowers", "textures/terrain/path");
		TerrainTexture blendMap = new TerrainTexture(Loader.INSTANCE.loadTexture("textures/terrain/blendMap"));
		Terrain t = new Terrain(0, 0, texturePack, blendMap);
		
		world.addEntityToWorld(player);
		world.addTerrain(t);
	}

	public void loadGame()
	{

	}

	public void render()
	{
		player.move(world.getTerrainAt(player.getPosition().x, player.getPosition().y));
		color += 0.005;
		Color tmpClr = new Color(Color.HSBtoRGB(color, 0.5F, 1f));
		text.setColor(new Vector3f(tmpClr.getRed() / 255F, tmpClr.getGreen() / 255F, tmpClr.getBlue() / 255F));
		if(up)
		{
			boarder += 0.01;
			if(boarder > 0.75)
				up = false;
		}
		else
		{
			boarder -= 0.01;
			if(boarder < 0.5)
				up = true;
		}
		text.setBoarderWidth(boarder);
		world.renderWorld(camera);
	}

	public void cleanUp()
	{
	}
}
