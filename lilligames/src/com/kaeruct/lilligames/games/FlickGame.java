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
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import com.kaeruct.lilligames.common.Particle;
import com.kaeruct.lilligames.screen.GameScreen;

public class FlickGame extends MicroGame {
	Array<Particle> objects;
	
	Texture asteroidTx;
	TextureRegion asteroidImage;
	
	Vector2 lastTouchPos;
	int spawnInterval = 1000000000;
	long lastTime = 0;
	
	class InputHandler extends InputAdapter {
		@Override
		public boolean touchDragged (int screenX, int screenY, int pointer) {
			Vector2 touchPos = new Vector2(screenX, screenY);
			Vector2 center = new Vector2();
			
			for (Particle o : objects) {
				center.set(o.x, o.y);
				if (Intersector.intersectSegmentCircle(lastTouchPos, touchPos, center, o.radius * o.radius)) {
					Vector2 penetration = center.sub(touchPos);
					o.dx += penetration.x * 0.2;
					o.dy += penetration.y * 0.2;
					break;
				}
			}
			
			lastTouchPos.set(touchPos);
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
		lastTouchPos = new Vector2();
		
		Gdx.input.setInputProcessor(new InputHandler());
	}
	
	private void spawnObject (boolean shouldFlick) {
		Particle p = new Particle();
		p.x = -10.0f;
		p.y = Gdx.graphics.getHeight() / 2.0f;
		p.radius = 50.0f;
		p.dx += 3.5f;
		p.misc = shouldFlick ? 1 : 0;
		objects.add(p);
	}
	
	@Override
	public void onRender() {
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
		if (t - lastTime > spawnInterval) {
			lastTime = t;
			spawnObject(MathUtils.random(0, 10) < 7);
		}
		
		Iterator<Particle> bb = objects.iterator(); 
		Particle o;

		while (bb.hasNext()) {
			o = bb.next();
			
	    	if (!o.update()) {
	    		bb.remove();
	    		continue;
	    	}
	    	
	    	if (o.x > Gdx.graphics.getWidth()) {
	    		o.kill();
	    	}
		}
	}

}
