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

package com.navercorp.fixturemonkey.expression;

import java.util.List;

import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;

import com.navercorp.fixturemonkey.api.customizer.NextNodePredicate;
import com.navercorp.fixturemonkey.api.customizer.NodeResolver;
import com.navercorp.fixturemonkey.api.tree.ArbitraryNode;

@API(since = "0.4.0", status = Status.MAINTAINED)
public final class ApplyStrictNodeResolver implements NodeResolver {
	private final NodeResolver nodeResolver;

	public ApplyStrictNodeResolver(NodeResolver nodeResolver) {
		this.nodeResolver = nodeResolver;
	}

	@Override
	public List<? extends ArbitraryNode> resolve(ArbitraryNode arbitraryNode) {
		List<? extends ArbitraryNode> selectedNodes = nodeResolver.resolve(arbitraryNode);

		if (selectedNodes.isEmpty()) {
			throw new IllegalArgumentException("No matching results for given NodeResolvers.");
		}
		return selectedNodes;
	}

	@Override
	public List<NextNodePredicate> toNextNodePredicate() {
		return nodeResolver.toNextNodePredicate();
	}
}
