/*
 * Java
 *
 * Copyright 2019 Andy Poudret. All rights reserved.
 */
package com.choupom.forgik.formula;

public class FormulaSettings {

	public static final String DEFAULT_CONJUNCTION_STRING = "^";
	public static final String DEFAULT_DISJUNCTION_STRING = "v";
	public static final String DEFAULT_IMPLICATION_STRING = ">";
	public static final String DEFAULT_NEGATION_STRING = "-";

	private static FormulaSettings Instance = new FormulaSettings();

	private String conjunctionString;
	private String disjunctionString;
	private String implicationString;
	private String negationString;

	private FormulaSettings() {
		this.conjunctionString = DEFAULT_CONJUNCTION_STRING;
		this.disjunctionString = DEFAULT_DISJUNCTION_STRING;
		this.implicationString = DEFAULT_IMPLICATION_STRING;
		this.negationString = DEFAULT_NEGATION_STRING;
	}

	public static FormulaSettings getInstance() {
		return Instance;
	}

	public String getConjunctionString() {
		return this.conjunctionString;
	}

	public String getDisjunctionString() {
		return this.disjunctionString;
	}

	public String getImplicationString() {
		return this.implicationString;
	}

	public String getNegationString() {
		return this.negationString;
	}

	public void setConjunctionString(String conjunctionString) {
		this.conjunctionString = conjunctionString;
	}

	public void setDisjunctionString(String disjunctionString) {
		this.disjunctionString = disjunctionString;
	}

	public void setImplicationString(String implicationString) {
		this.implicationString = implicationString;
	}

	public void setNegationString(String negationString) {
		this.negationString = negationString;
	}
}
