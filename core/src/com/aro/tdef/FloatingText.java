package com.aro.tdef;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class FloatingText {
	private BitmapFont font;
	private Vector2 position = new Vector2();
	private Vector2 velocity;
	private String text;
	private float duration;
	private float timer;
	private int alphaPerSecond;
	private int initialAlpha;

	public boolean isActive() {
		if(timer >= duration) return false;
		else return true;
	}
	
	public FloatingText(BitmapFont font, Vector2 initialPos, Vector2 velocity, String text, float duration) {
		this.font = font;
		this.position = initialPos;
		this.velocity = velocity;
		this.duration = duration;
		this.text = text;
		initialAlpha = 255;
		alphaPerSecond = Math.round(initialAlpha / duration);
	}
	
	public void update(float deltaTime) {
		this.timer += deltaTime;
		
		this.position.x = this.position.x + this.velocity.x;
		this.position.y = this.position.y + this.velocity.y;
		
		int alpha = Math.round(alphaPerSecond * timer);
		
		font.setColor(220, 220, 220, Math.round(initialAlpha - alpha));
	}
	
	public void draw(SpriteBatch batch) {
		font.draw(batch, text, position.x, position.y);
	}
}
