/*
 * Java
 *
 * Copyright 2019 Andy Poudret. All rights reserved.
 */
package com.choupom.forgik.formula;

public class FormulaSettings {

	private static String ConjunctionString = "^";
	private static String DisjunctionString = "v";
	private static String ImplicationString = ">";
	private static String NegationString = "-";

	private FormulaSettings() {
		// private constructor
	}

	public static void setOperatorsStrings(String conjunctionString, String disjunctionString, String implicationString,
			String negationString) {
		ConjunctionString = conjunctionString;
		DisjunctionString = disjunctionString;
		ImplicationString = implicationString;
		NegationString = negationString;
	}

	public static String getConjunctionString() {
		return ConjunctionString;
	}

	public static String getDisjunctionString() {
		return DisjunctionString;
	}

	public static String getImplicationString() {
		return ImplicationString;
	}

	public static String getNegationString() {
		return NegationString;
	}
}
