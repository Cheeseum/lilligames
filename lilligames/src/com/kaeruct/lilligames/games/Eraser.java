package com.kaeruct.lilligames.games;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Array;
import com.kaeruct.lilligames.common.Particle;
import com.kaeruct.lilligames.screen.GameScreen;

public class Eraser extends MicroGame {
	
	Array<Particle> bubbles;
	Texture bubbleImage;
	Sound eraseSound;
	boolean points[][];
	int h, w;
	Color pencilColor;
	Texture pxTexture;
	int eraserRadius;
	
	public Eraser(GameScreen parent) {
		super(parent);
		
		h = Gdx.graphics.getHeight();
		w = Gdx.graphics.getWidth();
		
		eraseSound = Gdx.audio.newSound(Gdx.files.internal("data/pop.ogg"));
		pxTexture = new Texture(Gdx.files.internal("data/bubble.png"));
		
		points = new boolean[h][w];
		for (int i = 0; i < h; i += 1) {
			for (int j = 0; j < w; j += 1) {
				points[i][j] = false;
			}
		}

        pencilColor = Color.WHITE;
        bg = Color.BLACK;
        eraserRadius = 20;
        
		timeLeft = 20;
		parent.showMessage("ERASE!");
	}
	
	@Override
	public void onRender() {
		batch.setColor(pencilColor);

		for (int i = 0; i < h; i += 1) {
			for (int j = 0; j < w; j += 1) {
				if (points[i][j]) {
					batch.draw(pxTexture, j-1, i-1, 2f, 2f);
				}
			}
		}
	}

	@Override
	public void update(float delta) {
		if (Gdx.input.isTouched()) {
			touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
			camera.unproject(touchPos);
			
			int y0 = (int) touchPos.y,
				x0 = (int) touchPos.x;
			paint(x0, y0);
			int f = 1 - eraserRadius;
			int ddF_x = 1;
			int ddF_y = -2 * eraserRadius;
			int x = 0;
			int y = eraserRadius;
			
			for (int i = x0 - eraserRadius; i <= x0 + eraserRadius + x; i++) {
				paint(i, y0);
	        }
			
			while (x < y) {
				if (f >= 0) {
					y -= 1;
					ddF_y += 2;
					f += ddF_y;
				}
				x += 1;
				ddF_x += 2;
				f += ddF_x;
				for (int i = x0 - x; i <= x0 + x; i++) {
		            paint(i, y0 - y);
		            paint(i, y0 + y);
		        }
		        for (int i = x0 - y; i <= x0 + y; i++) {
		        	paint(i, y0 - x);
		        	paint(i, y0 + x);
		        }
			}
		}
	}
	
	protected void paint(int x, int y) {
		setPoint(x, y, true);
	}
	protected void erase(int x, int y) {
		setPoint(x, y, false);
	}
	protected void setPoint(int x, int y, boolean val) {
		if (x < 0 || x >= w) return;
		if (y < 0 || y >= h) return;
		points[y][x] = val;
	}

	@Override
	public boolean isFinished() {
		return false;
	}
	
	public void dispose() {
		super.dispose();
		pxTexture.dispose();
		eraseSound.dispose();
	}
}