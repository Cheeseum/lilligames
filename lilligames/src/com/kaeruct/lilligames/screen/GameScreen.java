package com.kaeruct.lilligames.screen;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;
import com.kaeruct.lilligames.LilliGame;
import com.kaeruct.lilligames.games.MicroGame;

public class GameScreen extends Screen {
	
	public MicroGame mg;
	protected String message;
	public SpriteBatch batch;
	public BitmapFont font;
	final static int DEFAULT_MESSAGE_TIME = 2;
	final static ArrayList<String> MICROGAMES = new ArrayList<String>(Arrays.asList(
		"BubbleGame"
	));
	
	public GameScreen(LilliGame gm) {
		super(gm);
		batch = new SpriteBatch();
		font = new BitmapFont(Gdx.files.internal("data/default.fnt"), Gdx.files.internal("data/default.png"),false);
		mg = randomMicroGame(null);
	}

	@Override
	public void render (float delta) {
		Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		stage.act(delta);
		stage.draw();
		
		mg.update(delta);
		mg.render();
		
		if (message != null) {
			float fontX = Gdx.graphics.getWidth()/2 - font.getBounds(message).width/2;
			float fontY = Gdx.graphics.getHeight()/2 + font.getBounds(message).height/2;
			
			batch.begin();
			font.draw(batch, message, fontX, fontY);
			batch.end();
		}
		
		if (mg.isFinished()) {
			this.switchMicroGame();
		}
	}
	
	public void showMessage(String message) {
		showMessage(message, DEFAULT_MESSAGE_TIME);
	}
	
	public void showMessage(String message, float time) {
		final GameScreen parent = this;
		this.message = message;
		Timer.schedule(new Task(){
		    @Override
		    public void run() {
		        parent.hideMessage();
		    }
		}, time);
	}

	protected void hideMessage() {
		this.message = null;
	}

	protected void switchMicroGame() {
		MicroGame oldMg = mg;
		MicroGame newMg = randomMicroGame(oldMg);
		
		mg = newMg;
		oldMg.dispose();
	}
	
	protected MicroGame randomMicroGame(MicroGame prev) {
		MicroGame game = null;
	
		@SuppressWarnings("unchecked")
		ArrayList<String> microgames = (ArrayList<String>) MICROGAMES.clone();
		
		if (prev != null) {
			String prevName = prev.getClass().getName();
			microgames.remove(prevName);
		}
		
		String name = microgames.get(MathUtils.random(0, microgames.size()-1));

		Class<?> screenClass;
		try {
			screenClass = Class.forName("com.kaeruct.lilligames.games."+name);
	        Constructor<?> constructor = screenClass.getConstructor(GameScreen.class);      
			game = (MicroGame) constructor.newInstance(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return game;
	}

	@Override
	public void dispose() {
		font.dispose();
		batch.dispose();
	}
}
