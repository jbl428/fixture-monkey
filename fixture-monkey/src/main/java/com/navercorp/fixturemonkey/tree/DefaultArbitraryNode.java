/*
 * Fixture Monkey
 *
 * Copyright (c) 2021-present NAVER Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.navercorp.fixturemonkey.tree;

import static com.navercorp.fixturemonkey.api.generator.DefaultNullInjectGenerator.ALWAYS_NULL_INJECT;
import static com.navercorp.fixturemonkey.api.generator.DefaultNullInjectGenerator.NOT_NULL_INJECT;
import static com.navercorp.fixturemonkey.api.type.Types.isAssignable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;

import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;

import com.navercorp.fixturemonkey.api.customizer.NextNodePredicate;
import com.navercorp.fixturemonkey.api.customizer.Values.ManipulatingSequenceValue;
import com.navercorp.fixturemonkey.api.generator.ArbitraryContainerInfo;
import com.navercorp.fixturemonkey.api.generator.ArbitraryProperty;
import com.navercorp.fixturemonkey.api.generator.ContainerProperty;
import com.navercorp.fixturemonkey.api.property.MapEntryElementProperty;
import com.navercorp.fixturemonkey.api.property.Property;
import com.navercorp.fixturemonkey.api.tree.ArbitraryNode;
import com.navercorp.fixturemonkey.api.type.Types;
import com.navercorp.fixturemonkey.customizer.ContainerInfoManipulator;
import com.navercorp.fixturemonkey.expression.IdentityNodeResolver;
import com.navercorp.fixturemonkey.resolver.DecomposableContainerValue;
import com.navercorp.fixturemonkey.resolver.DecomposedContainerValueFactory;

@API(since = "0.4.0", status = Status.MAINTAINED)
final class DefaultArbitraryNode implements ArbitraryNode {
	@Nullable
	private final Property resolvedParentProperty;

	private Property resolvedProperty;
	private ArbitraryProperty arbitraryProperty;

	private List<DefaultArbitraryNode> children;
	private final ArbitraryTraverser traverser;

	private final DecomposedContainerValueFactory decomposedContainerValueFactory;

	@Nullable
	private Arbitrary<?> arbitrary;

	private boolean manipulated = false;

	@SuppressWarnings("rawtypes")
	private final List<Predicate> arbitraryFilters = new ArrayList<>();

	DefaultArbitraryNode(
		@Nullable Property resolvedParentProperty,
		Property resolvedProperty,
		ArbitraryProperty arbitraryProperty,
		List<DefaultArbitraryNode> children,
		ArbitraryTraverser traverser,
		DecomposedContainerValueFactory decomposedContainerValueFactory
	) {
		this.resolvedParentProperty = resolvedParentProperty;
		this.resolvedProperty = resolvedProperty;
		this.arbitraryProperty = arbitraryProperty;
		this.children = children;
		this.traverser = traverser;
		this.decomposedContainerValueFactory = decomposedContainerValueFactory;
	}

	@Override
	public List<? extends ArbitraryNode> nextNodes(NextNodePredicate nextNodePredicate) {
		List<DefaultArbitraryNode> nextNodes = this.getChildren().stream()
			.filter(it -> nextNodePredicate.test(it.arbitraryProperty.getObjectProperty()))
			.collect(Collectors.toList());

		nextNodes.forEach(DefaultArbitraryNode::mark);
		return nextNodes;
	}

	@Override
	public void mark() {
		this.setManipulated(true);
		this.setArbitraryProperty(this.getArbitraryProperty().withNullInject(NOT_NULL_INJECT));
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void filter(Class<?> type, Predicate filter) {
		Class<?> actualType = Types.getActualType(this.getProperty().getType());
		if (!isAssignable(actualType, type)) {
			throw new IllegalArgumentException(
				"Wrong type filter is applied. Expected: " + type + ", Actual: " + actualType
			);
		}

		this.arbitraryFilters.add(filter);
	}

	@Override
	public void setRecursively(ManipulatingSequenceValue manipulatingSequenceValue) {
		Object value = manipulatingSequenceValue.getValue();
		Class<?> actualType = Types.getActualType(this.getProperty().getType());
		if (value != null
			&& !(this.getProperty() instanceof MapEntryElementProperty)
			&& !isAssignable(value.getClass(), actualType)) {
			throw new IllegalArgumentException(
				"The value is not of the same type as the property."
					+ " node name: " + this.getArbitraryProperty()
					.getObjectProperty()
					.getResolvedPropertyName()
					+ " node type: " + this.getProperty().getType().getTypeName()
					+ " value type: " + value.getClass().getTypeName()
			);
		}

		this.mark();
		if (value == null) {
			this.setArbitraryProperty(this.getArbitraryProperty().withNullInject(ALWAYS_NULL_INJECT));
			return;
		}

		ContainerProperty containerProperty = this.getArbitraryProperty().getContainerProperty();
		if (containerProperty != null) {
			DecomposableContainerValue decomposableContainerValue = decomposedContainerValueFactory.from(value);
			Object containerValue = decomposableContainerValue.getContainer();
			int decomposedContainerSize = decomposableContainerValue.getSize();

			int setManipulatingSequence = manipulatingSequenceValue.getManipulatingSequence();
			if (containerProperty.getContainerInfo().getManipulatingSequence() == null
				|| setManipulatingSequence > containerProperty.getContainerInfo().getManipulatingSequence()) {
				ContainerInfoManipulator containerInfoManipulator = new ContainerInfoManipulator(
					IdentityNodeResolver.INSTANCE.toNextNodePredicate(),
					new ArbitraryContainerInfo(decomposedContainerSize, decomposedContainerSize)
				);

				DefaultArbitraryNode newNode = traverser.traverse(
					this.getProperty(),
					Collections.singletonList(containerInfoManipulator),
					Collections.emptyList()
				);
				this.setArbitraryProperty(
					this.getArbitraryProperty()
						.withContainerProperty(newNode.getArbitraryProperty().getContainerProperty())
				);
				this.setChildren(newNode.getChildren());
			}

			List<DefaultArbitraryNode> children = this.getChildren();

			if (this.getArbitraryProperty()
				.getObjectProperty()
				.getProperty() instanceof MapEntryElementProperty) {
				decomposedContainerSize *= 2; // key, value
			}

			int decomposedNodeSize = Math.min(decomposedContainerSize, children.size());

			for (int i = 0; i < decomposedNodeSize; i++) {
				DefaultArbitraryNode child = children.get(i);
				Property childProperty = child.getProperty();
				child.setRecursively(
					new ManipulatingSequenceValue(
						manipulatingSequenceValue.getManipulatingSequence(),
						childProperty.getValue(containerValue)
					)
				);
			}
			return;
		}

		List<DefaultArbitraryNode> children = this.getChildren();
		if (children.isEmpty() || Types.getActualType(this.getProperty().getType()).isInterface()) {
			this.setArbitrary(Arbitraries.just(value));
			return;
		}

		Entry<Property, List<Property>> childPropertiesByResolvedProperty = this.getArbitraryProperty()
			.getObjectProperty()
			.getChildPropertiesByResolvedProperty(
				property -> isAssignable(Types.getActualType(property.getType()), value.getClass())
			);

		Property resolvedParentProperty = childPropertiesByResolvedProperty.getKey();
		this.setResolvedProperty(resolvedParentProperty);
		List<Property> childProperties = childPropertiesByResolvedProperty.getValue();
		for (DefaultArbitraryNode child : children) {
			if (childProperties.contains(child.getProperty())
				&& resolvedParentProperty.equals(child.getResolvedParentProperty())) {
				Property childProperty = child.getProperty();
				child.setRecursively(
					new ManipulatingSequenceValue(
						manipulatingSequenceValue.getManipulatingSequence(),
						childProperty.getValue(value)
					)
				);
			}
		}
	}

	@Override
	public void setJust(@Nullable Object value) {
		this.arbitrary = Arbitraries.just(value);
	}

	@Override
	public void markAsNull(boolean toNull) {
		ArbitraryProperty arbitraryProperty = this.getArbitraryProperty();
		if (toNull) {
			this.setArbitraryProperty(arbitraryProperty.withNullInject(ALWAYS_NULL_INJECT));
		} else {
			if (this.getArbitrary() != null) {
				//noinspection ConstantConditions
				if (this.getArbitrary().sample() == null) { // without nullInject
					this.setArbitrary(null);
				}
			}
			this.setArbitraryProperty(arbitraryProperty.withNullInject(NOT_NULL_INJECT));
		}
	}

	void setArbitraryProperty(ArbitraryProperty arbitraryProperty) {
		this.arbitraryProperty = arbitraryProperty;
	}

	void setChildren(List<DefaultArbitraryNode> children) {
		this.children = children;
	}

	@Nullable
	Property getResolvedParentProperty() {
		return resolvedParentProperty;
	}

	ArbitraryProperty getArbitraryProperty() {
		return this.arbitraryProperty;
	}

	Property getProperty() {
		return this.getArbitraryProperty().getObjectProperty().getProperty();
	}

	List<DefaultArbitraryNode> getChildren() {
		return this.children;
	}

	@Nullable
	Arbitrary<?> getArbitrary() {
		return this.arbitrary;
	}

	void setArbitrary(@Nullable Arbitrary<?> arbitrary) {
		this.arbitrary = arbitrary;
	}

	@SuppressWarnings("rawtypes")
	List<Predicate> getArbitraryFilters() {
		return arbitraryFilters;
	}

	boolean isNotManipulated() {
		boolean sized = arbitraryProperty.getContainerProperty() != null
			&& arbitraryProperty.getContainerProperty().getContainerInfo().isManipulated();

		return !manipulated && !sized;
	}

	void setManipulated(boolean manipulated) {
		this.manipulated = manipulated;
	}

	public void setResolvedProperty(Property resolvedProperty) {
		this.resolvedProperty = resolvedProperty;
	}

	public Property getResolvedProperty() {
		return resolvedProperty;
	}
}
