package death_penalty.util;

import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

public final class RuleMatcher {
	private RuleMatcher() {}

	private static final Map<String, Pattern> GLOB_CACHE = new ConcurrentHashMap<>();

	public static boolean anyPlainMatch(String value, List<String> patterns) {
		if (patterns == null || patterns.isEmpty()) return true;
		if (value == null) return false;
		for (String pattern : patterns) {
			if (pattern == null || pattern.isBlank()) continue;
			if (matchesGlobOrExact(value, pattern.trim())) return true;
		}
		return false;
	}

	public static boolean anyEntityMatch(String entityTypeId, List<String> patterns) {
		if (patterns == null || patterns.isEmpty()) return true;
		if (entityTypeId == null) return false;
		for (String pattern : patterns) {
			if (pattern == null || pattern.isBlank()) continue;
			String pat = pattern.trim();
			if (pat.startsWith("#")) {
				if (isEntityTypeInTag(entityTypeId, pat.substring(1))) return true;
			} else {
				if (matchesGlobOrExact(entityTypeId, pat)) return true;
			}
		}
		return false;
	}

	public static boolean anyItemMatch(String itemId, List<String> patterns) {
		if (patterns == null || patterns.isEmpty()) return true;
		if (itemId == null) return false;
		for (String pattern : patterns) {
			if (pattern == null || pattern.isBlank()) continue;
			String pat = pattern.trim();
			if (pat.startsWith("#")) {
				if (isItemInTag(itemId, pat.substring(1))) return true;
			} else {
				if (matchesGlobOrExact(itemId, pat)) return true;
			}
		}
		return false;
	}

	private static boolean matchesGlobOrExact(String value, String pattern) {
		if (value.equals(pattern)) return true;
		if (!pattern.contains("*")) return false;

		StringBuilder regex = new StringBuilder();
		regex.append('^');
		for (int i = 0; i < pattern.length(); i++) {
			char chars = pattern.charAt(i);
			if (chars == '*') {
				regex.append(".*");
			} else {
				if ("\\.^$|?+()[]{}".indexOf(chars) >= 0) regex.append('\\');
				regex.append(chars);
			}
		}
		regex.append('$');

		Pattern compiled = GLOB_CACHE.computeIfAbsent(regex.toString(), Pattern::compile);
		return compiled.matcher(value).matches();
	}

	private static boolean isItemInTag(String itemId, String tagIdRaw) {
		Identifier itemIdentifier = Identifier.tryParse(itemId);
		Identifier tagIdentifier = Identifier.tryParse(tagIdRaw);

		if (itemIdentifier == null || tagIdentifier == null) return false;

		Item item = Registry.ITEM.getOrEmpty(itemIdentifier).orElse(null);

		if (item == null) return false;

		TagKey<Item> tag = TagKey.of(Registry.ITEM_KEY, tagIdentifier);
		return item.getRegistryEntry().isIn(tag);
	}

	private static boolean isEntityTypeInTag(String entityTypeId, String tagIdRaw) {
		Identifier entIdent = Identifier.tryParse(entityTypeId);
		Identifier tagIdent = Identifier.tryParse(tagIdRaw);

		if (entIdent == null || tagIdent == null) return false;

		EntityType<?> type = Registry.ENTITY_TYPE.getOrEmpty(entIdent).orElse(null);

		if (type == null) return false;

		TagKey<EntityType<?>> tag = TagKey.of(Registry.ENTITY_TYPE_KEY, tagIdent);
		return type.getRegistryEntry().isIn(tag);
	}
}