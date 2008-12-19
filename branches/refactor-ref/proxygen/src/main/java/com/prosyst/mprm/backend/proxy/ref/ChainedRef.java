package com.prosyst.mprm.backend.proxy.ref;

import java.util.Map;

public class ChainedRef<I, O> extends RefImpl<I> {
	private final ObjectFactory<I, O> convertor;
	private final Ref<O> next;

	public ChainedRef(Class<I> type, Ref<O> next, ObjectFactory<I, O> convertor) {
		super(type);
		this.convertor = convertor;
		this.next = next;
	}

	public Ref<O> next() {
		return next;
	}
	
	protected I bindImpl(I delegate, Map<String, ?> props) {
		next.bind(convertor.create(delegate, props), props);
		return delegate;
	}
	
	protected void unbindImpl(I delegate, Map<String, ?> props) {
		O created = next.delegate();
		try {
			next.unbind();
		} finally {
			convertor.destroy(created, delegate, props);
		}
	}
	
	protected I updateImpl(I delegate, Map<String, ?> props) {
		O created = next.delegate();
		try {
			next.update(convertor.create(delegate, props), props);
			return delegate;
		} finally {
			convertor.destroy(created, delegate, props);
		}
	}
}
