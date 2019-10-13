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

	private final String name;

	public Predicate(String name) {
		if (!Character.isLowerCase(name.charAt(0))) {
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
		if (!(object instanceof Predicate)) {
			return false;
		}

		Predicate predicate = (Predicate) object;
		return this.name.equals(predicate.name);
	}

	@Override
	public boolean identify(Formula formula, Map<String, List<Formula>> map) {
		if (formula instanceof FreeFormula) {
			FreeFormula freeFormula = (FreeFormula) formula;
			return freeFormula.identify(this, map);
		}

		if (!(formula instanceof Predicate)) {
			return false;
		}

		Predicate predicate = (Predicate) formula;
		return this.name.equals(predicate.name);
	}

	@Override
	public Formula apply(Map<String, Formula> map) {
		return this;
	}

	@Override
	public void getFreeFormulas(Set<String> freeFormulas) {
		// do nothing
	}
}
