import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;

import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.util.glu.GLU;

import static org.lwjgl.opengl.ARBBufferObject.*;
import static org.lwjgl.opengl.ARBVertexBufferObject.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.util.glu.GLU.*;

public final class Main {

	static {
		try {
			int mode = -1;
			DisplayMode[] modes = Display.getAvailableDisplayModes();
			for (int i = 0; i < modes.length; i++) {
				if (modes[i].getWidth() == 640 && modes[i].getHeight() == 480
						&& modes[i].getBitsPerPixel() >= 16) {
					mode = i;
					break;
				}
			}
			if (mode != -1) {
				System.out.println("Setting display mode to " + modes[mode]);
				Display.setDisplayMode(modes[mode]);
				Display.setFullscreen(true);
				System.out.println("Created display.");
			}
		} catch (Exception e) {
			System.err.println("Failed to create display due to " + e);
		}
	}

	static {
		try {
			Display.create();
			System.out.println("Created OpenGL.");
		} catch (Exception e) {
			System.err.println("Failed to create OpenGL due to " + e);
			System.exit(1);
		}
	}

	/**
	 * Is the game finished?
	 */
	private static boolean finished;

	/**
	 * A rotating square!
	 */
	private static float angle;
	private static int buffer_id;
	private static int indices_buffer_id;
	private static FloatBuffer vertices;
	private static ByteBuffer mapped_buffer;
	private static FloatBuffer mapped_float_buffer;
	private static IntBuffer indices;
	private static ByteBuffer mapped_indices_buffer;
	private static IntBuffer mapped_indices_int_buffer;
	public static Texture tex;
	public static Texture tex2;
	private static Shader sha;
	public static ArrayList<Particle> parts;
	public static Shader sha2;
	private static Timer time;
	private static Ball bal;
	private static Player pl;
	public static void main(String[] arguments) {
		try {
			tex = new Texture();
			tex.loadPNGTexture("dosya.png", GL13.GL_TEXTURE0);
			tex2 = new Texture();
			tex2.loadPNGTexture("saha.png", GL13.GL_TEXTURE0);
			
			sha = new Shader();
			sha.loadShaders("shader.vert", "shader.frag");

			sha2 = new Shader();
			sha2.loadShaders("part.vert", "part.frag");
			parts = new ArrayList<Particle>();
			bal=new Ball();
			bal.shader=sha;
			pl=new Player();
			time=new Timer();
			
			init();
			while (!finished) {
				Display.update();

				if (!Display.isVisible())
					Thread.sleep(200);
				else if (Display.isCloseRequested())
					System.exit(0);

				mainLoop();
				render();
				Display.sync(60);
			}
		} catch (Throwable t) {
			t.printStackTrace();
		} finally {
			cleanup();
		}
		System.exit(0);
	}

	private static void addpart() {
		Particle par = new Particle();
		par.tex = tex;
		par.shader = sha2;
		parts.add(par);
	}

	/**
	 * All calculations are done in here
	 */
	private static void mainLoop() {
		angle += 0.09f;
		bal.update();
		pl.update();
		if (time.ticked(20)) {
		//	addpart();
		}
		
		
		if (angle > 360.0f)
			angle = 0.0f;

		for (int i = 0; i < parts.size(); i++) {
			if (parts.get(i) instanceof Particle)
			{
			if (!parts.get(i).dead)
				parts.get(i).update();
			else
				parts.remove(i);
			}
		}
		if (Mouse.getDX() != 0 || Mouse.getDY() != 0 || Mouse.getDWheel() != 0)
			System.out.println("Mouse moved " + Mouse.getDX() + " "
					+ Mouse.getDY() + " " + Mouse.getDWheel());
		for (int i = 0; i < Mouse.getButtonCount(); i++)
			if (Mouse.isButtonDown(i))
				System.out.println("Button " + i + " down");
		if (Keyboard.isKeyDown(Keyboard.KEY_ESCAPE))
			finished = true;
		while(Keyboard.next()) {
			if (Keyboard.getEventKey() == Keyboard.KEY_ESCAPE
					&& Keyboard.getEventKeyState())
				finished = true;
			if (Keyboard.getEventKey() == Keyboard.KEY_T
					&& Keyboard.getEventKeyState())
			{
				bal.speed.x *= 2.0f;
				bal.speed.y *= 2.0f;
			}
			
			if (Keyboard.getEventKey() == Keyboard.KEY_R
					&& Keyboard.getEventKeyState())
			{
				bal.speed.z *= 2.0f;
			}
			
			if (Keyboard.getEventKey() == Keyboard.KEY_RIGHT
					&& Keyboard.getEventKeyState())
			{
				pl.pos.x += 2.0f;
			}
			
			if (Keyboard.getEventKey() == Keyboard.KEY_LEFT
					&& Keyboard.getEventKeyState())
			{
				pl.pos.x -= 2.0f;
			}
			
			if (Keyboard.getEventKey() == Keyboard.KEY_SPACE
					&& Keyboard.getEventKeyState())
			{
				pl.speed.z = 2.0f;
			}
		}
	}


	private static void render() {
		glClear(GL_COLOR_BUFFER_BIT);
		glPushMatrix();
		glTranslatef(64.0f,256.0f,0.0f);
		tex2.render();
		glTranslatef(128.0f,0.0f,0.0f);
		tex2.render();
		glTranslatef(128.0f,0.0f,0.0f);
		tex2.render();
		glTranslatef(128.0f,0.0f,0.0f);
		tex2.render();
		glTranslatef(128.0f,0.0f,0.0f);
		tex2.render();
		glTranslatef(128.0f,0.0f,0.0f);
		tex2.render();
		glPopMatrix();
		
		
		for (int i = 0; i < parts.size(); i++)
			parts.get(i).render();
		bal.render();
		pl.render();
		

		/*sha.begin();
		int tx = ARBShaderObjects.glGetUniformLocationARB(sha.shaderProgram,
				"tex");
		ARBShaderObjects.glUniform1iARB(tx, tex.texUnit);
		glPushMatrix();
		glTranslatef(Display.getDisplayMode().getWidth() / 2, Display
				.getDisplayMode().getHeight() / 2, 0.0f);
		glRotatef(angle, 1.0f, 0.0f, 0.0f);
		tex.render();
		glPopMatrix();
		sha.end();*/

	}


	private static void init() throws Exception {

		System.out.println("Timer resolution: " + Sys.getTimerResolution());
		// Go into orthographic projection mode.
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();

		glOrtho(0, Display.getDisplayMode().getWidth(), Display
				.getDisplayMode().getHeight(), 0, -100.0f, 100.0f);
		// gluOrtho2D(0, Display.getDisplayMode().getWidth(),
		// Display.getDisplayMode().getHeight(),0 );

		glMatrixMode(GL_MODELVIEW);
		glLoadIdentity();
		glViewport(0, 0, Display.getDisplayMode().getWidth(), Display
				.getDisplayMode().getHeight());
		if (!GLContext.getCapabilities().GL_ARB_vertex_buffer_object) {
			System.out.println("ARB VBO not supported!");
			System.exit(1);
		}
		
	}

	/**
	 * Cleanup
	 */
	private static void cleanup() {
		IntBuffer int_buffer = ByteBuffer.allocateDirect(8)
				.order(ByteOrder.nativeOrder()).asIntBuffer();
		int_buffer.put(0, buffer_id);
		int_buffer.put(1, indices_buffer_id);
		glDeleteBuffersARB(int_buffer);
		Display.destroy();
	}
}