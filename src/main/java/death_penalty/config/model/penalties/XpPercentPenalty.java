package death_penalty.config.model.penalties;

import death_penalty.config.model.Penalty;
import death_penalty.util.DeathContext;
import net.minecraft.server.network.ServerPlayerEntity;

public class XpPercentPenalty implements Penalty {
	public String type = "xp_percent";
	public float valueF = 0.25f;

	@Override
	public void apply(ServerPlayerEntity playerEntity, DeathContext context) {
		float clamp = Math.max(0f, Math.min(1f, valueF));
		int total = playerEntity.totalExperience;
		int remove = Math.round(total * clamp);
		int keep = Math.max(0, total - remove);
		playerEntity.addExperienceLevels(-playerEntity.experienceLevel);
		playerEntity.addExperience(-playerEntity.totalExperience);
		playerEntity.addExperience(keep);
	}
}