package com.aro.tdef;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool.PooledEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;


public class ParticleEffectHandler {
	
	private final int MAX_POOL_SIZE = 10;
	private final int INITIAL_POOL_SIZE = 5;
	
	private Array<PooledEffect> effects;
	
	private ParticleEffectPool explosionPool;
	private ParticleEffect explosion;
	
	
	public ParticleEffectHandler() {
		effects = new Array<PooledEffect>();
		
		loadEffects();
		initializePools();
	}
	
	private void loadEffects() {
		explosion = new ParticleEffect();
		explosion.load(Gdx.files.internal(Constants.ExplosionEffectAsset), Gdx.files.internal("sprites"));
	}
	
	private void initializePools() {
		explosionPool = new ParticleEffectPool(explosion, INITIAL_POOL_SIZE, MAX_POOL_SIZE);
	}
	
	
	public void explosion(float x, float y) {
		PooledEffect effect = explosionPool.obtain();
		effect.setPosition(x, y);
		effects.add(effect);
	}
	
	public void draw(SpriteBatch batch, float deltaTime) {
		for (int i = effects.size - 1; i >= 0; i--) {
		    PooledEffect effect = effects.get(i);
		    effect.draw(batch, deltaTime);
		    if (effect.isComplete()) {
		        effect.free();
		        effects.removeIndex(i);
		    }
		}
	}
	
	public void cleanAll() {
		for (int i = effects.size - 1; i >= 0; i--)
		    effects.get(i).free();
		effects.clear();
	}
	
	public void dispose() {
		for(int i = 0; i < effects.size; i++) {
			effects.get(i).dispose();
		}
	}
}
