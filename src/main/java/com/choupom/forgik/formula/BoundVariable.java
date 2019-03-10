/*
 * Java
 *
 * Copyright 2019 Andy Poudret. All rights reserved.
 */
package com.choupom.forgik.formula;

import java.util.List;
import java.util.Map;

public class BoundVariable extends Formula {

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
	protected boolean isStringEmbraced() {
		return false;
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
	public boolean identify(Formula formula, Map<String, Formula> map) {
		if (!(formula instanceof BoundVariable)) {
			return false;
		}

		BoundVariable variable = (BoundVariable) formula;
		return this.name.equals(variable.name);
	}

	@Override
	public Formula apply(Map<String, Formula> map, List<String> leftover) {
		return this;
	}
}
