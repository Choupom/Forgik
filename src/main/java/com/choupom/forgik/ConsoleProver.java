/*
 * Java
 *
 * Copyright 2019 Andy Poudret. All rights reserved.
 */
package com.choupom.forgik;

import java.util.HashMap;

import com.choupom.forgik.formula.Formula;
import com.choupom.forgik.identifier.FormulaIdentifier;
import com.choupom.forgik.identifier.Identification;
import com.choupom.forgik.proof.ProofIO;
import com.choupom.forgik.proof.ProofIO.Decision;
import com.choupom.forgik.proof.ProofInfo;
import com.choupom.forgik.proof.Prover;
import com.choupom.forgik.rule.Rulebook;
import com.choupom.forgik.suggester.FormulaSuggesterReverse;
import com.choupom.forgik.suggester.SuggestionReverse;

public class ConsoleProver {

	private ConsoleProver() {
		// private constructor
	}

	public static void prove(Formula[] antecedents, Formula consequents[], Rulebook rulebook) {
		Prover prover = new Prover(antecedents, consequents);
		ProofIO io = new StandardProofIO();

		while (true) {
			ProofInfo proofInfo = prover.getProofInfo();
			if (proofInfo == null) {
				return;
			}

			int goalId = io.requestSubproof(proofInfo.antecedents, proofInfo.consequents,
					proofInfo.completedConsequents);
			if (goalId == -1) {
				prover.cancelProof();
				continue;
			}

			Formula consequent = proofInfo.consequents[goalId];

			Decision decision = io.requestDecision(proofInfo.antecedents, consequent);

			if (decision == Decision.CANCEL_PROOF) {
				prover.cancelProof();
			} else if (decision == Decision.COMPLETE_PROOF) {
				if (checkProved(proofInfo.antecedents, consequent)) {
					Identification identification = new Identification(consequent, new HashMap<String, Formula>());
					prover.completeConsequent(goalId, identification);
				} else {
					Identification[] identifications = getIdentifications(proofInfo.antecedents, consequent);

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
				SuggestionReverse[] suggestions = getSuggestedRules(consequent, rulebook);

				int suggestionId = io.requestSuggestion(suggestions);
				if (suggestionId != -1) {
					prover.proveByRule(goalId, suggestions[suggestionId]);
				}
			}
		}
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

	public static SuggestionReverse[] getSuggestedRules(Formula consequent, Rulebook rulebook) {
		return FormulaSuggesterReverse.suggestFromRulebook(consequent, rulebook);
	}
}
