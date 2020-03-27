/*
 * Java
 *
 * Copyright 2020 Andy Poudret. All rights reserved.
 */
package com.choupom.forgik.utils;

import java.util.Iterator;

public class ArrayIterator<E> implements Iterator<E> {

	private final E[] array;

	private int index;

	public ArrayIterator(E[] array) {
		this.array = array;
		this.index = 0;
	}

	@Override
	public boolean hasNext() {
		return (this.index < this.array.length);
	}

	@Override
	public E next() {
		return this.array[this.index++];
	}
}
