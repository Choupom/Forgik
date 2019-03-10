/*
 * Java
 *
 * Copyright 2019 Andy Poudret. All rights reserved.
 */
package com.choupom.forgik.formula;

import java.util.List;
import java.util.Map;

public class Variable extends Formula {

	private final String name;

	public Variable(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}

	@Override
	public String toString() {
		return this.name;
	}

	@Override
	protected boolean isStringEmbraced() {
		return false;
	}

	@Override
	public boolean checkEquals(Formula formula) {
		if (!(formula instanceof Variable)) {
			return false;
		}

		Variable variable = (Variable) formula;
		return this.name.equals(variable.name);
	}

	@Override
	public boolean identify(Formula formula, Map<String, Formula> map) {
		Formula mappedFormula = map.get(this.name);
		if (mappedFormula == null) {
			map.put(this.name, formula);
			return true;
		} else {
			return mappedFormula.checkEquals(formula);
		}
	}

	@Override
	public Formula apply(Map<String, Formula> map, List<String> leftover) {
		Formula formula = map.get(this.name);
		if (formula != null) {
			return formula;
		} else {
			if (leftover != null) {
				leftover.add(this.name);
			}
			return new Variable(this.name);
		}
	}
}
