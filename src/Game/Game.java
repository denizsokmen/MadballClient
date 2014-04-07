package Game;

import static org.lwjgl.opengl.GL11.GL_MODELVIEW;
import static org.lwjgl.opengl.GL11.GL_PROJECTION;
import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.opengl.GL11.glMatrixMode;
import static org.lwjgl.opengl.GL11.glOrtho;
import static org.lwjgl.opengl.GL11.glViewport;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

import Packet.*;

public class Game {

	public static int serial = 0; // global serial

	public List<Packets> messages;
	public ArrayList<Entity> entityList;
	public ArrayList<Texture> textureList;
	public ArrayList<Shader> shaderList;
	public NetworkTCP client;
	public static Ball ball;
	public boolean finished;
	public boolean[] KEYS;
	public Player pl;
	public Vector camera;
	public FontManager font;

	public Texture texBackground;

	public Timer fpstimer;

	public Game(String IP) {
		fpstimer = new Timer();
		camera = new Vector();
		messages = Collections.synchronizedList(new ArrayList<Packets>());
		font = new FontManager();

		client = new NetworkTCP() {

			@Override
			protected void connected(boolean alreadyConnected) {

			}

			@Override
			protected void disconnected() {
				// TODO Auto-generated method stub

			}

			@Override
			protected void messageReceived(Packets packets) {
				// TODO Auto-generated method stub
				messages.add(packets);

			}

		};
		try {
			NetworkTCP.IP = InetAddress.getByName(IP);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		new Thread(client).start();

	}

	public void processMessages() {
		synchronized (messages) {
			if (messages.size() > 0) {

				Iterator<Packets> i = messages.iterator();
				while (i.hasNext()) {
					processMessage(i.next());
					i.remove();
				}
			}
		}
	}

	public void processMessage(Packets packets) {
		if (packets == null)
			return;
		switch (packets.getType()) {
		case 1: {
			MovementPacket pk = (MovementPacket) packets;
			Entity pl = getEntity(pk.serial);
			pl.targetpos.x = pk.x;
			pl.targetpos.y = pk.y;
			pl.targetpos.z = pk.z;
			pl.dir = pk.dir;
			break;
		}

		case 2: {
			CreateObjectPacket pk = (CreateObjectPacket) packets;
			Entity pl = null;
			if (pk.obj == 0) {
				pl = new Player();

			} else if (pk.obj == 1) {
				pl = new Ball();
				ball = (Ball) pl;
			}
			pl.pos.x = pk.x;
			pl.pos.y = pk.y;
			pl.pos.z = pk.z;
			pl.targetpos.x = pk.x;
			pl.targetpos.y = pk.y;
			pl.targetpos.z = pk.z;

			if (pl != null)
				addEntity(pl, pk.serial);
		}

			break;

		case 3: {
			DestroyPacket pk = (DestroyPacket) packets;
			entityList.remove(getEntity(pk.serial));
		}
			break;

		case 6: {
			AnimatePacket pk = (AnimatePacket) packets;
			Entity pl = getEntity(pk.serial);

			if (pl != null) {
				pl.setAnim(pl.animset.get(pk.animSet));
			}
		}
			break;
		default:
			break;
		}
	}

	public Entity getEntity(int ser) {
		for (Entity e : entityList) {

			if (e.serial == ser)
				return e;
		}
		return null;
	}

	public void addEntity(Entity e) {
		serial++;
		e.serial = serial;
		entityList.add(e);
	}

	public void addEntity(Entity e, int ser) {
		e.serial = ser;
		entityList.add(e);
	}

	public void initDisplay() {
		try {
			int mode = -1;
			DisplayMode[] modes = Display.getAvailableDisplayModes();
			for (int i = 0; i < modes.length; i++) {
				if (modes[i].getWidth() == 800 && modes[i].getHeight() == 600
						&& modes[i].getBitsPerPixel() >= 16) {
					mode = i;
					break;
				}
			}
			if (mode != -1) {
				Display.setDisplayMode(modes[mode]);
				// Display.setFullscreen(true);
			}
		} catch (Exception e) {
			System.err.println("Failed to create display : " + e);
		}

		try {
			Display.create();
		} catch (Exception e) {
			System.err.println("Failed to create OpenGL : " + e);
			System.exit(1);
		}
	}

	public void initProjection() {
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		glOrtho(0, Display.getDisplayMode().getWidth(), Display
				.getDisplayMode().getHeight(), 0, -100.0f, 100.0f);

		glMatrixMode(GL_MODELVIEW);
		glLoadIdentity();
		glViewport(0, 0, Display.getDisplayMode().getWidth(), Display
				.getDisplayMode().getHeight());

		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_ALPHA_TEST);
	}

	public void initVars() {
		KEYS = new boolean[Keyboard.KEYBOARD_SIZE];
		entityList = new ArrayList<Entity>();
		textureList = new ArrayList<Texture>();
		shaderList = new ArrayList<Shader>();
	}

	public void loadTextures() {
		texBackground = new Texture();
		texBackground.loadPNGTexture("saha2.png", GL13.GL_TEXTURE0);

		font.load("fonts.png");
	}

