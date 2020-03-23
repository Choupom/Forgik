/*
 * Java
 *
 * Copyright 2020 Andy Poudret. All rights reserved.
 */
package com.choupom.forgik.prover;

import java.util.Map;

import com.choupom.forgik.formula.Formula;
import com.choupom.forgik.rule.Rule;

public class ProofReport {

	public Rule rule;
	public Formula[] assumptions;
	public Formula[] antecedents;
	public Formula consequent;
	public ProofReport[] subReports;

	public ProofReport(Rule rule, Formula[] assumptions, Formula[] antecedents, Formula consequent,
			ProofReport[] subReports) {
		this.rule = rule;
		this.assumptions = assumptions.clone();
		this.antecedents = antecedents.clone();
		this.consequent = consequent;
		this.subReports = subReports.clone();
	}

	public ProofReport apply(Map<Integer, Formula> map) {
		Formula[] assumptions = new Formula[this.assumptions.length];
		for (int i = 0; i < assumptions.length; i++) {
			assumptions[i] = this.assumptions[i].apply(map);
		}

		Formula[] antecedents = new Formula[this.antecedents.length];
		for (int i = 0; i < antecedents.length; i++) {
			antecedents[i] = this.antecedents[i].apply(map);
		}

		Formula consequent = this.consequent.apply(map);

		ProofReport[] subReports = new ProofReport[this.subReports.length];
		for (int i = 0; i < subReports.length; i++) {
			if (this.subReports[i] != null) {
				subReports[i] = this.subReports[i].apply(map);
			}
		}

		return new ProofReport(this.rule, assumptions, antecedents, consequent, subReports);
	}
}
