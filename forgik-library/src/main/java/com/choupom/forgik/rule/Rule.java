/*
 * Java
 *
 * Copyright 2019 Andy Poudret. All rights reserved.
 */
package com.choupom.forgik.rule;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.choupom.forgik.formula.Formula;
import com.choupom.forgik.formula.FreeFormula;

public class Rule {

	private final String name;
	private final Formula[] assumptions;
	private final Formula[] antecedents;
	private final Formula consequent;
	private final String[] freeFormulas;

	public Rule(String name, Formula[] assumptions, Formula[] antecedents, Formula consequent) {
		this.name = name;
		this.assumptions = assumptions;
		this.antecedents = antecedents;
		this.consequent = consequent;

		Set<String> freeFormulasSet = new HashSet<>();
		for (Formula ruleAssumption : assumptions) {
			ruleAssumption.getFreeFormulas(freeFormulasSet);
		}
		for (Formula ruleAntecedent : antecedents) {
			ruleAntecedent.getFreeFormulas(freeFormulasSet);
		}
		consequent.getFreeFormulas(freeFormulasSet);
		this.freeFormulas = freeFormulasSet.toArray(new String[freeFormulasSet.size()]);
	}

	public String getName() {
		return this.name;
	}

	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(this.name);
		stringBuilder.append(": ");
		for (int i = 0; i < this.antecedents.length; i++) {
			if (i != 0) {
				stringBuilder.append(", ");
			}
			stringBuilder.append(this.antecedents[i]);
		}
		stringBuilder.append(" |- ");
		stringBuilder.append(this.consequent);
		return stringBuilder.toString();
	}

	public RuleApplicationResult apply(Formula consequent) {
		Map<String, List<Formula>> map = new HashMap<>();
		if (!this.consequent.identify(consequent, map)) { // TODO: use FormulaIdentifier
			return null;
		}

		Map<String, Formula> simpleMap = new HashMap<>();
		for (Map.Entry<String, List<Formula>> mapping : map.entrySet()) {
			List<Formula> list = mapping.getValue();
			if (!checkAllEquals(list)) {
				return null;
			}
			simpleMap.put(mapping.getKey(), list.get(0));
		}

		Set<String> leftover = new HashSet<>();
		for (String freeFormula : this.freeFormulas) {
			if (!simpleMap.containsKey(freeFormula)) {
				leftover.add(freeFormula);
			}
		}

		Formula[] assumptions = new Formula[this.assumptions.length];
		for (int i = 0; i < this.assumptions.length; i++) {
			assumptions[i] = this.assumptions[i].apply(simpleMap);
		}

		Formula[] antecedents = new Formula[this.antecedents.length];
		for (int i = 0; i < this.antecedents.length; i++) {
			antecedents[i] = this.antecedents[i].apply(simpleMap);
		}

		Map<String, Formula> consequentMap = new HashMap<>();
		Set<String> consequentFreeFormulas = new HashSet<>();
		consequent.getFreeFormulas(consequentFreeFormulas);
		for (String consequentFreeFormula : consequentFreeFormulas) {
			Formula mapped = simpleMap.get(consequentFreeFormula);
			if (mapped != null) {
				mapped = mapped.apply(simpleMap);
				if (!mapped.equals(new FreeFormula(consequentFreeFormula))) {
					consequentMap.put(consequentFreeFormula, mapped);
				}
			}
		}

		return new RuleApplicationResult(assumptions, antecedents, leftover, consequentMap);
	}

	private static boolean checkAllEquals(List<Formula> formulas) {
		if (formulas.size() < 2) {
			return true;
		}

		Formula reference = formulas.get(0);
		for (int i = 1; i < formulas.size(); i++) {
			if (!formulas.get(i).equals(reference)) {
				return false;
			}
		}

		return true;
	}
}
