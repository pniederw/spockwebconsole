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
