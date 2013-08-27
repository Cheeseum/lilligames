package com.kaeruct.lilligames.games;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.kaeruct.lilligames.screen.GameScreen;

public abstract class MicroGame {
	static final int MULTITOUCH_COUNT = 10;
	Vector3 touchPos;
	OrthographicCamera camera;
	SpriteBatch batch;
	Color bg;
	BitmapFont font;
	
	public MicroGame(GameScreen parent) {
		touchPos = new Vector3();
		camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		camera.position.set(Gdx.graphics.getWidth() / 2.0f, Gdx.graphics.getHeight() / 2.0f, 0);
		batch = parent.batch;
		font = parent.font;
	}
	
	public void render() {
		Gdx.gl.glClearColor(bg.r, bg.g, bg.b, bg.a);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
	}
	public abstract void update(float delta);
	public abstract boolean isFinished();
	public abstract void dispose();
	
	public Color randomBrightColor() {
		float r = MathUtils.random(0.5f, 1),
			  g = MathUtils.random(0.5f, 1),
			  b = MathUtils.random(0.5f, 1);
		return new Color(r, g, b, 1);
	}
}
