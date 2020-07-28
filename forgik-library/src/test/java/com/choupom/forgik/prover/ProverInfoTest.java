/*
 * Java
 *
 * Copyright 2020 Andy Poudret. All rights reserved.
 */
package com.choupom.forgik.prover;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import com.choupom.forgik.formula.Formula;
import com.choupom.forgik.formula.Formulas;
import com.choupom.forgik.parser.FormulaParser;
import com.choupom.forgik.rule.Rule;
import com.choupom.forgik.rule.RuleParser;

public class ProverInfoTest {

	private static final Formulas ANTECEDENTS;
	private static final Formulas CONSEQUENTS;

	private static final Rule RULE_EFQ;
	private static final Rule RULE_RAA;

	static {
		try {
			ANTECEDENTS = Formulas.list(FormulaParser.parse("P"));
			CONSEQUENTS = Formulas.list(FormulaParser.parse("(P v P) ^ P"));
			RULE_EFQ = RuleParser.parseRule("efq");
			RULE_RAA = RuleParser.parseRule("raa");
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

	@Test
	public void test() throws ProverException {
		Prover prover = new Prover(ANTECEDENTS, CONSEQUENTS);

		prover.proveByRule(0, RULE_EFQ);
		checkProofInfo(prover, new String[] { "P" }, new String[] { "$1", "-$1" });
		prover.proveByRule(1, RULE_RAA);
		checkProofInfo(prover, new String[] { "P", "$1" }, new String[] { "$2", "-$2" });
		prover.completeConsequent(1, 1);
		checkProofInfo(prover, new String[] { "P", "-$2" }, new String[] { "$2", "-$2" });
		prover.completeConsequent(0, 0);
		checkProofInfo(prover, new String[] { "P", "-P" }, new String[] { "P", "-P" });
		prover.completeProof();
		checkProofInfo(prover, new String[] { "P" }, new String[] { "-P", "--P" });
		prover.proveByRule(0, RULE_RAA);
		checkProofInfo(prover, new String[] { "P", "P" }, new String[] { "$3", "-$3" });
		prover.completeConsequent(0, 0);
		checkProofInfo(prover, new String[] { "P", "P" }, new String[] { "P", "-P" });

		try {
			prover.completeConsequent(1, 1);
			prover.completeProof();
			prover.completeProof();
			Assert.fail();
		} catch (ProverException e) {
			// expected exception
		}
	}

	private static void checkProofInfo(Prover prover, String[] antecedents, String[] consequents) {
		ProofInfo info = prover.getProofInfo();
		Formulas infoAntecedents = info.getAntecedents();
		Formulas infoConsequents = info.getConsequents();

		Assert.assertEquals(antecedents.length, infoAntecedents.size());
		Assert.assertEquals(consequents.length, infoConsequents.size());

		for (int i = 0; i < antecedents.length; i++) {
			Formula antecedent = FormulaParser.parse(antecedents[i]);
			Assert.assertEquals(antecedent, infoAntecedents.get(i));
		}

		for (int i = 0; i < consequents.length; i++) {
			Formula consequent = FormulaParser.parse(consequents[i]);
			Assert.assertEquals(consequent, infoConsequents.get(i));
		}
	}
}
