package Game;
import java.util.Random;

import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.GL11;

public class Particle extends Entity {
	public Vector origin;
	public Timer time;
	public boolean dead;
	public int lifespan;
	public float distance;

	public Particle() {
		super();
		time = new Timer();
		dead = false;
		Random rd = new Random();
		origin = new Vector();

		pos.x = 50.0f;
		pos.y = 200.0f;
		origin.set(pos);
		float ang = 35.0f + rd.nextFloat() * 40.0f;
		ang /= 180.0f;
		ang *= Math.PI;
		speed.x = (float) Math.cos(ang) * .7f;
		speed.y = (float) -Math.sin(ang) * .7f;
		accel.y = 0.09f;

	}

	public Particle(float X, float Y) {
		super();
		time = new Timer();
		dead = false;
		origin = new Vector();
		origin.x = X;
		origin.y = Y;
		pos.x = X;
		pos.y = Y;
	}

	public void render() {
		if (lifespan > 0)
			if (time.ticked(lifespan))
				dead = true;

		if (shadow != null) {
			rot += 5.0f;
			GL11.glPushMatrix();
			if (Vector.dist(pos, origin) > 80.0f)
				dead = true;
			if (shader != null)
				shader.begin();
			int tx = ARBShaderObjects.glGetUniformLocationARB(
					shader.shaderProgram, "dist");
			int tx2 = ARBShaderObjects.glGetUniformLocationARB(
					shader.shaderProgram, "tex");
			ARBShaderObjects.glUniform1iARB(tx2, shadow.texUnit);
			ARBShaderObjects.glUniform1fARB(tx, Vector.dist(pos, origin));
			GL11.glTranslatef(pos.x, pos.y, pos.z);
			GL11.glRotatef(
					(float) ((float) 180.0f * Math.atan2(speed.y, speed.x) / Math.PI),
					0.0f, 0.0f, 1.0f);
			shadow.render();
			GL11.glPopMatrix();

			if (shader != null)
				shader.end();
		}
	}
}
