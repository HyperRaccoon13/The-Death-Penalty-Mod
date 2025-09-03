package death_penalty.config.model;

import death_penalty.util.DeathContext;
import net.minecraft.server.network.ServerPlayerEntity;

public interface Penalty {
	void apply(ServerPlayerEntity player, DeathContext ctx);
}