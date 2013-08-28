package com.kaeruct.lilligames.games;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.kaeruct.lilligames.common.Grid;
import com.kaeruct.lilligames.common.Particle;
import com.kaeruct.lilligames.screen.GameScreen;

public class GridGame extends MicroGame {
	
	Texture rockTx, shipTx, borderTx, bgTx;
	TextureRegion rockImage, shipImage;
	Sound explodeSound;
	Color borderColor, gridBgColor;
	private int offx, offy;
	Grid<Particle> grid;
	
	public GridGame(GameScreen parent) {
		super(parent);
		borderColor = new Color(0.2f, 0.2f, 0.2f, 0.8f);
		gridBgColor = new Color(0.05f, 0.01f, 0.05f, 1f);
		bg = Color.BLACK;
		timeLeft = 999;
		
		int s = 48;
		int w = (int)(Gdx.graphics.getWidth() / s);
		int h = (int)(Gdx.graphics.getHeight() / s);
		offx = (Gdx.graphics.getWidth() % s) / 2;
		offy = (Gdx.graphics.getHeight() % s) / 2;
		
		grid = new Grid<Particle>(w, h, s);
		
		borderTx = new Texture(Gdx.files.internal("data/borderbox.png"));
		bgTx = new Texture(Gdx.files.internal("data/pixel.png"));
		
		rockTx = new Texture(Gdx.files.internal("data/potato.png"));
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
		batch.draw(bgTx, offx, offy, grid.pxw, grid.pxh);
		
		for (int y = 0; y < grid.h; y += 1) {
			for (int x = 0; x < grid.w; x += 1) {
				Particle o = grid.at(x, y);
				
				float px = offx + x*grid.s,
					  py = offy + (grid.h-1-y)*grid.s;
				
				batch.setColor(borderColor);
				batch.draw(borderTx, px, py, grid.s, grid.s);
				
				if (o != null) {
					batch.setColor(o.color);
					batch.draw(rockImage,
							px, py,
							grid.s/2, grid.s/2,
							grid.s, grid.s,
							1.0f, 1.0f, o.rotation);
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
		
		for (int y = 0; y < grid.h; y += 1) {
			for (int x = 0; x < grid.w; x += 1) {
				Particle o = grid.at(x, y);
				
				if (o != null) {
					o.update();
					o.misc += (int)(delta*100);
					if (o.misc%50 == 0) {
						int nx = x,
							ny = y;
							
						if (MathUtils.randomBoolean()) nx += (MathUtils.randomBoolean() ? 1 : -1);
						if (MathUtils.randomBoolean()) ny += (MathUtils.randomBoolean() ? 1 : -1);
						
						nx = grid.fixXCoord(nx);
						ny = grid.fixYCoord(ny);
						
						grid.moveFromTo(x, y, nx, ny);
					}
				}
			}
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
