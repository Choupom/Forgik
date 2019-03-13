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

	public static Suggestion[] suggestFromRulebook(Formula[] formulas, Rulebook rulebook) {
		List<Suggestion> suggestions = new ArrayList<>();
		for (Rule rule : rulebook.getRules()) {
			if (rule.mayApply(formulas.length)) {
				List<Suggestion> ruleSuggestions = suggestFromRule(formulas, rule);
				suggestions.addAll(ruleSuggestions);
			}
		}
		return suggestions.toArray(new Suggestion[suggestions.size()]);
	}

	private static List<Suggestion> suggestFromRule(Formula[] formulas, Rule rule) {
		List<Suggestion> suggestions = new ArrayList<>(2);

		makeSuggestion(formulas, rule, suggestions);

		if (formulas.length == 2) {
			Formula[] alternateFormulas = new Formula[] { formulas[1], formulas[0] };
			makeSuggestion(alternateFormulas, rule, suggestions);
		} else if (formulas.length == 3) {
			makeSuggestion(new Formula[] { formulas[0], formulas[2], formulas[1] }, rule, suggestions);
			makeSuggestion(new Formula[] { formulas[1], formulas[0], formulas[2] }, rule, suggestions);
			makeSuggestion(new Formula[] { formulas[1], formulas[2], formulas[0] }, rule, suggestions);
			makeSuggestion(new Formula[] { formulas[2], formulas[0], formulas[1] }, rule, suggestions);
			makeSuggestion(new Formula[] { formulas[2], formulas[1], formulas[0] }, rule, suggestions);
		} else if (formulas.length > 3) {
			throw new IllegalArgumentException();
		}

		return suggestions;
	}

	private static void makeSuggestion(Formula[] formulas, Rule rule, List<Suggestion> suggestions) {
		Set<String> leftover = new HashSet<>();
		Formula result = rule.apply(formulas, leftover);
		if (result != null) {
			Suggestion suggestion = new Suggestion(rule, result, leftover);
			suggestions.add(suggestion);
		}
	}
}
