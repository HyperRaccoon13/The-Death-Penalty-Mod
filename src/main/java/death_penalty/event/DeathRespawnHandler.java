package death_penalty.event;

import death_penalty.config.ConfigManager;
import death_penalty.config.model.DeathConfig;
import death_penalty.config.model.Penalty;
import death_penalty.config.model.Rule;
import death_penalty.util.DamageDescribe;
import death_penalty.util.DeathContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TranslatableText;
import net.minecraft.world.GameRules;

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

		Rule match = deathConfig.rules.stream().filter(r -> r.matches(deathContext)).findFirst().orElse(null);
		boolean appliedAny = false;

		if (match != null) {
			for (Penalty p : match.penalties) {
				p.apply(newPlayer, deathContext);
				appliedAny = true;
			}
			if (deathConfig.global.dev) {
				newPlayer.sendMessage(new TranslatableText("text.death_penalty.applied_rule", match.name), false);
			}
		} else {
			for (Penalty penalty : deathConfig.defaultPenalties) {
				penalty.apply(newPlayer, deathContext);
				appliedAny = appliedAny || !deathConfig.defaultPenalties.isEmpty();
			}
			if (deathConfig.global.dev && appliedAny) {
				newPlayer.sendMessage(new TranslatableText("text.death_penalty.applied_default"), false);
			}
		}

		if (deathConfig.global.dev) {
			var src = oldPlayer.getRecentDamageSource();

			String attackerStr = DamageDescribe.describeEntity(src != null ? src.getAttacker() : null);
			String sourceStr = DamageDescribe.describeSource(src != null ? src.getSource() : null);

			String proj = (src != null && src.isProjectile()) ? "1" : "0";
			String fire = (src != null && src.isFire()) ? "1" : "0";
			String magic = (src != null && src.isMagic()) ? "1" : "0";
			String expl = (src != null && src.isExplosive()) ? "1" : "0";
			String using = deathContext.projectileItemId == null ? "null" : deathContext.projectileItemId;

			newPlayer.sendMessage(new net.minecraft.text.TranslatableText("text.death_penalty.debug", deathContext.causeId, attackerStr, sourceStr, proj, fire, magic, expl, using), false
			);
		}
	}

	private static String b(boolean v) { return v ? "1" : "0"; }
}