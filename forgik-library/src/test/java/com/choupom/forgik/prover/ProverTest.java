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
import com.choupom.forgik.formula.Formulas;
import com.choupom.forgik.parser.FormulaParser;
import com.choupom.forgik.proof.ProofConverter;
import com.choupom.forgik.proof.linear.AssumptionStatement;
import com.choupom.forgik.proof.linear.PremiseStatement;
import com.choupom.forgik.proof.linear.RuleStatement;
import com.choupom.forgik.proof.linear.Statement;
import com.choupom.forgik.rule.Rule;
import com.choupom.forgik.rule.RuleParser;

public class ProverTest {

	private static final Formulas ANTECEDENTS;
	private static final Formulas CONSEQUENTS;

	private static final Rule RULE_DOUBLE_NEG_ELIMINATION;
	private static final Rule RULE_EFQ;
	private static final Rule RULE_IMPLICATION_INTRO;
	private static final Rule RULE_RAA;

	static {
		try {
			ANTECEDENTS = Formulas.list(FormulaParser.parse("-(P > Q)"));
			CONSEQUENTS = Formulas.list(FormulaParser.parse("P"), FormulaParser.parse("-Q"));
			RULE_DOUBLE_NEG_ELIMINATION = RuleParser.parseRule("double_neg_elim");
			RULE_EFQ = RuleParser.parseRule("efq");
			RULE_IMPLICATION_INTRO = RuleParser.parseRule("implication_intro");
			RULE_RAA = RuleParser.parseRule("raa");
		} catch (IOException e) {
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

		checkPremiseStatement(statements[0], "-(P > Q)");
		checkAssumptionStatement(statements[1], 0, "-P");
		checkAssumptionStatement(statements[2], 1, "P");
		checkRuleStatement(statements[3], 2, "Q", "EFQ", new int[] { 2, 1 });
		checkRuleStatement(statements[4], 1, "P > Q", ">I", new int[] { 2, 3 });
		checkRuleStatement(statements[5], 0, "--P", "RAA", new int[] { 1, 4, 0 });
		checkRuleStatement(statements[6], 0, "P", "--E", new int[] { 5 });
		checkAssumptionStatement(statements[7], 0, "Q");
		checkAssumptionStatement(statements[8], 1, "P");
		checkRuleStatement(statements[9], 1, "P > Q", ">I", new int[] { 8, 7 });
		checkRuleStatement(statements[10], 0, "-Q", "RAA", new int[] { 7, 9, 0 });
	}

	private static void checkPremiseStatement(Statement s, String expectedConclusionString) {
		Formula expectedConclusion = FormulaParser.parse(expectedConclusionString);

		Assert.assertTrue(s instanceof PremiseStatement);
		PremiseStatement statement = (PremiseStatement) s;

		Assert.assertEquals(0, statement.getDepth());
		Assert.assertEquals(expectedConclusion, statement.getConclusion());
	}

	private static void checkAssumptionStatement(Statement s, int expectedDepth, String expectedConclusionString) {
		Formula expectedConclusion = FormulaParser.parse(expectedConclusionString);

		Assert.assertTrue(s instanceof AssumptionStatement);
		AssumptionStatement statement = (AssumptionStatement) s;

		Assert.assertEquals(expectedDepth, statement.getDepth());
		Assert.assertEquals(expectedConclusion, statement.getConclusion());
	}

	private static void checkRuleStatement(Statement s, int expectedDepth, String expectedConclusionString,
			String expectedRuleName, int[] expectedAntecedentStatements) {
		Formula expectedConclusion = FormulaParser.parse(expectedConclusionString);

		Assert.assertTrue(s instanceof RuleStatement);
		RuleStatement statement = (RuleStatement) s;

		Assert.assertEquals(expectedDepth, statement.getDepth());
		Assert.assertEquals(expectedConclusion, statement.getConclusion());
		Assert.assertEquals(expectedRuleName, statement.getRule().getName());
		Assert.assertArrayEquals(expectedAntecedentStatements, statement.getAntecedentStatements());
	}

	@SuppressWarnings("unused")
	private static void printProofInfo(ProofInfo proofInfo) {
		int index = 0;
		for (Formula antecedent : proofInfo.getAntecedents()) {
			System.out.println("[" + index + "] " + antecedent);
			index++;
		}

		System.out.println("...");

		boolean[] completedConsequents = proofInfo.getCompletedConsequents();
		index = 0;
		for (Formula consequent : proofInfo.getConsequents()) {
			if (!completedConsequents[index]) {
				System.out.println("[G" + index + "] " + consequent + "?");
			}
			index++;
		}
	}
}
