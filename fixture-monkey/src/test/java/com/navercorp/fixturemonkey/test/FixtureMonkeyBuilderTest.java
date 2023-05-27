package com.navercorp.fixturemonkey.test;

import com.navercorp.fixturemonkey.FixtureMonkey;
import com.navercorp.fixturemonkey.api.introspector.ConstructorPropertiesArbitraryIntrospector;
import com.navercorp.fixturemonkey.api.matcher.MatcherOperator;
import org.jetbrains.annotations.NotNull;
import net.jqwik.api.Property;

import static com.navercorp.fixturemonkey.api.generator.DefaultNullInjectGenerator.NOT_NULL_INJECT;
import static org.junit.jupiter.api.Assertions.*;

class FixtureMonkeyBuilderTest {
	@Property
	void jetBrain() {
		FixtureMonkeyTestSpecs.JetBrainAnnotationObject jetBrainAnnotationObject = FixtureMonkey.builder()
			.objectIntrospector(ConstructorPropertiesArbitraryIntrospector.INSTANCE)
			.pushNullInjectGenerator(
				new MatcherOperator<>(
					property -> {
						boolean present = property.getAnnotation(NotNull.class).isPresent();
						return present;
					},
					context -> NOT_NULL_INJECT
				)
			)
			.build()
			.giveMeOne(FixtureMonkeyTestSpecs.JetBrainAnnotationObject.class);

		assertNotNull(jetBrainAnnotationObject.getValue());
	}
}
