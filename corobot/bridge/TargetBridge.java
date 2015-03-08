package corobot.bridge;

import javax.vecmath.Vector3f;

import net.minecraft.entity.Entity;

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
		return target.isDead;
	}

}
