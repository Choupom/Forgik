/*
 * Java
 *
 * Copyright 2019 Andy Poudret. All rights reserved.
 */
package com.choupom.forgik.console;

import com.choupom.forgik.console.ConsoleProverIO.Decision;
import com.choupom.forgik.formula.Formula;
import com.choupom.forgik.formula.Formulas;
import com.choupom.forgik.identifier.FormulaIdentifier;
import com.choupom.forgik.identifier.Identification;
import com.choupom.forgik.proof.ProofConverter;
import com.choupom.forgik.proof.linear.AssumptionStatement;
import com.choupom.forgik.proof.linear.PremiseStatement;
import com.choupom.forgik.proof.linear.RuleStatement;
import com.choupom.forgik.proof.linear.Statement;
import com.choupom.forgik.prover.ProofInfo;
import com.choupom.forgik.prover.Prover;
import com.choupom.forgik.prover.ProverException;
import com.choupom.forgik.rule.Rule;

public class ConsoleProver {

	private ConsoleProver() {
		// private constructor
	}

	public static void prove(Formulas antecedents, Formulas consequents, Rule[] rules) {
		Prover prover = new Prover(antecedents, consequents);
		ConsoleProverIO io = new ConsoleProverIO();

		while (!prover.isMainProofComplete()) {
			try {
				proveStep(prover, io, rules);
			} catch (ProverException e) {
				System.out.println("Proof step failed: " + e.getMessage());
			}
		}

		ProofInfo info = prover.getProofInfo();
		Statement[] statements = ProofConverter.generateLinearProof(info.getAntecedents(), info.getConsequentProofs());
		printLinearProof(statements);
	}

	public static void proveStep(Prover prover, ConsoleProverIO io, Rule[] rules) throws ProverException {
		ProofInfo proofInfo = prover.getProofInfo();
		Formulas antecedents = proofInfo.getAntecedents();
		Formulas consequents = proofInfo.getConsequents();
		boolean[] completedConsequents = proofInfo.getCompletedConsequents();

		int consequentId = io.requestSubproof(antecedents, consequents, completedConsequents);
		if (consequentId == -1) {
			prover.cancelProof();
			return;
		}

		Formula consequent = consequents.get(consequentId);
		Decision decision = io.requestDecision(antecedents, consequent);

		if (decision == Decision.CANCEL_PROOF) {
			prover.cancelProof();
		} else if (decision == Decision.COMPLETE_PROOF) {
			Identification[] identifications = new Identification[antecedents.size()];
			for (int i = 0; i < identifications.length; i++) {
				identifications[i] = FormulaIdentifier.identify(antecedents.get(i), consequent);
			}

			int antecedentId = io.requestIdentification(identifications);
			if (antecedentId != -1) {
				prover.completeConsequent(consequentId, antecedentId);
				while (prover.isProofComplete() && !prover.isOnMainProof()) {
					prover.completeProof();
				}
			}
		} else if (decision == Decision.SUGGEST_RULE) {
			int ruleId = io.requestRule(rules);
			if (ruleId != -1) {
				prover.proveByRule(consequentId, rules[ruleId]);
			}
		}
	}

	private static void printLinearProof(Statement[] statements) {
		for (int statementId = 0; statementId < statements.length; statementId++) {
			Statement statement = statements[statementId];

			StringBuilder line = new StringBuilder();
			if (statementId < 10) {
				line.append(" ");
			}
			line.append(statementId);
			line.append(". ");
			for (int i = 0; i < statement.getDepth(); i++) {
				line.append("  ");
			}

			if (statement instanceof PremiseStatement) {
				printPremiseStatement((PremiseStatement) statement, line);
			} else if (statement instanceof AssumptionStatement) {
				printAssumptionStatement((AssumptionStatement) statement, line);
			} else if (statement instanceof RuleStatement) {
				printRuleStatement((RuleStatement) statement, line);
			}

			System.out.println(line);
		}
	}

	private static void printPremiseStatement(PremiseStatement statement, StringBuilder line) {
		line.append(statement.getConclusion());
		line.append(" | premise");
	}

	private static void printAssumptionStatement(AssumptionStatement statement, StringBuilder line) {
		line.append("asm ");
		line.append(statement.getConclusion());
	}

	private static void printRuleStatement(RuleStatement statement, StringBuilder line) {
		line.append(statement.getConclusion());
		line.append(" | ");
		line.append(statement.getRule().getName());
		line.append(" ");

		boolean first = true;
		for (int antecedentStatementId : statement.getAntecedentStatements()) {
			if (!first) {
				line.append(",");
			}
			first = false;
			line.append(antecedentStatementId);
		}
	}
}
