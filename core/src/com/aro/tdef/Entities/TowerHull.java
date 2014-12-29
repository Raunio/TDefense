package com.aro.tdef.Entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;

public class TowerHull extends Entity{
	private float mass;
	private float maxLoad;
	
	private EntityAnimation idleAnimation;
	private EntityAnimation shootingAnimation;
	
	/**
	 * Returns the mass of the hull.
	 */
	public float getMass() {
		return mass;
	}
	
	/**
	 * Returns the maximum load of the hull.
	 */
	public float getMaxLoad() {
		return maxLoad;
	}
	
	public TowerHull(Texture texture, Vector2 position, float rotation, int width, int height, float mass) {
		this.position = new Vector2(position.x - width / 2, position.y - height / 2);
		this.rotation = rotation;
		this.mass = mass;
		
		initializeAnimations(texture);
		
		currentAnimation.update(0f);
	}
	
	public void update(float deltaTime, float towerRotation) {
		currentAnimation.update(deltaTime);
		this.rotation = towerRotation;
		
		if(currentAnimation == shootingAnimation) {
			if(shootingAnimation.isAnimationFinished()) {
				currentAnimation = idleAnimation;
			}
		}
	}
	
	public void playShootingAnimation() {
		shootingAnimation.resetAnimation();
		shootingAnimation.update(0f);
		currentAnimation = shootingAnimation;
	}
	
	private void initializeAnimations(Texture spriteSheet) {
		idleAnimation = new EntityAnimation(spriteSheet, 0.025f, true, 32, 32, 0, 1, 0);
		shootingAnimation = new EntityAnimation(spriteSheet, 0.025f, false, 32, 32, 0, 1, 0);
		
		currentAnimation = idleAnimation;
	}
}
