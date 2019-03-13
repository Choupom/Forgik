/*
 * Java
 *
 * Copyright 2019 Andy Poudret. All rights reserved.
 */
package com.choupom.forgik.identifier;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.choupom.forgik.formula.Formula;

public class FormulaIdentifier {

	public static Identification identify(Formula formula1, Formula formula2) {
		Map<String, Formula> identificationMap = new HashMap<>();

		while (true) {
			Map<String, List<Formula>> map = new HashMap<>();
			if (!formula2.identify(formula1, map)) {
				return null;
			}

			String latestVariable = null;
			for (String variable : map.keySet()) {
				if (latestVariable == null || variable.compareTo(latestVariable) > 0) {
					latestVariable = variable;
				}
			}

			if (latestVariable == null) {
				return new Identification(formula1, identificationMap);
			}

			Formula substitute = map.get(latestVariable).get(0);
			if (substitute.containsFreeVariable(latestVariable)) {
				return null;
			}

			Map<String, Formula> simpleMap = new HashMap<>();
			simpleMap.put(latestVariable, substitute);
			formula1 = formula1.apply(simpleMap, null);
			formula2 = formula2.apply(simpleMap, null);
			identificationMap.put(latestVariable, substitute);
		}
	}
}
