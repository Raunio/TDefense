package com.aro.tdef.Entities;

/**
 * EnemyGuidePoints are points in the map of the game that assign enemies that walk over it a new direction.
 * These are used to avoid the need for any AI.
 * @author Niko
 */
public class EnemyGuidePoint {
	private float guideRotation;
	
	/**
	 * Gets the rotation in degrees that the guidepoint directs enemies to.
	 */
	public float getGuideRotation() {
		return guideRotation;
	}
	
	/**
	 * @param position The initial position of the point.
	 * @param guideRotation The rotation in degrees that the guidepoint directs enemies to.
	 */
	public EnemyGuidePoint(float guideRotation) {
		this.guideRotation = guideRotation;
	}
}
