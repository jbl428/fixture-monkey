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

package com.navercorp.fixturemonkey.customizer;

import java.util.List;
import java.util.function.UnaryOperator;

import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;

import com.navercorp.fixturemonkey.api.customizer.NodeManipulator;
import com.navercorp.fixturemonkey.api.customizer.NodeResolver;
import com.navercorp.fixturemonkey.api.tree.ArbitraryNode;
import com.navercorp.fixturemonkey.tree.ArbitraryTree;

@API(since = "0.4.0", status = Status.MAINTAINED)
public final class ArbitraryManipulator {
	private final NodeResolver nodeResolver;
	private final NodeManipulator nodeManipulator;

	ArbitraryManipulator(NodeResolver nodeResolver, NodeManipulator nodeManipulator) {
		this.nodeResolver = nodeResolver;
		this.nodeManipulator = nodeManipulator;
	}

	public ArbitraryManipulator withNodeResolver(UnaryOperator<NodeResolver> nodeResolverOperator) {
		return new ArbitraryManipulator(
			nodeResolverOperator.apply(this.nodeResolver),
			this.nodeManipulator
		);
	}

	public ArbitraryManipulator withNodeManipulator(UnaryOperator<NodeManipulator> nodeManipulatorOperator) {
		return new ArbitraryManipulator(
			this.nodeResolver,
			nodeManipulatorOperator.apply(nodeManipulator)
		);
	}

	public NodeResolver getNodeResolver() {
		return nodeResolver;
	}

	public NodeManipulator getNodeManipulator() {
		return nodeManipulator;
	}

	public void manipulate(ArbitraryTree tree) {
		List<? extends ArbitraryNode> nodes = nodeResolver.resolve(tree.findRoot());
		for (ArbitraryNode node : nodes) {
			nodeManipulator.manipulate(node);
		}
	}
}
