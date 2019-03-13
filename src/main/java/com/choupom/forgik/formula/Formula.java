/*
 * Java
 *
 * Copyright 2019 Andy Poudret. All rights reserved.
 */
package com.choupom.forgik.formula;

import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class Formula {

	protected String toStringNested() {
		if (isStringEmbraced()) {
			return "(" + toString() + ")";
		} else {
			return toString();
		}
	}

	protected abstract boolean isStringEmbraced();

	public abstract boolean checkEquals(Formula formula);

	public abstract boolean identify(Formula formula, Map<String, List<Formula>> map);

	public abstract Formula apply(Map<String, Formula> map, Set<String> leftover);

	public abstract boolean containsFreeVariable(String variableName);
}
