package com.kaeruct.lilligames.games;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.kaeruct.lilligames.common.Grid;
import com.kaeruct.lilligames.common.Particle;
import com.kaeruct.lilligames.screen.GameScreen;

public class FillGrid extends MicroGame {
	
	Texture fillTx, borderTx, bgTx;
	Sound splatSound;
	Color borderColor, gridBgColor, fillColor;
	private int offx, offy;
	Grid<Particle> grid;
	
	public FillGrid(GameScreen parent) {
		super(parent);
		borderColor = new Color(0.2f, 0.2f, 0.2f, 0.8f);
		gridBgColor = new Color(0.05f, 0.01f, 0.05f, 1f);
		fillColor = randomNormalColor();
		bg = Color.BLACK;
		
		int s = 64;
		int w = (int)(Gdx.graphics.getWidth() / s);
		int h = (int)(Gdx.graphics.getHeight() / s);
		offx = (Gdx.graphics.getWidth() % s) / 2;
		offy = (Gdx.graphics.getHeight() % s) / 2;
		
		timeLeft = (int) Math.ceil(w*h/15);
		
		grid = new Grid<Particle>(w, h, s);
		
		borderTx = new Texture(Gdx.files.internal("data/borderbox.png"));
		bgTx = new Texture(Gdx.files.internal("data/pixel.png"));
		
		fillTx = new Texture(Gdx.files.internal("data/pixel.png"));
		splatSound = Gdx.audio.newSound(Gdx.files.internal("data/pop.ogg"));
		
		parent.showMessage("FILL THE GRID!");
	}

	@Override
	public void onRender() {
		batch.setColor(gridBgColor);
		batch.draw(bgTx, offx, offy, grid.pxw, grid.pxh);
		
		for (int y = 0; y < grid.h; y += 1) {
			for (int x = 0; x < grid.w; x += 1) {
				Particle o = grid.at(x, y);
				
				float px = offx + x*grid.s,
					  py = offy + (grid.h-1-y)*grid.s;
				
				batch.setColor(borderColor);
				batch.draw(borderTx, px, py, grid.s, grid.s);
				
				if (o != null) {
					float r2 = o.radius*2,
						  gs2 = grid.s/2;
					
					batch.setColor(fillColor);
					batch.draw(fillTx,
							px-o.radius+gs2, py-o.radius+gs2,
							r2, r2);
				}
			}
		}
	}

	@Override
	public void update(float delta) {
		for (int i = 0; i < MULTITOUCH_COUNT; i++) {
			if (Gdx.input.isTouched(i) && Gdx.input.justTouched()) {
				touchPos.set(Gdx.input.getX(i), Gdx.input.getY(i), 0);
				camera.unproject(touchPos);
				int cx, cy;
	
				cx = ((int) (touchPos.x - offx) / grid.s);
				cy = grid.h - 1 - ((int) (touchPos.y - offy) / grid.s);
				
				cx = grid.fixXCoord(cx);
				cy = grid.fixYCoord(cy);
				
				grid.addAt(new Particle(), cx, cy);
			}
		}
		
		for (int y = 0; y < grid.h; y += 1) { for (int x = 0; x < grid.w; x += 1) {
			Particle o = grid.at(x, y);
			
			if (o != null) {
				o.misc += (int)(delta*100);
				if (o.radius+0.5 <= grid.s/2) o.radius += 0.5;
				
				if (o.misc%25 == 0) {
					int[] c = {x+1, y, x-1, y, x, y+1, x, y-1};
					
					for (int i = 0; i < c.length; i += 2) {
						if (!grid.isOcuppied(c[i], c[i+1])) {
							grid.addAt(new Particle(), c[i], c[i+1]);
						}
					}
				}
			}
		}}
	}
	
	@Override
	public boolean isFinished() {
		return grid.isFull();
	}
	public void dispose() {
		super.dispose();
		splatSound.dispose();
		borderTx.dispose();
		bgTx.dispose();
		fillTx.dispose();
	}
}
