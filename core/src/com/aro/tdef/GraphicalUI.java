package com.aro.tdef;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;

public class GraphicalUI {
	
	private Stage stage;
	private TextureRegion buttonUpTexture;
	private TextureRegion buttonDownTexture;
	private static BitmapFont gameFont;
	
	private static Array<FloatingText> fTexts;
	
	Label creditsLabel;
	
	public void create() {
		stage = new Stage();
		Table table = new Table();
		table.setFillParent(true);
		stage.addActor(table);
		
		buttonUpTexture = new TextureRegion(new Texture(Gdx.files.internal(Constants.buttonUpTexture)));
		buttonDownTexture = new TextureRegion(new Texture(Gdx.files.internal(Constants.buttonDownTexture)));
		
		LabelStyle labelStyle = new LabelStyle();
		gameFont = new BitmapFont();
		TextButtonStyle bStyle = new TextButtonStyle();
		
		bStyle.font = gameFont;
		bStyle.up = new TextureRegionDrawable(buttonUpTexture);
		bStyle.down = new TextureRegionDrawable(buttonDownTexture);
		labelStyle.font = gameFont;
		
		TextButton buildButton = new TextButton("Build", bStyle);
		Label addressLabel = new Label("Credits: ", labelStyle);
		creditsLabel = new Label("0", labelStyle);
		
		//table.add(buildButton).right().top();
		table.row();
		table.add(addressLabel).pad(5f).bottom().left();
		table.add(creditsLabel).pad(5f).bottom().left();
		
		fTexts = new Array<FloatingText>();
		
	}
	
	public void render() {
		stage.act();
		stage.draw();
		
	}
	
	public void updateCredits(int amount) {
		creditsLabel.setText("" + amount);
		creditsLabel.invalidate();
	}
	
	public void renderFloatingTexts(SpriteBatch batch, float deltaTime) {
		for(int i = 0; i < fTexts.size; i++) {
			
			fTexts.get(i).update(deltaTime);
			fTexts.get(i).draw(batch);
			
			if(!fTexts.get(i).isActive())
				fTexts.removeIndex(i);
		}
	}
	
	public void dispose() {
		stage.dispose();
	}
	
	public static void displayFloatingText(String text, Vector2 position, float duration) {
		FloatingText fText = new FloatingText(gameFont, position, new Vector2(0, 0.5f), text, duration);
		
		fTexts.add(fText);
	}

}
