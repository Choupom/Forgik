/*
 * Java
 *
 * Copyright 2019 Andy Poudret. All rights reserved.
 */
package com.choupom.forgik.identifier;

import java.util.Map;

import com.choupom.forgik.formula.Formula;

public class Identification {

	private final Formula formula;
	private final Map<String, Formula> map;

	public Identification(Formula formula, Map<String, Formula> map) {
		this.formula = formula;
		this.map = map;
	}

	public Formula getFormula() {
		return this.formula;
	}

	public Map<String, Formula> getMap() {
		return this.map;
	}
}
