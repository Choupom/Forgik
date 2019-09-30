/*
 * Java
 *
 * Copyright 2019 Andy Poudret. All rights reserved.
 */
package com.choupom.forgik.rule;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.choupom.forgik.formula.Formula;
import com.choupom.forgik.parser.FormulaParser;

public class Rule {

	private final String name;
	private final Formula[] antecedents; // "premises"
	private final Formula consequent; // "conclusion"

	public Rule(String name, String[] antecedentsStr, String consequentStr) {
		this.name = name;

		this.antecedents = new Formula[antecedentsStr.length];
		for (int i = 0; i < antecedentsStr.length; i++) {
			this.antecedents[i] = FormulaParser.parse(antecedentsStr[i]);
		}

		this.consequent = FormulaParser.parse(consequentStr);
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
				stringBuilder.append(" ; ");
			}
			stringBuilder.append(this.antecedents[i]);
		}
		stringBuilder.append(" -| ");
		stringBuilder.append(this.consequent);
		return stringBuilder.toString();
	}

	public Formula[] apply(Formula consequent, Set<String> leftover) {
		Map<String, List<Formula>> map = new HashMap<>();
		if (!this.consequent.identify(consequent, map)) {
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

		if (leftover != null) {
			for (Formula ruleAntecedent : this.antecedents) {
				ruleAntecedent.getFreeVariables(leftover);
			}
			leftover.removeAll(simpleMap.keySet());
		}

		Formula[] antecedents = new Formula[this.antecedents.length];
		for (int i = 0; i < this.antecedents.length; i++) {
			antecedents[i] = this.antecedents[i].apply(simpleMap);
		}

		return antecedents;
	}

	private static boolean checkAllEquals(List<Formula> formulas) {
		if (formulas.size() < 2) {
			return true;
		}

		Formula reference = formulas.get(0);
		for (int i = 1; i < formulas.size(); i++) {
			if (!formulas.get(i).checkEquals(reference)) {
				return false;
			}
		}

		return true;
	}
}
