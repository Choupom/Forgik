/*
 * Java
 *
 * Copyright 2019 Andy Poudret. All rights reserved.
 */
package com.choupom.forgik.rule;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.choupom.forgik.formula.Formula;

public class Rule {

	private final Formula[] antecedents; // "premises"
	private final Formula consequent; // "conclusion"

	public Rule(Formula[] antecedents, Formula consequent) {
		this.antecedents = antecedents.clone();
		this.consequent = consequent;
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

	public boolean mayApply(int numFormulas) {
		return (numFormulas == this.antecedents.length);
	}

	public Formula apply(Formula[] formulas, List<String> leftover) {
		Map<String, Formula> map = new HashMap<>();
		for (int i = 0; i < this.antecedents.length; i++) {
			if (!this.antecedents[i].identify(formulas[i], map)) {
				return null;
			}
		}

		return this.consequent.apply(map, leftover);
	}

	public Formula[] applyReverse(Formula formula) {
		Map<String, Formula> map = new HashMap<>();
		if (!this.consequent.identify(formula, map)) {
			return null;
		}

		Formula[] formulas = new Formula[this.antecedents.length];
		for (int i = 0; i < this.antecedents.length; i++) {
			formulas[i] = this.antecedents[i].apply(map, null);
		}
		return formulas;
	}
}
