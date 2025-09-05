package death_penalty.config;

import com.google.gson.*;
import death_penalty.TheDeathPenalty;
import death_penalty.config.model.DeathConfig;
import death_penalty.config.model.Penalty;
import death_penalty.config.model.penalties.AddEffectPenalty;
import death_penalty.config.model.penalties.SetFoodPenalty;
import death_penalty.config.model.penalties.SetHealthPenalty;
import death_penalty.config.model.penalties.XpPercentPenalty;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public final class ConfigManager {
	private static final Path PATH = Path.of("config", TheDeathPenalty.MOD_ID + ".json");
	private static DeathConfig CURRENT;

	private static final Gson GSON = new GsonBuilder()
			.registerTypeAdapter(Penalty.class, new PenaltyAdapter())
			.addSerializationExclusionStrategy(new ExclusionStrategy() {
				@Override public boolean shouldSkipField(FieldAttributes field) {
					return field.getDeclaringClass() == DeathConfig.Global.class
							&& field.getName().equals("dev");
				}
				@Override public boolean shouldSkipClass(Class<?> c) { return false; }
			})
			.setPrettyPrinting()
			.create();

	public static void load() {
		try {
			if (!Files.exists(PATH)) {
				Files.createDirectories(PATH.getParent());
				CURRENT = DeathConfig.defaultConfig();
				save();
				return;
			}
			try (Reader reader = Files.newBufferedReader(PATH, StandardCharsets.UTF_8)) {
				CURRENT = GSON.fromJson(reader, DeathConfig.class);
			}
			if (CURRENT == null) CURRENT = DeathConfig.defaultConfig();
		} catch (IOException error) {
			error.printStackTrace();
			CURRENT = DeathConfig.defaultConfig();
		}
	}

	public static void save() {
		try (Writer writer = Files.newBufferedWriter(PATH, StandardCharsets.UTF_8)) {
			GSON.toJson(CURRENT, writer);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static DeathConfig get() { return CURRENT; }

	private static final class PenaltyAdapter implements JsonSerializer<Penalty>, JsonDeserializer<Penalty> {
		@Override
		public JsonElement serialize(Penalty src, Type typeOfSrc, JsonSerializationContext ctx) {
			String type = penaltyType(src);
			JsonObject object = ctx.serialize(src, src.getClass()).getAsJsonObject();
			object.addProperty("type", type);
			return object;
		}

		@Override
		public Penalty deserialize(JsonElement element, Type typeOfT, JsonDeserializationContext c) throws JsonParseException {
			if (!element.isJsonObject())
				throw new JsonParseException("Penalty must be a JSON object, got: " + element);

			JsonObject object = element.getAsJsonObject();
			if (!object.has("type") || object.get("type").isJsonNull())
				throw new JsonParseException("Penalty object missing required 'type': " + object);

			String type = object.getAsJsonPrimitive("type").getAsString();
			switch (type) {
				case "set_food": return c.deserialize(object, SetFoodPenalty.class);
				case "set_health": return c.deserialize(object, SetHealthPenalty.class);
				case "add_effect": return c.deserialize(object, AddEffectPenalty.class);
				case "xp_percent": return c.deserialize(object, XpPercentPenalty.class);
				default: throw new JsonParseException("Unknown penalty type: " + type);
			}
		}

		private static String penaltyType(Penalty penalty) {
			if (penalty instanceof SetFoodPenalty) return "set_food";
			if (penalty instanceof SetHealthPenalty) return "set_health";
			if (penalty instanceof AddEffectPenalty) return "add_effect";
			if (penalty instanceof XpPercentPenalty) return "xp_percent";
			throw new IllegalStateException("Unknown Penalty subtype: " + penalty.getClass());
		}
	}
}