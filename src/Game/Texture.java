package Game;


import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL30;
//import de.matthiasmann.twl.utils.PNGDecoder;
//import de.matthiasmann.twl.utils.PNGDecoder.Format;

import static org.lwjgl.opengl.GL11.*;

public class Texture {
	public int texID;
	public int texUnit;
	private int width;
	private int height;
	private int xoff;
	private int yoff;

	public Texture() {

	}
	
	public void setOffset(int x, int y) {
		xoff = x;
		yoff = y;
	}

	public void release() {
		if (texID != 0) {
			GL11.glDeleteTextures(texID);
		}
	}

	public void bind() {
		GL13.glActiveTexture(texUnit);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texID);
	}

	public void unbind() {
		GL13.glActiveTexture(texUnit);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
	}

	public void render() {

		glPushMatrix();
		bind();
		glEnable(GL_TEXTURE_2D);
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		GL11.glTranslatef(-xoff, -yoff, 0);
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glTexCoord2f(0.0f, 0.0f);
		GL11.glVertex2f(0,0);
		GL11.glTexCoord2f(1.0f, 0.0f);
		GL11.glVertex2f(width, 0);
		GL11.glTexCoord2f(1.0f, 1.0f);
		GL11.glVertex2f(width, height);
		GL11.glTexCoord2f(0.0f, 1.0f);
		GL11.glVertex2f(0, height);
		GL11.glEnd();
		unbind();
		glPopMatrix();

	}

	public int loadPNGTexture(String filename, int textureUnit) {
		ByteBuffer buf = null;
		PNGDecoder decoder;
		try {
			InputStream in = this.getClass().getClassLoader().getResourceAsStream("Game/"+filename); //new FileInputStream(filename);
			decoder = new PNGDecoder(in);

			width = decoder.getWidth();
			height = decoder.getHeight();
			decoder.overwriteTRNS((byte) 255, (byte) 0, (byte) 0);
			buf = ByteBuffer.allocateDirect(4 * decoder.getWidth()
					* decoder.getHeight());
		
			decoder.decode(buf, decoder.getWidth() * 4, PNGDecoder.Format.RGBA);
			
			buf.flip();
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		
		xoff = width/2;
		yoff = height/2;

		// Ekran kart�nda texture i�in yer iste
		texID = glGenTextures();
		GL13.glActiveTexture(textureUnit);

		// �stenilen haf�zay� kullanaca��m�z� bildir
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texID);

		// Her bile�en (r,g,b,a) 1 byte
		GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);

		// Texture pixellerini ekran kart�na y�kle
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, width, height,
				0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buf);

		// Kartta b�y�k yerler kaplamamak i�in texturenin k���lt�lm��
		// �rneklerini y�kle
		GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S,
				GL11.GL_REPEAT);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T,
				GL11.GL_REPEAT);

		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER,
				GL11.GL_NEAREST);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER,
				GL11.GL_LINEAR_MIPMAP_LINEAR);

		return texID;
	}
	
	public int loadPNGTexturePart(String filename, int textureUnit, int xoff, int wd, int yoff, int hg) {
		ByteBuffer buf = null;
		ByteBuffer buf2 = null;
		PNGDecoder decoder;
		try {
			InputStream in = this.getClass().getClassLoader().getResourceAsStream("Game/"+filename); // new FileInputStream(filename);
			decoder = new PNGDecoder(in);

			width = decoder.getWidth();
			height = decoder.getHeight();
			decoder.overwriteTRNS((byte) 255, (byte) 0, (byte) 0);
			buf = ByteBuffer.allocateDirect(4 * decoder.getWidth()
					* decoder.getHeight());
			buf2 = ByteBuffer.allocateDirect(4 * wd
					* hg);
			decoder.decode(buf, decoder.getWidth() * 4, PNGDecoder.Format.RGBA);
			buf.rewind();
			int off=0;
			for (int i = 0; i < hg; i++) {
				for (int j = 0; j < wd; j++) {
					int offset=width * (yoff + i)*4   + j*4 + xoff *4;
					buf2.put(off, buf.get(offset));
					buf2.put(off+1, buf.get(offset+1));
					buf2.put(off+2, buf.get(offset+2));
					buf2.put(off+3, buf.get(offset+3));
					off += 4;
				}
			}

			buf.flip();
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		width=wd;
		height=hg;
		
		xoff = width/2;
		yoff = height/2;
		// Ekran kart�nda texture i�in yer iste
		texID = glGenTextures();
		GL13.glActiveTexture(textureUnit);

		// �stenilen haf�zay� kullanaca��m�z� bildir
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texID);

		// Her bile�en (r,g,b,a) 1 byte
		GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);

		// Texture pixellerini ekran kart�na y�kle
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, width, height,
				0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buf2);

		// Kartta b�y�k yerler kaplamamak i�in texturenin k���lt�lm��
		// �rneklerini y�kle
		GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S,
				GL11.GL_REPEAT);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T,
				GL11.GL_REPEAT);

		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER,
				GL11.GL_NEAREST);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER,
				GL11.GL_LINEAR_MIPMAP_LINEAR);

		return texID;
	}
}
