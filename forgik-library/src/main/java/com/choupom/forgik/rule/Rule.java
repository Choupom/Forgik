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
import com.choupom.forgik.formula.FormulaSettings;
import com.choupom.forgik.formula.FreeVariable;

public class Rule {

	private final String name;
	private final Formula[] assumptions;
	private final Formula[] antecedents;
	private final Formula consequent;
	private final String[] freeVariables;

	public Rule(String name, Formula[] assumptions, Formula[] antecedents, Formula consequent) {
		this.name = name;
		this.assumptions = assumptions;
		this.antecedents = antecedents;
		this.consequent = consequent;

		Set<String> freeVariablesSet = new HashSet<>();
		for (Formula ruleAssumption : assumptions) {
			ruleAssumption.getFreeVariables(freeVariablesSet);
		}
		for (Formula ruleAntecedent : antecedents) {
			ruleAntecedent.getFreeVariables(freeVariablesSet);
		}
		consequent.getFreeVariables(freeVariablesSet);
		this.freeVariables = freeVariablesSet.toArray(new String[freeVariablesSet.size()]);
	}

	public String getName() {
		FormulaSettings settings = FormulaSettings.getInstance();
		String string = this.name;
		string = string.replace(FormulaSettings.DEFAULT_CONJUNCTION_STRING, settings.getConjunctionString());
		string = string.replace(FormulaSettings.DEFAULT_DISJUNCTION_STRING, settings.getDisjunctionString());
		string = string.replace(FormulaSettings.DEFAULT_IMPLICATION_STRING, settings.getImplicationString());
		string = string.replace(FormulaSettings.DEFAULT_NEGATION_STRING, settings.getNegationString());
		return string;
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
		for (String freeVariable : this.freeVariables) {
			if (!simpleMap.containsKey(freeVariable)) {
				leftover.add(freeVariable);
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
		Set<String> consequentVariables = new HashSet<>();
		consequent.getFreeVariables(consequentVariables);
		for (String consequentVariable : consequentVariables) {
			Formula mapped = simpleMap.get(consequentVariable);
			if (mapped != null) {
				mapped = mapped.apply(simpleMap);
				if (!mapped.checkEquals(new FreeVariable(consequentVariable))) {
					consequentMap.put(consequentVariable, mapped);
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
			if (!formulas.get(i).checkEquals(reference)) {
				return false;
			}
		}

		return true;
	}
}
