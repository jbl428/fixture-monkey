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

package com.navercorp.fixturemonkey.resolver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;

import net.jqwik.api.Arbitrary;

import com.navercorp.fixturemonkey.ArbitraryBuilder;
import com.navercorp.fixturemonkey.api.context.MonkeyContext;
import com.navercorp.fixturemonkey.api.customizer.FixtureCustomizer;
import com.navercorp.fixturemonkey.api.customizer.NextNodePredicate;
import com.navercorp.fixturemonkey.api.customizer.NodeResolver;
import com.navercorp.fixturemonkey.api.matcher.MatcherOperator;
import com.navercorp.fixturemonkey.api.option.GenerateOptions;
import com.navercorp.fixturemonkey.api.property.Property;
import com.navercorp.fixturemonkey.api.property.RootProperty;
import com.navercorp.fixturemonkey.api.tree.ArbitraryNode;
import com.navercorp.fixturemonkey.customizer.ArbitraryManipulator;
import com.navercorp.fixturemonkey.customizer.ContainerInfoManipulator;
import com.navercorp.fixturemonkey.expression.CompositeNodeResolver;
import com.navercorp.fixturemonkey.expression.PropertyPredicate;
import com.navercorp.fixturemonkey.tree.ArbitraryTraverser;
import com.navercorp.fixturemonkey.tree.ArbitraryTree;
import com.navercorp.fixturemonkey.tree.ArbitraryTreeMetadata;

@API(since = "0.4.0", status = Status.MAINTAINED)
public final class ArbitraryResolver {
	private final ArbitraryTraverser traverser;
	private final ManipulatorOptimizer manipulatorOptimizer;

	private final GenerateOptions generateOptions;
	private final ManipulateOptions manipulateOptions;
	private final MonkeyContext monkeyContext;

	public ArbitraryResolver(
		ArbitraryTraverser traverser,
		ManipulatorOptimizer manipulatorOptimizer,
		GenerateOptions generateOptions,
		ManipulateOptions manipulateOptions,
		MonkeyContext monkeyContext
	) {
		this.traverser = traverser;
		this.manipulatorOptimizer = manipulatorOptimizer;
		this.generateOptions = generateOptions;
		this.manipulateOptions = manipulateOptions;
		this.monkeyContext = monkeyContext;
	}

	@SuppressWarnings("rawtypes")
	public Arbitrary<?> resolve(
		RootProperty rootProperty,
		List<ArbitraryManipulator> manipulators,
		List<MatcherOperator<? extends FixtureCustomizer>> customizers,
		List<ContainerInfoManipulator> containerInfoManipulators
	) {
		List<MatcherOperator<List<ContainerInfoManipulator>>> registeredContainerInfoManipulators =
			manipulateOptions.getRegisteredArbitraryBuilders()
				.stream()
				.map(it ->
					new MatcherOperator<>(
						it.getMatcher(),
						((DefaultArbitraryBuilder<?>)it.getOperator()).getContext().getContainerInfoManipulators()
					)
				)
				.collect(Collectors.toList());

		ArbitraryTree arbitraryTree = new ArbitraryTree(
			rootProperty,
			this.traverser.traverse(
				rootProperty,
				containerInfoManipulators,
				registeredContainerInfoManipulators
			),
			generateOptions,
			monkeyContext,
			customizers
		);

		List<ArbitraryManipulator> registeredManipulators = getRegisteredToManipulators(
			manipulateOptions,
			arbitraryTree.getMetadata()
		);

		List<ArbitraryManipulator> joinedManipulators =
			Stream.concat(registeredManipulators.stream(), manipulators.stream())
				.collect(Collectors.toList());

		List<ArbitraryManipulator> optimizedManipulator = manipulatorOptimizer
			.optimize(joinedManipulators)
			.getManipulators();

		for (ArbitraryManipulator manipulator : optimizedManipulator) {
			manipulator.manipulate(arbitraryTree);
		}

		return arbitraryTree.generate();
	}

	private List<ArbitraryManipulator> getRegisteredToManipulators(
		ManipulateOptions manipulateOptions,
		ArbitraryTreeMetadata metadata
	) {
		List<ArbitraryManipulator> manipulators = new ArrayList<>();
		Map<Property, List<? extends ArbitraryNode>> nodesByType = metadata.getNodesByProperty();
		List<MatcherOperator<? extends ArbitraryBuilder<?>>> registeredArbitraryBuilders =
			manipulateOptions.getRegisteredArbitraryBuilders();

		for (Entry<Property, List<? extends ArbitraryNode>> nodeByType : nodesByType.entrySet()) {
			Property property = nodeByType.getKey();
			List<? extends ArbitraryNode> arbitraryNodes = nodeByType.getValue();

			DefaultArbitraryBuilder<?> registeredArbitraryBuilder =
				(DefaultArbitraryBuilder<?>)registeredArbitraryBuilders.stream()
					.filter(it -> it.match(property))
					.findFirst()
					.map(MatcherOperator::getOperator)
					.filter(it -> it instanceof DefaultArbitraryBuilder<?>)
					.orElse(null);

			if (registeredArbitraryBuilder == null) {
				continue;
			}

			ArbitraryBuilderContext context = registeredArbitraryBuilder.getContext();
			List<ArbitraryManipulator> arbitraryManipulators = context.getManipulators().stream()
				.map(it -> it.withNodeResolver(nodeResolver ->
						new CompositeNodeResolver(
							prependPropertyNodeResolver(property, arbitraryNodes),
							nodeResolver
						)
					)
				)
				.collect(Collectors.toList());

			manipulators.addAll(arbitraryManipulators);
		}
		return manipulators;
	}

	private NodeResolver prependPropertyNodeResolver(
		Property property,
		List<? extends ArbitraryNode> arbitraryNodes
	) {
		return new NodeResolver() {
			@Override
			public List<? extends ArbitraryNode> resolve(ArbitraryNode arbitraryNode) {
				arbitraryNodes.forEach(ArbitraryNode::mark);
				return arbitraryNodes;
			}

			@Override
			public List<NextNodePredicate> toNextNodePredicate() {
				return Collections.singletonList(new PropertyPredicate(property));
			}
		};
	}
}
