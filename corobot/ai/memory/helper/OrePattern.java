package corobot.ai.memory.helper;

public class OrePattern {

	private int yMin = 0;
	private int yMax = 255;
	private boolean isOnSurface = false;

	public OrePattern(int yMin, int yMax) {
		this.yMin = yMin;
		this.yMax = yMax;
	}
	
	public OrePattern(int yMin, int yMax, boolean isOnSurface) {
		this(yMin, yMax);
		this.isOnSurface = isOnSurface;
	}
	
	public OrePattern(boolean isOnSurface) {
		this.isOnSurface = isOnSurface;
	}
	
	public int getYMin() {
		return yMin;
	}
	
	public int getYMax() {
		return yMax;
	}
	
	public int getYMiddle() {
		return getYMin() + (getYMax() - getYMin())/2;
	}
	
	public boolean isOnSurface() {
		return isOnSurface;
	}

	public void setOnSurface(boolean isOnSurface) {
		this.isOnSurface = isOnSurface;
	}
	
	
	
}
