/*
 * Java
 *
 * Copyright 2019 Andy Poudret. All rights reserved.
 */
package com.choupom.forgik.formula;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class BoundVariable implements Formula {

	private final String name;

	public BoundVariable(String name) {
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
	public boolean checkEquals(Formula formula) {
		if (!(formula instanceof BoundVariable)) {
			return false;
		}

		BoundVariable variable = (BoundVariable) formula;
		return this.name.equals(variable.name);
	}

	@Override
	public boolean identify(Formula formula, Map<String, List<Formula>> map) {
		if (formula instanceof FreeVariable) {
			FreeVariable variable = (FreeVariable) formula;
			return variable.identify(this, map);
		}

		if (!(formula instanceof BoundVariable)) {
			return false;
		}

		BoundVariable variable = (BoundVariable) formula;
		return this.name.equals(variable.name);
	}

	@Override
	public Formula apply(Map<String, Formula> map) {
		return this;
	}

	@Override
	public void getFreeVariables(Set<String> variables) {
		// do nothing
	}
}
