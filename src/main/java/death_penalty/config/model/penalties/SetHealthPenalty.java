package death_penalty.config.model.penalties;

import death_penalty.config.model.Penalty;
import death_penalty.util.DeathContext;
import net.minecraft.server.network.ServerPlayerEntity;

public class SetHealthPenalty implements Penalty {
	public String type = "set_health";
	public float valueF = 20f;

	@Override
	public void apply(ServerPlayerEntity playerEntity, DeathContext ctx) {
		playerEntity.setHealth(Math.max(0.0f, valueF));
	}
}