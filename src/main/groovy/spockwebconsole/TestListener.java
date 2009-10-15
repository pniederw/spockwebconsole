/*
 * Copyright 2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package spockwebconsole;

import java.util.List;
import java.util.ArrayList;

import org.junit.runner.notification.RunListener;
import org.junit.runner.notification.Failure;
import org.junit.runner.Description;
import org.junit.runner.Result;

public class TestListener extends RunListener {
  private final StringBuilder result = new StringBuilder();
  private final List<String> testClassNames = new ArrayList<String>();
  private boolean firstFailure;
  
  public void testRunStarted(Description description) {}

  public void testRunFinished(Result result) {}

  public void testStarted(Description description) {
    String className = description.getClassName();
    if (!testClassNames.contains(className)) {
      if (!testClassNames.isEmpty())
        result.append("\n");
      result.append(className).append("\n");
      testClassNames.add(className);
    }
    result.append(" - ").append(description.getMethodName());

    firstFailure = true;
  }

  public void testFinished(Description description) {
    result.append("\n");
  }

  public void testFailure(Failure failure) {
    if (firstFailure) {
      result.append("   FAILED\n");
      firstFailure = false;
    }
    result.append("\n").append(failure.getTrace());
  }

  public void testAssumptionFailure(Failure failure) {
    if (firstFailure) {
      result.append("   ASSUMPTION FAILED\n");
      firstFailure = false;
    }
    result.append("\n").append(failure.getTrace());
  }

  public void testIgnored(Description description) {
    result.append(" - ").append(description.getMethodName()).append("   IGNORED\n");
  }

  public String getResult() {
    return result.toString();
  }
}
