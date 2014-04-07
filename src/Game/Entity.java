package Game;

import java.util.ArrayList;

import org.lwjgl.opengl.GL11;

public class Entity {

	protected Vector pos;
	protected Vector prevpos;
	protected Vector targetpos;
	protected Vector speed;
	protected Vector accel;
	protected float rot;
	protected Shader shader;
	public float dir;
	public int serial; // server taraf�ndan �retilen her �eyin seriali var
	public Animation curAnim;
	public ArrayList<Animation> animset;
	public Texture shadow;

	public void setAnim(Animation anim) {
		if (anim != null) {
			curAnim = anim;
			curAnim.timer.tick();
			curAnim.curFrame = 0;
		}
	}

	public int getAnimIndex() {
		return animset.indexOf(curAnim);
	}

	public void addAnim(int numfr, int spf, String fname, int xoff, int yoff,
			int w, int h) {
		Animation anim = new Animation(numfr, spf);
		anim.loadSet(fname, xoff, yoff, w, h);
		animset.add(anim);
	}
	
	public void addAnim(int numfr, int spf, String fname, int xoff, int yoff,
			int w, int h, int ofx, int ofy) {
		Animation anim = new Animation(numfr, spf);
		anim.loadSet(fname, xoff, yoff, w, h, ofx, ofy);
		animset.add(anim);
	}
	
	public void addAnim(Animation anim)	 {
		animset.add(anim);
	}

	public Entity() {
		dir = 1.0f;
		pos = new Vector();
		speed = new Vector();
		accel = new Vector();
		prevpos = new Vector();
		targetpos = new Vector();
		animset = new ArrayList<Animation>();
	}

	public void update() {
		pos.x = Game.lerp(pos.x, targetpos.x, 0.3f);
		pos.y = Game.lerp(pos.y, targetpos.y, 0.3f);
		pos.z = Game.lerp(pos.z, targetpos.z, 0.3f);
		
		if (curAnim != null)
			curAnim.checkFrame();
	}

	public float getX() {
		return pos.x;
	}

	public float getY() {
		return pos.y - pos.z; // fake 3d
	}

	public void render() {

	}

	public void beginShader() {
		if (shader != null)
			shader.begin();
	}

	public void endShader() {
		if (shader != null)
			shader.end();
	}
	

	public void shadow() {
		if (shadow != null) {
			GL11.glPushMatrix();
			
			GL11.glTranslatef(getX(), pos.y, -0.0001f);
			GL11.glColor3d(0.0, 0.0, 0.0);
			GL11.glRotatef(rot, 0.0f, 0.0f, 1.0f);
			shadow.render();
			GL11.glColor3d(1.0, 1.0, 1.0);
			GL11.glPopMatrix();

		}
	}
}
