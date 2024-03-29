package com.zeddic.war;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.view.MotionEvent;

import com.zeddic.common.opengl.AbstractGame;
import com.zeddic.common.opengl.Screen;
import com.zeddic.common.opengl.Sprite;
import com.zeddic.common.opengl.TextureLibrary;
import com.zeddic.war.collision.CollisionSystem;
import com.zeddic.war.effects.Effects;
import com.zeddic.war.level.FileLevelLoader;
import com.zeddic.war.ships.FighterShip;
import com.zeddic.war.ui.GameHud;

/**
 * The main entry point for the game. Initializes game objects,
 * sets up OpenGL, and is the root of the game tree.
 *
 * @author scott@zeddic.com (Scott Bailey)
 */
public class WarGame extends AbstractGame {

  private BattleCommandManager commandManager;
  private GameHud hud;
  
  public WarGame() { }

  @Override
  public void onInitialize() {
    GameState.level = new FileLevelLoader().load("levels/1.txt");
    CollisionSystem.get().initializeForLevel(GameState.level);
    commandManager = new BattleCommandManager();
    
    hud = new GameHud();

    // Create the enemies and reusable game objects. 
    GameState.stockpiles = new Stockpiles();
    GameState.stockpiles.populate();
    GameState.camera.reset();

    FighterShip ship = GameState.stockpiles.ships.take(FighterShip.class);
    ship.x = 150;
    ship.y = 50;
    ship.enable();

    /*ship = GameState.stockpiles.ships.take(FighterShip.class);
    ship.x = 350;
    ship.y = 350;
    ship.enable();*/
  }

  @Override
  public void onSurfaceCreated(GL10 gl, EGLConfig config) {

    // Build any textures registered with the texture library.
    // Note: whenever the surface is recreated all prior textures are lost.
    TextureLibrary.get().init(gl, GameState.context);
    TextureLibrary.get().reload();

    // Enable texture support.
    gl.glShadeModel(GL10.GL_SMOOTH);
    
    // Screen clear color.
    gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
    
    // Blending Options.
    gl.glDisable(GL10.GL_DITHER);
    gl.glEnable(GL10.GL_BLEND);
    gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
    
    gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST); 
    
    // Setup depth tests for proper z-index rendering.
    gl.glClearDepthf(1.0f);
    gl.glEnable(GL10.GL_DEPTH_TEST);
    gl.glDepthFunc(GL10.GL_LEQUAL);

    // Clockwise winding. Note that while CW is specified here, vertices are still
    // specified CCW. This is is because we are inverting the y-axis and to match
    // the device screen norm of a 0,0 origin as the top left. Due to this inversion
    // we are actually looking at the back side of the world. This change also
    // requires that any UV texture mappings have their y-component inverted.
    gl.glFrontFace(GL10.GL_CW);
    gl.glEnable(GL10.GL_CULL_FACE);
    gl.glCullFace(GL10.GL_BACK);
  }
  
  @Override
  public void onSurfaceChanged(GL10 gl, int width, int height) {
    super.onSurfaceChanged(gl, width, height);
    
    // Set device dimensions.
    gl.glViewport(0, 0, width, height); 
    
    // Set a orthographic view that matche's the device's screen.
    // Note the inversion of the y-axis.
    gl.glMatrixMode(GL10.GL_PROJECTION);
    gl.glLoadIdentity();
    gl.glOrthof(0, width, height, 0, 0, 100);
    
    // Update the HUD positioning based on the new screen size.
    hud.reposition();
  }
  
  @Override
  public void update(long time) {
    commandManager.update(time);
    GameState.stockpiles.update(time);
    GameState.level.update(time);

    Effects.get().update(time);
    
    hud.update(time);
  }

  @Override
  public void draw(GL10 gl) {
    // Setup the Model View Matrix.
    gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);  
    gl.glMatrixMode(GL10.GL_MODELVIEW);
    gl.glLoadIdentity();

    // Apply the camera.
    GameState.camera.apply(gl);
    
    // Draw all game objects.
    GameState.level.draw(gl);
    GameState.stockpiles.draw(gl);
    Effects.get().draw(gl);
    commandManager.draw(gl);
    
    // Pop any camera transformations.
    GameState.camera.end(gl);
    
    // Any user interface elements, such as scores or a menu may be drawn here.
    hud.draw(gl);
  }

  @Override
  public void onTouchEvent(MotionEvent e) {
    
    boolean handled = hud.onTouch(e);
    
    if (!handled) {
      handled = commandManager.onTouch(e);
    }
    
    if (!handled) {
      GameState.camera.onTouchEvent(e);
    }
  }
}
