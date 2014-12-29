package com.aro.tdef;

import com.aro.tdef.Levels.GameLevel;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class TdefImpl extends ApplicationAdapter {
	private OrthographicCamera camera;
	private SpriteBatch batch;
	
	private GameLevel level;
	
	@Override
	public void create () {
		float w = Gdx.graphics.getWidth();
		float h = Gdx.graphics.getHeight();
		
		camera = new OrthographicCamera(w, h);
		camera.position.set((w / 2), (h / 2), 0f);
		//camera.zoom = 1.5f;
		batch = new SpriteBatch();
		
		level = new GameLevel();
		level.create(camera);
	}
	
	@Override
	public void dispose() {
		batch.dispose();
		level.dispose();
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		camera.update();

		level.update(Gdx.graphics.getDeltaTime());
		level.handleInput(Gdx.input, camera.zoom);
		batch.setProjectionMatrix(camera.combined);
			
		level.render(batch, Gdx.graphics.getDeltaTime());
		
		level.renderUI();
	}
	
	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}
}
