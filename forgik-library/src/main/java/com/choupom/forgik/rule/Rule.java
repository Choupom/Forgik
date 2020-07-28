/*
 * Java
 *
 * Copyright 2019 Andy Poudret. All rights reserved.
 */
package com.choupom.forgik.rule;

import com.choupom.forgik.formula.Formula;
import com.choupom.forgik.formula.Formulas;

public class Rule {

	private final String name;
	private final Formulas assumptions;
	private final Formulas antecedents;
	private final Formula consequent;

	public Rule(String name, Formulas assumptions, Formulas antecedents, Formula consequent) {
		this.name = name;
		this.assumptions = assumptions;
		this.antecedents = antecedents;
		this.consequent = consequent;
	}

	public String getName() {
		return this.name;
	}

	public Formulas getAssumptions() {
		return this.assumptions;
	}

	public Formulas getAntecedents() {
		return this.antecedents;
	}

	public Formula getConsequent() {
		return this.consequent;
	}
}
