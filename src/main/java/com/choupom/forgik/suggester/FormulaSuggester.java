/*
 * Java
 *
 * Copyright 2019 Andy Poudret. All rights reserved.
 */
package com.choupom.forgik.suggester;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.choupom.forgik.formula.Formula;
import com.choupom.forgik.rule.Rule;
import com.choupom.forgik.rule.Rulebook;

public class FormulaSuggester {

	public static Suggestion[] suggestFromRulebook(Formula formula, Rulebook rulebook) {
		List<Suggestion> suggestions = new ArrayList<>();
		for (Rule rule : rulebook.getRules()) {
			Set<String> leftover = new HashSet<>();
			Formula[] result = rule.apply(formula, leftover);
			if (result != null) {
				Suggestion suggestion = new Suggestion(rule, result, leftover);
				suggestions.add(suggestion);
			}
		}
		return suggestions.toArray(new Suggestion[suggestions.size()]);
	}
}
