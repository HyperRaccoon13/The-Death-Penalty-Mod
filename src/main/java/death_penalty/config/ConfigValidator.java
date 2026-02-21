package death_penalty.config;

import death_penalty.TheDeathPenalty;
import death_penalty.config.model.DeathConfig;
import death_penalty.config.model.Rule;

import java.util.ArrayList;
import java.util.Comparator;

final class ConfigValidator {
	private ConfigValidator() {}

	static void validateInPlace(DeathConfig config) {
		if (config == null) return;
		if (config.global == null) config.global = new DeathConfig.Global();

		if (config.global.matchMode == null || config.global.matchMode.isBlank()) {
			config.global.matchMode = DeathConfig.MatchMode.FIRST.name();
		}

		if (config.rules == null) config.rules = new ArrayList<>();
		config.rules.removeIf(rule -> rule == null);
		for (Rule rule : config.rules) {
			if (rule.name == null)      rule.name      = "unnamed";
			if (rule.when == null)      rule.when      = new ArrayList<>();
			if (rule.where == null)     rule.where     = new ArrayList<>();
			if (rule.from == null)  	rule.from      = new ArrayList<>();
			if (rule.using == null)     rule.using     = new ArrayList<>();
			if (rule.penalties == null) rule.penalties = new ArrayList<>();
			rule.penalties.removeIf(penalty -> penalty == null);
		}

		if (config.defaultPenalties == null) config.defaultPenalties = new ArrayList<>();
		config.defaultPenalties.removeIf(penalty -> penalty == null);

		config.rules.sort(Comparator.comparingInt((Rule r) -> r.priority).reversed());

		if (!DeathConfig.MatchMode.isValid(config.global.matchMode)) {
			TheDeathPenalty.LOGGER.warn("death_penalty: Unknown global.matchMode '{}'. Using FIRST.", config.global.matchMode);
			config.global.matchMode = DeathConfig.MatchMode.FIRST.name();
		}
	}
}