/*
 * Java
 *
 * Copyright 2019 Andy Poudret. All rights reserved.
 */
package com.choupom.forgik.prover;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.choupom.forgik.formula.Formula;
import com.choupom.forgik.parser.FormulaParser;
import com.choupom.forgik.rule.Rule;

public class ProverTest {

	private static final Formula[] ANTECEDENTS = new Formula[] { FormulaParser.parse("-(p > q)") };
	private static final Formula[] CONSEQUENTS = new Formula[] { FormulaParser.parse("p"), FormulaParser.parse("-q") };

	private static final Rule RULE_DOUBLE_NEG_ELIMINATION = new Rule("X", "--X");
	private static final Rule RULE_EFQ = new Rule("Y", "X", "-X");

	@Test
	public void testEasy() {
		Prover prover = new Prover(ANTECEDENTS, CONSEQUENTS);

		prover.proveByRule(0, RULE_DOUBLE_NEG_ELIMINATION);
		prover.proveByContradiction(0);
		prover.completeConsequent(1, 0, createSimpleMap("A", "p > q"));
		prover.proveImplication(0);
		prover.proveByRule(0, RULE_EFQ);
		prover.completeConsequent(0, 2, createSimpleMap("B", "p"));
		prover.completeConsequent(1, 1, new HashMap<String, Formula>());

		prover.proveByContradiction(1);
		prover.completeConsequent(1, 0, createSimpleMap("C", "p > q"));
		prover.proveImplication(0);
		prover.completeConsequent(0, 1, new HashMap<String, Formula>());

		Assert.assertNull(prover.getProofInfo());
	}

	@Test
	public void testHard() {
		Prover prover = new Prover(ANTECEDENTS, CONSEQUENTS);

		prover.proveByRule(0, RULE_DOUBLE_NEG_ELIMINATION);
		prover.proveByContradiction(0);
		prover.proveImplication(0);
		prover.proveByRule(0, RULE_EFQ);
		prover.completeConsequent(0, 2, createSimpleMap("D", "B"));
		prover.completeConsequent(1, 1, createSimpleMap("B", "p"));
		prover.completeConsequent(1, 0, createSimpleMap("C", "q"));

		prover.proveByContradiction(1);
		prover.proveImplication(0);
		prover.completeConsequent(0, 1, createSimpleMap("G", "q"));
		prover.completeConsequent(1, 0, createSimpleMap("F", "p"));

		Assert.assertNull(prover.getProofInfo());
	}

	private Map<String, Formula> createSimpleMap(String variable, String formulaStr) {
		Map<String, Formula> map = new HashMap<>();
		map.put(variable, FormulaParser.parse(formulaStr));
		return map;
	}

	@SuppressWarnings("unused")
	private static void printProofInfo(ProofInfo proofInfo) {
		int i = 0;
		for (Formula antecedent : proofInfo.getAntecedents()) {
			System.out.println("[" + (i++) + "] " + antecedent);
		}
		System.out.println("...");
		for (i = 0; i < proofInfo.getConsequents().length; i++) {
			if (!proofInfo.getCompletedConsequents()[i]) {
				Formula consequent = proofInfo.getConsequents()[i];
				System.out.println("[G" + i + "] " + consequent + "?");
			}
		}
	}
}
