/*
 * Java
 *
 * Copyright 2019 Andy Poudret. All rights reserved.
 */
package com.choupom.forgik.console;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.choupom.forgik.formula.Conjunction;
import com.choupom.forgik.formula.Formula;
import com.choupom.forgik.formula.Implication;
import com.choupom.forgik.parser.FormulaParser;
import com.choupom.forgik.rule.Rule;
import com.choupom.forgik.rulebook.RulebookParser;

public class Main {

	private static final String RULEBOOK = "classical_logic";

	// private static final String MAIN_FORMULA = "((p > q) ^ (q > r)) > (p > r)";
	// private static final String MAIN_FORMULA = "p > ((p v p) ^ p)";
	// private static final String MAIN_FORMULA = "p > --p";
	// private static final String MAIN_FORMULA = "-(p > q) > --p";
	private static final String MAIN_FORMULA = "-(p > q) > (p ^ -q)";
	// private static final String MAIN_FORMULA = "(p > q) > -(p ^ -q)";
	// private static final String MAIN_FORMULA = "(p v (p ^ p)) > p";
	// private static final String MAIN_FORMULA = "((p v q) ^ -p) > q";
	// private static final String MAIN_FORMULA = "((p > q) ^ -q) > -p";
	// private static final String MAIN_FORMULA = "p v -p";
	// private static final String MAIN_FORMULA = "-p > (p > q)";

	public static void main(String[] args) throws IOException {
		Formula formula = FormulaParser.parse(MAIN_FORMULA);

		Formula[] antecedents;
		Formula[] consequents;
		if (formula instanceof Implication) {
			Implication implication = (Implication) formula;

			List<Formula> antecedentsList = new ArrayList<>();
			getConjuctions(implication.getOperand1(), antecedentsList);

			List<Formula> consequentsList = new ArrayList<>();
			getConjuctions(implication.getOperand2(), consequentsList);

			antecedents = antecedentsList.toArray(new Formula[antecedentsList.size()]);
			consequents = consequentsList.toArray(new Formula[consequentsList.size()]);
		} else {
			antecedents = new Formula[0];
			consequents = new Formula[] { formula };
		}

		Rule[] rules = RulebookParser.parseRulebook(RULEBOOK).getRules();

		ConsoleProver.prove(antecedents, consequents, rules);
	}

	private static void getConjuctions(Formula formula, List<Formula> list) {
		if (formula instanceof Conjunction) {
			Conjunction conjunction = (Conjunction) formula;
			getConjuctions(conjunction.getOperand1(), list);
			getConjuctions(conjunction.getOperand2(), list);
		} else {
			list.add(formula);
		}
	}
}
