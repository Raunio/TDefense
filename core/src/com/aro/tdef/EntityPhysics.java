package com.aro.tdef;

import com.aro.tdef.Entities.Entity;

/**
 * @author Niko
 *
 */
public class EntityPhysics {
	
	private static EntityPhysics instance;
	
	public static EntityPhysics instance()
	{
		if(instance == null)
			instance = new EntityPhysics();
		
		return instance;
	}
	
	/**
	 * Apply basic physics to an entity.
	 */
	public void apply(Entity subject){
		rotateEntity(subject);
		
		subject.applyVelocities();
		
		if(subject.getCurrentEntityState() == Constants.EntityState.Moving) {
			moveEntity(subject);
		}
		else if (subject.getCurrentEntityState() == Constants.EntityState.Stopped) {
			stopEntity(subject);
		}
	}
	
    private void rotateEntity(Entity subject) {
    	if(subject.getRotationDirection() == Constants.RotationDirection.Clockwise 
    			&& subject.getRotationVelocity() < subject.getRotationMaxSpeed()){
    		
    		subject.setRotationVelocity(subject.getRotationVelocity() + subject.getRotationAcceleration());
    	}
    	else if(subject.getRotationDirection() == Constants.RotationDirection.CounterClockwise
    			&& subject.getRotationVelocity() > -subject.getRotationMaxSpeed()){
    		
    		subject.setRotationVelocity(subject.getRotationVelocity() - subject.getRotationAcceleration());
    	}
    	else{
    		
    		if(subject.getRotationVelocity() > subject.getRotationAcceleration()) {
    			subject.setRotationVelocity(subject.getRotationVelocity() - subject.getRotationAcceleration());
    		}
    		else if(subject.getRotationVelocity() < -subject.getRotationAcceleration()) {
    			subject.setRotationVelocity(subject.getRotationVelocity() + subject.getRotationAcceleration());
    		}
    		else {
    			subject.setRotationVelocity(0f);
    			subject.setRotation(subject.getTargetRotation());
    		}
    	}
    }
	
    private void moveEntity(Entity subject) {
    	if(subject.getTangentialVelocity() < subject.getTangentialVelocityMax()) {
    		subject.setTangentialVelocity(subject.getTangentialVelocity() + subject.getAcceleration());
    	}
    	
    	float vX = (float)Math.cos(Math.toRadians(subject.getRotation())) * subject.getTangentialVelocity();
    	float vY = (float)Math.sin(Math.toRadians(subject.getRotation())) * subject.getTangentialVelocity();
    	
    	subject.setVelocity(vX, vY);
    }
    
    private void stopEntity(Entity subject) {
    		subject.setTangentialVelocity(0f);
    }
}
