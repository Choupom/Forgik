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

public class FreeFormula implements Formula {

	private final String name;

	public FreeFormula(String name) {
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
	public String toStringNested() {
		return toString();
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof FreeFormula)) {
			return false;
		}

		FreeFormula freeFormula = (FreeFormula) object;
		return this.name.equals(freeFormula.name);
	}

	@Override
	public boolean identify(Formula formula, Map<String, List<Formula>> map) {
		if (formula instanceof FreeFormula) {
			FreeFormula freeFormula = (FreeFormula) formula;
			if (freeFormula.name.equals(this.name)) {
				return true;
			}
			addToMap(freeFormula.name, this, map);
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
	public void getFreeFormulas(Set<String> freeFormulas) {
		freeFormulas.add(this.name);
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
