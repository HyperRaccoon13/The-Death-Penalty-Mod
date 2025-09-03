package death_penalty;

import death_penalty.command.DeathPenaltyCommands;
import death_penalty.config.ConfigManager;
import death_penalty.event.DeathRespawnHandler;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TheDeathPenalty implements ModInitializer {
	public static final String MOD_ID = "death_penalty";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ConfigManager.load();
		DeathPenaltyCommands.register();
		ServerPlayerEvents.AFTER_RESPAWN.register(DeathRespawnHandler::onAfterRespawn);
	}
}