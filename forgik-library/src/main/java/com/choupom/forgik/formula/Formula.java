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

	boolean identify(Formula formula, Map<Integer, List<Formula>> map);

	Formula apply(Map<Integer, Formula> map);

	void getFreeFormulas(Set<Integer> freeFormulas);
}
