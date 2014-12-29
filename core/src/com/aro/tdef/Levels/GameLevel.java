package com.aro.tdef.Levels;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.aro.tdef.CombatHandler;
import com.aro.tdef.EntityPhysics;
import com.aro.tdef.Constants;
import com.aro.tdef.GraphicalUI;
import com.aro.tdef.ParticleEffectHandler;
import com.aro.tdef.Entities.Enemy;
import com.aro.tdef.Entities.EnemySpawner;
import com.aro.tdef.Entities.SelectionTool;
import com.aro.tdef.Entities.Tower;
import com.aro.tdef.Entities.TowerBarrel;
import com.aro.tdef.Entities.Projectiles.LineProjectile;
import com.aro.tdef.Entities.Projectiles.Projectile;

/**
 * Holds all game objects and logic.
 *
 */
public class GameLevel {
	private EnemySpawner enemySpawner;
	private Array<Enemy> enemies;
	public Array<Tower> towers;
	
	private GameMap map;
	
	private GraphicalUI ui;
	
	private SelectionTool selectTool;
	
	private int touchCounter;
	
	private final int towerPrice = 5;
	
	private Vector2 buildLocation = new Vector2();
	
	private OrthographicCamera camera;
	
	private ParticleEffectHandler effectHandler;
	
	private Texture pointer;
	
	private ShapeRenderer sr = new ShapeRenderer();
	
	private float counter;
	
	private FPSLogger fpslogger = new FPSLogger();
	
	private Tower selectedTower;
	
	private Vector2 tempVector = new Vector2();
	/**
	 * Initializes the level.
	 */
	public void create(OrthographicCamera camera){
		
		towers = new Array<Tower>();
	
		map = new GameMap(1540, 900f);
		map.loadContent();
		map.createMap(Constants.map1Data);
		
		this.camera = camera;
		
		//camera.position.x = 0;
		//camera.position.y = 0;
		
		enemySpawner = new EnemySpawner(map.getSpawnerTile().getPosition(), 90);	
		enemies = enemySpawner.spawnedEnemies;
	
		Projectile.loadSpriteSheet(Constants.FireBoltTextureAsset);
		Enemy.loadSpriteSheet(Constants.ZombieSpriteSheetAsset);
		Tower.loadSpriteSheet(Constants.TowerTextureAsset);
		TowerBarrel.loadSpriteSheet(Constants.TowerBarrelSheetAsset);
		LineProjectile.loadSpritesheet(Constants.lineProjectileAsset);
		
		pointer = new Texture(Gdx.files.internal(Constants.PointerTextureAsset));

		ui = new GraphicalUI();
		ui.create();
		
		selectTool = new SelectionTool();
		
		effectHandler = new ParticleEffectHandler();
		
		touchCounter = 0;
		
		CombatHandler.instance().initialize(effectHandler);
		
		CombatHandler.instance().setTotalCredits(600);
	}
	
	/**
	 * Main update method. Holds all game logic.
	 */
	public void update(float deltaTime){
		
		counter += deltaTime;
		
		this.fpslogger.log();
		
		enemySpawner.Update(deltaTime);
		
		if(enemySpawner.isWaiting()) {
			cleanUp();
		}
		
		for(Enemy e : enemies){
			e.update(deltaTime);
			EntityPhysics.instance().apply(e);
			map.GuideEnemy(e);
		}
		
		for(Tower t : towers){
			t.update(deltaTime, enemies);
			t.updateProjectiles(deltaTime);
			EntityPhysics.instance().apply(t);
			t.killProjectiles(map.getMapWidth(), map.getMapHeight());
		}
		
		CombatHandler.instance().update(towers, enemies, deltaTime);
		
		ui.updateCredits(CombatHandler.instance().getTotalCredits());
	}
	
	/**
	 * Main draw method.
	 */
	public void render(SpriteBatch batch, float deltaTime){
		batch.begin();
		
		map.draw(batch);
		
		for(Enemy e : enemies){
			e.draw(batch);
		}
		
		for(Tower t : towers){
			t.draw(batch);
		}
		
		selectTool.draw(batch);
		
		effectHandler.draw(batch, deltaTime);
		
		ui.renderFloatingTexts(batch, deltaTime);
		
		batch.end();
		
		if(selectedTower != null) {
			selectedTower.drawAttackRange(sr, camera);
			selectedTower.drawTargetLine(sr, camera);
		}		
		
	}
	
	/**
	 * Renders the graphical user interface.
	 */
	public void renderUI() {
		ui.render();
	}
	
	public void handleInput(Input input, float zoom) {
		float x = input.getX() + (camera.position.x / zoom) - Gdx.graphics.getWidth() / 2;
		float y = input.getY() - (camera.position.y / zoom) + Gdx.graphics.getHeight() / 2;
		
		if(input.isTouched()) {
			camera.position.x -= input.getDeltaX();
			camera.position.y += input.getDeltaY();
		}
		if(input.justTouched()) {
			for(int i = 0; i < map.getTileRowCount(); i++)
				for(int j = 0; j < map.getTileColumnCount(); j++)
				{
					if(map.getTileArray()[i][j] != null && map.getTileArray()[i][j].containsPoint(x * zoom, Gdx.graphics.getHeight() * zoom - y * zoom)) {
						touchCounter++;	
						selectTool.selectTile(map.getTileArray()[i][j], x * zoom, Gdx.graphics.getHeight() * zoom - y * zoom);
						for(Tower t : towers) {
							if(selectTool.getBoundingBox().contains(t.getBoundingBox())) {
								selectedTower = t;
								return;
							}
						}
						
						selectedTower = null;
						
						if(selectTool.getPreviousTile() != null) {
							if(selectTool.getPreviousTile().containsPoint(x * zoom, Gdx.graphics.getHeight() * zoom - y * zoom)) {
								if(touchCounter >= 1) {
									buildTower(i, j, selectTool.getSelectedSection());
									touchCounter = 0;
								}
								
								touchCounter++;
							}
							else if(!selectTool.getPreviousTile().containsPoint(x * zoom,  Gdx.graphics.getHeight() * zoom - y * zoom)) {
								touchCounter = 0;
							}
						}
							
					}
				}
		}	
	}
	
	public void dispose() {
		map.dispose();
		ui.dispose();
		selectTool.dispose();
		Enemy.disposeSpriteSheet();
		Tower.disposeSpriteSheet();
		TowerBarrel.disposeSpriteSheet();
		Projectile.disposeSpriteSheet();
		effectHandler.dispose();
	}
	
	private void buildTower(int x, int y, int section) {
		if(!map.getTileArray()[x][y].isOccupied(section) && map.getTileArray()[x][y].canBuild() && CombatHandler.instance().getTotalCredits() >= towerPrice) {
			
			buildLocation.x = map.getTileArray()[x][y].getSectionPosition(section).x + map.getTileArray()[x][y].getSectionWidth() / 2;
			buildLocation.y = map.getTileArray()[x][y].getSectionPosition(section).y + map.getTileArray()[x][y].getSectionHeight() / 2;
			
			Tower t = new Tower(Constants.aoelightningCode, buildLocation);
			towers.add(t);
			map.getTileArray()[x][y].setOccupied(true, section);
			CombatHandler.instance().setTotalCredits(CombatHandler.instance().getTotalCredits() - towerPrice);
		}
	}
	
	private void cleanUp() {
		for(int i = 0; i < enemies.size; i++) {
			if(enemies.get(i).getCurrentEntityState() == Constants.EntityState.Dead) {
				enemies.removeIndex(i);
			}
		}
	}


}
