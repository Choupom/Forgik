/*
 * Java
 *
 * Copyright 2019 Andy Poudret. All rights reserved.
 */
package com.choupom.forgik.console;

import com.choupom.forgik.console.ConsoleProverIO.Decision;
import com.choupom.forgik.formula.Formula;
import com.choupom.forgik.identifier.FormulaIdentifier;
import com.choupom.forgik.identifier.Identification;
import com.choupom.forgik.prover.ProofInfo;
import com.choupom.forgik.prover.Prover;
import com.choupom.forgik.rule.Rule;

public class ConsoleProver {

	private ConsoleProver() {
		// private constructor
	}

	public static void prove(Formula[] antecedents, Formula[] consequents, Rule[] rules) {
		Prover prover = new Prover(antecedents, consequents);
		ConsoleProverIO io = new ConsoleProverIO();

		while (true) {
			if (!proveStep(prover, io, rules)) {
				break;
			}
		}
	}

	public static boolean proveStep(Prover prover, ConsoleProverIO io, Rule[] rules) {
		ProofInfo proofInfo = prover.getProofInfo();
		if (proofInfo == null) {
			return false;
		}

		Formula[] antecedents = proofInfo.getAntecedents();
		Formula[] consequents = proofInfo.getConsequents();
		boolean[] completedConsequents = proofInfo.getCompletedConsequents();

		int consequentId = io.requestSubproof(antecedents, consequents, completedConsequents);
		if (consequentId == -1) {
			prover.cancelProof();
			return true;
		}

		Formula consequent = consequents[consequentId];

		Decision decision = io.requestDecision(antecedents, consequent);

		if (decision == Decision.CANCEL_PROOF) {
			prover.cancelProof();
		} else if (decision == Decision.COMPLETE_PROOF) {
			int antecedentId = checkProved(antecedents, consequent);
			if (antecedentId != -1) {
				prover.completeConsequent(consequentId, antecedentId);
			} else {
				Identification[] identifications = new Identification[antecedents.length];
				for (int i = 0; i < identifications.length; i++) {
					identifications[i] = FormulaIdentifier.identify(antecedents[i], consequent);
				}

				antecedentId = io.requestIdentification(identifications);
				if (antecedentId != -1) {
					prover.completeConsequent(consequentId, antecedentId);
				}
			}
		} else if (decision == Decision.SUGGEST_RULE) {
			int suggestionId = io.requestSuggestion(rules);
			if (suggestionId != -1) {
				Rule suggestion = rules[suggestionId];
				prover.proveByRule(consequentId, suggestion);
			}
		}

		return true;
	}

	private static int checkProved(Formula[] antecedents, Formula consequent) {
		for (int i = 0; i < antecedents.length; i++) {
			if (antecedents[i].checkEquals(consequent)) {
				return i;
			}
		}
		return -1;
	}
}
