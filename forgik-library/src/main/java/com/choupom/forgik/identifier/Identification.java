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
	private final Map<Integer, Formula> map;

	public Identification(Formula formula, Map<Integer, Formula> map) {
		this.formula = formula;
		this.map = map;
	}

	public Formula getFormula() {
		return this.formula;
	}

	public Map<Integer, Formula> getMap() {
		return this.map;
	}
}
