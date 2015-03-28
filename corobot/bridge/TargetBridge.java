package corobot.bridge;

import javax.vecmath.Vector3f;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;

import com.corosus.entity.IEntity;
import com.corosus.world.IWorld;

import corobot.Corobot;

public class TargetBridge implements IEntity {

	public Entity target;
	
	public TargetBridge(Entity target) {
		this.target = target;
	}
	
	public void cleanup() {
		target = null;
	}
	
	@Override
	public IWorld getLevel() {
		return Corobot.playerAI.getLevel();
	}

	@Override
	public Vector3f getMvtvec() {
		return null;
	}

	@Override
	public Vector3f getPos() {
		return new Vector3f((float)target.posX, (float)target.boundingBox.minY, (float)target.posZ);
	}

	@Override
	public void stopMoving() {

	}

	@Override
	public void computePath(Vector3f moveTo) {

	}

	@Override
	public void setMoveTo(Vector3f parMoveTo) {

	}

	@Override
	public void setPos(Vector3f parVec) {

	}

	@Override
	public boolean getIsDead() {
		if (target instanceof EntityLivingBase) {
			if (((EntityLivingBase) target).getHealth() < 0) {
				return true;
			}
		}
		return target.isDead;
	}

	@Override
	public float getHealthMax() {
		if (target instanceof EntityLivingBase) {
			return ((EntityLivingBase) target).getMaxHealth();
		}
		return 0;
	}

	@Override
	public float getHealthCur() {
		if (target instanceof EntityLivingBase) {
			return ((EntityLivingBase) target).getHealth();
		}
		return 0;
	}

}
