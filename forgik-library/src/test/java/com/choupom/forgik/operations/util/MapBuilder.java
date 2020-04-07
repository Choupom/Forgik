package com.choupom.forgik.operations.util;

import java.util.HashMap;
import java.util.Map;

import com.choupom.forgik.formula.Formula;
import com.choupom.forgik.parser.FormulaParser;

public class MapBuilder {

	private final Map<Integer, Formula> map;

	public MapBuilder() {
		this.map = new HashMap<>();
	}

	public MapBuilder add(int original, String substitute) {
		this.map.put(original, FormulaParser.parse(substitute));
		return this;
	}

	public Map<Integer, Formula> build() {
		return this.map;
	}
}