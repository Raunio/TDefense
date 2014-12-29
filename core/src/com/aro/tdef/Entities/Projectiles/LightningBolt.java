package com.aro.tdef.Entities.Projectiles;

import java.util.Random;

import com.aro.tdef.Constants;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;

public class LightningBolt extends Projectile {
	
	private Array<Float> positions = new Array<Float>();
	private Vector2 tangent = new Vector2();
	private Vector2 normal = new Vector2();
	private Vector2 prevPoint = new Vector2();
	private float length;
	private Random rand = new Random();
	private final float sway = 80f;
	private final float jaggedness = 1 / sway;
	private float prevDisplacement;
	private float scale;
	private float envelope;
	private float displacement;
	private Vector2 point = new Vector2();
	private Array<LineProjectile> lineArray = new Array<LineProjectile>();
	private Pool<LineProjectile> linePool;
	private float lifeCounter;
	private int[] code;
	private float alpha;
	
	public LightningBolt() {}
	
	public LightningBolt(final int[] code, Vector2 source, Vector2 dest) {
		super(code);
		
		linePool = new Pool<LineProjectile>() {
			@Override
			protected LineProjectile newObject() {
				return new LineProjectile(code);
			}
		};
		
		this.code = code;
		
		createBolt(code, source, dest, rayThickness);		
		
	}
	
	public LightningBolt(final int[] code) {
		super(code);
		this.code = code;
		
		linePool = new Pool<LineProjectile>() {
			@Override
			protected LineProjectile newObject() {
				return new LineProjectile(code);
			}
		};
	}
	
	public void initializeBolt(Vector2 source, Vector2 dest) {
		createBolt(code, source, dest, rayThickness);
	}
	
	@Override
	public void reset(Vector2 position, Vector2 targetPosition) {
		this.hasDamaged = false;
		lifeCounter = 0;
		this.setEntityState(Constants.EntityState.Stopped);
		this.setPosition(targetPosition.x, targetPosition.y);
		
		linePool.freeAll(lineArray);
		lineArray.clear();
		
		positions.clear();
		createBolt(code, position, targetPosition, rayThickness);
	}
	
	public void update(float deltaTime) {
		if(rayLifeTime > 0) {
			lifeCounter += deltaTime;

			alpha = 1 - lifeCounter / rayLifeTime;
			
			if(lifeCounter >= rayLifeTime) this.currentEntityState = Constants.EntityState.Dead;
		}
				
		for(int i = 0; i < lineArray.size; i++) {
			lineArray.get(i).updatePosition(this.velocity);
		}
		
	}
	
	@Override
	public void draw(SpriteBatch batch) {		
		for(int i = 0; i < lineArray.size; i++) {
			lineArray.get(i).draw(batch, alpha);
		}
	}
	
	private void createBolt(int[] code, Vector2 position, Vector2 targetPosition, float thickness) {

		tangent.x = targetPosition.x - position.x;
		tangent.y = targetPosition.y - position.y;
		
		normal.x = tangent.y;
		normal.y = -tangent.x;
		
		normal.nor();
		
		length = tangent.len();	
		
		positions.add(0f);
		
		for(int i = 0; i < length / 4; i++) {
			positions.add(Constants.getRandomFloat(0f, 1f, rand));
		}
		
		positions.sort();
		
		prevPoint.x = position.x;
		prevPoint.y = position.y;
		
		prevDisplacement = 0f;
		
		for(int i = 1; i < positions.size; i++) {
			scale = (length * jaggedness) * (positions.get(i) - positions.get(i - 1));
			envelope = positions.get(i) > 0.95f ? 20 * (1 - positions.get(i)) : 1;
			displacement = Constants.getRandomFloat(-sway, sway, rand);
			displacement -= (displacement - prevDisplacement) * (1 - scale);
	        displacement *= envelope;
	        
	        point.x = position.x + positions.get(i) * tangent.x + displacement * normal.x;
	        point.y = position.y + positions.get(i) * tangent.y + displacement * normal.y;
	        
	        LineProjectile l = linePool.obtain();
	        l.initialize(prevPoint.cpy(), point.cpy(), thickness);
	        lineArray.add(l);
	        
	        prevPoint.x = point.x;
	        prevPoint.y = point.y;
	        prevDisplacement = displacement;
		}
		
		LineProjectile f = linePool.obtain();
		f.initialize(prevPoint.cpy(), targetPosition.cpy(), thickness);
		lineArray.add(f);
		
	}

}
