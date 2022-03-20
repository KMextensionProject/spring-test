package sk.golddigger.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.core.type.AnnotatedTypeMetadata;

// TODO: add support for RegEx
// TODO: lookup design pattern and implement "negate", "and", "or" operators

@Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Conditional(OnPropertyContent.PropertyCondition.class)
public @interface OnPropertyContent {

	public static final String EMPTY = "null";

	String propertyName();
	String lookupValue() default EMPTY;
	boolean ignoreCase() default false;

	class PropertyCondition implements Condition {

		// TODO: refactor to better visual design
		@Override
		public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
			Map<String, Object> attrs = metadata.getAnnotationAttributes(OnPropertyContent.class.getName());

			String propertyName = String.valueOf(attrs.get("propertyName"));
			String lookupValue = String.valueOf(attrs.get("lookupValue"));
			boolean ignoreCase = (boolean) attrs.get("ignoreCase");

			String propertyValue = String.valueOf(context.getEnvironment().getProperty(propertyName)); // "null"

			if (EMPTY.equals(lookupValue) && (EMPTY.equals(propertyValue) || propertyValue.isEmpty())) {
				return true;
			}

			boolean negate = false;
			if (propertyValue.startsWith("negate:")) {
				propertyValue = propertyValue.replace("negate:", "");
				negate = true;
			}

			boolean contains;
			if (ignoreCase) {
				contains = StringUtils.containsIgnoreCase(propertyValue, lookupValue);
			} else {
				contains = propertyValue.contains(lookupValue);
			}

			return negate ? !contains : contains;
		}
	}
}