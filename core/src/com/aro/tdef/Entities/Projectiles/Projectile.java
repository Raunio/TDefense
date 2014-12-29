package com.aro.tdef.Entities.Projectiles;

import java.util.Random;

import com.aro.tdef.Constants;
import com.aro.tdef.Entities.Entity;
import com.aro.tdef.Entities.EntityAnimation;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;

/**
 * @author Niko
 *
 */
public class Projectile extends Entity {
	protected static Texture spriteSheet;
	
	protected int minDamage;
	protected int maxDamage;
	protected int area;
	
	protected int minReleases;
	protected int maxReleases;
	protected float releaseRange;
	
	protected float rayThickness;
	protected float rayLifeTime;
	protected boolean hasDamaged;
	
	protected Array<Projectile> releases = new Array<Projectile>();
	
	protected int[] releaseProjectileCode;
	
	protected Random r = new Random();
	
	protected Constants.ReleaseProjectileType releaseType;
	protected Constants.ProjectileType projectileType;
	
	protected float timer;
	protected float disableTime;
	
	/**
	 * Returns the area of effect of the projectile.
	 */
	public int getArea() {
		return area;
	}
	
	public boolean hasDamaged() {
		return hasDamaged;
	}
	
	public void setDamaged(boolean value) {
		hasDamaged = value;
	}
	
	public Constants.ReleaseProjectileType getReleaseType() {
		return releaseType;
	}
	
	public int getMinDamage() {
		return minDamage;
	}
	
	public int getMaxDamage() {
		return maxDamage;
	}
	
	public float getReleaseRange() {
		return releaseRange;
	}
	
	public Constants.ProjectileType getProjectileType() {
		return projectileType;
	}
	
	public boolean isDisabled() {
		if(timer < disableTime || getCurrentEntityState() == Constants.EntityState.Disabled) return true;
		
		return false;
	}
	
	private int getReleaseAmount() {
		return Constants.getRandomInteger(minReleases, maxReleases, r);
	}
	
	public Array<Projectile> getTargetedReleases(Array<Vector2> targets) {
		for(int i = 0; i < targets.size; i++) {
			releases.add(getProjectile(releaseProjectileCode, this.position, targets.get(i)));
			releases.peek().disableTime = 0.1f;
		}
		
		return releases;
	}
	
	public Array<Projectile> getRandomReleases() {
		for(int i = 0; i < getReleaseAmount(); i++) {
			releases.add(new Projectile(releaseProjectileCode, this.position));
			releases.peek().disableTime = 0.1f;
		}
		
		return releases;
	}
	
	public void disable(float amount) {
		this.disableTime = amount;
		this.timer = 0;
	}
	
	/**
	 * Load the sprite sheet for all projectiles.
	 */
	public static void loadSpriteSheet(String path) {
		spriteSheet = new Texture(Gdx.files.internal(path));
	}
	
	public static void disposeSpriteSheet() {
		spriteSheet.dispose();
	}
	
	public Projectile() {}
	
	public Projectile(int[] code) {
		initialize(code);

		if(releaseRange != 0)
			initReleaseProjectile(code);
	}
	
	public Projectile(int[] code, Vector2 position, float rotation) {
		initialize(code);
		currentAnimation.update(0f);
		this.position = new Vector2(position.x, position.y);
		this.rotation = rotation;
		this.setVelocity((float)Math.cos(Math.toRadians(rotation)) * this.tangentialVelocityMax, 
    			(float)Math.sin(Math.toRadians(rotation)) * this.tangentialVelocityMax);
	}
	
	public Projectile(int[] code, Vector2 position, Vector2 targetPosition) {
		initialize(code);
		currentAnimation.update(0f);
		this.position = new Vector2(position.x, position.y);
		this.setFacingPoint(targetPosition.x, targetPosition.y);
		this.updateRotation();
		this.rotationDirection = Constants.RotationDirection.None;
		this.rotation = this.targetRotation;
		this.setVelocity((float)Math.cos(Math.toRadians(rotation)) * this.tangentialVelocityMax, 
    			(float)Math.sin(Math.toRadians(rotation)) * this.tangentialVelocityMax);
	}
	
