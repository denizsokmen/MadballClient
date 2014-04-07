package Game;
import org.lwjgl.Sys;

public class Timer {
	public long lastTime;

	public Timer() {
		tick();
	}

	public void tick() {
		lastTime = getTime2();
	}

	public boolean ticked(long millis) {
		if (getTime2() - lastTime >= millis) {
			lastTime=getTime2();
			return true;
		}
		else
			return false;
	}

	public long getTime() {
		return System.nanoTime() / 1000000;
	}

	public long getTime2() {
		return (Sys.getTime() * 1000) / Sys.getTimerResolution();
	}
}
