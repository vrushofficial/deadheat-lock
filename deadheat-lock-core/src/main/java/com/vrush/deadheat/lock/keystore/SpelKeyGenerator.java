/**
 * @author Vrushabh Joshi
 *
 */
package com.vrush.deadheat.lock.keystore;

import com.vrush.deadhead.lock.key.KeyGenerator;
import com.vrush.deadheat.lock.exception.EvaluationConvertException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.context.expression.AnnotatedElementKey;
import org.springframework.context.expression.CachedExpressionEvaluator;
import org.springframework.context.expression.MethodBasedEvaluationContext;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

@Data
@EqualsAndHashCode(callSuper = false)
public class SpelKeyGenerator extends CachedExpressionEvaluator implements KeyGenerator {
  private final Map<ExpressionKey, Expression> conditionCache = new ConcurrentHashMap<>();
  private final ConversionService conversionService;

  @Override
  public List<String> resolveKeys(final String lockKeyPrefix, final String expression, final Object object, final Method method, final Object[] args) {
    final Object expressionValue = evaluateExpression(expression, object, method, args);
    final List<String> keys = convertResultToList(expressionValue);

    if (keys.stream().anyMatch(Objects::isNull)) {
      throw new EvaluationConvertException("null keys are not supported: " + keys);
    }

    if (StringUtils.isEmpty(lockKeyPrefix)) {
      return keys;
    }

    return keys.stream().map(key -> lockKeyPrefix + key).collect(Collectors.toList());
  }

  protected List<String> convertResultToList(final Object expressionValue) {
    final List<String> list;
    if (expressionValue instanceof Iterable) {
      list = iterableToList(expressionValue);
    } else if (expressionValue.getClass().isArray()) {
      list = arrayToList(expressionValue);
    } else {
      list = Collections.singletonList(expressionValue.toString());
    }

    if (CollectionUtils.isEmpty(list)) {
      throw new EvaluationConvertException("Expression evaluated in an empty list");
    }

    return list;
  }

  private Object evaluateExpression(final String expression, final Object object, final Method method, final Object[] args) {
    final EvaluationContext context = new MethodBasedEvaluationContext(object, method, args, super.getParameterNameDiscoverer());
    context.setVariable("executionPath", object.getClass().getCanonicalName() + "." + method.getName());

    final Expression evaluatedExpression = getExpression(this.conditionCache, new AnnotatedElementKey(method, object.getClass()), expression);
    final Object expressionValue = evaluatedExpression.getValue(context);
    if (expressionValue == null) {
      throw new EvaluationConvertException("Expression evaluated in a null");
    }

    return expressionValue;
  }

  private List<String> iterableToList(final Object expressionValue) {
    final TypeDescriptor genericCollection = TypeDescriptor.collection(Collection.class, TypeDescriptor.valueOf(Object.class));
    return toList(expressionValue, genericCollection);
  }

  private List<String> arrayToList(final Object expressionValue) {
    final TypeDescriptor genericArray = TypeDescriptor.array(TypeDescriptor.valueOf(Object.class));
    return toList(expressionValue, genericArray);
  }

  private List<String> toList(final Object expressionValue, final TypeDescriptor from) {
    final TypeDescriptor listTypeDescriptor = TypeDescriptor.collection(List.class, TypeDescriptor.valueOf(String.class));
    return (List<String>) conversionService.convert(expressionValue, from, listTypeDescriptor);
  }
}
