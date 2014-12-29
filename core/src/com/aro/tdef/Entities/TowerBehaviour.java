package com.aro.tdef.Entities;

import com.aro.tdef.Constants;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class TowerBehaviour {

	private boolean targetLocked = false;
	private Enemy target;
	private Vector2 targetPosition = new Vector2();
	private float tempDistance = 0f;
	private float travelTime = 0f;
	private Vector2 tempVector = new Vector2();
	private Vector2 tempVelocity = new Vector2();
	/**
	 * Applies basic ai to specified tower.
	 */
	public void apply(Tower tower, Array<Enemy> enemies) {
		if(!targetLocked) {
			for(Enemy e : enemies) {
				if(e.getCurrentEntityState() == Constants.EntityState.Dead) continue;
				
				targetPosition.x = e.getPosition().x + e.getOrigin().x;
				targetPosition.y = e.getPosition().y + e.getOrigin().y;
				
				tempDistance = targetPosition.dst(tower.getPosition());
	
				if(tempDistance < tower.getAttackRange()) {
					target = e;
					targetLocked = true;
					break;
				}
			}
		}
		else {
			if(target.getCurrentEntityState() == Constants.EntityState.Dead) {
				targetLocked = false;
				return;
			}
			
			targetPosition.x = target.getPosition().x + target.getOrigin().x;
			targetPosition.y = target.getPosition().y + target.getOrigin().y;
			
			tempDistance = targetPosition.dst(tower.getPosition());
			
			travelTime = tempDistance / tower.getProjectileInfo().getTangentialVelocityMax();
			
			tempVector.x = targetPosition.x;
			tempVector.y = targetPosition.y;
			
			if(tower.getProjectileInfo().getProjectileType() == Constants.ProjectileType.Ballistic) {
				tempVelocity.x = target.getVelocity().x;
				tempVelocity.y = target.getVelocity().y;
				
				tempVector = tempVelocity.nor().scl(travelTime);
				tempVector.add(targetPosition);
			}
			
			if(targetPosition.dst(tower.getPosition()) < tower.getAttackRange()) {
				tower.setFacingPoint(tempVector.x, tempVector.y);
				tower.updateRotation();
				
				if(distance(tower.getRotation(), tower.getTargetRotation()) < 1f)
					tower.shoot();
			}
			else {
				targetLocked = false;
			}
		}
	}
	
	public Enemy getTarget() {
		return target;
	}
	
	private float distance(float a, float b) {
		return a < b ? b - a : a - b;
	}
}
