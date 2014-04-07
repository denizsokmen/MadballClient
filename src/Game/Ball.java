package Game;

import java.util.ArrayList;
import java.util.Random;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

public class Ball extends Entity {
	public Player owner;

	public Ball() {
		super();
		Texture tx = new Texture();
		tx.loadPNGTexture("ball.png", GL13.GL_TEXTURE0);
		shadow = tx;
		this.addAnim(1, 150, "sprites.png", 365, 311, 46, 40); // idle
		this.addAnim(3, 150, "sprites.png", 365, 311, 46, 40); // idle
		curAnim=animset.get(1);
	}

	public void update() {
		super.update();
		
	}

	public void render() {
		if (shadow != null) {
			shadow();
			GL11.glPushMatrix();
			GL11.glTranslatef(getX(), getY(), 0.0f);
			GL11.glRotatef(rot, 0.0f, 0.0f, 1.0f);
			curAnim.render();
			GL11.glPopMatrix();

		}
	}
	
	
}
