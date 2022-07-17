package sk.golddigger.annotations;

import static sk.golddigger.enums.RegexPatterns.NULL;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.core.type.AnnotatedTypeMetadata;

@Target({ ElementType.TYPE, ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Conditional(OnPropertyContent.PropertyCondition.class)
public @interface OnPropertyContent {

	String propertyName();
	String lookupRegex() default NULL;

	class PropertyCondition implements Condition {

		@Override
		public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
			Map<String, Object> attrs = metadata.getAnnotationAttributes(OnPropertyContent.class.getName());
			if (attrs == null || attrs.isEmpty()) {
				return false;
			}
			String propertyName = String.valueOf(attrs.get("propertyName"));
			String lookupRegex = String.valueOf(attrs.get("lookupRegex"));
			String propertyValue = String.valueOf(context.getEnvironment().getProperty(propertyName));

			if (NULL.equals(lookupRegex) && (NULL.equals(propertyValue) || propertyValue.isEmpty())) {
				return true;
			}

			Matcher regexPattern = Pattern.compile(lookupRegex).matcher(propertyValue);
			return regexPattern.find();
		}
	}
}
