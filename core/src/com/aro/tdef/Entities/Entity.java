package com.aro.tdef.Entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.aro.tdef.Constants;

/** Base class for all game entities.
 * @author Niko
 *
 */
public abstract class Entity {
	
	protected Vector2 position = new Vector2();
	protected Vector2 velocity = new Vector2();
	protected Vector2 origin;
	protected float rotationVelocity;
	protected float acceleration;
	protected float tangentialVelocityMax;
	protected float tangentialVelocity;
	
	protected float rotation;
	protected float scaleX;
	protected float scaleY;
	
	protected EntityAnimation currentAnimation;
	
	protected Constants.RotationDirection rotationDirection;
	protected Constants.EntityState currentEntityState;
	
	protected Vector2 facingPoint;
	protected float rotationMaxSpeed;
	protected float rotationAcceleration;
	protected float targetRotation;
	
	private Vector2 distance2D = new Vector2();
	
	private Rectangle boundingBox = new Rectangle();
	

	/**
	 * Gets the position of the entity.
	 */
	public Vector2 getPosition(){
		return position;
	}
	
	/**
	 * Gets the veclotiy of the entity.
	 */
	public Vector2 getVelocity(){
		return velocity == null ? Vector2.Zero : velocity;
	}
	
	/**
	 * Gets the rotation velocity of the entity.
	 */
	public float getRotationVelocity(){
		return rotationVelocity;
	}
	
	/**
	 * Gets the acceleration of the entity.
	 */
	public float getAcceleration(){
		return acceleration;
	}
	
	/**
	 * Returns the maximum tangential velocity of the entity.
	 */
	public float getTangentialVelocityMax() {
		return tangentialVelocityMax;
	}
	
	/**
	 * Returns the current tangential velocity of the entity.
	 */
	public float getTangentialVelocity() {
		return tangentialVelocity;
	}
	
	/**
	 * Gets the current rotation of the entity.
	 */
	public float getRotation(){
		return rotation;
	}
	
	/**
	 * Gets the x-axis scale of the entity.
	 */
	public float getScaleX(){
		return scaleX;
	}
	
	/**
	 * Gets the y-axis scale of the entity.
	 */
	public float getScaleY(){
		return scaleY;
	}
	
	/**
	 * Returns the origin of the entitys current animation.
	 */
	public Vector2 getOrigin() {
		return origin == null ? currentAnimation.frameOrigin() : origin;
	}
	
	/**
	 * Gets the current rotation direction of the entity.
	 */
	public Constants.RotationDirection getRotationDirection(){
		return rotationDirection;
	}
	
	/**
	 * Gets the current state of the entity.
	 */
	public Constants.EntityState getCurrentEntityState() {
		return currentEntityState;
	}
	
	/**
	 * Gets the facing point the entity is either facing currently or rotating towards.
	 */
	public Vector2 getFacingPoint(){
		return facingPoint;
	}
	
	/**
	 * Gets the maximun rotation speed of the entity.
	 */
	public float getRotationMaxSpeed(){
		return rotationMaxSpeed;
	}
	
	/**
	 * Gets the rotation acceleration of the entity.
	 */
	public float getRotationAcceleration(){
		return rotationAcceleration;
	}
	
	/**
	 * Gets the last known target rotation of the entity.
	 */
	public float getTargetRotation(){
		return targetRotation;
	}
	
	/**
	 * Returns a rectangle the size of the current frame of the entitys animation. This is primarily used for collision detection.
	 */
	public Rectangle getBoundingBox(){
		boundingBox.x = position.x;
		boundingBox.y = position.y;
		boundingBox.width = currentAnimation.currentFrameRegion().getRegionWidth();
		boundingBox.height = currentAnimation.currentFrameRegion().getRegionHeight();
		
		return boundingBox;
	}
	
	/**
	 * Applies all velocites to entity.
	 */
	public void applyVelocities(){
		position.x += getVelocity().x;
		position.y += getVelocity().y;
		rotation += rotationVelocity;
	}

	/**
	 * Sets the velocity of the entity.
	 */
	public void setVelocity(float x, float y){
		this.velocity.x = x;
		this.velocity.y = y;
	}
	
	public void setOrigin(float x, float y) {
		this.origin = new Vector2(x, y);
	}
	
	/**
	 * Sets the rotation velocity of the entity.
	 */
	public void setRotationVelocity(float value){
		this.rotationVelocity = value;
	}
	
	public void setTangentialVelocity(float value) {
		this.tangentialVelocity = value;
	}
	
	/**
	 * Sets a facing point for the entity.
	 */
	public void setFacingPoint(float x, float y){
		if(facingPoint == null) {
			facingPoint = new Vector2(x, y);
			return;
		}
		
		this.facingPoint.x = x;
		this.facingPoint.y = y;
	}
	
	/**
	 * Sets a new target rotation for the entity.
	 */
	public void setTargetRotation(float value) {
		targetRotation = value;
	}
	
	/**
	 * Sets a rotation value for the entity in degrees.
	 */
	public void setRotation(float rotation) {
		this.rotation = rotation;
	}
	
	/**
	 * Sets the position of the entity.
	 */
	public void setPosition(float x, float y) {
		this.position.x = x;
		this.position.y = y;
	}
	
	/**
	 * Sets the scale of the entity.
	 */
	public void setScale(float x, float y) {
		scaleX = x;
		scaleY = y;
	}
	
	/**
	 * Sets the entity to the state.
	 */
	public void setEntityState(Constants.EntityState value) {
		this.currentEntityState = value;
	}
	
	/**
	 * Main draw method for the entity.
	 */
	public void draw(SpriteBatch batch){
		batch.draw(currentAnimation.currentFrameRegion(), position.x, position.y, 
				getOrigin().x, getOrigin().y, 
				getBoundingBox().width, getBoundingBox().height, scaleX, scaleY, rotation);
	}
	
	/**
	 * Calculates a new rotation value for the entity according to its facing point and sets its rotation direction accordingly.
	 */
	public void updateRotation(){
		
		if(facingPoint != null){
			distance2D.x = facingPoint.x - position.x;
			distance2D.y = facingPoint.y - position.y;
			
			targetRotation = (float)Math.toDegrees(Math.atan2(distance2D.y, distance2D.x));
		}
		
		if(targetRotation < 0) {
			targetRotation += 360f;
		}
		else if(targetRotation > 360f) {
			targetRotation -= 360f;
		}
		
		if(rotation < 0) {
			rotation += 360f;
		}
		else if(rotation > 360) {
			rotation -= 360f;
		}
		
		float distance = rotation - targetRotation < 0 ? targetRotation - rotation : rotation - targetRotation;
		
		if(distance > this.rotationMaxSpeed / this.rotationAcceleration) {
			if(rotation < targetRotation) {
				if(distance > 180f) {
					rotationDirection = Constants.RotationDirection.CounterClockwise;
				}
				else {
					rotationDirection = Constants.RotationDirection.Clockwise;
				}
			}
			
			else {
				if(distance > 180f) {
					rotationDirection = Constants.RotationDirection.Clockwise;
				}
				else {
					rotationDirection = Constants.RotationDirection.CounterClockwise;
				}
			}
		}
		else {
			rotationDirection = Constants.RotationDirection.None;
		}
	}

}
