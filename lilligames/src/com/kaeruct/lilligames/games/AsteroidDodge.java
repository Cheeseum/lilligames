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
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;
import com.kaeruct.lilligames.common.Particle;
import com.kaeruct.lilligames.screen.GameScreen;

public class AsteroidDodge extends MicroGame {
	
	Array<Particle> objects;
	Texture asteroidTx, shipTx, explosionTx;
	TextureRegion asteroidImage, shipImage;
	Sound explodeSound;
	long lastTime = 0;
	int interval = 700000000;
	float minr, maxr;
	Particle p; // player
	Color fadeColor;
	Color expColor;
	Color expColor2;
	
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
		
		explosionTx = new Texture(Gdx.files.internal("data/explosion.png"));
		
		explodeSound = Gdx.audio.newSound(Gdx.files.internal("data/explosion.ogg"));
		
		bg = new Color(0.05f, 0.1f, 0.1f, 1.0f);
		timeLeft = MathUtils.random(5, 15);
		
		fadeColor = new Color(0.01f, 0.01f, 0.01f, 0.05f);
		expColor = new Color(0.3f, 0.1f, 0.1f, 1f);
		
		float d = Math.max(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		minr = d/32;
		maxr = d/12;
		
		p = new Particle(
				Gdx.graphics.getWidth()/2,
				Gdx.graphics.getHeight()/2,
				d/32);
		p.misc = 0;
		p.color = p.color.cpy();
		expColor2 = p.color.cpy();

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
		float r2 = p.radius*2;
		batch.setColor(p.color);
		batch.draw(shipImage,
			p.x-p.radius, p.y-p.radius,
			p.radius, p.radius,
			r2, r2,
			1.0f, 1.0f, p.rotation);

		for (Particle o : objects) {
			batch.setColor(o.color);
			batch.draw(asteroidImage,
					o.x-o.radius, o.y-o.radius,
					o.radius, o.radius,
					o.radius*2, o.radius*2,
					1.0f, 1.0f, o.rotation);
	    }
		
		if (p.misc == 1) {
			batch.setColor(expColor2);
			expColor2.a -= 0.02;
			expColor2.clamp();
			batch.draw(explosionTx,
				p.x-r2, p.y-r2,
				r2*2, r2*2);
		}
	}

	@Override
	public void update(float delta) {
	    long t = TimeUtils.nanoTime();
	    if(t - lastTime > interval) {
		    addAsteroid();
		    lastTime = t;
	    }
	    
	    if (p.misc == 0) {
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
		    
		    if (p.x < 0) p.x = 0;
		    if (p.y < 0) p.y = 0;
		    if (p.x > Gdx.graphics.getWidth()) p.x = Gdx.graphics.getWidth();
		    if (p.y > Gdx.graphics.getHeight()) p.y = Gdx.graphics.getHeight();
	    }
		
		Iterator<Particle> bb = objects.iterator(); 

		while (bb.hasNext()) {
			Particle o = bb.next();
			
			o.rotation -= 0.5*o.misc;

	    	if (!o.update()) {
	    		bb.remove();
	    		continue;
	    	}
	    	
			if (p.misc == 0 && o.collidesWith(p)) {
				explodeSound.play();
				p.misc = 1;
			}
	    	
	    	if (o.offscreen()) {
	    		o.kill();
	    	}
		}
		
		if (p.misc == 1) {
			bg.add(expColor);
			p.radius += 7;
			p.color.sub(fadeColor);
			
			if (p.color.a == 0) {
				Timer.schedule(new Task(){
				    @Override
				    public void run() {
				    	lost = true; // finish immediately
				    }
				}, 1);
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
		explosionTx.dispose();
	}
}