	// Game'in maini
	public void init() {
		initDisplay();
		initProjection();
		preLoad();
		loadTextures();
		initVars();
	}

	public void frame() {
		processMessages();

		checkInput();

		if (fpstimer.ticked(30)) {
			for (Entity en : entityList) {
				en.update();
			}
		}
	}

	public void render() {
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

		GL11.glAlphaFunc(GL11.GL_GREATER, 0);
		GL11.glPushMatrix();
		if (ball != null) {
			// interpoley��n, kamera g�zel g�z�k�yo
			camera.x = Math.max(-2666 + 800,
					Math.min(0, lerp(camera.x, -ball.pos.x + 400, 0.05f)));
			camera.y = Math.max(-1000 + 600,
					Math.min(0, lerp(camera.y, -ball.pos.y + 300, 0.05f)));

			GL11.glTranslatef(camera.x, camera.y, 0.0f);
		}
		renderBackground();

		for (Entity en : entityList) {
			GL11.glPushMatrix();
			if (en instanceof Player) {
				GL11.glTranslatef(en.pos.x, en.pos.y - 75.0f, 10.000001f);
				font.drawText(-10.0f, 0.0f, "TEST");
				GL11.glTranslatef(-en.pos.x, -en.pos.y + 75.0f, -10.000001f);
			}

			GL11.glTranslatef(0.0f, 0.0f, en.pos.y / 2000.0f);
			en.render();
			GL11.glPopMatrix();
		}

		GL11.glPopMatrix();

	}

	public void renderBackground() {
		GL11.glPushMatrix();
		GL11.glTranslatef(1333.0f, 500.0f, -2.0f);
		texBackground.render();
		/*
		 * GL11.glTranslatef(128.0f, 0.0f, 0.0f); texBackground.render();
		 * GL11.glTranslatef(128.0f, 0.0f, 0.0f); texBackground.render();
		 * GL11.glTranslatef(128.0f, 0.0f, 0.0f); texBackground.render();
		 * GL11.glTranslatef(128.0f, 0.0f, 0.0f); texBackground.render();
		 * GL11.glTranslatef(128.0f, 0.0f, 0.0f); texBackground.render();
		 * GL11.glTranslatef(128.0f, 0.0f, 0.0f); texBackground.render();
		 * GL11.glTranslatef(128.0f, 0.0f, 0.0f); texBackground.render();
		 * GL11.glTranslatef(128.0f, 0.0f, 0.0f); texBackground.render();
		 * GL11.glTranslatef(128.0f, 0.0f, 0.0f); texBackground.render();
		 * GL11.glTranslatef(128.0f, 0.0f, 0.0f); texBackground.render();
		 * GL11.glTranslatef(128.0f, 0.0f, 0.0f); texBackground.render();
		 * GL11.glTranslatef(128.0f, 0.0f, 0.0f); texBackground.render();
		 */
		GL11.glPopMatrix();

	}

	public void checkInput() {
		while (Keyboard.next()) {
			if (Keyboard.getEventKey() > 0) {
				if (Keyboard.getEventKeyState())
					onKeyPressed(Keyboard.getEventKey());
				else
					onKeyReleased(Keyboard.getEventKey());
			}
		}
		onKey();

	}

	public void onKeyPressed(int k) {
		KEYS[k] = true;

		KeyPressPacket pk = new KeyPressPacket();
		pk.key = k;
		client.write(pk);

	}

	public void onKey() {

		if (KEYS[Keyboard.KEY_ESCAPE])
			finished = true;

	}

	public void onKeyReleased(int k) {
		KEYS[k] = false;

		KeyReleasePacket pk = new KeyReleasePacket();
		pk.key = k;
		client.write(pk);

	}

	public static float lerp(float v0, float v1, float t) {
		return v0 * (1 - t) + v1 * t;
	}

	public void preLoad() {
		Resources res = Resources.getInstance();
		res.addAnim(Resources.Anim.PL_IDLE, 1, 150, "sprites.png", 4, 553, 74,
				70, 37, 65);
		res.addAnim(Resources.Anim.PL_MOV, 2, 150, "sprites.png", 4, 553, 74,
				70, 37, 65);
		res.addAnim(Resources.Anim.PL_SHOOT, 3, 150, "sprites.png", 0, 184, 66,
				74, 37, 65);
		res.addAnim(Resources.Anim.PL_HEAD, 2, 200, "sprites.png", 0, 365, 71,
				73, 37, 65);
		res.addAnim(Resources.Anim.PL_FHEAD, 3, 100, "sprites.png", 105, 2705,
				73, 73, 37, 65);
		res.addAnim(Resources.Anim.PL_DEAD, 1, 150, "sprites.png", 37, 2896,
				57, 74, 37, 65);
		res.addAnim(Resources.Anim.PL_LIE, 1, 150, "sprites.png", 169, 3046,
				70, 73, 37, 65);
		
		res.addFont(Resources.Font.FNT_TEXT, "fonts.png");
	}

	public void cleanup() {
		Display.destroy();
	}

}
