/*
 * LiquidBounce++ Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/PlusPlusMC/LiquidBouncePlusPlus/
 */
package net.ccbluex.liquidbounce.features.module.modules.movement

import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.JumpEvent
import net.ccbluex.liquidbounce.event.StrafeEvent
import net.ccbluex.liquidbounce.event.UpdateEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.utils.MovementUtils
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.FloatValue
import kotlin.math.*

@ModuleInfo(name = "Strafe", description = "Allows you to freely move in mid air.", category = ModuleCategory.MOVEMENT)
class Strafe : Module() {

    private var strengthValue = FloatValue("Strength", 0.5F, 0F, 1F, "x")
    private var noMoveStopValue = BoolValue("NoMoveStop", false)
    private var onGroundStrafeValue = BoolValue("OnGroundStrafe", false)
    private var allDirectionsJumpValue = BoolValue("AllDirectionsJump", false)

    private var wasDown: Boolean = false
    private var jump: Boolean = false

    @EventTarget
    fun onJump(event: JumpEvent) {
        if (jump) {
            event.cancelEvent()
        }
    }

    override fun onEnable() {
        wasDown = false
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        if (mc.thePlayer!!.onGround && mc.gameSettings.keyBindJump.isKeyDown && allDirectionsJumpValue.get() && MovementUtils.isMoving && !(mc.thePlayer!!.isInWater || mc.thePlayer!!.isInLava || mc.thePlayer!!.isOnLadder || mc.thePlayer!!.isInWeb)) {
            if (mc.gameSettings.keyBindJump.isKeyDown) {
                mc.gameSettings.keyBindJump.pressed = false
                wasDown = true
            }
            val yaw = mc.thePlayer!!.rotationYaw
            mc.thePlayer!!.rotationYaw = Math.toDegrees(MovementUtils.direction).toFloat()
            mc.thePlayer!!.jump()
            mc.thePlayer!!.rotationYaw = yaw
            jump = true
            if (wasDown) {
                mc.gameSettings.keyBindJump.pressed = true
                wasDown = false
            }
        } else {
            jump = false
        }
    }

    @EventTarget
    fun onStrafe(event: StrafeEvent) {
        if (!MovementUtils.isMoving) {
            if (noMoveStopValue.get()) {
                mc.thePlayer!!.motionX = .0
                mc.thePlayer!!.motionZ = .0
            }
            return
        }
        
        val shotSpeed = MovementUtils.speed
        val speed = shotSpeed * strengthValue.get()
        val motionX = mc.thePlayer!!.motionX * (1 - strengthValue.get())
        val motionZ = mc.thePlayer!!.motionZ * (1 - strengthValue.get())
        
        if (!mc.thePlayer!!.onGround || onGroundStrafeValue.get()) {
            val yaw = getMoveYaw()
            mc.thePlayer!!.motionX = (((-sin(Math.toRadians(yaw.toDouble())) * speed) + motionX))
            mc.thePlayer!!.motionZ = (((cos(Math.toRadians(yaw.toDouble())) * speed) + motionZ))
        }
    }


    private fun getMoveYaw(): Float {
        var moveYaw = mc.thePlayer!!.rotationYaw
        if (mc.thePlayer!!.moveForward != 0F && mc.thePlayer!!.moveStrafing == 0F) {
            moveYaw += if(mc.thePlayer!!.moveForward > 0) 0 else 180
        } else if (mc.thePlayer!!.moveForward != 0F && mc.thePlayer!!.moveStrafing != 0F) {
            if (mc.thePlayer!!.moveForward > 0) {
                moveYaw += if (mc.thePlayer!!.moveStrafing > 0) -45 else 45
            } else {
                moveYaw -= if (mc.thePlayer!!.moveStrafing > 0) -45 else 45
            }
            moveYaw += if(mc.thePlayer!!.moveForward > 0) 0 else 180
        } else if (mc.thePlayer!!.moveStrafing != 0F && mc.thePlayer!!.moveForward == 0F) {
            moveYaw += if(mc.thePlayer!!.moveStrafing > 0) -90 else 90
        }
        return moveYaw
    }
}
