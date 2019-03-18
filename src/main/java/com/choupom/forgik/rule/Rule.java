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

	private final Formula[] antecedents; // "premises"
	private final Formula consequent; // "conclusion"

	public Rule(String consequentStr, String... antecedentsStr) {
		this.antecedents = new Formula[antecedentsStr.length];
		for (int i = 0; i < antecedentsStr.length; i++) {
			this.antecedents[i] = FormulaParser.parse(antecedentsStr[i]);
		}

		this.consequent = FormulaParser.parse(consequentStr);
	}

	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
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

	public Formula[] apply(Formula formula, Set<String> leftover) {
		Map<String, List<Formula>> map = new HashMap<>();
		if (!this.consequent.identify(formula, map)) {
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

		Formula[] formulas = new Formula[this.antecedents.length];
		for (int i = 0; i < this.antecedents.length; i++) {
			formulas[i] = this.antecedents[i].apply(simpleMap, leftover);
		}
		return formulas;
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
