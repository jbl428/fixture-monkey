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

/**
 * Provides static methods or constants for extensions of ArbitraryBuilder {@code set}.
 */
@API(since = "0.5.1", status = Status.EXPERIMENTAL)
public final class Values {
	@Deprecated
	public static final Object NOT_NULL = com.navercorp.fixturemonkey.api.customizer.Values.NOT_NULL;

	private Values() {
	}

	/**
	 * @see com.navercorp.fixturemonkey.api.customizer.Values#just(Object)
	 */
	@Deprecated
	public static com.navercorp.fixturemonkey.api.customizer.Values.Just just(@Nullable Object value) {
		return com.navercorp.fixturemonkey.api.customizer.Values.just(value);
	}
}
