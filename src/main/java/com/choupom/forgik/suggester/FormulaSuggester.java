/*
 * Java
 *
 * Copyright 2019 Andy Poudret. All rights reserved.
 */
package com.choupom.forgik.suggester;

import java.util.ArrayList;
import java.util.List;

import com.choupom.forgik.formula.Formula;
import com.choupom.forgik.rule.Rule;

public class FormulaSuggester {

	public static Suggestion[] suggest(Formula formula, Rule[] rules) {
		List<Suggestion> suggestions = new ArrayList<>();
		for (Rule rule : rules) {
			Formula[] result = rule.apply(formula, null);
			if (result != null) {
				Suggestion suggestion = new Suggestion(rule, result);
				suggestions.add(suggestion);
			}
		}
		return suggestions.toArray(new Suggestion[suggestions.size()]);
	}
}
