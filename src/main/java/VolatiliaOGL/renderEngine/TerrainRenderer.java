package main.java.VolatiliaOGL.renderEngine;

import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;

import main.java.VolatiliaOGL.game.Terrain;
import main.java.VolatiliaOGL.game.World;
import main.java.VolatiliaOGL.models.RawModel;
import main.java.VolatiliaOGL.shaders.terrain.TerrainShader;
import main.java.VolatiliaOGL.textures.TerrainTexturePack;
import main.java.VolatiliaOGL.util.Loader;
import main.java.VolatiliaOGL.util.MathUtil;

public class TerrainRenderer
{
	private final RawModel quad;
	private TerrainShader shader;

	public TerrainRenderer(TerrainShader shader)
	{
		this.shader = shader;
		float[] positions = { -1, 1, -1, -1, 1, 1, 1, -1 };
		quad = Loader.INSTANCE.loadToVAO(positions, 2);
		shader.start();
		shader.connectTextureUnits();
		shader.loadFogData(World.fogDensity, World.fogGradient);
		shader.stop();
	}

	public void render(List<Terrain> terrains)
	{
		for(Terrain terrain : terrains)
		{
			this.prepareTerrain(terrain);
			this.loadModelMatrix(terrain);
			GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, quad.getVertexCount());
			this.unbindTexturedModel();
		}
	}

	private void prepareTerrain(Terrain terrain)
	{
		GL30.glBindVertexArray(quad.getVaoID());
		GL20.glEnableVertexAttribArray(0);
		this.bindTextures(terrain);
		shader.loadShineVariables(1, 0);
	}
	
	private void bindTextures(Terrain terrain)
	{
		TerrainTexturePack pack = terrain.getTexturePack();
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, pack.getBackgroundTexture().getTextureID());
		GL13.glActiveTexture(GL13.GL_TEXTURE1);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, pack.getrTexture().getTextureID());
		GL13.glActiveTexture(GL13.GL_TEXTURE2);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, pack.getgTexture().getTextureID());
		GL13.glActiveTexture(GL13.GL_TEXTURE3);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, pack.getbTexture().getTextureID());
		GL13.glActiveTexture(GL13.GL_TEXTURE4);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, terrain.getBlendMap().getTextureID());
	}

	private void unbindTexturedModel()
	{
		GL20.glDisableVertexAttribArray(0);
		GL30.glBindVertexArray(0);
	}

	private void loadModelMatrix(Terrain terrain)
	{
		Matrix4f transformationMatrix = MathUtil.createTransformationMatrix(new Vector2f(terrain.getX(), terrain.getZ()), 0, 1);
		shader.loadTransformationMatrix(transformationMatrix);
	}

}
