package Game;

import java.util.HashMap;
import java.util.Map;

public class Resources {

	public static enum Anim {
		PL_IDLE, PL_MOV, PL_SHOOT, PL_HEAD, PL_FHEAD, PL_DEAD, PL_LIE,

	}

	public static enum Tex {
		TEX_SHADOW
	}

	public static enum Font {
		FNT_MENU, FNT_TEXT,
	}
	
	public static enum Shade {
		SHD_PARTICLE
	}

	private Resources() {
		animMap = new HashMap<Anim, Animation>();
		textureMap = new HashMap<Tex, Texture>();
		fontMap = new HashMap<Font, FontManager>();

	}

	public final static Resources getInstance() {
		return instance;
	}

	public void addAnim(Anim ID, int numfr, int spf, String fname, int xoff,
			int yoff, int w, int h, int ofx, int ofy) {
		Animation anim = new Animation(numfr, spf);
		anim.loadSet(fname, xoff, yoff, w, h, ofx, ofy);
		animMap.put(ID, anim);
	}
	
	public Animation getAnim(Anim ID) {
		return animMap.get(ID);
	}
	
	
	public void addFont(Font ID, String fname) {
		FontManager fnt=new FontManager();
		fnt.load(fname);
		fontMap.put(ID, fnt);
	}
	
	public FontManager getFont(Font ID) {
		return fontMap.get(ID);
	}

	private static Resources instance = new Resources();
	private Map<Anim, Animation> animMap;
	private Map<Tex, Texture> textureMap;
	private Map<Font, FontManager> fontMap;
	private Map<Shade, Shader> shaderMap;

}
