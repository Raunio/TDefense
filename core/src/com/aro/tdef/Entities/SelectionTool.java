package com.aro.tdef.Entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.aro.tdef.Constants;
import com.aro.tdef.Levels.Tile;

public class SelectionTool extends Entity{
	private Tile currentTile;
	private Texture toolTexture;
	private Tile previousTile;
	private Rectangle sectionRect = new Rectangle();
	private int selectedSection;
	private int previousSection;
	/**
	 * Returns the currently selected tile of the tool.
	 */
	public Tile getSelectedTile() {
		return currentTile;
	}
	
	public Tile getPreviousTile() {
		return previousTile;
	}
	
	public int getSelectedSection() {
		return selectedSection;
	}
	
	public int getPreviousSection() {
		return previousSection;
	}
	
	public SelectionTool() {
		toolTexture = new Texture(Gdx.files.internal(Constants.SelectToolTextureAsset));
		currentAnimation = new EntityAnimation(toolTexture, 0.025f, true, Constants.TILE_WIDTH, Constants.TILE_HEIGHT, 0, 1, 0);
		this.scaleX = 1f;
		this.scaleY = 1f;
		
		currentAnimation.update(0f);
		
		this.position = new Vector2(-Constants.TILE_WIDTH, -Constants.TILE_HEIGHT);
		this.setOrigin(0, 0);
	}
	
	public void selectTile(Tile tile, float x, float y) {
		previousTile = currentTile;
		currentTile = tile;
		
		if(tile.canBuild()) {
			
			sectionRect.width = tile.getSectionWidth();
			sectionRect.height = tile.getSectionHeight();
			
			for(int i = 0; i < tile.getSectionCount(); i++) {
				sectionRect.x = tile.getSectionPosition(i).x;
				sectionRect.y = tile.getSectionPosition(i).y;
				
				if(sectionRect.contains(x, y)) {
					this.position = tile.getSectionPosition(i);
					this.scaleX = 0.5f;
					this.scaleY = 0.5f;
					this.previousSection = selectedSection;
					this.selectedSection = i;
					return;
				}
			}
		}
		else {
			this.position = tile.getPosition();
			this.scaleX = 1f;
			this.scaleY = 1f;
		}
			
	}
	
	
	public void dispose() {
		toolTexture.dispose();
	}
	
}
