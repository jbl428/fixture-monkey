package com.navercorp.fixturemonkey.api.introspector;

import java.lang.reflect.InvocationHandler;
import java.util.Map;

final class InvocationHandlerBuilder {
	private final Map<String, Object> generatedValuesByMethodName;

	InvocationHandlerBuilder(Map<String, Object> generatedValuesByMethodName) {
		this.generatedValuesByMethodName = generatedValuesByMethodName;
	}

	void put(String methodName, Object value) {
		generatedValuesByMethodName.put(methodName, value);
	}

	InvocationHandler build() {
		return (proxy, method, args) -> generatedValuesByMethodName.get(method.getName());
	}

	boolean isEmpty() {
		return generatedValuesByMethodName.isEmpty();
	}
}
