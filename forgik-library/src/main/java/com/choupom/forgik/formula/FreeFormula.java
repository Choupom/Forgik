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

	public static final String STRING_PREFIX = "$";

	private final Integer id;

	public FreeFormula(int id) {
		this.id = Integer.valueOf(id);
	}

	public Integer getId() {
		return this.id;
	}

	@Override
	public String toString() {
		return STRING_PREFIX + this.id;
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
		return (this.id == freeFormula.id);
	}

	@Override
	public boolean identify(Formula formula, Map<Integer, List<Formula>> map) {
		if (formula instanceof FreeFormula) {
			FreeFormula freeFormula = (FreeFormula) formula;
			if (freeFormula.id == this.id) {
				return true;
			}
			addToMap(freeFormula.id, this, map);
		}

		addToMap(this.id, formula, map);
		return true;
	}

	@Override
	public Formula apply(Map<Integer, Formula> map) {
		Formula formula = map.get(this.id);
		if (formula != null) {
			return formula;
		} else {
			return this;
		}
	}

	@Override
	public void getFreeFormulas(Set<Integer> freeFormulas) {
		freeFormulas.add(this.id);
	}

	private static void addToMap(Integer id, Formula formula, Map<Integer, List<Formula>> map) {
		List<Formula> list = map.get(id);
		if (list == null) {
			list = new ArrayList<>();
			map.put(id, list);
		}
		list.add(formula);
	}
}
