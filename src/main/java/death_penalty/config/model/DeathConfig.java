package death_penalty.config.model;

import death_penalty.config.model.penalties.SetFoodPenalty;
import death_penalty.config.model.penalties.XpPercentPenalty;

import java.util.ArrayList;
import java.util.List;

public class DeathConfig {
	public Global global = new Global();
	public List<Rule> rules = new ArrayList<>();
	public List<Penalty> defaultPenalties = new ArrayList<>();

	public static class Global {
		public boolean enabled = true;
		public boolean onlyWithKeepInventory = true;
		public boolean dev = false;
	}

	public static DeathConfig defaultConfig() {
		DeathConfig deathConfig = new DeathConfig();
		Rule fire = new Rule();
		fire.name = "Death By Fire (Overworld only)";
		fire.when.add("inFire");
		fire.when.add("onFire");
		fire.where.add("minecraft:overworld");
		SetFoodPenalty penalty = new SetFoodPenalty();
		penalty.value = 1;
		penalty.saturationValue = 0f;
		fire.penalties.add(penalty);
		deathConfig.rules.add(fire);

		XpPercentPenalty xp = new XpPercentPenalty();
		xp.valueF = 0.25f;
		deathConfig.defaultPenalties.add(xp);
		return deathConfig;
	}
}