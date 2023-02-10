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

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;

import com.navercorp.fixturemonkey.api.customizer.NextNodePredicate;
import com.navercorp.fixturemonkey.api.customizer.NodeResolver;
import com.navercorp.fixturemonkey.api.tree.ArbitraryNode;

@API(since = "0.4.0", status = Status.MAINTAINED)
public class DefaultNodeResolver implements NodeResolver {
	private final NextNodePredicate nextNodePredicate;

	public DefaultNodeResolver(NextNodePredicate nextNodePredicate) {
		this.nextNodePredicate = nextNodePredicate;
	}

	@Override
	public List<? extends ArbitraryNode> resolve(ArbitraryNode arbitraryNode) {
		return arbitraryNode.nextNodes(nextNodePredicate);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}
		DefaultNodeResolver that = (DefaultNodeResolver)obj;
		return nextNodePredicate.equals(that.nextNodePredicate);
	}

	@Override
	public int hashCode() {
		return Objects.hash(nextNodePredicate);
	}

	@Override
	public List<NextNodePredicate> toNextNodePredicate() {
		return Collections.singletonList(nextNodePredicate);
	}
}
