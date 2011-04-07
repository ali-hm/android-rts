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

package com.zeddic.common.effects;

import javax.microedition.khronos.opengles.GL10;

import com.zeddic.common.Entity;
import com.zeddic.common.particle.ParticleEmitter;
import com.zeddic.common.particle.SpriteParticle;
import com.zeddic.common.particle.ParticleEmitter.ParticleEmitterBuilder;

public class Explosion extends Entity {

  ParticleEmitter emitter;
  
  public Explosion() {
    this(0, 0);
  }
  
  public Explosion(float x, float y) {
    super(x, y);
    createEmitter();
  }
  
  protected void createEmitter() {
    emitter = new ParticleEmitterBuilder()
        .at(x, y)
        .withEmitMode(ParticleEmitter.MODE_OMNI)
        .withEmitSpeedJitter(2)
        .withEmitLife(500)
        .withParticleSpeed(20)
        .withParticleAlphaRate(-8)
        .withParticleLife(500)
        .withMaxParticles(20)
        .withEmitRate(1000)
        .withParticleClass(SpriteParticle.class)
        .build();
  }
  
  public void ignite() {
    enable();
    emitter.reset();
  }
  
  @Override
  public void draw(GL10 gl) {
    emitter.draw(gl);
  }
  
  @Override
  public void update(long time) {
    super.update(time);
    emitter.x = this.x;
    emitter.y = this.y;
    emitter.update(time);
    if (!emitter.enabled)
      kill();
  }

  @Override
  public void reset() {
    emitter.reset();
  }
}
