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

package com.zeddic.war.guns;

import com.zeddic.common.Entity;
import com.zeddic.war.guns.control.DirectionalGunControl;

public class Arsenal {
  
  public static Gun getPeaShooter(Entity owner) {
    Gun gun = new GunBuilder()
      .withOwner(owner)
      .withAutoFire(true)
      .withBulletSpeed(250)
      .withCooldown(200)
      .withFireOffset(30)
      .build();

    return gun;
  }
  
  public static Gun getSniper(Entity owner) {
    Gun gun = new GunBuilder()
      .withOwner(owner)
      .withAutoFire(true)
      .withBulletSpeed(120f)
      .withCooldown(2500)
      .withFireOffset(40)
      .build();
    
    return gun;
  }
  
  public static Gun getTriGun(Entity owner) {
    Gun gun = getPeaShooter(owner);
    gun.multiplier = 3;
    gun.multiplierStartAngle = -5;
    gun.multiplierAngleBetweenBullets = 5;
    
    return gun;
  }

  public static Gun getDeathBlossom(Entity owner) {
    Gun gun = getPeaShooter(owner);
    gun.multiplier = 2;
    gun.multiplierAngleBetweenBullets = 180;
    gun.setAutoFire(true);
    gun.setFireOffset(20);
    gun.setGunControl(new DirectionalGunControl(owner));
    return gun;
  }
}
