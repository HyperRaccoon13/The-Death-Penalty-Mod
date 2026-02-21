package death_penalty.config.model.penalties;

import death_penalty.config.model.Penalty;
import death_penalty.util.DeathContext;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class AddEffectPenalty implements Penalty {
	public String type = "add_effect";
	public String id = "minecraft:weakness";
	public int amplifier = 0;
	public int durationTicks = 20 * 30;
	public boolean ambient = false;
	public boolean showParticles = true;
	public boolean showIcon = true;

	@Override
	public void apply(ServerPlayerEntity playerEntity, DeathContext ctx) {
		Identifier ident = Identifier.tryParse(id);
		if (ident == null) return;
		StatusEffect effect = Registry.STATUS_EFFECT.getOrEmpty(ident).orElse(null);
		if (effect == null) return;
		playerEntity.addStatusEffect(new StatusEffectInstance(effect, Math.max(1, durationTicks), Math.max(0, amplifier), ambient, showParticles, showIcon));
	}
}