package com.zeddic.war.collision;

import javax.microedition.khronos.opengles.GL10;

import com.zeddic.common.Entity;
import com.zeddic.common.GameObject;
import com.zeddic.common.util.SimpleList;

public class CollideComponent implements GameObject {

  private final CollisionSystem collisionSystem;
  public final Entity entity;
  public SimpleList<EntityCell> currentCells = SimpleList.create(EntityCell.class, 1);
  private boolean registered;
  private CollideBehavior behavior;

  public CollideComponent(Entity entity, CollideBehavior behavior) {
    this(CollisionSystem.get(), entity, behavior);
  }
  
  public CollideComponent(
      CollisionSystem collisionSystem,
      Entity entity,
      CollideBehavior behavior) {

    this.collisionSystem = collisionSystem;
    this.behavior = behavior;
    this.entity = entity;
  }
  
  /**
   * Changes the collision behavior for this object.
   */
  public void setBehavior(CollideBehavior behavior) {
    if (this.behavior == behavior) {
      return;
    }
    
    if (!registered) {
      this.behavior = behavior;
      return;
    }

    // Different collision behaviors may be registered with
    // the underlying collision grid differently. To make
    // sure the object is correctly represented, first unregister
    // it then re-register it with the new form.
    unregisterObject();
    this.behavior = behavior;
    registerObject();
  }
  
  public CollideBehavior getBehavior() {
    return behavior;
  }
  
  private void updateRegistration() {
    register(entity.enabled);
  }

  private void register(boolean register) {
    if (this.registered && !register) {
      unregisterObject();
    } else if (!this.registered && register) {
      registerObject();
    }
  }
  
  /**
   * Registers the object with the collision management system so it
   * can be tracked in the underlying data structures.
   */  
  public void registerObject() {
    if (registered)
      return;
    
    collisionSystem.register(this);  
    registered = true;
  }

  /**
   * Unregisters an object with the collision system. Other objects will 
   * no longer be aware of it or collide with it. This should be done
   * whenever an object removed from the world or enters a
   * 'dead' state. This is neccessary to prevent the world from becoming
   * cluttered with dead objects and slowing down the world.
   */
  public void unregisterObject() {
    if (!registered) {
      return;
    }

    collisionSystem.unregister(this);
    registered = false;
  }

  @Override
  public void update(long time) {
    updateRegistration();
  }
  
  public void move(float dX, float dY) {
    if (behavior == CollideBehavior.NONE) {
      entity.x += dX;
      entity.y += dY;
      return;
    } else {
      collisionSystem.move(this, dX, dY);
    }
  }

  @Override
  public void draw(GL10 gl) {
    // Do nothing.
  }

  @Override
  public void reset() {
    // Do nothing.
  }
}

