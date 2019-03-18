/*
 * Java
 *
 * Copyright 2019 Andy Poudret. All rights reserved.
 */
package com.choupom.forgik;

import java.util.HashMap;

import com.choupom.forgik.ConsoleProverIO.Decision;
import com.choupom.forgik.formula.Formula;
import com.choupom.forgik.identifier.FormulaIdentifier;
import com.choupom.forgik.identifier.Identification;
import com.choupom.forgik.prover.ProofInfo;
import com.choupom.forgik.prover.Prover;
import com.choupom.forgik.rule.Rulebook;
import com.choupom.forgik.suggester.FormulaSuggester;
import com.choupom.forgik.suggester.Suggestion;

public class ConsoleProver {

	private ConsoleProver() {
		// private constructor
	}

	public static void prove(Formula[] antecedents, Formula[] consequents, Rulebook rulebook) {
		Prover prover = new Prover(antecedents, consequents);
		ConsoleProverIO io = new ConsoleProverIO();

		while (true) {
			if (!proveStep(prover, io, rulebook)) {
				break;
			}
		}
	}

	public static boolean proveStep(Prover prover, ConsoleProverIO io, Rulebook rulebook) {
		ProofInfo proofInfo = prover.getProofInfo();
		if (proofInfo == null) {
			return false;
		}

		Formula[] antecedents = proofInfo.getAntecedents();
		Formula[] consequents = proofInfo.getConsequents();
		boolean[] completedConsequents = proofInfo.getCompletedConsequents();

		int goalId = io.requestSubproof(antecedents, consequents, completedConsequents);
		if (goalId == -1) {
			prover.cancelProof();
			return true;
		}

		Formula consequent = consequents[goalId];

		Decision decision = io.requestDecision(antecedents, consequent);

		if (decision == Decision.CANCEL_PROOF) {
			prover.cancelProof();
		} else if (decision == Decision.COMPLETE_PROOF) {
			if (checkProved(antecedents, consequent)) {
				Identification identification = new Identification(consequent, new HashMap<String, Formula>());
				prover.completeConsequent(goalId, identification);
			} else {
				Identification[] identifications = getIdentifications(antecedents, consequent);

				int antecedentId = io.requestIdentification(identifications);
				if (antecedentId != -1) {
					prover.completeConsequent(goalId, identifications[antecedentId]);
				}
			}
		} else if (decision == Decision.ASSUME) {
			prover.proveImplication(goalId);
		} else if (decision == Decision.ASSUME_NEGATION) {
			prover.proveByContradiction(goalId);
		} else if (decision == Decision.SUGGEST_RULE) {
			Suggestion[] suggestions = FormulaSuggester.suggestFromRulebook(consequent, rulebook);

			int suggestionId = io.requestSuggestion(suggestions);
			if (suggestionId != -1) {
				Suggestion suggestion = suggestions[suggestionId];
				prover.proveByRule(goalId, suggestion.getRule());
			}
		}

		return true;
	}

	private static boolean checkProved(Formula[] antecedents, Formula consequent) {
		for (Formula antecedent : antecedents) {
			if (antecedent.checkEquals(consequent)) {
				return true;
			}
		}
		return false;
	}

	public static Identification[] getIdentifications(Formula[] antecedents, Formula consequent) {
		Identification[] identifications = new Identification[antecedents.length];
		for (int i = 0; i < identifications.length; i++) {
			identifications[i] = FormulaIdentifier.identify(antecedents[i], consequent);
		}
		return identifications;
	}
}
