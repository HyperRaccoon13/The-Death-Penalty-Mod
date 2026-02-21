package death_penalty.util;

import death_penalty.mixin.accessor.PersistentProjectileEntityInvoker;
import death_penalty.mixin.accessor.ThrownItemEntityInvoker;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import net.minecraft.util.registry.RegistryKey;


public final class DeathContext {
	public final String causeId;
	public final String dimensionId;
	public final String attackerTypeId;
	public final String sourceTypeId;
	public final String attackerHeldItemId;
	public final String projectileItemId;

	private DeathContext(String causeId, String dimensionId, String attackerTypeId, String sourceTypeId, String attackerHeldItemId, String projectileItemId) {
		this.causeId = causeId;
		this.dimensionId = dimensionId;
		this.attackerTypeId = attackerTypeId;
		this.sourceTypeId = sourceTypeId;
		this.attackerHeldItemId = attackerHeldItemId;
		this.projectileItemId = projectileItemId;
	}

	public static DeathContext from(ServerPlayerEntity oldPlayer, ServerPlayerEntity newPlayer) {
		DamageSource src = oldPlayer.getRecentDamageSource();
		String cause = (src != null ? src.getName() : "unknown");

		RegistryKey<World> key = oldPlayer.getWorld().getRegistryKey();
		String dim = key.getValue().toString();

		Entity attacker = (src != null) ? src.getAttacker() : null;
		Entity source   = (src != null) ? src.getSource()   : null;

		String attackerId = (attacker != null) ? Registry.ENTITY_TYPE.getId(attacker.getType()).toString() : null;

		String sourceId = (source != null) ? Registry.ENTITY_TYPE.getId(source.getType()).toString() : null;

		String heldItemId = null;
		if (attacker instanceof LivingEntity living) {
			ItemStack stack = living.getMainHandStack();
			if (stack != null && !stack.isEmpty()) {
				heldItemId = Registry.ITEM.getId(stack.getItem()).toString();
			}
		}

		String projItemId = null;
		if (source instanceof ThrownItemEntity tie) {
			ItemStack stack = ((ThrownItemEntityInvoker) tie).invokeGetItem();
			if (stack != null && !stack.isEmpty()) {
				projItemId = Registry.ITEM.getId(stack.getItem()).toString();
			}
		} else if (source instanceof PersistentProjectileEntity ppe) {
			ItemStack stack = ((PersistentProjectileEntityInvoker) ppe).invokeAsItemStack();
			if (stack != null && !stack.isEmpty()) {
				projItemId = Registry.ITEM.getId(stack.getItem()).toString();
			}
		}

		return new DeathContext(cause, dim, attackerId, sourceId, heldItemId, projItemId);
	}
}