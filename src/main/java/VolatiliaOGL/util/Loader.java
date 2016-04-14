package main.java.VolatiliaOGL.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.EXTTextureFilterAnisotropic;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL33;
import org.lwjgl.opengl.GLContext;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;

import main.java.VolatiliaOGL.models.RawModel;

public class Loader
{
	float[] vertices = {
	        -0.5f, 0.5f, 0f,    // Left top         ID: 0
	        -0.5f, -0.5f, 0f,   // Left bottom      ID: 1
	        0.5f, -0.5f, 0f,    // Right bottom     ID: 2
	        0.5f, 0.5f, 0f  // Right left       ID: 3
	};
	
	byte[] indices = {
	        // Left bottom triangle
	        0, 1, 2,
	        // Right top triangle
	        2, 3, 0
	};
	
	public static Loader INSTANCE = new Loader();
	private List<Integer> vaos = new ArrayList<Integer>();
	private List<Integer> vbos = new ArrayList<Integer>();
	private List<Integer> textures = new ArrayList<Integer>();

	public RawModel loadToVAO(float[] positions, float[] textureCoords, float[] normals, int[] indices)
	{
		int vaoID = this.createVAO();
		this.bindIndicesBuffer(indices);
		this.storeDataInAttributesList(0, 3, positions);
		this.storeDataInAttributesList(1, 2, textureCoords);
		this.storeDataInAttributesList(2, 3, normals);
		this.unbindVAO();
		return new RawModel(vaoID, indices.length);
	}

	public RawModel loadToVAO(float[] positions, float[] textureCoords, float[] normals, float[] tangents, int[] indices)
	{
		int vaoID = this.createVAO();
		this.bindIndicesBuffer(indices);
		this.storeDataInAttributesList(0, 3, positions);
		this.storeDataInAttributesList(1, 2, textureCoords);
		this.storeDataInAttributesList(2, 3, normals);
		this.storeDataInAttributesList(3, 3, tangents);
		this.unbindVAO();
		return new RawModel(vaoID, indices.length);
	}

	public int loadToVAO(float[] positions, float[] textureCoords)
	{
		int vaoID = this.createVAO();
		this.storeDataInAttributesList(0, 2, positions);
		this.storeDataInAttributesList(1, 2, textureCoords);
		this.unbindVAO();
		return vaoID;
	}

	public RawModel loadToVAO(float[] positions, int dimensions)
	{
		int vaoID = this.createVAO();
		this.storeDataInAttributesList(0, dimensions, positions);
		this.unbindVAO();
		return new RawModel(vaoID, positions.length / dimensions);
	}

	public int createEmptyVbo(int floatCount)
	{
		int vbo = GL15.glGenBuffers();
		vbos.add(vbo);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, floatCount * 4, GL15.GL_STREAM_DRAW);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		return vbo;
	}

	public void addInstancedAttribute(int vao, int vbo, int attribute, int dataSize, int instancedDataLength, int offset)
	{
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
		GL30.glBindVertexArray(vao);
		GL20.glVertexAttribPointer(attribute, dataSize, GL11.GL_FLOAT, false, instancedDataLength * 4, offset * 4);
		GL33.glVertexAttribDivisor(attribute, 1);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		GL30.glBindVertexArray(0);
	}

	public void updateVbo(int vbo, float[] data, FloatBuffer buffer)
	{
		buffer.clear();
		buffer.put(data);
		buffer.flip();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer.capacity() * 4, GL15.GL_STATIC_DRAW);
		GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, 0, buffer);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
	}

	public int loadTexture(String fileName)
	{
		Texture texture = null;
		try
		{
			texture = TextureLoader.getTexture("PNG", new FileInputStream("res/" + fileName + ".png"));
			GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_LINEAR);
			GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL14.GL_TEXTURE_LOD_BIAS, 0);
			if(GLContext.getCapabilities().GL_EXT_texture_filter_anisotropic)
			{
				float amount = Math.min(4f, GL11.glGetFloat(EXTTextureFilterAnisotropic.GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT));
				GL11.glTexParameterf(GL11.GL_TEXTURE_2D, EXTTextureFilterAnisotropic.GL_TEXTURE_MAX_ANISOTROPY_EXT, amount);
			}
			else
			{
				System.out.println("Anisotropic not supported!");
			}
		} catch(FileNotFoundException e)
		{
			e.printStackTrace();
		} catch(IOException e)
		{
			e.printStackTrace();
		}
		int textureID = texture.getTextureID();
		textures.add(textureID);
		return textureID;
	}

	public void CleanUp()
	{
		for(int vao : this.vaos)
			GL30.glDeleteVertexArrays(vao);

		for(int vbo : this.vbos)
			GL15.glDeleteBuffers(vbo);

		for(int texture : this.textures)
			GL11.glDeleteTextures(texture);
	}

	private int createVAO()
	{
		int vaoID = GL30.glGenVertexArrays();
		vaos.add(vaoID);
		GL30.glBindVertexArray(vaoID);
		return vaoID;
	}

	private void storeDataInAttributesList(int attributeNumber, int size, float[] data)
	{
		int vbo = GL15.glGenBuffers();
		vbos.add(vbo);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
		FloatBuffer buffer = this.storeDataInFloatBuffer(data);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
		GL20.glVertexAttribPointer(attributeNumber, size, GL11.GL_FLOAT, false, 0, 0);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
	}

	private void unbindVAO()
	{
		GL30.glBindVertexArray(0);
	}

	private void bindIndicesBuffer(int[] indicies)
	{
		int id = GL15.glGenBuffers();
		vbos.add(id);
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, id);
		IntBuffer buffer = storeDataInIntBuffer(indicies);
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
	}

	private FloatBuffer storeDataInFloatBuffer(float[] data)
	{
		FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
		buffer.put(data);
		buffer.flip();
		return buffer;
	}

	private IntBuffer storeDataInIntBuffer(int[] data)
	{
		IntBuffer buffer = BufferUtils.createIntBuffer(data.length);
		buffer.put(data);
		buffer.flip();
		return buffer;
	}
}
