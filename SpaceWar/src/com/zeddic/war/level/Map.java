/*
 * Copyright (C) 2010 Geo Siege Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.zeddic.war.level;

import javax.microedition.khronos.opengles.GL10;

import com.zeddic.common.AbstractGameObject;
import com.zeddic.common.opengl.Color;
import com.zeddic.common.opengl.Sprite;
import com.zeddic.common.util.Countdown;
import com.zeddic.war.GameState;
import com.zeddic.war.R;
import com.zeddic.war.ships.Square;

public class Map extends AbstractGameObject {
  
  private static final float EDGE_BUFFER = 0;
  public int rows;
  public int cols;
  public float width;
  public float height;
  public float top;
  public float left;
  public float right;
  public float bottom;
  
  public float spawnLeft;
  public float spawnTop;
  public float spawnWidth;
  public float spawnHeight;
  public float spawnRight;
  public float spawnBottom;
  
  private static final int ENEMIES_PER_WAVE = 10;
  
  private final Planet planet;
  private final InvadePath path;
  private final Countdown nextWaveCountdown = new Countdown(10000);
  private final Countdown spawnCountdown = new Countdown(1000);
  private int leftToSpawn;
  private Sprite grid;
  private Sprite borderTop;
  private Sprite borderLeft;
  private Sprite borderRight;
  private Sprite borderBottom;
  
  public Map(int rows, int cols) {
    setSize(rows, cols);

    grid = new Sprite(width , height, R.drawable.grid);
    grid.setTextureScale(cols, rows);
    grid.setTop(0);
    grid.setLeft(0);
    
    createBorders();
    
    planet = new Planet(600, 100, new Color(255, 187, 0, 255));
    path = new InvadePath.Builder()
        .add(0, 100)
        .add(200, 100)
        .add(200, 300)
        .add(600, 100)
        .build();
   
    leftToSpawn = ENEMIES_PER_WAVE;
    nextWaveCountdown.start();
    spawnCountdown.start();
  }
  
  int BORDER_BUFFER = 4;
  
  
  private void createBorders() {
    borderTop = createBorderSprite(BORDER_BUFFER, cols + BORDER_BUFFER * 2);
    borderTop.setBottom(0);
    
    borderBottom = createBorderSprite(BORDER_BUFFER, cols + BORDER_BUFFER * 2);
    borderBottom.setTop(height);
    
    borderLeft = createBorderSprite(rows + BORDER_BUFFER * 2, BORDER_BUFFER);
    borderLeft.setRight(0);

    borderRight = createBorderSprite(rows + BORDER_BUFFER * 2, BORDER_BUFFER);
    borderRight.setLeft(width);
  }
  
  private Sprite createBorderSprite(int borderRows, int borderCols) {
    float borderWidth = borderCols * Level.TILE_SIZE;
    float borderHeight = borderRows * Level.TILE_SIZE;
    Sprite sprite = new Sprite(borderWidth, borderHeight, R.drawable.border);
    sprite.setTextureScale((float) borderCols / 2, (float) borderRows / 2);
    sprite.x = width / 2;
    sprite.y = height / 2;
    
    return sprite;
  }
  
  private void setSize(int rows, int cols) {
    this.rows = rows;
    this.cols = cols;
    this.width = cols * Level.TILE_SIZE;
    this.height = rows * Level.TILE_SIZE;
    top = 0 + EDGE_BUFFER;
    left = 0 + EDGE_BUFFER;
    right = left + width; 
    bottom = top + height;
  }
  
  public boolean inMapArea(float x, float y) {
    return x >= left
        && x <= right
        && y >= top
        && y <= bottom;
  }
  
  @Override
  public void update(long time) {
    planet.update(time);
    path.update(time);
    
    if (leftToSpawn > 0) {
      spawnCountdown.update(time);
      if (spawnCountdown.isDone()) {
        Square square = GameState.stockpiles.ships.take(Square.class);
        square.spawn(path);
        spawnCountdown.restart();
        leftToSpawn--;
      }
    } else {
      nextWaveCountdown.update(time);
      if (nextWaveCountdown.isDone()) {
        leftToSpawn = ENEMIES_PER_WAVE;
        nextWaveCountdown.restart();
      }
    }
  }

  @Override
  public void draw(GL10 gl) {
    borderTop.draw(gl);
    borderBottom.draw(gl);
    borderLeft.draw(gl);
    borderRight.draw(gl);
    grid.draw(gl);
    planet.draw(gl);
    path.draw(gl);
  }
}
