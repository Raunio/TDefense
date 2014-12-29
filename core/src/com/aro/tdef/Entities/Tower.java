package com.aro.tdef.Entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.aro.tdef.Constants;
import com.aro.tdef.Entities.Projectiles.Projectile;

/**
 * @author Niko
 *
 */
public class Tower extends Entity {
	private float attackSpeed;
	
	private Array<Projectile> activeProjectiles;
	private Array<Projectile> activeReleaseProjectiles;
	
	private Pool<Projectile> projectilePool;
	private Pool<Projectile> releasePool;
	
	private EntityAnimation idleAnimation;
	private EntityAnimation shootingAnimation;
	
	private Constants.TowerState currentTowerState;
	
	private float shootTimer;
	
	private TowerBarrel mainBarrel;
	private TowerHull hull;

	private static Texture spriteSheet;
	
	private TowerBehaviour behaviour;
	
	private Projectile projectileInfo;

	
	/**
	 * Returns the attack range of the tower in pixels.
	 */
	public float getAttackRange() {
		return 150f;
	}
	
	public Vector2 getFacingPoint() {
		return this.facingPoint;
	}
	
	public Projectile getProjectileInfo() {
		return projectileInfo;
	}
	
	public Enemy getTarget() {
		return behaviour.getTarget();
	}
	
	public Pool<Projectile> getProjectilePool() {
		return projectilePool;
	}
	
	public Array<Projectile> getActiveProjectiles() {
		return activeProjectiles;
	}
	
	public Tower(int[] code, Vector2 position) {	
		initializeAnimations();
		
		this.position = new Vector2(position.x, position.y);
		
		rotationDirection = Constants.RotationDirection.None;
		currentEntityState = Constants.EntityState.Stopped;
		currentTowerState = Constants.TowerState.Idle;
		
		behaviour = new TowerBehaviour();
		
		currentAnimation.update(0f);
		
		activeProjectiles = new Array<Projectile>();
		activeReleaseProjectiles = new Array<Projectile>();
		
		mainBarrel = new TowerBarrel(new Vector2(this.position.x, this.position.y),
				this.rotation);
		
		mainBarrel.setProjectile(code);
		
		projectileInfo = new Projectile(code, Vector2.Zero, 0f);
		
		hull = new TowerHull(spriteSheet, this.position, rotation, 32, 32, 100);
		hull.setScale(1f, 1f);

		this.attackSpeed = 0.6f;
		
		initializeStats();
		
		projectilePool = new Pool<Projectile>() {
			@Override
			protected Projectile newObject() {
				return mainBarrel.getProjectile(getFacingPoint());
			}
		};
		
		if(projectileInfo.getReleaseType() != Constants.ReleaseProjectileType.None) {
			releasePool = new Pool<Projectile>() {
				@Override
				protected Projectile newObject() {
					return projectileInfo.getReleaseProjectile();
				}
			};
		}
	}
	
	public static void loadSpriteSheet(String path) {
		spriteSheet = new Texture(Gdx.files.internal(path));
	}
	
	public static void disposeSpriteSheet() {
		spriteSheet.dispose();
	}
	
	private void initializeAnimations() {
		idleAnimation = new EntityAnimation(spriteSheet, 0.025f, true, 32, 32, 0, 1, 0);
		shootingAnimation = new EntityAnimation(spriteSheet, 0.025f, false, 32, 32, 0, 1, 0);
		
		currentAnimation = idleAnimation;
	}
	
	private void initializeStats() {
		rotationMaxSpeed = 3f;
		rotationAcceleration = 0.5f;
	}
	
	/**
	 * Main update method.
	 */
	public void update(float deltaTime, Array<Enemy> enemies){
		handleAnimations();
		currentAnimation.update(deltaTime);
		updateRotation();
		shootTimer += deltaTime;
		
		mainBarrel.update(deltaTime, rotation);
		hull.update(deltaTime, rotation);
		
		behaviour.apply(this, enemies);
	}
	
	public void drawPointer(Texture t) {
		
	}
	
	/**
	 * Shoots a projectile if the turrets attack is ready.
	 */
	public void shoot() {
		if(isAttackReady()) {
			//activeProjectiles.add(mainBarrel.getProjectile(this.facingPoint));
			Projectile p = projectilePool.obtain();
			p.reset(mainBarrel.getShootingPoint(), this.facingPoint);
			activeProjectiles.add(p);
			mainBarrel.playShootingAnimation();
			hull.playShootingAnimation();
			shootTimer = 0;
		}
	}
	
