package com.aro.tdef.Entities.Projectiles;

import com.aro.tdef.Constants;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class LineProjectile {
	private Vector2 startPosition = new Vector2();
	private Vector2 endPosition = new Vector2();
	private TextureRegion halfCircle;
	private TextureRegion lightningSegment;
	private static Texture spriteSheet;
	private Vector2 tangent = new Vector2();
	private float rotation;
	private Vector2 capOrigin = new Vector2();
	private Vector2 middleOrigin = new Vector2();
	private Vector2 middleScale = new Vector2();
	private float thickScale;
	private Color rayColor;
	
	private final float imageThickness = 10;
	
	public LineProjectile(int[] code) {
		
		initializeTextureRegions(code);
		
		initColor(code);

	}
	
	public void updatePosition(Vector2 amount) {
		this.startPosition.add(amount);
		this.endPosition.add(amount);
	}
	
	public void initialize(Vector2 start, Vector2 end, float thickness) {
		startPosition = start;
		endPosition = end;
		
		thickScale = thickness / imageThickness;
		
		tangent.x = endPosition.x - startPosition.x;
		tangent.y = endPosition.y - startPosition.y;
		
		capOrigin.x = halfCircle.getRegionWidth();
		capOrigin.y = halfCircle.getRegionHeight() / 2f;
		
		middleOrigin.x = 0;
		middleOrigin.y = lightningSegment.getRegionHeight() / 2f;

		middleScale.x = tangent.len();
		middleScale.y = thickScale;
		
		rotation = (float)Math.toDegrees(Math.atan2(tangent.y, tangent.x));
	}
	
	private void initializeTextureRegions(int[] code) {
		halfCircle = new TextureRegion(spriteSheet, 0, 0, 74, 128);
		lightningSegment = new TextureRegion(spriteSheet, 192, 0, 1, 128);
	}
	
	private void initColor(int[] code) {
		if(code == Constants.aoelightningCode) rayColor = new Color(1, 1, 1, 1);
		else if(code == Constants.slightningCode) rayColor = new Color(1, 1, 1, 1);
	}
	
	public static void loadSpritesheet(String path) {
		spriteSheet = new Texture(Gdx.files.internal(path));
	}
	
	public void draw(SpriteBatch batch, float alpha) {
		batch.setColor(rayColor.r, rayColor.g, rayColor.b, alpha);
		
		batch.draw(lightningSegment, startPosition.x - middleOrigin.x, startPosition.y - middleOrigin.y, 
				middleOrigin.x, middleOrigin.y, 
				lightningSegment.getRegionWidth(), lightningSegment.getRegionHeight(), middleScale.x, middleScale.y, rotation);
		
		batch.draw(halfCircle, startPosition.x - capOrigin.x, startPosition.y - capOrigin.y, capOrigin.x, capOrigin.y, halfCircle.getRegionWidth(), halfCircle.getRegionHeight(),
				thickScale, thickScale, rotation);
		
		batch.draw(halfCircle, endPosition.x - capOrigin.x, endPosition.y - capOrigin.y, capOrigin.x, capOrigin.y, halfCircle.getRegionWidth(), halfCircle.getRegionHeight(),
				thickScale, thickScale, rotation + 180f);
		
		batch.setColor(Color.WHITE);
		
	}
}