	public Projectile(int[] code, Vector2 position) {
		initialize(code);
		currentAnimation.update(0f);
		this.position = new Vector2(position.x, position.y);
		this.rotation = Constants.getRandomFloat(0f, 360f, r);
		this.setVelocity((float)Math.cos(Math.toRadians(rotation)) * this.tangentialVelocityMax, 
    			(float)Math.sin(Math.toRadians(rotation)) * this.tangentialVelocityMax);
	}
	
	public void reset(Vector2 position, Vector2 targetPosition) {
		this.position.x = position.x;
		this.position.y = position.y;
		
		this.setEntityState(Constants.EntityState.Stopped);
		this.hasDamaged = false;
		
		this.setFacingPoint(targetPosition.x, targetPosition.y);
		this.updateRotation();
		this.rotationDirection = Constants.RotationDirection.None;
		this.rotation = this.targetRotation;
		this.setVelocity((float)Math.cos(Math.toRadians(rotation)) * this.tangentialVelocityMax, 
    			(float)Math.sin(Math.toRadians(rotation)) * this.tangentialVelocityMax);
	}
	
	public static Projectile getProjectile(int[] code, Vector2 position, float rotation) {
		return new Projectile(code, position, rotation);
	}
	
	public static Projectile getProjectile(int[] code, Vector2 position, Vector2 targetPosition) {
		if(code == Constants.aoelightningCode) return new LightningBolt(code, position, targetPosition);
		else if(code == Constants.slightningCode) return new LightningBolt(code, position, targetPosition);
		else return new Projectile(code, position, targetPosition);
	}
	
	public static Projectile getProjectile(int[] code, Vector2 position) {
		return new Projectile(code, position);
	}
	
	public static Projectile getProjectile(int[] code) {
		if(code == Constants.aoelightningCode) return new LightningBolt(code);
		else return new Projectile(code);
	}
	
	public Projectile getReleaseProjectile() {
		if(releaseProjectileCode == Constants.slightningCode) return new LightningBolt(releaseProjectileCode);
		else return new Projectile(releaseProjectileCode);
	}
	
	private void initReleaseProjectile(int[] code) {
		if(code == Constants.chainboltCode) {
			this.releaseProjectileCode = Constants.fireboltCode;
		}
		else if(code == Constants.aoelightningCode) {
			this.releaseProjectileCode = Constants.slightningCode;
		}
	}
	
	public void update(float deltaTime) {
		currentAnimation.update(deltaTime);
		
		if(this.projectileType == Constants.ProjectileType.Ballistic)
			this.applyVelocities();
		
		timer += deltaTime;
	}
	
	private void initialize(int[] code) {
		
		initStats(code);
		initAnimations(code);
		
		if(releaseRange != 0)
			initReleaseProjectile(code);
	}
	
	private void initStats(int[] code) {
		if(code == Constants.fireboltCode) {
			tangentialVelocityMax = 5;
			minDamage = 2;
			maxDamage = 4;
			area = 1;
			scaleX = 1.3f;
			scaleY = 1.3f;
			releaseType = Constants.ReleaseProjectileType.None;
			projectileType = Constants.ProjectileType.Ballistic;
		}
		else if(code == Constants.chainboltCode) {
			releaseRange = 400f;
			releaseType = Constants.ReleaseProjectileType.Point;
			projectileType = Constants.ProjectileType.Ballistic;
			minReleases = 1;
			maxReleases = 1;
			tangentialVelocityMax = 6;
			minDamage = 2;
			maxDamage = 4;
			area = 1;
			scaleX = 1.3f;
			scaleY = 1.3f;
		}
		else if(code == Constants.aoelightningCode) {
			releaseRange = 200f;
			releaseType = Constants.ReleaseProjectileType.Point;
			projectileType = Constants.ProjectileType.Instant;
			minReleases = 1;
			maxReleases = 1;
			minDamage = 3;
			maxDamage = 7;
			rayThickness = 2;
			rayLifeTime = 0.3f;
		}
		else if(code == Constants.slightningCode) {
			releaseRange = 0f;
			releaseType = Constants.ReleaseProjectileType.None;
			projectileType = Constants.ProjectileType.Instant;
			minDamage = 1;
			maxDamage = 3;
			rayThickness = 1;
			rayLifeTime = 0.15f;
		}
	}
	
	private void initAnimations(int[] code) {
		currentAnimation = new EntityAnimation(spriteSheet, 0.025f, true, 16, 8, 0, 1, 0);
	}

}
