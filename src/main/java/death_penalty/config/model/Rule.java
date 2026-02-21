package death_penalty.config.model;

import death_penalty.util.DeathContext;
import death_penalty.util.RuleMatcher;

import java.util.ArrayList;
import java.util.List;

public class Rule {
	public String name = "unnamed";
	public int priority = 0;
	public List<String> when = new ArrayList<>();
	public List<String> where = new ArrayList<>();
	public List<String> from = new ArrayList<>();
	public List<String> using = new ArrayList<>();

	public List<Penalty> penalties = new ArrayList<>();

	public boolean matches(DeathContext deathContext) {
		if (!RuleMatcher.anyPlainMatch(deathContext.causeId, when)) return false;
		if (!RuleMatcher.anyPlainMatch(deathContext.dimensionId, where)) return false;

		if (!from.isEmpty()) {
			if (deathContext.attackerTypeId == null) return false;
			if (!RuleMatcher.anyEntityMatch(deathContext.attackerTypeId, from)) return false;
		}

		if (!using.isEmpty()) {
			boolean ok = false;
			if (deathContext.sourceTypeId != null && RuleMatcher.anyEntityMatch(deathContext.sourceTypeId, using)) ok = true;
			if (deathContext.attackerHeldItemId != null && RuleMatcher.anyItemMatch(deathContext.attackerHeldItemId, using)) ok = true;
			if (deathContext.projectileItemId != null && RuleMatcher.anyItemMatch(deathContext.projectileItemId, using)) ok = true;
			if (!ok) return false;
		}

		return true;
	}
}