package com.kaeruct.lilligames.common;

import com.badlogic.gdx.utils.Array;


public class Grid<T> {
	protected Array<Array<T>> objects;
	public int w, h;
	public int s;
	public int pxw, pxh;
	
	public Grid(int w, int h, int gsize) {
		this.w = w;
		this.h = h;
		this.s = gsize;
		this.pxw = w*s;
		this.pxh = h*s;
		objects = new Array<Array<T>>(h);
		
		for (int i = 0; i < h; i += 1) {
			Array<T> line = new Array<T>(w);
			for (int j = 0; j < w; j += 1) {
				line.add(null);
			}
			objects.add(line);
		}
	}

	public T at(int x, int y) {
		return objects.get(y).get(x);
	}
	

	public void moveFromTo(int x, int y, int nx, int ny) {
		if (!isOcuppied(nx, ny)) {
			T o = removeFrom(x, y);
			addAt(o, nx, ny);
		}
	}
	
	public void addAt(T o, int x, int y) {
		objects.get(y).set(x, o);
	}
	
	public T removeFrom(int x, int y) {
		T o = null;
		o = objects.get(y).get(x);
		objects.get(y).set(x, null);
		return o;
	}

	public boolean isOcuppied(int x, int y) {
		try {
			return objects.get(y).get(x) != null;
		} catch (ArrayIndexOutOfBoundsException e) {
			return true;
		}
	}
	
	public int fixXCoord(int x) {
		if (x < 0) x = 0;
		if (x >= w) x = w-1;
		return x;
	}
	
	public int fixYCoord(int y) {
		if (y < 0) y = 0;
		if (y >= h) y = h-1;
		return y;
	}
}
