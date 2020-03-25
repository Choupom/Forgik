/*
 * Java
 *
 * Copyright 2019 Andy Poudret. All rights reserved.
 */
package com.choupom.forgik.identifier;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.choupom.forgik.formula.Formula;

public class FormulaIdentifier {

	private FormulaIdentifier() {
		// private constructor
	}

	public static Identification identify(Formula formula1, Formula formula2) {
		Map<Integer, Formula> identificationMap = new HashMap<>();

		while (true) {
			Map<Integer, List<Formula>> map = new HashMap<>();
			if (!formula2.identify(formula1, map)) {
				return null;
			}

			Integer latestFreeFormula = null;
			for (Integer freeFormula : map.keySet()) {
				if (latestFreeFormula == null || freeFormula.compareTo(latestFreeFormula) > 0) {
					latestFreeFormula = freeFormula;
				}
			}

			if (latestFreeFormula == null) {
				return new Identification(formula1, identificationMap);
			}

			Formula substitute = map.get(latestFreeFormula).get(0);

			Set<Integer> freeFormulas = new HashSet<>();
			substitute.getFreeFormulas(freeFormulas);
			if (freeFormulas.contains(latestFreeFormula)) {
				return null;
			}

			Map<Integer, Formula> newMap = new HashMap<>();
			newMap.put(latestFreeFormula, substitute);

			formula1 = formula1.apply(newMap);
			formula2 = formula2.apply(newMap);
			for (Map.Entry<Integer, Formula> entry : identificationMap.entrySet()) {
				Integer freeFormula = entry.getKey();
				Formula newFormula = entry.getValue().apply(newMap);
				identificationMap.put(freeFormula, newFormula);
			}
			identificationMap.put(latestFreeFormula, substitute);
		}
	}
}
