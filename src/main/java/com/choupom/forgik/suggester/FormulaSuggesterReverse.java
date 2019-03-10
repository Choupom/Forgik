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
import com.choupom.forgik.rule.Rulebook;

public class FormulaSuggesterReverse {

	public static SuggestionReverse[] suggestFromRulebook(Formula formula, Rulebook rulebook) {
		List<SuggestionReverse> suggestions = new ArrayList<>();
		for (Rule rule : rulebook.getRules()) {
			Formula[] result = rule.applyReverse(formula);
			if (result != null) {
				SuggestionReverse suggestion = new SuggestionReverse(rule, result);
				suggestions.add(suggestion);
			}
		}
		return suggestions.toArray(new SuggestionReverse[suggestions.size()]);
	}
}
