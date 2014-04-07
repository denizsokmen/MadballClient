package Game;

import org.lwjgl.opengl.GL13;

public class Animation {
	public int milPerFrame;
	public int frameCount;
	public int curFrame;
	public Timer timer;
	public Texture[] animSet;

	public Animation(int count, int spf) {
		frameCount = count;
		milPerFrame = spf;
		timer = new Timer();
		animSet = new Texture[count];
		for (int i = 0; i < count; i++)
			animSet[i] = new Texture();
	}

	public void loadSet(String name, int xoff, int yoff, int width, int height) {
		if (width == 0 && height == 0) // tek frame b�t�n image
			animSet[0].loadPNGTexture(name, GL13.GL_TEXTURE0);
		else {
			for (int i = 0; i < frameCount; i++) {
				animSet[i].loadPNGTexturePart(name, GL13.GL_TEXTURE0, xoff
						+ (i * width), width, yoff, height);
				
				animSet[i].setOffset(width/2, height/2);
				
			}
		}
	}
	
	public void loadSet(String name, int xoff, int yoff, int width, int height, int xf, int yf) {
		if (width == 0 && height == 0) // tek frame b�t�n image
			animSet[0].loadPNGTexture(name, GL13.GL_TEXTURE0);
		else {
			for (int i = 0; i < frameCount; i++) {
				animSet[i].loadPNGTexturePart(name, GL13.GL_TEXTURE0, xoff
						+ (i * width), width, yoff, height);
				animSet[i].setOffset(xf, yf);
			}
		}
	}

	public int checkFrame() {
		if (timer.ticked(milPerFrame)) {
			curFrame += 1;
			curFrame %= frameCount;
			if (curFrame == 0)
				return -1;
		}
		return curFrame;
	}

	public void render() {
		animSet[curFrame].render();
	}
}
