package death_penalty.config.model.penalties;

import death_penalty.config.model.Penalty;
import death_penalty.util.DeathContext;
import net.minecraft.server.network.ServerPlayerEntity;

public class SetFoodPenalty implements Penalty {
	public String type = "set_food";
	public int value = 1;
	public float saturationValue = 0f;

	@Override
	public void apply(ServerPlayerEntity playerEntity, DeathContext ctx) {
		playerEntity.getHungerManager().setFoodLevel(Math.max(0, Math.min(20, value)));
		playerEntity.getHungerManager().setSaturationLevel(Math.max(0f, saturationValue));
	}
}