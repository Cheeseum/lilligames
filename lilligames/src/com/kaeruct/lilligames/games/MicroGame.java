package com.kaeruct.lilligames.games;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;
import com.kaeruct.lilligames.screen.GameScreen;

public abstract class MicroGame {
	static final int MULTITOUCH_COUNT = 10;
	protected Vector3 touchPos;
	protected OrthographicCamera camera;
	protected SpriteBatch batch;
	protected Color bg;
	protected BitmapFont font;
	protected GameScreen parent;
	public boolean lost = false;
	public int timeLeft;
	public static int DEFAULT_TIME_LEFT = 10;
	
	public Task dtlTask;
	
	public MicroGame(GameScreen parent) {
		Texture.setEnforcePotImages(true);
		this.parent = parent;
		touchPos = new Vector3();
		camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		camera.position.set(Gdx.graphics.getWidth() / 2.0f, Gdx.graphics.getHeight() / 2.0f, 0);
		batch = parent.batch;
		font = parent.font;
		timeLeft = DEFAULT_TIME_LEFT;
		
		dtlTask = new Task(){
		    @Override
		    public void run() {
		        decreaseTimeLeft();
		    }
		};
		decreaseTimeLeft();
	}
	
	public final void render() {
		preRender();
		Gdx.gl.glClearColor(bg.r, bg.g, bg.b, bg.a);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		
		camera.update();
		batch.setProjectionMatrix(camera.combined);
		batch.begin();

		onRender();
		
		font.draw(batch, "Time left: "+timeLeft, 0, font.getLineHeight());
		batch.end();
		
		postRender();
	}
	
	public void preRender() {};
	public abstract void onRender();
	public void postRender() {};
	public abstract void update(float delta);
	
	public boolean isFinished() {
		return false;
	}
	
	public void dispose() {
		dtlTask.cancel();
	}
	
	protected void decreaseTimeLeft() {
		if (timeLeft >= 1) timeLeft -= 1;
		Timer.schedule(dtlTask, 1);
	}
	
	public Color randomDarkColor() {
		float r = MathUtils.random(0, 0.4f),
			  g = MathUtils.random(0, 0.4f),
			  b = MathUtils.random(0, 0.4f);
		return new Color(r, g, b, 1);
	}
	
	public Color randomNormalColor() {
		float r = MathUtils.random(0.2f, 0.6f),
			  g = MathUtils.random(0.2f, 0.6f),
			  b = MathUtils.random(0.2f, 0.6f);
		return new Color(r, g, b, 1);
	}
	
	public Color randomBrightColor() {
		float r = MathUtils.random(0.5f, 1),
			  g = MathUtils.random(0.5f, 1),
			  b = MathUtils.random(0.5f, 1);
		return new Color(r, g, b, 1);
	}
}
