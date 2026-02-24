package death_penalty.config.model.penalties;

import death_penalty.TheDeathPenalty;
import death_penalty.config.ConfigManager;
import death_penalty.config.model.Penalty;
import death_penalty.util.DeathContext;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.GameRules;

public class ItemDropPenalty implements Penalty {
	public String type = "item_drop";
	public int value;

	@Override
	public void apply(ServerPlayerEntity playerEntity, DeathContext context) {
		TheDeathPenalty.LOGGER.info("item_drop apply");
		if (playerEntity.world.getGameRules().getBoolean(GameRules.KEEP_INVENTORY)) {
			for (int i = 0; i < playerEntity.getInventory().size(); i++) {
				ItemStack itemStack = playerEntity.getInventory().getStack(i);
				if (!itemStack.isEmpty() && EnchantmentHelper.hasVanishingCurse(itemStack) || itemStack.isIn(ConfigManager.ITEM_DROP_PENALTY_ITEMS)) {
					playerEntity.getInventory().removeStack(i);
				}
			}
		}
	}
}