/*
 * Java
 *
 * Copyright 2019 Andy Poudret. All rights reserved.
 */
package com.choupom.forgik.proof;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.choupom.forgik.formula.Formula;
import com.choupom.forgik.proof.linear.AssumptionStatement;
import com.choupom.forgik.proof.linear.PremiseStatement;
import com.choupom.forgik.proof.linear.RuleStatement;
import com.choupom.forgik.proof.linear.Statement;
import com.choupom.forgik.proof.tree.ProofReport;
import com.choupom.forgik.proof.tree.ProofReportIdentification;
import com.choupom.forgik.proof.tree.ProofReportRule;

public class ProofConverter {

	private ProofConverter() {
		// private constructor
	}

	public static Statement[] generateLinearProof(Formula[] antecedents, ProofReport[] consequentProofs) {
		List<Statement> statements = new ArrayList<>();
		int[] assumptionStatementIds = new int[antecedents.length];

		// add premise statements
		for (int i = 0; i < antecedents.length; i++) {
			Statement statement = new PremiseStatement(antecedents[i]);
			assumptionStatementIds[i] = statements.size();
			statements.add(statement);
		}

		// browse consequent proofs
		Map<ProofReport, Integer> proofStatementMap = new HashMap<>();
		for (ProofReport consequentProof : consequentProofs) {
			browseProof(consequentProof, statements, assumptionStatementIds, proofStatementMap, 0);
		}

		return statements.toArray(new Statement[statements.size()]);
	}

	private static void browseProof(ProofReport proof, List<Statement> statements, int[] assumptionStatementIds,
			Map<ProofReport, Integer> proofStatementMap, int indent) {
		if (proof instanceof ProofReportIdentification) {
			browseIdentificationReport((ProofReportIdentification) proof, statements, assumptionStatementIds,
					proofStatementMap, indent);
		} else {
			browseRuleReport((ProofReportRule) proof, statements, assumptionStatementIds, proofStatementMap, indent);
		}
	}

	private static void browseIdentificationReport(ProofReportIdentification proof, List<Statement> statements,
			int[] assumptionStatementIds, Map<ProofReport, Integer> proofStatementMap, int indent) {
		// set statement id of this subproof
		int assumptionId = proof.getAntecedentId();
		int statementId = assumptionStatementIds[assumptionId];
		proofStatementMap.put(proof, Integer.valueOf(statementId));
	}

	private static void browseRuleReport(ProofReportRule proof, List<Statement> statements,
			int[] assumptionStatementIds, Map<ProofReport, Integer> proofStatementMap, int indent) {
		Formula[] assumptions = proof.getAssumptions();
		ProofReport[] subproofs = proof.getSubproofs();

		// add assumption statements
		int[] newAssumptionStatementIds = new int[assumptionStatementIds.length + assumptions.length];
		System.arraycopy(assumptionStatementIds, 0, newAssumptionStatementIds, 0, assumptionStatementIds.length);
		for (int i = 0; i < assumptions.length; i++) {
			Statement statement = new AssumptionStatement(assumptions[i], indent);
			newAssumptionStatementIds[assumptionStatementIds.length + i] = statements.size();
			statements.add(statement);
		}

		// browse subproofs
		int subIndent = (assumptions.length > 0 ? indent + 1 : indent);
		for (ProofReport subproof : subproofs) {
			browseProof(subproof, statements, newAssumptionStatementIds, proofStatementMap, subIndent);
		}

		// add rule statement
		int[] antecedents = new int[assumptions.length + subproofs.length];
		for (int i = 0; i < assumptions.length; i++) {
			antecedents[i] = newAssumptionStatementIds[assumptionStatementIds.length + i];
		}
		for (int i = 0; i < subproofs.length; i++) {
			antecedents[assumptions.length + i] = proofStatementMap.get(subproofs[i]).intValue();
		}
		Statement statement = new RuleStatement(proof.getRule(), antecedents, proof.getConclusion(), indent);
		proofStatementMap.put(proof, Integer.valueOf(statements.size()));
		statements.add(statement);
	}
}
