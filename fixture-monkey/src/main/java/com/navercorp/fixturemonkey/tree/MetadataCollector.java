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

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;

import com.navercorp.fixturemonkey.api.property.Property;
import com.navercorp.fixturemonkey.api.tree.ArbitraryNode;

@API(since = "0.4.0", status = Status.MAINTAINED)
final class MetadataCollector {
	private final DefaultArbitraryNode rootNode;
	private final Map<Property, List<? extends ArbitraryNode>> nodesByProperty;

	public MetadataCollector(DefaultArbitraryNode rootNode) {
		this.rootNode = rootNode;
		this.nodesByProperty = new LinkedHashMap<>();
	}

	public ArbitraryTreeMetadata collect() {
		for (DefaultArbitraryNode child : rootNode.getChildren()) {
			collect(child);
		}
		return new ArbitraryTreeMetadata(Collections.unmodifiableMap(nodesByProperty));
	}

	private void collect(DefaultArbitraryNode node) {
		Property property = node.getArbitraryProperty().getObjectProperty().getProperty();

		List<DefaultArbitraryNode> children = node.getChildren();
		for (DefaultArbitraryNode child : children) {
			collect(child);
		}

		List<DefaultArbitraryNode> list = Collections.singletonList(node);
		nodesByProperty.merge(
			property,
			list,
			(prev, now) -> Stream.concat(prev.stream(), now.stream()).collect(Collectors.toList())
		);
	}

}
