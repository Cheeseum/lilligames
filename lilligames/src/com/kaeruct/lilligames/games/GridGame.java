package com.kaeruct.lilligames.games;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.kaeruct.lilligames.common.Particle;
import com.kaeruct.lilligames.screen.GameScreen;

public class GridGame extends MicroGame {
	
	Texture rockTx, shipTx, borderTx, bgTx;
	TextureRegion rockImage, shipImage;
	Sound explodeSound;
	Particle[][] objects;
	Color borderColor, gridBgColor;
	private int w, h;
	int gsize;
	
	public GridGame(GameScreen parent) {
		super(parent);
		
		gsize = 64;
		borderColor = new Color(0.9f, 0.2f, 0.2f, 0.8f);
		gridBgColor = new Color(0.5f, 0.1f, 0.1f, 1f);
		bg = Color.BLACK;
		timeLeft = 999;
		
		w = (int)(Gdx.graphics.getWidth() / gsize);
		h = (int)(Gdx.graphics.getHeight() / gsize);
		objects = new Particle[h][w];
		
		borderTx = new Texture(Gdx.files.internal("data/borderbox.png"));
		bgTx = new Texture(Gdx.files.internal("data/pixel.png"));
		
		rockTx = new Texture(Gdx.files.internal("data/bubble.png"));
		rockTx.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
		rockImage = new TextureRegion(rockTx, 0, 0,
				rockTx.getWidth(), rockTx.getHeight());
		
		shipTx = new Texture(Gdx.files.internal("data/ship.png"));
		shipTx.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
		shipImage = new TextureRegion(shipTx, 0, 0,
				 shipTx.getWidth(),  shipTx.getHeight());
		
		explodeSound = Gdx.audio.newSound(Gdx.files.internal("data/pop.ogg"));
		
		parent.showMessage("");
	}

	@Override
	public void onRender() {
		batch.setColor(gridBgColor);
		batch.draw(bgTx, 0, 0, w*gsize, h*gsize);
		
		for (int y = 0; y < h; y += 1) {
			for (int x = 0; x < w; x += 1) {
				Particle o = objects[y][x];
				
				float px = x*gsize,
					  py = (h-1-y)*gsize;
				
				batch.setColor(borderColor);
				batch.draw(borderTx, px, py, gsize, gsize);
				
				if (o != null) {
					batch.setColor(o.color);
					batch.draw(rockImage,
							px, py,
							gsize, gsize,
							gsize, gsize,
							1.0f, 1.0f, o.rotation);
				}
			}
		}
	}

	@Override
	public void update(float delta) {
		if (Gdx.input.isTouched() && Gdx.input.justTouched()) {
			touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
			camera.unproject(touchPos);
			int cx, cy;

			cx = ((int) touchPos.x / gsize);
			cy = h - 1 - ((int) touchPos.y / gsize);
			
			if (cx < 0) cx = 0;
			if (cy < 0) cy = 0;
			if (cx >= w) cx = w-1;
			if (cy >= h) cy = h-1;
			
			if (!isOcuppied(cx, cy)) {
				addAt(new Particle(), cx, cy);
			}
		}
	   
	}
	
	private void addAt(Particle particle, int cx, int cy) {
		try {
			objects[cy][cx] = new Particle();
		} catch (ArrayIndexOutOfBoundsException e) {
		}
	}

	public boolean isOcuppied(int x, int y) {
		try {
			return objects[y][x] != null;
		} catch (ArrayIndexOutOfBoundsException e) {
			return true;
		}
	}
	
	@Override
	public boolean isFinished() {
		return false;
	}
	public void dispose() {
		super.dispose();
		explodeSound.dispose();
		borderTx.dispose();
		bgTx.dispose();
		rockTx.dispose();
		shipTx.dispose();
	}
}
