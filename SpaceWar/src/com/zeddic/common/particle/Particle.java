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

package com.zeddic.common.particle;

import javax.microedition.khronos.opengles.GL10;

import android.graphics.Color;
import android.graphics.Paint;

import com.zeddic.common.Entity;
import com.zeddic.common.util.Vector2d;

/**
 * A single 'dot' in the world, that when combined with other
 * dots in an animation can recreate various effects. For example:
 * explosions or fire. Particles ussually have a short time life
 * which is coordinated by a {@link ParticleEmitter}. 
 * 
 * @author scott@zeddic.com (Scott Bailey)
 */
public class Particle extends Entity {

  /** How many milliseconds the particle should be rendered for. */
  public float maxLife;
  
  /** The current life of the particle in milliseconds. */
  public float life;
  
  /** 
   * An acceleration that should be added to the velocity.
   **/
  public float acceleration;
  
  /** The alpha visibility of the particle. */
  public float alpha;
  
  /** The rate at which the alpha transparency should change. */
  public float alphaRate;
  
  /** The rate at which particle's scale should change. */
  public float scaleRate;
  
  /**
   * The maximum speed that the particle can reach. 0 for no limit.
   */
  public float maxSpeed;
  
  /** The weight (scalar) of the gravity well force. */
  public float gravityWellForce = 5f;
  
  /** A gravity well, or object, that the particle should be pulled towards. */
  public Entity gravityWell = null;
  
  /** Max distance the gravity well can influence particles from. 0 for no limit. */
  public float gravityWellMaxDistance;
  
  /** Distance from the gravity well within which the particle despawns */
  public float gravityWellDespawnDistance;
  
  /** If true, will trigger a collide with the gravity well when it reaches it. */
  public boolean gravityWellCollide;
  
  /** Paint used to draw the particle. */
  protected Paint paint;
  
  /** Vector used for drawing the particle as a line based on speed. */
  //protected Vector2d scaledVelocity = new Vector2d(0, 0);
  /** A gravity force vector applied by the gravity well. */
  protected Vector2d gravityVector = new Vector2d();
  
  //// REUSED OBJECTS BETWEEN FUNCTION CALLS TO SAVE MEMORY
  /** What fraction of a regular update to perform based on the time passed. */
  float timeFraction;

  /**
   * Creates a new particle at the origin with default values.
   */
  public Particle() {
    this(0, 0);
  }
  
  /**
   * Creates a new particle at the given position with default values.
   */
  public Particle(float x, float y) {
    this.x = x;
    this.y = y;
    this.velocity = new Vector2d(0, 0);
    this.scale = 3;
    this.life = 0;
    
    paint = new Paint();
    paint.setColor(Color.RED);
    paint.setStrokeWidth(4);
  }
  
  /**
   * Called by the emitter once the emitter has just been emited.
   */
  public void onEmit(ParticleData data) { }
  
  /**
   * Called by the emitter once the emitter has just been created.
   */
  public void onCreate(ParticleData data) { }

  /**
   * Main update loop responsible for updating the particle state
   * based on the amount of time passed.
   */
  @Override
  public void update(long time) {
    super.update(time);
    
    timeFraction = (float) time / 1000;
    
    // Check to see if the particle should be dead.
    life += time;
    if (life > maxLife) {
      kill();
    }
    
    // Update transparency.
    alpha += alphaRate * timeFraction;
    
    // Update any gravity well effects
    applyGravityWellToVelocity();
    
    // Throttle back if needed.
    applyMaxSpeed();
    
    // Apply any acel.
    velocity.x += Math.signum(velocity.x) * acceleration * timeFraction;
    velocity.y += Math.signum(velocity.y) * acceleration * timeFraction;
    
    // Update scale.
    scale += scaleRate * timeFraction;
  }

  /**
   * Draws the particle to the screen.
   */
  @Override
  public void draw(GL10 gl) {
    
  }
  
  @Override
  public void reset() {
  
  }
  
  /**
   * Throttles the velocity if needed.
   */
  public void applyMaxSpeed() {
    // Maxspeed = 0 is special for no limit.
    if (maxSpeed == 0) {
      return;
    }
    
    float speedSquared = velocity.x * velocity.x + velocity.y * velocity.y;
    if ( speedSquared > maxSpeed * maxSpeed) {
      velocity.normalize();
      velocity.x *= maxSpeed;
      velocity.y *= maxSpeed;
    }
  }
  
  /**
   * Applies the effects of a gravity well to our velocity. This will
   * cause the velocity to eventually point towards the gravity well.
   */
  public void applyGravityWellToVelocity() {
    
    // Check that a gravity well is present.
    if (gravityWell == null) {
      return;
    }
    
    // Figure out the direction of the gravity well 
    // force vector and how strong it should be.
    float dX = gravityWell.x - this.x;
    float dY = gravityWell.y - this.y;
    float distanceSquared = dX * dX + dY * dY;
    
    // If we are not within range of the gravity well, don't apply affects.
    if ( gravityWellMaxDistance != 0 && 
         distanceSquared > gravityWellMaxDistance * gravityWellMaxDistance) {
      return;
    }
    
    // If we are within the gravity well's despawn point, kill the particle.
    if (gravityWellDespawnDistance > 0 &&
        distanceSquared < gravityWellDespawnDistance * gravityWellDespawnDistance) {
        kill();
        if (gravityWellCollide) {
          gravityWell.collide(this, gravityVector, true);
        }
    }
    
    gravityVector.x = dX;
    gravityVector.y = dY;
    gravityVector.normalize();
    gravityVector.x *= gravityWellForce / (distanceSquared / 50000);
    gravityVector.y *= gravityWellForce / (distanceSquared / 50000);
    
    // Apply the force vector against our velocity.
    velocity.x += gravityVector.x * timeFraction;
    velocity.y += gravityVector.y * timeFraction;
  }
}
