package Game;


import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.lang.reflect.Field;

import org.lwjgl.opengl.Display;

public class Main {
	public static void main(String[] args) throws Exception {
		
		System.setProperty( "java.library.path", "./asd" );
		 
		Field fieldSysPath = ClassLoader.class.getDeclaredField( "sys_paths" );
		fieldSysPath.setAccessible( true );
		fieldSysPath.set( null, null );
		
		String host="localhost";
		if (args.length > 0)
			host=args[0];
		Game game = new Game(host);

		try {

			game.init();
			while (!game.finished) {
				Display.update();

				if (!Display.isVisible())
					Thread.sleep(200);
				else if (Display.isCloseRequested())
					System.exit(0);

				game.frame();
				game.render();
				//Display.sync(60);
			}
		} catch (Throwable t) {
			t.printStackTrace();
		} finally {
			game.cleanup();
		}
		System.exit(0);
	}

}