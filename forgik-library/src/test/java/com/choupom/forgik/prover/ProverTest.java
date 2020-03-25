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
import com.choupom.forgik.parser.FormulaParserException;
import com.choupom.forgik.proof.ProofConverter;
import com.choupom.forgik.proof.linear.AssumptionStatement;
import com.choupom.forgik.proof.linear.RuleStatement;
import com.choupom.forgik.proof.linear.Statement;
import com.choupom.forgik.rule.Rule;
import com.choupom.forgik.rule.RuleParser;

public class ProverTest {

	private static final Formula[] ANTECEDENTS;
	private static final Formula[] CONSEQUENTS;

	private static final Rule RULE_DOUBLE_NEG_ELIMINATION;
	private static final Rule RULE_EFQ;
	private static final Rule RULE_IMPLICATION_INTRO;
	private static final Rule RULE_RAA;

	static {
		try {
			ANTECEDENTS = new Formula[] { FormulaParser.parse("-(P > Q)") };
			CONSEQUENTS = new Formula[] { FormulaParser.parse("P"), FormulaParser.parse("-Q") };
			RULE_DOUBLE_NEG_ELIMINATION = RuleParser.parseRule("double_neg_elim");
			RULE_EFQ = RuleParser.parseRule("efq");
			RULE_IMPLICATION_INTRO = RuleParser.parseRule("implication_intro");
			RULE_RAA = RuleParser.parseRule("raa");
		} catch (IOException | FormulaParserException e) {
			throw new IllegalStateException(e);
		}
	}

	@Test
	public void testEasy() throws ProverException {
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

		checkProof(prover);
	}

	@Test
	public void testHard() throws ProverException {
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

		checkProof(prover);
	}

	private void checkProof(Prover prover) {
		Assert.assertTrue(prover.isMainProofComplete());

		ProofInfo info = prover.getProofInfo();
		Statement[] statements = ProofConverter.generateLinearProof(info.getAntecedents(), info.getConsequentProofs());

		checkAssumptionStatement(statements[0], "-(P > Q)");
		checkAssumptionStatement(statements[1], "-P");
		checkAssumptionStatement(statements[2], "P");
		checkRuleStatement(statements[3], "Q", "EFQ", new int[] { 2, 1 });
		checkRuleStatement(statements[4], "P > Q", ">I", new int[] { 2, 3 });
		checkRuleStatement(statements[5], "--P", "RAA", new int[] { 1, 4, 0 });
		checkRuleStatement(statements[6], "P", "--E", new int[] { 5 });
		checkAssumptionStatement(statements[7], "Q");
		checkAssumptionStatement(statements[8], "P");
		checkRuleStatement(statements[9], "P > Q", ">I", new int[] { 8, 7 });
		checkRuleStatement(statements[10], "-Q", "RAA", new int[] { 7, 9, 0 });
	}

	private static void checkAssumptionStatement(Statement s, String expectedConclusionString) {
		Formula expectedConclusion;
		try {
			expectedConclusion = FormulaParser.parse(expectedConclusionString);
		} catch (FormulaParserException e) {
			throw new IllegalArgumentException(e);
		}

		Assert.assertTrue(s instanceof AssumptionStatement);
		AssumptionStatement statement = (AssumptionStatement) s;

		Assert.assertEquals(statement.getConclusion(), expectedConclusion);
	}

	private static void checkRuleStatement(Statement s, String expectedConclusionString, String expectedRuleName,
			int[] expectedAntecedentStatements) {
		Formula expectedConclusion;
		try {
			expectedConclusion = FormulaParser.parse(expectedConclusionString);
		} catch (FormulaParserException e) {
			throw new IllegalArgumentException(e);
		}

		Assert.assertTrue(s instanceof RuleStatement);
		RuleStatement statement = (RuleStatement) s;

		Assert.assertEquals(expectedConclusion, statement.getConclusion());
		Assert.assertEquals(expectedRuleName, statement.getRule().getName());
		Assert.assertArrayEquals(expectedAntecedentStatements, statement.getAntecedentStatements());
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
