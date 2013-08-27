package com.kaeruct.lilligames.games;

import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import com.kaeruct.lilligames.common.Particle;
import com.kaeruct.lilligames.screen.GameScreen;

public class AsteroidDodge extends MicroGame {
	
	Array<Particle> objects;
	Texture asteroidTx, shipTx;
	TextureRegion asteroidImage, shipImage;
	Sound explodeSound;
	long lastTime = 0;
	int interval = 700000000;
	float minr, maxr;
	Particle p; // player
	
	public AsteroidDodge(GameScreen parent) {
		super(parent);
		objects = new Array<Particle>();
		asteroidTx = new Texture(Gdx.files.internal("data/potato.png"));
		asteroidTx.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
		asteroidImage = new TextureRegion(asteroidTx, 0, 0,
				asteroidTx.getWidth(), asteroidTx.getHeight());
		
		shipTx = new Texture(Gdx.files.internal("data/ship.png"));
		shipTx.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
		shipImage = new TextureRegion(shipTx, 0, 0,
				 shipTx.getWidth(),  shipTx.getHeight());
		
		
		explodeSound = Gdx.audio.newSound(Gdx.files.internal("data/pop.ogg"));
		
		bg = new Color(0.05f, 0.1f, 0.1f, 1.0f);
		timeLeft = MathUtils.random(5, 15);
		
		float d = Math.max(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		minr = d/32;
		maxr = d/16;
		
		p = new Particle(
				Gdx.graphics.getWidth()/2,
				Gdx.graphics.getHeight()/2,
				d/32);
		
		parent.showMessage("AVOID THE ASTEROIDS!");
	}

	private void addAsteroid() {
		float r = MathUtils.random(minr, maxr), x, y;
		
		if (MathUtils.randomBoolean()) {
			x = MathUtils.randomBoolean() ? -r : Gdx.graphics.getWidth() + r;
			y = MathUtils.random(-r, Gdx.graphics.getHeight());
		} else {
			y = MathUtils.randomBoolean() ? -r : Gdx.graphics.getHeight() + r;
			x = MathUtils.random(-r, Gdx.graphics.getWidth());
		}
		
		float rad = MathUtils.atan2(
				y - p.y,
				p.x - x);
		
		Particle asteroid = new Particle(x, y, r);
		float v = MathUtils.random(0.5f, 1.5f);
		asteroid.dx += Math.cos(rad)*v;
		asteroid.dy += Math.sin(rad)*v;
		asteroid.misc = MathUtils.randomBoolean() ? -1 : 1;
		asteroid.rotation = 270 - (MathUtils.radDeg * rad);
		
		objects.add(asteroid);
	}
	@Override
	public void onRender() {
		batch.setColor(p.color);
		batch.draw(shipImage,
				p.x-p.radius, p.y-p.radius,
				p.radius, p.radius,
				p.radius*2, p.radius*2,
				1.0f, 1.0f, p.rotation);

		for (Particle o : objects) {
			batch.setColor(o.color);
			batch.draw(asteroidImage,
					o.x-o.radius, o.y-o.radius,
					o.radius, o.radius,
					o.radius*2, o.radius*2,
					1.0f, 1.0f, o.rotation);
	    }
	}

	@Override
	public void update(float delta) {
	    long t = TimeUtils.nanoTime();
	    if(t - lastTime > interval) {
		    addAsteroid();
		    lastTime = t;
	    }
	    
		if (Gdx.input.isTouched()) {
			touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
			camera.unproject(touchPos);
			
			float rad = MathUtils.atan2(
				p.y - touchPos.y,
				touchPos.x - p.x);
			
			p.dx += Math.cos(rad)*0.1;
			p.dy += Math.sin(rad)*0.1;
			p.rotation = 270 - (MathUtils.radDeg * rad);
			deacc(p);
		}
	    p.update();
		
		Iterator<Particle> bb = objects.iterator(); 
		Particle o;

		while (bb.hasNext()) {
			o = bb.next();
			
			o.rotation -= 0.5*o.misc;

	    	if (!o.update()) {
	    		bb.remove();
	    		continue;
	    	}
	    	
			if (o.collidesWith(p)) {
				explodeSound.play();
				p.radius = 0;
				lost = true;
			}
	    	
	    	if (o.offscreen()) {
	    		o.kill();
	    	}
		}
	}
	
	@Override
	public boolean isFinished() {
		return p.alive;
	}
	
	protected void deacc(Particle p) {
		float d = 0.01f;
		if (p.dx != 0) {
			p.dx += p.dx < 0 ? d : -d;
		}
		if (p.dy != 0) {
			p.dy += p.dy < 0 ? d : -d;
		}
	}
	
	public void dispose() {
		super.dispose();
		explodeSound.dispose();
		asteroidTx.dispose();
		shipTx.dispose();
	}
}