package com.aro.tdef.Entities;

import com.aro.tdef.Entities.Projectiles.Projectile;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;

public class TowerBarrel extends Entity {
	private static Texture spriteSheet;
	private int[] projectileCode;
	private Vector2 shootPoint = new Vector2();
	
	private EntityAnimation idleAnimation;
	private EntityAnimation shootingAnimation;
	
	public Projectile getProjectile(Vector2 facingPoint) {			
		return Projectile.getProjectile(projectileCode);
	}
	
	/**
	 * Returns the spawning point for projectiles.
	 */
	public Vector2 getShootingPoint() {
		int radius = currentAnimation.getFrameWidth();
		double rads = Math.toRadians(rotation);
		this.shootPoint.x = position.x + (float)Math.cos(rads) * radius;
		this.shootPoint.y = position.y + (float)Math.sin(rads) * radius;
		return shootPoint;
	}
	
	public void setProjectile(int[] code) {
		this.projectileCode = code;
	}
	
	public static void loadSpriteSheet(String path) {
		spriteSheet = new Texture(Gdx.files.internal(path));
	}
	
	public static void disposeSpriteSheet() {
		spriteSheet.dispose();
	}
	
	public TowerBarrel(Vector2 position, float rotation) {
		initialize();
		currentAnimation.update(0f);
		
		this.setOrigin(0, currentAnimation.getFrameHeight() / 2);
		
		this.position = new Vector2(position.x, position.y - this.getOrigin().y);
		this.rotation = rotation;
	}
	
	public void update(float deltaTime, float towerRotation) {
		this.rotation = towerRotation;
		handleAnimations();
		
		currentAnimation.update(deltaTime);
	}
	
	public void playShootingAnimation() {
		shootingAnimation.update(0f);
		shootingAnimation.resetAnimation();
		currentAnimation = shootingAnimation;
	}
	
	private void initialize() {
		idleAnimation = new EntityAnimation(spriteSheet, 0.025f, true, 32, 8, 0, 1, 0);
		shootingAnimation = new EntityAnimation(spriteSheet, 0.025f, false, 32, 8, 0, 1, 0);
		this.scaleX = 1f;
		this.scaleY = 1f;

		currentAnimation = idleAnimation;
	}
	
	private void handleAnimations() {
		if(currentAnimation == shootingAnimation) {
			if(shootingAnimation.isAnimationFinished()) {
				currentAnimation = idleAnimation;
			}
		}
	}
}
