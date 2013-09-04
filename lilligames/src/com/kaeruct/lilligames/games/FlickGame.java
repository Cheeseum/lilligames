package com.kaeruct.lilligames.games;

import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import com.kaeruct.lilligames.common.Particle;
import com.kaeruct.lilligames.screen.GameScreen;

public class FlickGame extends MicroGame {
	Array<Particle> objects;
	
	Texture asteroidTx;
	TextureRegion asteroidImage;
	
	Vector3 lastTouchPos;
	int spawnInterval = 1000000000;
	int misses = 0;
	long lastTime = 0;
	
	class InputHandler extends InputAdapter {
		@Override
		public boolean touchDown(int screenX, int screenY, int pointer, int button) {
			lastTouchPos.set(screenX, screenY, 0);
			camera.unproject(lastTouchPos);
			return true;
		}
		
		@Override
		public boolean touchDragged (int screenX, int screenY, int pointer) {
			touchPos.set(screenX, screenY, 0);
			camera.unproject(touchPos);
			
			Vector2 center = new Vector2();
			Vector2 touchPos2 = new Vector2(touchPos.x, touchPos.y);
			Vector2 lastTouchPos2 = new Vector2(lastTouchPos.x, lastTouchPos.y);
			
			for (Particle o : objects) {
				center.set(o.x, o.y);
				if (Intersector.intersectSegmentCircle(lastTouchPos2, touchPos2, center, o.radius * o.radius)) {
					Vector2 velocity = lastTouchPos2.sub(touchPos2).mul(0.5f);
					if (velocity.len() > 3.0f) {
						o.dx += -velocity.x;
						o.dy += velocity.y;
						break;
					}
				}
			}
			
			// touchPos is already in world space
			lastTouchPos.set(touchPos);
			return true;
		}
		
		@Override
		public boolean touchUp(int screenX, int screenY, int pointer, int button) {
			return true;
		}
	}
	
	public FlickGame (GameScreen parent) {
		super(parent);
		bg = new Color(0.2f, 0.2f, 0.2f, 1.0f);
		
		asteroidTx = new Texture(Gdx.files.internal("data/potato.png"));
		asteroidTx.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
		asteroidImage = new TextureRegion(asteroidTx, 0, 0,
		asteroidTx.getWidth(), asteroidTx.getHeight());
		
		objects = new Array<Particle>();
		lastTouchPos = new Vector3();
		
		Gdx.input.setInputProcessor(new InputHandler());
	}
	
	private void spawnObject (boolean shouldFlick) {
		Particle p = new Particle();
		p.x = -10.0f;
		p.y = Gdx.graphics.getHeight() / 2.0f;
		p.radius = 50.0f;
		p.dx += 6.0f;
		p.misc = shouldFlick ? 1 : 0;
		objects.add(p);
	}
	
	@Override
	public void onRender() {
		for (Particle o : objects) {
			if (o.misc > 0) {
				batch.setColor(Color.ORANGE);
			} else {
				batch.setColor(o.color);
			}
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
		if (t - lastTime > spawnInterval) {
			lastTime = t;
			spawnObject(MathUtils.random() < 0.7);
		}
		
		Iterator<Particle> bb = objects.iterator(); 
		Particle o;

		while (bb.hasNext()) {
			o = bb.next();
			
	    	if (!o.update()) {
	    		bb.remove();
	    		continue;
	    	}
	    	
	    	// flickables entering the "capture zone"
	    	// offscreen to prevent excessive miss tallying
	    	if (o.misc == 1
	    		&& o.x > Gdx.graphics.getWidth() + o.radius * 2 
	    		&& o.y > Gdx.graphics.getHeight() / 2 - 50.0f
	    		&& o.y > Gdx.graphics.getHeight() / 2 - 50.0f) {
	    		
	    		misses++;
	    		parent.showMessage("Misses: " + misses + "!");	    		

	    		if (misses > 3) {
	    			lost = true;
	    		}
	    	}
	    	
	    	if (o.offscreen()) {
	    		o.kill();
	    	}
		}
	}
	
	@Override
	public boolean isFinished() {
		return misses < 3;
	}
	
	@Override
	public void dispose() {
		super.dispose();
		asteroidTx.dispose();
		Gdx.input.setInputProcessor(null);
	}
}
