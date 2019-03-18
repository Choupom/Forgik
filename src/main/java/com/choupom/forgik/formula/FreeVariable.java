/*
 * Java
 *
 * Copyright 2019 Andy Poudret. All rights reserved.
 */
package com.choupom.forgik.formula;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
	public boolean identify(Formula formula, Map<String, List<Formula>> map) {
		if (formula instanceof FreeVariable) {
			FreeVariable variable = (FreeVariable) formula;
			if (variable.name.equals(this.name)) {
				return true;
			}
			addToMap(variable.name, this, map);
		}

		addToMap(this.name, formula, map);
		return true;
	}

	@Override
	public Formula apply(Map<String, Formula> map) {
		Formula formula = map.get(this.name);
		if (formula != null) {
			return formula;
		} else {
			return this;
		}
	}

	@Override
	public void getFreeVariables(Set<String> variables) {
		variables.add(this.name);
	}

	private static void addToMap(String name, Formula formula, Map<String, List<Formula>> map) {
		List<Formula> list = map.get(name);
		if (list == null) {
			list = new ArrayList<>();
			map.put(name, list);
		}
		list.add(formula);
	}
}
