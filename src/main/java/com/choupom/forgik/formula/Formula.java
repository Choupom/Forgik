/*
 * Java
 *
 * Copyright 2019 Andy Poudret. All rights reserved.
 */
package com.choupom.forgik.formula;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface Formula {

	String toStringNested();

	boolean checkEquals(Formula formula);

	boolean identify(Formula formula, Map<String, List<Formula>> map);

	Formula apply(Map<String, Formula> map);

	void getFreeVariables(Set<String> variables);
}
