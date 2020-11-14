/**
 * @author Vrushabh Joshi
 *
 */
package com.vrush.deadheat.lock.key;

import com.vrush.deadheat.lock.exception.EvaluationConvertException;
import com.vrush.deadheat.lock.keystore.SpelKeyGenerator;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.Test;
import org.springframework.core.convert.support.DefaultConversionService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

public class SpelKeyGeneratorTest {
  private final KeyGenerator keyGenerator = new SpelKeyGenerator(new DefaultConversionService());
  private final MessageService service = new MessageService();
  private final Method sendMessageMethod;

  public SpelKeyGeneratorTest() throws NoSuchMethodException {
    sendMessageMethod = MessageService.class.getMethod("sendMessage", String.class);
  }

  @Test
  public void shouldGenerateExecutionPath() {
    assertThat(keyGenerator.resolveKeys("lock_", "#executionPath", service, sendMessageMethod, new Object[]{"vrush"}))
      .containsExactly("lock_com.vrush.deadheat.lock.key.SpelKeyGeneratorTest.MessageService.sendMessage");
  }

  @Test
  public void shouldGenerateSingleKeyFromContextAndVariables() {
    assertThat(keyGenerator.resolveKeys("lock_", "#p0", service, sendMessageMethod, new Object[]{"vrush"}))
      .containsExactly("lock_vrush");

    assertThat(keyGenerator.resolveKeys("lock_", "#a0", service, sendMessageMethod, new Object[]{"vrush"}))
      .containsExactly("lock_vrush");

    assertThat(keyGenerator.resolveKeys("lock_", "#message", service, sendMessageMethod, new Object[]{"vrush"}))
      .containsExactly("lock_vrush");
  }

  @Test(expected = EvaluationConvertException.class)
  public void shouldFailWithExpressionThatEvaluatesInNull() {
    keyGenerator.resolveKeys("lock_", "null", service, sendMessageMethod, new Object[]{"vrush"});
    fail("Expected exception with expression that evaluated in null");
  }

  private static class MessageService {
    public void sendMessage(String message) {
    }
  }
}