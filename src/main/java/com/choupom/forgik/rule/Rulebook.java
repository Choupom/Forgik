/*
 * Java
 *
 * Copyright 2019 Andy Poudret. All rights reserved.
 */
package com.choupom.forgik.rule;

import java.util.ArrayList;
import java.util.List;

import com.choupom.forgik.formula.Formula;
import com.choupom.forgik.parser.FormulaParser;

public class Rulebook {

	private final List<Rule> rules;

	public Rulebook() {
		this.rules = new ArrayList<>();
	}

	public Rule[] getRules() {
		return this.rules.toArray(new Rule[this.rules.size()]);
	}

	protected void addRule1(String antecedentStr, String consequentStr) {
		Formula antecedent = FormulaParser.parse(antecedentStr);
		Formula consequent = FormulaParser.parse(consequentStr);
		Formula[] antecedents = new Formula[] { antecedent };
		this.rules.add(new Rule(antecedents, consequent));
	}

	protected void addRule2(String antecedent1Str, String antecedent2Str, String consequentStr) {
		Formula antecedent1 = FormulaParser.parse(antecedent1Str);
		Formula antecedent2 = FormulaParser.parse(antecedent2Str);
		Formula consequent = FormulaParser.parse(consequentStr);
		Formula[] antecedents = new Formula[] { antecedent1, antecedent2 };
		this.rules.add(new Rule(antecedents, consequent));
	}

	protected void addRule3(String antecedent1Str, String antecedent2Str, String antecedent3Str, String consequentStr) {
		Formula antecedent1 = FormulaParser.parse(antecedent1Str);
		Formula antecedent2 = FormulaParser.parse(antecedent2Str);
		Formula antecedent3 = FormulaParser.parse(antecedent3Str);
		Formula consequent = FormulaParser.parse(consequentStr);
		Formula[] antecedents = new Formula[] { antecedent1, antecedent2, antecedent3 };
		this.rules.add(new Rule(antecedents, consequent));
	}
}
