package Game;


import java.util.ArrayList;
import java.util.Random;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

public class Player extends Entity {
	public Random rd;
	public Game game;
	public Texture tx2;

	public Player() {
		super();
		dir = 1.0f;
		pos.x = 100.0f;
		pos.y = 100.0f;

		rd = new Random();
		animset = new ArrayList<Animation>();
		Texture tx = new Texture();
		tx.loadPNGTexturePart("sprites.png", GL13.GL_TEXTURE0, 2, 45, 3, 70);
		/*this.addAnim(1, 150, "sprites.png", 4, 553, 74, 70, 37,65); // idle
		this.addAnim(2, 150, "sprites.png", 4, 553, 74, 70 , 37,65); // ko�
		this.addAnim(3, 150, "sprites.png", 0, 184, 66, 74, 37,65); // �ut
		this.addAnim(2, 200, "sprites.png", 0, 365, 71, 73, 37,65); // kafa
		this.addAnim(3, 100, "sprites.png", 105, 2705, 73, 73, 37,65); // u�an kafa
		this.addAnim(1, 100, "sprites.png", 37, 2896, 57, 74, 37,65); // dayak
		this.addAnim(1, 100, "sprites.png", 169, 3046, 70, 73, 37,65); // yatma*/
		
		this.addAnim(Resources.getInstance().getAnim(Resources.Anim.PL_IDLE));
		this.addAnim(Resources.getInstance().getAnim(Resources.Anim.PL_MOV));
		this.addAnim(Resources.getInstance().getAnim(Resources.Anim.PL_SHOOT));
		this.addAnim(Resources.getInstance().getAnim(Resources.Anim.PL_HEAD));
		this.addAnim(Resources.getInstance().getAnim(Resources.Anim.PL_FHEAD));
		this.addAnim(Resources.getInstance().getAnim(Resources.Anim.PL_DEAD));
		this.addAnim(Resources.getInstance().getAnim(Resources.Anim.PL_LIE));
		
		curAnim = animset.get(0);
		shadow = tx;
		tx2 = new Texture();
		tx2.loadPNGTexture("golge.png", GL13.GL_TEXTURE0);
	}
	
	
	


	public void update() {
		super.update();
	}

	public void render() {
		if (shadow != null) {

			shadow();
			GL11.glPushMatrix();
			//GL11.glTranslatef(0.0f, -30.0f, 0.0f);
			GL11.glTranslatef(getX(), getY(), 0.0f);
			GL11.glRotatef(rot, 0.0f, 0.0f, 1.0f);

			GL11.glScalef(dir, 1.0f, 1.0f);
			// tex.render();
			curAnim.render();
			GL11.glPopMatrix();

		}
	}

	public void shadow() {
		if (tx2 != null) {
			GL11.glPushMatrix();

			GL11.glTranslatef(getX(), pos.y, -0.0001f);
			GL11.glColor3d(0.0, 0.0, 0.0);
			GL11.glRotatef(rot, 0.0f, 0.0f, 1.0f);
			tx2.render();
			GL11.glColor3d(1.0, 1.0, 1.0);
			GL11.glPopMatrix();

		}
	}

}
