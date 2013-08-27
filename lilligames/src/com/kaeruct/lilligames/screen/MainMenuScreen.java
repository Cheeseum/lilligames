package com.kaeruct.lilligames.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Scaling;
import com.kaeruct.lilligames.LilliGame;

public class MainMenuScreen extends MenuScreen {
	private final Texture logoTex;
	
	public MainMenuScreen (LilliGame gm) {
		super(gm);
		logoTex = new Texture(Gdx.files.internal("data/logo.png"));
		Image logo = new Image(logoTex);
		logo.setScaling(Scaling.fit);
		add(logo);
		
		TextButton t = new TextButton("Start!", skin);
		t.addListener(new ClickListener() {
		    public void clicked(InputEvent event, float x, float y) {
		    	game.setScreen("GameScreen");
		    }
		});
		t.pad(5, 10, 5, 10);
		add(t).expandY();

		Label cpright = new Label("Copyright KaeruCT 2013 - All rights reserved", skin);
		cpright.setAlignment(Align.center);
		cpright.setColor(1f, 1f, .8f, 1f);
		add(cpright).pad(10);
	}
	
	@Override
	public void show() {
		// set up input
		InputMultiplexer im = new InputMultiplexer(stage);
		Gdx.input.setInputProcessor(im);
	}
	
	@Override
	public void dispose() {
		logoTex.dispose();
	}
	
	@Override
	public void onKeyUp(int keycode) {
		if (keycode == Keys.BACK) {
			Gdx.app.exit();
		}
	}
}
