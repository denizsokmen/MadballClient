package Game;
/*
 * GL Shading Language desteði
 */

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;


import org.lwjgl.opengl.ARBFragmentShader;
import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.ARBVertexShader;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

public class Shader {
	private int vertShader;
	private int fragShader;
	public int shaderProgram;

	public Shader() {

	}

	public void release() {
		ARBShaderObjects.glDeleteObjectARB(fragShader);
		ARBShaderObjects.glDeleteObjectARB(vertShader);
		ARBShaderObjects.glDeleteObjectARB(shaderProgram);
	}

	public void loadShaders(String vert, String frag) {
		try {
			vertShader = compileShader(vert,
					ARBVertexShader.GL_VERTEX_SHADER_ARB);

			fragShader = compileShader(frag,
					ARBFragmentShader.GL_FRAGMENT_SHADER_ARB);

			attachShaders();
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	private void attachShaders() {
		// Shader programa vertex ve fragment shader ekle
		shaderProgram = ARBShaderObjects.glCreateProgramObjectARB();

		if (shaderProgram == 0)
			return;

		ARBShaderObjects.glAttachObjectARB(shaderProgram, vertShader);
		ARBShaderObjects.glAttachObjectARB(shaderProgram, fragShader);
		ARBShaderObjects.glLinkProgramARB(shaderProgram);
		ARBShaderObjects.glValidateProgramARB(shaderProgram);

	}

	private int compileShader(String file, int type) throws Exception {
		int shader = 0;
		String content = "";
		try {
			content = readFile(file);

			shader = ARBShaderObjects.glCreateShaderObjectARB(type);

			if (shader == 0)
				return 0;

			ARBShaderObjects.glShaderSourceARB(shader, content);
			ARBShaderObjects.glCompileShaderARB(shader);
			String infoLog = GL20.glGetShaderInfoLog(shader,
					GL20.glGetShaderi(shader, GL20.GL_INFO_LOG_LENGTH));
			if (GL20.glGetShaderi(shader, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE)
				throw new RuntimeException(infoLog);

			return shader;
		} catch (Exception ex) {

			ARBShaderObjects.glDeleteObjectARB(shader);
			throw ex;
		}
	}

	private String readFile(String file) throws Exception {
		StringBuilder source = new StringBuilder();
		FileInputStream in = new FileInputStream(file);
		Exception exception = null;

		BufferedReader reader;
		try {
			reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
			Exception innerExc = null;
			try {
				String line;
				while ((line = reader.readLine()) != null)
					source.append(line).append('\n');
			} catch (Exception exc) {
				exception = exc;
			} finally {
				try {
					reader.close();
				} catch (Exception exc) {
					if (innerExc == null)
						innerExc = exc;
					else
						exc.printStackTrace();
				}
			}

			if (innerExc != null)
				throw innerExc;
		} catch (Exception exc) {
			exception = exc;
		} finally {
			try {
				in.close();
			} catch (Exception exc) {
				if (exception == null)
					exception = exc;
				else
					exc.printStackTrace();
			}

			if (exception != null)
				throw exception;
		}

		return source.toString();
	}

	public void begin() {
		// Cizerken shaderi kullan
		ARBShaderObjects.glUseProgramObjectARB(shaderProgram);
	}

	public void end() {
		ARBShaderObjects.glUseProgramObjectARB(0);
	}

}
