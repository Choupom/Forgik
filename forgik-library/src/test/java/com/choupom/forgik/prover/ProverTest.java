/*
 * Java
 *
 * Copyright 2019 Andy Poudret. All rights reserved.
 */
package com.choupom.forgik.prover;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import com.choupom.forgik.formula.Formula;
import com.choupom.forgik.parser.FormulaParser;
import com.choupom.forgik.rule.Rule;
import com.choupom.forgik.rule.RuleParser;

public class ProverTest {

	private static final Formula[] ANTECEDENTS = new Formula[] { FormulaParser.parse("-(p > q)") };
	private static final Formula[] CONSEQUENTS = new Formula[] { FormulaParser.parse("p"), FormulaParser.parse("-q") };

	private static final Rule RULE_DOUBLE_NEG_ELIMINATION;
	private static final Rule RULE_EFQ;
	private static final Rule RULE_IMPLICATION_INTRO;
	private static final Rule RULE_RAA;

	static {
		try {
			RULE_DOUBLE_NEG_ELIMINATION = RuleParser.parseRule("double_neg_elim");
			RULE_EFQ = RuleParser.parseRule("efq");
			RULE_IMPLICATION_INTRO = RuleParser.parseRule("implication_intro");
			RULE_RAA = RuleParser.parseRule("raa");
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

	@Test
	public void testEasy() {
		Prover prover = new Prover(ANTECEDENTS, CONSEQUENTS);

		prover.proveByRule(0, RULE_DOUBLE_NEG_ELIMINATION);
		prover.proveByRule(0, RULE_RAA);
		prover.completeConsequent(1, 0);
		prover.proveByRule(0, RULE_IMPLICATION_INTRO);
		prover.proveByRule(0, RULE_EFQ);
		prover.completeConsequent(0, 2);
		prover.completeConsequent(1, 1);

		prover.proveByRule(1, RULE_RAA);
		prover.completeConsequent(1, 0);
		prover.proveByRule(0, RULE_IMPLICATION_INTRO);
		prover.completeConsequent(0, 1);

		Assert.assertNull(prover.getProofInfo());
	}

	@Test
	public void testHard() {
		Prover prover = new Prover(ANTECEDENTS, CONSEQUENTS);

		prover.proveByRule(0, RULE_DOUBLE_NEG_ELIMINATION);
		prover.proveByRule(0, RULE_RAA);
		prover.proveByRule(0, RULE_IMPLICATION_INTRO);
		prover.proveByRule(0, RULE_EFQ);
		prover.completeConsequent(0, 2);
		prover.completeConsequent(1, 1);
		prover.completeConsequent(1, 0);

		prover.proveByRule(1, RULE_RAA);
		prover.proveByRule(0, RULE_IMPLICATION_INTRO);
		prover.completeConsequent(0, 1);
		prover.completeConsequent(1, 0);

		Assert.assertNull(prover.getProofInfo());
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
