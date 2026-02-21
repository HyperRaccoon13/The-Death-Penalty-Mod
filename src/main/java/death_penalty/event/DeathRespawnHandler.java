package death_penalty.event;

import death_penalty.config.ConfigManager;
import death_penalty.config.model.DeathConfig;
import death_penalty.config.model.Penalty;
import death_penalty.config.model.Rule;
import death_penalty.TheDeathPenalty;
import death_penalty.util.DamageDescribe;
import death_penalty.util.DeathContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TranslatableText;
import net.minecraft.world.GameRules;

import java.util.List;

public final class DeathRespawnHandler {
	private DeathRespawnHandler() {}

	public static void onAfterRespawn(ServerPlayerEntity oldPlayer, ServerPlayerEntity newPlayer, boolean alive) {
		if (alive) return;

		DeathConfig deathConfig = ConfigManager.get();
		if (deathConfig == null) {
			ConfigManager.load();
			deathConfig = ConfigManager.get();
		}
		if (deathConfig == null || !deathConfig.global.enabled) return;

		boolean keepInv = newPlayer.world.getGameRules().getBoolean(GameRules.KEEP_INVENTORY);
		if (deathConfig.global.onlyWithKeepInventory && !keepInv) return;

		DeathContext deathContext = DeathContext.from(oldPlayer, newPlayer);

		DeathConfig.MatchMode mode = DeathConfig.MatchMode.from(deathConfig.global.matchMode, DeathConfig.MatchMode.FIRST);
		boolean appliedAny = false;
		List<Rule> matches = deathConfig.rules.stream().filter(r -> r.matches(deathContext)).toList();

		if (!matches.isEmpty()) {
			if (mode == DeathConfig.MatchMode.FIRST) {
				Rule match = matches.get(0);
				appliedAny = applyRule(newPlayer, deathContext, match, deathConfig.global.dev) || appliedAny;
				if (deathConfig.global.dev) {
					newPlayer.sendMessage(new TranslatableText("text.death_penalty.applied_rule", match.name), false);
				}
			} else {
				for (Rule rule : matches) {
					appliedAny = applyRule(newPlayer, deathContext, rule, deathConfig.global.dev) || appliedAny;
					if (deathConfig.global.dev) {
						newPlayer.sendMessage(new TranslatableText("text.death_penalty.applied_rule", rule.name), false);
					}
				}
			}
		} else {
			appliedAny = applyPenalties(newPlayer, deathContext, deathConfig.defaultPenalties, "default", deathConfig.global.dev) || appliedAny;
			if (deathConfig.global.dev && appliedAny) {
				newPlayer.sendMessage(new TranslatableText("text.death_penalty.applied_default"), false);
			}
		}

		if (deathConfig.global.dev) {
			var src = oldPlayer.getRecentDamageSource();

			String attackerStr = DamageDescribe.describeEntity(src != null ? src.getAttacker() : null);
			String sourceStr = DamageDescribe.describeSource(src != null ? src.getSource() : null);

			String proj  = (src != null && src.isProjectile()) ? "1" : "0";
			String fire  = (src != null && src.isFire())       ? "1" : "0";
			String magic = (src != null && src.isMagic())      ? "1" : "0";
			String expl  = (src != null && src.isExplosive())  ? "1" : "0";
			String using = deathContext.attackerHeldItemId != null
					? deathContext.attackerHeldItemId
					: (deathContext.projectileItemId == null ? "null" : deathContext.projectileItemId);

			newPlayer.sendMessage(new net.minecraft.text.TranslatableText("text.death_penalty.debug", deathContext.causeId, attackerStr, sourceStr, proj, fire, magic, expl, using), false
			);
		}
	}

	private static boolean applyRule(ServerPlayerEntity player, DeathContext ctx, Rule rule, boolean dev) {
		return applyPenalties(player, ctx, rule.penalties, rule.name, dev);
	}

	private static boolean applyPenalties(ServerPlayerEntity player, DeathContext ctx, java.util.List<Penalty> penalties, String ruleName, boolean dev) {
		boolean appliedAny = false;
		if (penalties == null) return false;
		for (Penalty penalty : penalties) {
			if (penalty == null) continue;
			try {
				penalty.apply(player, ctx);
				appliedAny = true;
			} catch (Throwable throwable) {
				TheDeathPenalty.LOGGER.error("Failed to apply penalty '{}' for rule '{}'", penalty.getClass().getName(), ruleName, throwable);
				if (dev) {
					player.sendMessage(new TranslatableText("text.death_penalty.penalty_error", ruleName, penalty.getClass().getSimpleName()), false);
				}
			}
		}
		return appliedAny;
	}
}