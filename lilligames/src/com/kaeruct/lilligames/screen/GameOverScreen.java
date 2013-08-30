package com.kaeruct.lilligames.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Scaling;
import com.kaeruct.lilligames.LilliGame;

public class GameOverScreen extends MenuScreen {
	private final Texture gameOverTex;
	
	public GameOverScreen (LilliGame gm) {
		super(gm);
		gameOverTex = new Texture(Gdx.files.internal("data/gameover.png"));
		Image logo = new Image(gameOverTex);
		logo.setScaling(Scaling.fit);
		add(logo);
		
		TextButton t = new TextButton("Try Again", skin);
		t.addListener(new ClickListener() {
		    public void clicked(InputEvent event, float x, float y) {
		    	game.setScreen("GameScreen");
		    }
		});
		t.pad(5, 10, 5, 10);
		add(t).expandY();
		
		t.row();
		
		TextButton t2 = new TextButton("Go back to Main Menu", skin);
		t2.addListener(new ClickListener() {
		    public void clicked(InputEvent event, float x, float y) {
		    	game.setScreen("MainMenuScreen");
		    }
		});
		t2.pad(5, 10, 5, 10);
		add(t2).expandY();
	}
	
	@Override
	public void show() {
		// set up input
		InputMultiplexer im = new InputMultiplexer(stage);
		Gdx.input.setInputProcessor(im);
	}
	
	@Override
	public void dispose() {
		gameOverTex.dispose();
	}
	
	@Override
	public void onKeyUp(int keycode) {
		if (keycode == Keys.BACK) {
			Gdx.app.exit();
		}
	}
}
