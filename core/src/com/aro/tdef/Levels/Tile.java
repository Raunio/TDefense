package com.aro.tdef.Levels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.aro.tdef.Constants;
import com.aro.tdef.Entities.EnemyGuidePoint;

public class Tile {
	private Rectangle tileRectangle;
	private Sprite tileSprite;
	
	private boolean occupied;
	
	private boolean hasTexture;
	
	private EnemyGuidePoint guidePoint;
	
	private Vector2 position = new Vector2();
	private Vector2 origin = new Vector2();
	
	private int sections;
	
	private boolean canBuild = false;
	
	private boolean[] isOccupied;
	
	
	/**
	 * Returns true if the tile is occupied, meaning that building on it is not allowed.
	 */
	public boolean isOccupied() {
		return occupied;
	}
	
	public boolean isOccupied(int section) {
		return isOccupied[section];
	}

	public void setOccupied(boolean value) {
		occupied = value;
	}
	
	public void setOccupied(boolean value, int section) {
		isOccupied[section] = value;
	}
	
	public boolean canBuild() {
		return canBuild;
	}
	
	/**
	 * Returns the tiles position.
	 */
	public Vector2 getPosition() {
		position.x = tileRectangle.x;
		position.y = tileRectangle.y;
		
		return position;
	}
	
	public Vector2 getAdjustedPosition() {
		position.x = tileRectangle.x - tileRectangle.width / 4;
		position.y = tileRectangle.y - tileRectangle.height / 4;
		
		return position;
	}
	
	public int getSectionWidth() {
		int cols = (int)Math.sqrt(sections);
		return Constants.TILE_WIDTH / cols;
	}
	
	public int getSectionHeight() {
		int cols = (int)Math.sqrt(sections);
		return Constants.TILE_HEIGHT / cols;
	}
	
	public Vector2 getSectionPosition(int s) {
		if(s >= sections) return null;
		
		int cols = (int)Math.sqrt(sections);
		
		int row = (int)s / cols;
		int col = s - (row * cols);
		
		position.x = tileRectangle.x + (tileRectangle.width / cols) * col;
		position.y = tileRectangle.y + (tileRectangle.height / cols) * row;
		
		return position;
	}
	
	public int getSectionCount() {
		return sections;
	}
	
	/**
	 * Returns the origin of the tile.
	 */
	public Vector2 getOrigin() {
		origin.x = Constants.TILE_WIDTH / 2;
		origin.y = Constants.TILE_HEIGHT / 2;
		return origin;
	}
	
	/**
	 * Returns a guidepoint for enemies if the tile has one.
	 */
	public EnemyGuidePoint getGuidePoint() {
		return guidePoint;
	}
	
	/**
	 * Set up a guide point for enemies. The guide point directs enemies to take a specified rotation when they reach its center.
	 */
	private void setGuidePoint(float rotation) {
		guidePoint = new EnemyGuidePoint(rotation);
	}
	
	/**
	 * Returns true if the tile contais the point.
	 */
	public boolean containsPoint(float x, float y) {
		return tileRectangle.contains(x, y);	
	}
	
	public Tile(Vector2 position, int sections) {
		this.tileRectangle = new Rectangle((int)position.x, (int)position.y, Constants.TILE_WIDTH, Constants.TILE_HEIGHT);
		hasTexture = true;
		occupied = true;
		this.sections = sections;
		this.isOccupied = new boolean[sections];
	}
	
	public void createSprite(char symbol) {
		Sprite sprite = new Sprite();
		switch(symbol) {
		case Constants.CornerPathSymbol_botLeft:
			sprite = new Sprite(new Texture(Gdx.files.internal(Constants.CornerPathTextureAsset)));
			sprite.rotate(180);
			setGuidePoint(180);
			break;
		case Constants.CornerPathSymbol_botRight:
			sprite = new Sprite(new Texture(Gdx.files.internal(Constants.CornerPathTextureAsset)));
			sprite.rotate(180);
			sprite.flip(true, false);
			setGuidePoint(0);
			break;
		case Constants.CornerPathSymbol_leftBot:
			sprite = new Sprite(new Texture(Gdx.files.internal(Constants.CornerPathTextureAsset)));
			sprite.rotate(270);
			sprite.flip(false, true);
			setGuidePoint(-90);
			break;
		case Constants.CornerPathSymbol_leftTop:
			sprite = new Sprite(new Texture(Gdx.files.internal(Constants.CornerPathTextureAsset)));
			sprite.flip(false, true);
			setGuidePoint(90);
			break;
		case Constants.CornerPathSymbol_rightBot:
			sprite = new Sprite(new Texture(Gdx.files.internal(Constants.CornerPathTextureAsset)));
			sprite.rotate(-90);
			setGuidePoint(-90);
			break;
		case Constants.CornerPathSymbol_rightTop:
			sprite = new Sprite(new Texture(Gdx.files.internal(Constants.CornerPathTextureAsset)));
			sprite.rotate(90);
			sprite.flip(false, true);
			setGuidePoint(90);
			break;
		case Constants.CornerPathSymbol_topLeft:
			sprite = new Sprite(new Texture(Gdx.files.internal(Constants.CornerPathTextureAsset)));
			sprite.flip(true, false);
			setGuidePoint(180);
			break;
		case Constants.CornerPathSymbol_topRight:
			sprite = new Sprite(new Texture(Gdx.files.internal(Constants.CornerPathTextureAsset)));
			setGuidePoint(0);
			break;
		case Constants.OpenPathSymbolX:
			sprite = new Sprite(new Texture(Gdx.files.internal(Constants.OpenPathTextureAsset)));
			sprite.rotate(90);
			break;
		case Constants.OpenPathSymbolY:
			sprite = new Sprite(new Texture(Gdx.files.internal(Constants.OpenPathTextureAsset)));
			break;
		default:
			hasTexture = false;
			occupied = false;
			canBuild = true;
		}
		
		sprite.setSize(Constants.TILE_WIDTH, Constants.TILE_HEIGHT);
		sprite.setPosition(tileRectangle.x, tileRectangle.y);
		
		tileSprite = sprite;
	}
	
	/**
	 * Draws the tile if it has a sprite.
	 */
	public void draw(SpriteBatch batch) {
		if(hasTexture)
			tileSprite.draw(batch);
	}
}
