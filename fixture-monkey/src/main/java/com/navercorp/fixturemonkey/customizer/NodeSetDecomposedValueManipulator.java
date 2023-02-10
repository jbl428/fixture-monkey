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

import javax.annotation.Nullable;

import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;

import com.navercorp.fixturemonkey.api.customizer.NodeManipulator;
import com.navercorp.fixturemonkey.api.customizer.Values.ManipulatingSequenceValue;
import com.navercorp.fixturemonkey.api.tree.ArbitraryNode;

@API(since = "0.4.0", status = Status.MAINTAINED)
final class NodeSetDecomposedValueManipulator<T> implements NodeManipulator {
	private final int sequence;
	@Nullable
	private final T value;

	public NodeSetDecomposedValueManipulator(int sequence, @Nullable T value) {
		this.sequence = sequence;
		this.value = value;
	}

	@Override
	public void manipulate(ArbitraryNode arbitraryNode) {
		arbitraryNode.setRecursively(new ManipulatingSequenceValue(sequence, value));
	}
}
