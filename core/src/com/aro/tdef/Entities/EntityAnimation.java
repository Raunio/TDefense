package com.aro.tdef.Entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

/**
 * @author Niko
 *
 */
public class EntityAnimation {
	
	private float animTimer;
	
	private Animation animation;
	private TextureRegion[] frames;
	private TextureRegion currentFrame;
	
	private boolean looping;
	
	private int frameWidth;
	private int frameHeight;
	
	private Vector2 origin = new Vector2();
	
	/**
	 * Returns the current frame of the animation.
	 */
	public TextureRegion currentFrameRegion() {
		return currentFrame;
	}
	
	/**
	 * Returns frame width of the animation.
	 */
	public int getFrameWidth() {
		return frameWidth;
	}
	
	/**
	 * Returns the frame height of the animation.
	 */
	public int getFrameHeight() {
		return frameHeight;
	}
	
	/**
	 * Returns the origin of the current frame of the animation.
	 */
	public Vector2 frameOrigin(){
		origin.x = currentFrame.getRegionWidth() / 2;
		origin.y = currentFrame.getRegionHeight() / 2;
		return origin;
	}
	
	/** Set up a basic looping animation.
	 * @param spriteSheet The image file.
	 * @param animationSpeed The interval for changing frames in seconds.
	 */
	public EntityAnimation(Texture spriteSheet, float animationSpeed, boolean looping, int frameWidth, int frameHeight, int startFrame, int endFrame, int sheetRow){
		int totalFrames = endFrame - startFrame;
		frames = new TextureRegion[totalFrames];
		
		for(int i = 0; i < totalFrames; i++) {
			frames[i] = new TextureRegion(spriteSheet, i * frameWidth, sheetRow * frameHeight, frameWidth, frameHeight);
		}
		
		animation = new Animation(animationSpeed, frames);
		animTimer = 0f;
		
		this.looping = looping;
		
		this.frameWidth = frameWidth;
		this.frameHeight = frameHeight;
	}
	
	/** Main update method. Required to call in the main game loop.
	 * @param deltaTime game deltatime required for animation timers.
	 */
	public void update(float deltaTime){
		animTimer += deltaTime;
		currentFrame = animation.getKeyFrame(animTimer, looping);
	}
	
	/**
	 * Resets the animation to its starting frame.
	 */
	public void resetAnimation() {
		animTimer = 0;
	}
	
	/**
	 * Returns true if the animation is finished.
	 */
	public boolean isAnimationFinished() {
		return animation.isAnimationFinished(animTimer);
	}

}
