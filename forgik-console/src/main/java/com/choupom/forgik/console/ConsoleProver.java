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
import com.choupom.forgik.prover.ProofReport;
import com.choupom.forgik.prover.Prover;
import com.choupom.forgik.prover.ProverException;
import com.choupom.forgik.rule.Rule;

public class ConsoleProver {

	private ConsoleProver() {
		// private constructor
	}

	public static void prove(Formula[] antecedents, Formula[] consequents, Rule[] rules) {
		Prover prover = new Prover(antecedents, consequents);
		ConsoleProverIO io = new ConsoleProverIO();

		while (!prover.isMainProofComplete()) {
			try {
				proveStep(prover, io, rules);
			} catch (ProverException e) {
				System.out.println("Proof step failed: " + e.getMessage());
			}
		}

		printProofReports(prover.getProofInfo(), prover.getProofReports());
	}

	public static void proveStep(Prover prover, ConsoleProverIO io, Rule[] rules) throws ProverException {
		ProofInfo proofInfo = prover.getProofInfo();
		Formula[] antecedents = proofInfo.getAntecedents();
		Formula[] consequents = proofInfo.getConsequents();
		boolean[] completedConsequents = proofInfo.getCompletedConsequents();

		int consequentId = io.requestSubproof(antecedents, consequents, completedConsequents);
		if (consequentId == -1) {
			prover.cancelProof();
			return;
		}

		Formula consequent = consequents[consequentId];
		Decision decision = io.requestDecision(antecedents, consequent);

		if (decision == Decision.CANCEL_PROOF) {
			prover.cancelProof();
		} else if (decision == Decision.COMPLETE_PROOF) {
			Identification[] identifications = new Identification[antecedents.length];
			for (int i = 0; i < identifications.length; i++) {
				identifications[i] = FormulaIdentifier.identify(antecedents[i], consequent);
			}

			int antecedentId = io.requestIdentification(identifications);
			if (antecedentId != -1) {
				prover.completeConsequent(consequentId, antecedentId);
			}
		} else if (decision == Decision.SUGGEST_RULE) {
			int suggestionId = io.requestSuggestion(rules);
			if (suggestionId != -1) {
				Rule suggestion = rules[suggestionId];
				prover.proveByRule(consequentId, suggestion);
			}
		}
	}

	private static void printProofReports(ProofInfo info, ProofReport[] reports) {
		for (Formula premise : info.getAntecedents()) {
			StringBuilder line = new StringBuilder();
			line.append("premise ");
			line.append(premise);
			System.out.println(line);
		}

		printProofReports(reports, 0);
	}

	private static void printProofReports(ProofReport[] reports, int indent) {
		for (ProofReport report : reports) {
			if (report == null) {
				// this subproof was proven by simple identification, no rule applied
				continue;
			}

			for (Formula assumption : report.assumptions) {
				StringBuilder line = new StringBuilder();
				for (int i = 0; i < indent; i++) {
					line.append("  ");
				}
				line.append("asm ");
				line.append(assumption);
				System.out.println(line);
			}

			int subIndent = (report.assumptions.length > 0 ? indent + 1 : indent);
			printProofReports(report.subReports, subIndent);

			StringBuilder line = new StringBuilder();
			for (int i = 0; i < indent; i++) {
				line.append("  ");
			}
			line.append(report.consequent);
			line.append(" | ");
			line.append(report.rule.getName());
			line.append(" on");
			for (Formula assumption : report.assumptions) {
				line.append(" [asm ");
				line.append(assumption);
				line.append("]");
			}
			for (Formula antecedent : report.antecedents) {
				line.append(" [");
				line.append(antecedent);
				line.append("]");
			}
			System.out.println(line);
		}
	}
}
