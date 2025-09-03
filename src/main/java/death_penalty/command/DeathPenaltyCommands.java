package death_penalty.command;

import com.mojang.brigadier.CommandDispatcher;
import death_penalty.config.ConfigManager;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public final class DeathPenaltyCommands {
	private DeathPenaltyCommands() {}

	public static void register() {
		CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> register(dispatcher));
	}

	private static void register(CommandDispatcher<ServerCommandSource> d) {
		d.register(CommandManager.literal("deathpenalty")
				.requires(src -> src.hasPermissionLevel(2))
				.then(CommandManager.literal("reload").executes(ctx -> {
					ConfigManager.load();
					ctx.getSource().sendFeedback(new TranslatableText("command.death_penalty.reload"), true);
					return 1;
				}))
		);

		d.register(CommandManager.literal("dpreload")
				.requires(src -> src.hasPermissionLevel(2))
				.executes(ctx -> {
					ConfigManager.load();
					ctx.getSource().sendFeedback(new TranslatableText("command.death_penalty.reload"), true);
					return 1;
				})
		);
	}
}