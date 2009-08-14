package spockwebconsole;

import java.util.ArrayList;
import java.util.List;

import org.junit.runner.JUnitCore;

import org.spockframework.runtime.model.SpeckMetadata;

public class ScriptRunner {
  public String run(String scriptText) {
    scriptText = addSpockLangStarImport(scriptText);
    ScriptCompiler compiler = new ScriptCompiler();
    List<Class> classes = compiler.compile(scriptText);
    List<Class> testClasses = findTestClasses(classes);
    JUnitCore junit = new JUnitCore();
    TestListener listener = new TestListener();
    junit.addListener(listener);
    junit.run(testClasses.toArray(new Class[0]));
    return listener.getResult().toString();
  }

  private String addSpockLangStarImport(String text) {
    return text + "\nimport spock.lang.*";
  }

  private List<Class> findTestClasses(List<Class> classes) {
    List<Class> testClasses = new ArrayList<Class>();
    for (Class clazz : classes)
      if (clazz.isAnnotationPresent(SpeckMetadata.class))
        testClasses.add(clazz);
    return testClasses;
  }
}