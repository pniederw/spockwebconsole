package spockwebconsole;

import java.lang.reflect.Method;

public class ScriptRunnerProxy {
  public String run(Object scriptRunner, String scriptText) {
    try {
      Method runMethod = scriptRunner.getClass().getMethod("run", String.class);
      return (String)runMethod.invoke(scriptRunner, scriptText);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}

