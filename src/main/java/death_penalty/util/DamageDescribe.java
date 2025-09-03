package death_penalty.util;

import death_penalty.mixin.accessor.PersistentProjectileEntityInvoker;
import death_penalty.mixin.accessor.ThrownItemEntityInvoker;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class DamageDescribe {
	private DamageDescribe() {}

	public static String describeEntity(Entity entity) {
		if (entity == null) return "null";
		Identifier typeId = Registry.ENTITY_TYPE.getId(entity.getType());
		String base = typeId != null ? typeId.toString() : entity.getType().toString();

		if (entity instanceof LivingEntity livingEntity) {
			ItemStack main = livingEntity.getMainHandStack();
			if (!main.isEmpty()) {
				Identifier itemId = Registry.ITEM.getId(main.getItem());
				base += " {held ="  + (itemId != null ? itemId : main.getItem().toString()) + "}";
			}
			String name = livingEntity.getName().getString();
			if (!name.isEmpty()) base += " {name = \"" + name + "\"}";
		}
		return base;
	}

	public static String describeSource(Entity source) {
		if (source == null) return "null";
		Identifier typeId = Registry.ENTITY_TYPE.getId(source.getType());
		String base = typeId != null ? typeId.toString() : source.getType().toString();

		if (source instanceof ThrownItemEntity thrownItemEntity) {
			ItemStack stack = ((ThrownItemEntityInvoker) thrownItemEntity).invokeGetItem();
			if (stack != null && !stack.isEmpty()) {
				Identifier itemId = Registry.ITEM.getId(stack.getItem());
				return base + " {item = " + (itemId != null ? itemId : stack.getItem().toString()) + "}";
			}
			return base + " {thrown_item}";
		}

		if (source instanceof PersistentProjectileEntity persistentProjectileEntity) {
			ItemStack stack = ((PersistentProjectileEntityInvoker) persistentProjectileEntity).invokeAsItemStack();
			if (stack != null && !stack.isEmpty()) {
				Identifier itemId = Registry.ITEM.getId(stack.getItem());
				return base + " {projectile_item = " + (itemId != null ? itemId : stack.getItem().toString()) + "}";
			}
			return base + " {projectile}";
		}

		if (source instanceof ProjectileEntity) {
			return base + " {projectile}";
		}

		if (source instanceof FallingBlockEntity fallingBlockEntity) {
			Block block = fallingBlockEntity.getBlockState().getBlock();
			Identifier blockId = Registry.BLOCK.getId(block);
			return base + " {block = " + (blockId != null ? blockId : block.toString()) + "}";
		}

		if (source instanceof EndCrystalEntity) {
			return base + " {end_crystal}";
		}

		return base;
	}

	public static String b(boolean v) { return v ? "1" : "0"; }
	public static String safe(String s) { return s == null ? "null" : s; }
}