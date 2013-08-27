package com.kaeruct.lilligames.common;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Circle;

public class Particle extends Circle {
	public float dx;
	public float dy;
	public Color color;
	public boolean alive;
	public float oscillation;
	public static final float DEFAULT_RADIUS = 5;
	public static final Color DEFAULT_COLOR = Color.WHITE;
	
	public Particle(float x, float y, float radius, Color color) {
		this.x = x;
		this.y = y;
		this.radius = radius;
		this.color = color;
		this.oscillation = 0;
		this.alive = true;
	}
	
	public Particle(float x, float y, float radius) {
		this(x, y, radius, DEFAULT_COLOR);
	}
	
	public Particle(float x, float y) {
		this(x, y, DEFAULT_RADIUS, DEFAULT_COLOR);
	}
	
	public boolean update() {
		x += dx;
		y -= dy;
		return alive;
	}
	
	public void kill() {
		this.alive = false;
	}
	
	public boolean offscreen() {
		float r2 = radius*2;
		return (x + r2) < 0 ||
			   (y + r2) < 0 ||
			   (x - r2) > Gdx.graphics.getWidth() ||
			   (y - r2) > Gdx.graphics.getHeight();
	}
}
