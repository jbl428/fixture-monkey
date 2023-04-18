package com.navercorp.fixturemonkey.api.introspector;

import java.lang.reflect.InvocationHandler;
import java.util.Map;

@SuppressWarnings("unused")
final class InvocationHandlerBuilder {
	private final Map<String, Object> generatedValuesByMethodName;

	InvocationHandlerBuilder(Map<String, Object> generatedValuesByMethodName) {
		this.generatedValuesByMethodName = generatedValuesByMethodName;
	}

	void put(String methodName, Object value) {
		generatedValuesByMethodName.put(methodName, value);
	}

	InvocationHandler build() {
		return (proxy, method, args) -> {
			if (method.isDefault()) {
				return InvocationHandler.invokeDefault(proxy, method, args);
			}
			return generatedValuesByMethodName.get(method.getName());
		};
	}

	boolean isEmpty() {
		return generatedValuesByMethodName.isEmpty();
	}
}
