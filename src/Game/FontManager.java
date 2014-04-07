package Game;

import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glEnable;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

public class FontManager {
	public Texture fonts;
	
	public FontManager() {
		fonts=new Texture();
	}
	
	public void load(String fname) {
		fonts.loadPNGTexture(fname, GL13.GL_TEXTURE0);
	}

	public void drawText(float x, float y, String text) {
		GL11.glPushMatrix();
		GL11.glTranslatef(x, y, 0.0f);
		GL11.glColor3f(1.0f, 1.0f, 1.0f);
		float fontwidth=9.0f;
		fonts.bind();
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		for (int i = 0; i < text.length(); i++) {
			char a = text.charAt(i);
			int xoffset = (a % 16) * 16;
			int yoffset = (a / 16) * 16;
			float l = (xoffset) / 256.0f;
			float r = (xoffset + 16.0f) / 256.0f;
			float t = (yoffset) / 256.0f;
			float b = (yoffset + 16.0f) / 256.0f;

			GL11.glBegin(GL11.GL_QUADS);
			GL11.glTexCoord2f(l, t);GL11.glVertex2f(0.0f, 0.0f);
			GL11.glTexCoord2f(r, t);GL11.glVertex2f(16.0f, 0.0f);
			GL11.glTexCoord2f(r, b);GL11.glVertex2f(16.0f, 16.0f);
			GL11.glTexCoord2f(l, b);GL11.glVertex2f(0.0f, 16.0f);
			GL11.glEnd();
			
			GL11.glTranslatef(fontwidth, 0.0f, 0.0f);
		}
		fonts.unbind();
		GL11.glPopMatrix();
	}

}
