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
  private final List<String> visitedTestClasses = new ArrayList<String>();
  private boolean firstFailure;

  public void testRunStarted(Description description) {}

  public void testRunFinished(Result result) {}

  public void testStarted(Description description) {
    printTestClassNameIfNotYetVisited(description, true);
    result.append(" - ").append(description.getMethodName());

    firstFailure = true;
  }

  private void printTestClassNameIfNotYetVisited(Description description, boolean addNewLine) {
    String className = getClassName(description);
    if (visitedTestClasses.contains(className)) return;

    if (!visitedTestClasses.isEmpty()) result.append("\n");
    result.append(className);
    if (addNewLine) result.append("\n");
    visitedTestClasses.add(className);
  }

  private String getClassName(Description description) {
    return isTestClass(description) ? description.getDisplayName() : description.getClassName();
  }

  // cannot use Description.isSuite() because it returns false for Description
  // of test class w/o test methods; probably JUnit does not run into this problem
  // because it doesn't allow test classes w/o test methods in the first place;
  // but even if we would do the same for Spock, we would still run into problems
  // at least for empty @Ignore'd specs
  private boolean isTestClass(Description description) {
    return !description.getDisplayName().endsWith(")"); // sad but true
  }

  public void testFinished(Description description) {
    result.append("\n");
  }

  public void testFailure(Failure failure) {
    if (firstFailure) {
      result.append("   FAILED\n");
      firstFailure = false;
    }
    result.append("\n");
    appendFailure(failure);
  }

  public void testAssumptionFailure(Failure failure) {
    if (firstFailure) {
      result.append("   ASSUMPTION FAILED\n");
      firstFailure = false;
    }
    result.append("\n");
    appendFailure(failure);
  }

  public void testIgnored(Description description) {
    if (isTestClass(description)) {
      printTestClassNameIfNotYetVisited(description, false);
    } else {
      printTestClassNameIfNotYetVisited(description, true);
      result.append(" - ").append(description.getMethodName());
    }
    result.append("   SKIPPED\n");
  }

  public String getResult() {
    return result.toString();
  }
  
  private void appendFailure(Failure failure) {
    String[] lines = failure.getTrace().split("\n");
    for (String line : lines) {
      result.append("   ");
      result.append(line.replace("\t", ""));
      result.append("\n");
    }
  }
}
