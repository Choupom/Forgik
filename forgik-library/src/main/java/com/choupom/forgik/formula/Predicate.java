/*
 * Java
 *
 * Copyright 2019 Andy Poudret. All rights reserved.
 */
package com.choupom.forgik.formula;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class Predicate implements Formula {

	private final char name;

	public Predicate(char name) {
		this.name = name;
	}

	public char getName() {
		return this.name;
	}

	@Override
	public String toString() {
		return Character.toString(this.name);
	}

	@Override
	public String toStringNested() {
		return toString();
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof Predicate)) {
			return false;
		}

		Predicate predicate = (Predicate) object;
		return (this.name == predicate.name);
	}

	@Override
	public boolean identify(Formula formula, Map<Integer, List<Formula>> map) {
		if (formula instanceof FreeFormula) {
			FreeFormula freeFormula = (FreeFormula) formula;
			return freeFormula.identify(this, map);
		}

		if (!(formula instanceof Predicate)) {
			return false;
		}

		Predicate predicate = (Predicate) formula;
		return (this.name == predicate.name);
	}

	@Override
	public Formula apply(Map<Integer, Formula> map) {
		return this;
	}

	@Override
	public void getFreeFormulas(Set<Integer> freeFormulas) {
		// do nothing
	}
}
