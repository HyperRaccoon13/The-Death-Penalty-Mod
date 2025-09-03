package death_penalty.config.model;

import death_penalty.util.DeathContext;

import java.util.ArrayList;
import java.util.List;

public class Rule {
	public String name = "unnamed";
	public List<String> when = new ArrayList<>();
	public List<String> where = new ArrayList<>();
	public List<String> from = new ArrayList<>();
	public List<String> using = new ArrayList<>();

	public List<Penalty> penalties = new ArrayList<>();

	public boolean matches(DeathContext deathContext) {
		if (!when.isEmpty()  && !when.contains(deathContext.causeId)) return false;
		if (!where.isEmpty() && !where.contains(deathContext.dimensionId)) return false;
		if (!from.isEmpty()  && (deathContext.attackerTypeId == null || !from.contains(deathContext.attackerTypeId))) return false;

		if (!using.isEmpty()) {
			boolean ok = false;
			if (deathContext.sourceTypeId != null && using.contains(deathContext.sourceTypeId)) ok = true;
			if (deathContext.projectileItemId != null && using.contains(deathContext.projectileItemId)) ok = true;
			if (!ok) return false;
		}

		return true;
	}
}