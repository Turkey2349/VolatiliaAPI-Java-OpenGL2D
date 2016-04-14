package main.java.VolatiliaOGL.game;

import java.awt.image.BufferedImage;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import main.java.VolatiliaOGL.models.RawModel;
import main.java.VolatiliaOGL.textures.TerrainTexture;
import main.java.VolatiliaOGL.textures.TerrainTexturePack;
import main.java.VolatiliaOGL.util.MathUtil;

public class Terrain
{
	public static final float SIZE = 800;
	private static final float MAX_HEIGHT = 40;
	private static final float MAX_PIXEL_COLOR = 256 * 256 * 256;

	private float x;
	private float z;
	private RawModel model;
	private TerrainTexturePack texurePack;
	private TerrainTexture blendMap;

	private float[][] heights;

	public Terrain(float gridX, float gridZ, TerrainTexturePack texture, TerrainTexture blendMap, String heightMap)
	{
		this.texurePack = texture;
		this.blendMap = blendMap;
		this.x = gridX * SIZE;
		this.z = gridZ * SIZE;
		//this.model = this.generateTerrain(heightMap);
	}

	private float getHeight(int x, int y, BufferedImage image)
	{
		if(x < 0 || x >= image.getHeight() || y < 0 || y >= image.getHeight())
			return 0;

		float height = image.getRGB(x, y);

		height += MAX_PIXEL_COLOR / 2f;
		height /= MAX_PIXEL_COLOR / 2f;
		height *= MAX_HEIGHT;
		return height;
	}

	public float getHeightOfTerrain(float x, float z)
	{
		float terrainX = x - this.x;
		float terrainZ = z - this.z;
		float gridSquareSize = SIZE / ((float) heights.length - 1);
		int gridX = (int) Math.floor(terrainX / gridSquareSize);
		int gridZ = (int) Math.floor(terrainZ / gridSquareSize);

		if(gridX >= heights.length - 1 || gridZ >= heights.length - 1 || gridX < 0 || gridZ < 0)
			return 0;

		float xCoord = (terrainX % gridSquareSize) / gridSquareSize;
		float zCoord = (terrainZ % gridSquareSize) / gridSquareSize;
		float height;
		if(xCoord <= (1 - zCoord))
			height = MathUtil.barryCentric(new Vector3f(0, heights[gridX][gridZ], 0), new Vector3f(1, heights[gridX + 1][gridZ], 0), new Vector3f(0, heights[gridX][gridZ + 1], 1), new Vector2f(xCoord, zCoord));
		else
			height = MathUtil.barryCentric(new Vector3f(1, heights[gridX + 1][gridZ], 0), new Vector3f(1, heights[gridX + 1][gridZ + 1], 1), new Vector3f(0, heights[gridX][gridZ + 1], 1), new Vector2f(xCoord, zCoord));
		return height;
	}

	public float getX()
	{
		return x;
	}

	public float getZ()
	{
		return z;
	}

	public float getGridX()
	{
		return x / SIZE;
	}

	public float getGridZ()
	{
		return z / SIZE;
	}

	public RawModel getModel()
	{
		return model;
	}

	public TerrainTexturePack getTexturePack()
	{
		return texurePack;
	}

	public TerrainTexture getBlendMap()
	{
		return blendMap;
	}
}