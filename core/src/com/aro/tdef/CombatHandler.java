package com.aro.tdef;

import java.util.Random;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.aro.tdef.Entities.Enemy;
import com.aro.tdef.Entities.Tower;
import com.aro.tdef.Entities.Projectiles.Projectile;

public class CombatHandler {
	
	private static CombatHandler instance;
	
	private int totalCredits;
	
	private ParticleEffectHandler effectHandler;
	
	private Array<Vector2> releaseTargets = new Array<Vector2>();
	
	Random random = new Random();
	
	public static CombatHandler instance() {
		if(instance == null) {
			instance = new CombatHandler();
		}
		
		return instance;
	}
	
	public void initialize(ParticleEffectHandler effectHandler) {
		this.effectHandler = effectHandler;
	}
	
	public int getTotalCredits() {
		return totalCredits;
	}
	
	public void setTotalCredits(int value) {
		totalCredits = value;
	}
	
	public void update(Array<Tower> towers, Array<Enemy> enemies, float deltaTime) {
		for(int t = 0; t < towers.size; t++) {
			for(int p = 0; p < towers.get(t).getActiveProjectiles().size; p++) {
				if(towers.get(t).getActiveProjectiles().get(p).getCurrentEntityState() == Constants.EntityState.Dead || 
						towers.get(t).getActiveProjectiles().get(p).isDisabled()) continue;
				if(towers.get(t).getActiveProjectiles().get(p).getProjectileType() == Constants.ProjectileType.Ballistic) {
					for(int e = 0; e < enemies.size; e++) {
						if(enemies.get(e).getCurrentEntityState() == Constants.EntityState.Dead) continue;				
						else if(towers.get(t).getActiveProjectiles().get(p).getProjectileType() == Constants.ProjectileType.Ballistic &&
								enemies.get(e).getBoundingBox().contains(towers.get(t).getActiveProjectiles().get(p).getBoundingBox())) {
													
							int min = towers.get(t).getActiveProjectiles().get(p).getMinDamage();
							int max = towers.get(t).getActiveProjectiles().get(p).getMaxDamage();
							
							int damage = Constants.getRandomInteger(min, max, random);
							
							enemies.get(e).applyDamage(damage);
							
							//effectHandler.explosion(enemies.get(e).getPosition().x, enemies.get(e).getPosition().y);
							
							GraphicalUI.displayFloatingText("" + damage, new Vector2(enemies.get(e).getPosition().x + enemies.get(e).getOrigin().x, 
									enemies.get(e).getPosition().y + enemies.get(e).getOrigin().y), 0.5f);
							
							towers.get(t).getActiveProjectiles().get(p).setEntityState(Constants.EntityState.Dead);
							
							if(towers.get(t).getActiveProjectiles().get(p).getReleaseType() != Constants.ReleaseProjectileType.None) {
								createReleases(towers.get(t), enemies,towers.get(t).getActiveProjectiles().get(p), enemies.get(e));
							}
							break;
						}
						
						if(enemies.get(e).getCurrentHealth() <= 0) {	
							totalCredits += enemies.get(e).getBounty();
							enemies.get(e).setEntityState(Constants.EntityState.Dead);
							break;
						}
					}
				}
				else if(!towers.get(t).getActiveProjectiles().get(p).hasDamaged() && 
						towers.get(t).getTarget() != null && 
						towers.get(t).getTarget().getCurrentEntityState() != Constants.EntityState.Dead) {
					
					int min = towers.get(t).getActiveProjectiles().get(p).getMinDamage();
					int max = towers.get(t).getActiveProjectiles().get(p).getMaxDamage();
					
					int damage = Constants.getRandomInteger(min, max, random);
					
					towers.get(t).getTarget().applyDamage(damage);
					
					effectHandler.explosion(towers.get(t).getTarget().getPosition().x + towers.get(t).getTarget().getOrigin().x, 
							towers.get(t).getTarget().getPosition().y + towers.get(t).getTarget().getOrigin().y);
					
					GraphicalUI.displayFloatingText("" + damage, new Vector2(towers.get(t).getTarget().getPosition().x + towers.get(t).getTarget().getOrigin().x, 
							towers.get(t).getTarget().getPosition().y + towers.get(t).getTarget().getOrigin().y), 0.5f);
					
					towers.get(t).getActiveProjectiles().get(p).setDamaged(true);
					
					if(towers.get(t).getActiveProjectiles().get(p).getReleaseType() != Constants.ReleaseProjectileType.None) {
						createReleases(towers.get(t), enemies, towers.get(t).getActiveProjectiles().get(p), towers.get(t).getTarget());
					}
					
					if(towers.get(t).getTarget().getCurrentHealth() <= 0) {	
						totalCredits += towers.get(t).getTarget().getBounty();
						towers.get(t).getTarget().setEntityState(Constants.EntityState.Dead);
						break;
					}
				}
			}
		}
	}
	
	private void createReleases(Tower t, Array<Enemy> enemies, Projectile p, Enemy from) {
		releaseTargets.clear();
		if(p.getReleaseType() == Constants.ReleaseProjectileType.Point) {
			
			for(int e = 0; e < enemies.size; e++) {
				Enemy to = enemies.get(e);
				
				if(from == to) continue;
				
				float dist = p.getPosition().dst(to.getPosition());
				
				if(dist <= p.getReleaseRange()) {
					if(releaseTargets.size == 0) releaseTargets.add(to.getPosition());
				}
			}
			
			if(releaseTargets.size == 0) return;
			
			t.addTargetedReleases(p.getPosition(), releaseTargets);
		}
	}

}
