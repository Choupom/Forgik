/*
 * Java
 *
 * Copyright 2019 Andy Poudret. All rights reserved.
 */
package com.choupom.forgik.formula;

import java.util.List;
import java.util.Map;

public class FreeVariable extends Formula {

	private final String name;

	public FreeVariable(String name) {
		if (!Character.isUpperCase(name.charAt(0))) {
			throw new IllegalArgumentException();
		}
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
		if (!(formula instanceof FreeVariable)) {
			return false;
		}

		FreeVariable variable = (FreeVariable) formula;
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
			return this;
		}
	}
}
