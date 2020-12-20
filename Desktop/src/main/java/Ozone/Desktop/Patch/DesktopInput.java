/*
 * Copyright 2020 Itzbenz
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package Ozone.Desktop.Patch;

import Ozone.Manifest;
import arc.Core;
import arc.math.Angles;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.scene.ui.layout.Table;
import arc.util.Tmp;
import mindustry.Vars;
import mindustry.gen.*;
import mindustry.input.Binding;
import mindustry.ui.Styles;

public class DesktopInput extends mindustry.input.DesktopInput {
	public float xav = 0f, yav = 0f;
	
	public synchronized void setMove(Vec2 mov) {
		xav = mov.x;
		yav = mov.y;
	}
	
	@Override
	public void buildPlacementUI(Table table) {
		super.buildPlacementUI(table);
		table.button(Icon.settings, Styles.colori, () -> Manifest.menu.show()).tooltip("@ozone.menu");
	}
	
	@Override
	protected void updateMovement(Unit unit) {
		boolean omni = !(unit instanceof WaterMovec);
		boolean ground = unit.isGrounded();
		float strafePenalty = ground ? 1.0F : Mathf.lerp(1.0F, unit.type().strafePenalty, Angles.angleDist(unit.vel().angle(), unit.rotation()) / 180.0F);
		float baseSpeed = unit.type().speed;
		if (unit.isCommanding()) {
			baseSpeed = unit.minFormationSpeed() * 0.95F;
		}
		
		float speed = baseSpeed * Mathf.lerp(1.0F, unit.type().canBoost ? unit.type().boostMultiplier : 1.0F, unit.elevation) * strafePenalty;
		float xa = Core.input.axis(Binding.move_x);
		float ya = Core.input.axis(Binding.move_y);
		if (xav != 0f) {
			xa = xav;
		}
		if (yav != 0f) {
			ya = yav;
		}
		
		boolean boosted = unit instanceof Mechc && unit.isFlying();
		this.movement.set(xa, ya).nor().scl(speed);
		if (Core.input.keyDown(Binding.mouse_move)) {
			this.movement.add(Core.input.mouseWorld().sub(Vars.player).scl(0.04F * speed)).limit(speed);
		}
		
		float mouseAngle = Angles.mouseAngle(unit.x, unit.y);
		boolean aimCursor = omni && Vars.player.shooting && unit.type().hasWeapons() && unit.type().faceTarget && !boosted && unit.type().rotateShooting;
		if (aimCursor) {
			unit.lookAt(mouseAngle);
		}else if (!this.movement.isZero()) {
			unit.lookAt(unit.vel.isZero() ? this.movement.angle() : unit.vel.angle());
		}
		
		if (omni) {
			unit.moveAt(this.movement);
		}else {
			unit.moveAt(Tmp.v2.trns(unit.rotation, this.movement.len()));
			if (!this.movement.isZero() && ground) {
				unit.vel.rotateTo(this.movement.angle(), unit.type().rotateSpeed);
			}
		}
		
		unit.aim(unit.type().faceTarget ? Core.input.mouseWorld() : Tmp.v1.trns(unit.rotation, Core.input.mouseWorld().dst(unit)).add(unit.x, unit.y));
		unit.controlWeapons(true, Vars.player.shooting && !boosted);
		Vars.player.boosting = Core.input.keyDown(Binding.boost) && !this.movement.isZero();
		Vars.player.mouseX = unit.aimX();
		Vars.player.mouseY = unit.aimY();
		if (unit instanceof Payloadc) {
			if (Core.input.keyTap(Binding.pickupCargo)) {
				this.tryPickupPayload();
			}
			
			if (Core.input.keyTap(Binding.dropCargo)) {
				this.tryDropPayload();
			}
		}
		
		if (Core.input.keyTap(Binding.command)) {
			Call.unitCommand(Vars.player);
		}
	}
}