	public void addTargetedReleases(Vector2 position, Array<Vector2> targets) {
		for(int i = 0; i < targets.size; i++) {
			Projectile r = releasePool.obtain();
			r.reset(position, targets.get(i));
			this.activeReleaseProjectiles.add(r);
		}
	}
	
	/**
	 * Updates all projectiles shot by the turret.
	 */
	public void updateProjectiles(float deltaTime) {	
		int len = activeProjectiles.size;
		int rlen = activeReleaseProjectiles.size;
		for(int i = len; --i >= 0;) {
			Projectile p = activeProjectiles.get(i);
			if(p.getCurrentEntityState() == Constants.EntityState.Dead) {
				activeProjectiles.removeIndex(i);
				projectilePool.free(p);
			}
			else p.update(deltaTime);
		}
		for(int i = rlen; --i >= 0;) {
			Projectile p = activeReleaseProjectiles.get(i);
			if(p.getCurrentEntityState() == Constants.EntityState.Dead) {
				activeReleaseProjectiles.removeIndex(i);
				releasePool.free(p);
			}
			else p.update(deltaTime);
		}
	}
	
	/**
	 * Draw all active projectiels shot by the turret.
	 */
	private void drawProjectiles(SpriteBatch batch) {
		for(int i = 0; i < activeReleaseProjectiles.size; i++) {
			activeReleaseProjectiles.get(i).draw(batch);
		}
		for(int i = 0; i < activeProjectiles.size; i++) {
			if(activeProjectiles.get(i).getCurrentEntityState() == Constants.EntityState.Dead) continue;
			activeProjectiles.get(i).draw(batch);
		}
		
	}
	
	private void drawBarrels(SpriteBatch batch) {
		mainBarrel.draw(batch);
	}
	
	private void drawHull(SpriteBatch batch) {
		hull.draw(batch);
	}
	
	public void drawTargetLine(ShapeRenderer sr, OrthographicCamera camera) {
		Vector3 prj = camera.project(new Vector3(this.getPosition().x, this.getPosition().y, 0));
		float x = prj.x;
		float y = prj.y;
		
		float rad = (float)Math.toRadians(-rotation);
		
		float x2 = x + getAttackRange() * (float)Math.sin(rad + Math.PI / 2);
    	float y2 = y + getAttackRange() * (float)Math.cos(rad + Math.PI / 2);
    	
    	
		sr.begin(ShapeType.Line);
		
		sr.line(x, y, x2, y2, Color.CYAN, Color.YELLOW);
		
		sr.end();
	}
	
	public void drawAttackRange(ShapeRenderer sr, OrthographicCamera camera) {
		Vector3 prj = camera.project(new Vector3(this.position.x, this.position.y, 0));
		
		sr.begin(ShapeType.Line);
		
		sr.circle(prj.x, prj.y, this.getAttackRange());
		
		sr.end();
	}
	
	@Override
	public void draw(SpriteBatch batch) {		
		drawProjectiles(batch);
		drawBarrels(batch);
		drawHull(batch);
	}
	
	/**
	 * Checks if any projectiles have left the game area and cleans them off.
	 */
	public void killProjectiles(float levelWidth, float levelHeight) {
		for(int i = 0; i < activeProjectiles.size; i++) {
			if(activeProjectiles.get(i).position.x < 0)
				activeProjectiles.get(i).setEntityState(Constants.EntityState.Dead);
			else if(activeProjectiles.get(i).position.y < 0) 
				activeProjectiles.get(i).setEntityState(Constants.EntityState.Dead);
			else if(activeProjectiles.get(i).position.x > levelWidth)
				activeProjectiles.get(i).setEntityState(Constants.EntityState.Dead);
			else if(activeProjectiles.get(i).position.y > levelHeight) {
				activeProjectiles.get(i).setEntityState(Constants.EntityState.Dead);
			}
		}
	}
	
	private void handleAnimations() {
		switch(currentTowerState) {
		case Idle:
			currentAnimation = idleAnimation;
			break;
		case Shooting:
			currentAnimation = shootingAnimation;
			break;
		}
	}
	
	private boolean isAttackReady() {
		return shootTimer >= attackSpeed ? true : false;
	}

}
