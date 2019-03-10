/*
 * Java
 *
 * Copyright 2019 Andy Poudret. All rights reserved.
 */
package com.choupom.forgik;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.choupom.forgik.formula.Conjunction;
import com.choupom.forgik.formula.Formula;
import com.choupom.forgik.formula.Implication;
import com.choupom.forgik.parser.FormulaParser;
import com.choupom.forgik.rule.DeductionRulebook;
import com.choupom.forgik.rule.Rulebook;

public class Main {

	// private static final String MAIN_FORMULA = "((p > q) ^ (q > r)) > (p > r)";
	// private static final String MAIN_FORMULA = "p > ((p v p) ^ p)";

	private static final String MAIN_FORMULA = "p > --p";
	// private static final String MAIN_FORMULA = "(p v (p ^ p)) > p";
	// private static final String MAIN_FORMULA = "((p v q) ^ -p) > q";
	// private static final String MAIN_FORMULA = "((p > q) ^ -q) > -p";
	// private static final String MAIN_FORMULA = "-(p > q) > --p";
	// private static final String MAIN_FORMULA = "-(p > q) > (p ^ -q)";
	// private static final String MAIN_FORMULA = "(p > q) > -(p ^ -q)";

	public static void main(String[] args) {
		Formula formula = FormulaParser.parse(MAIN_FORMULA);
		Implication implication = (Implication) formula;

		List<Formula> antecedentsList = new ArrayList<>();
		getConjuctions(implication.getOperand1(), antecedentsList);

		Formula[] antecedents = antecedentsList.toArray(new Formula[antecedentsList.size()]);
		Formula consequent = implication.getOperand2();

		Rulebook rulebook = DeductionRulebook.getInstance();

		Proof proof = new Proof(antecedents, consequent);

		try (Scanner input = new Scanner(System.in)) {
			proof.prove(input, rulebook);
		}
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
