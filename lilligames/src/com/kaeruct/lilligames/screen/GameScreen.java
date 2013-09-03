package com.kaeruct.lilligames.screen;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;
import com.kaeruct.lilligames.LilliGame;
import com.kaeruct.lilligames.games.MicroGame;

public class GameScreen extends Screen {
	
	public MicroGame mg;
	protected String message;
	public SpriteBatch batch;
	public BitmapFont font;
	protected int currentMg;
	protected ArrayList<String> shuffledMicrogames;
	private Task hideMessageTask; 
	
	final static int DEFAULT_MESSAGE_TIME = 2;
	final static ArrayList<String> MICROGAMES = new ArrayList<String>(Arrays.asList(
			"FillGrid",
			"BubblePop",
			"AsteroidDodge"
	));
	
	public GameScreen(LilliGame gm) {
		super(gm);
		batch = new SpriteBatch();
		font = new BitmapFont(Gdx.files.internal("data/default.fnt"), Gdx.files.internal("data/default.png"),false);
		currentMg = -1;
		
		final GameScreen parent = this;
		hideMessageTask = new Task(){
		    @Override
		    public void run() {
		        parent.hideMessage();
		    }
		};
	}

	@Override
	public void render (float delta) {
		Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		stage.act(delta);
		stage.draw();
		
		if (mg == null) {
			this.switchMicroGame();
		}
		
		mg.update(delta);
		mg.render();
		
		if (message != null) {
			float fontX = Gdx.graphics.getWidth()/2 - font.getBounds(message).width/2;
			float fontY = Gdx.graphics.getHeight()/2 + font.getBounds(message).height/2;
			
			batch.begin();
			font.draw(batch, message, fontX, fontY);
			batch.end();
		}
		
		if (mg.lost) {
			mg.dispose();
			mg = null;
			game.setScreen("GameOverScreen");
			return;
		}
		
		if (mg.timeLeft <= 0) {
			if (!mg.isFinished()) {
				mg.lost = true;
			} else if (!mg.lost) {
				this.switchMicroGame();
			}
		}
	}
	
	public void showMessage(String message) {
		showMessage(message, DEFAULT_MESSAGE_TIME);
	}
	
	public void showMessage(String message, float time) {
		if (hideMessageTask != null) {
			hideMessageTask.cancel();
		}
		
		this.message = message;
		Timer.schedule(hideMessageTask, time);
	}

	protected void hideMessage() {
		message = null;
	}

	protected void switchMicroGame() {
		MicroGame oldMg = mg;
		MicroGame newMg = randomMicroGame();
		
		mg = newMg;
		if (oldMg != null) oldMg.dispose();
	}
	
	@SuppressWarnings("unchecked")
	protected MicroGame randomMicroGame() {
		MicroGame game = null;
	
		if (currentMg == -1 || currentMg == MICROGAMES.size()) {
			shuffledMicrogames = (ArrayList<String>) MICROGAMES.clone();
			Collections.shuffle(shuffledMicrogames);
			currentMg = 0;
		}
		
		String name = shuffledMicrogames.get(currentMg++);

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
