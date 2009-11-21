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

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import org.junit.runner.JUnitCore;

import org.spockframework.runtime.SpecUtil;

/**
 * Runs a script containing one or more Spock specifications.
 */
public class ScriptRunner {
  public String run(String scriptText) {
    scriptText = addSpockLangStarImport(scriptText);
    ScriptCompiler compiler = new ScriptCompiler();
    List<Class> classes = compiler.compile(scriptText);
    List<Class> testClasses = findTestClasses(classes);
    if (testClasses.isEmpty()) return "No runnable specifications found";
    
    JUnitCore junit = new JUnitCore();
    TestListener listener = new TestListener();
    junit.addListener(listener);
    junit.run(testClasses.toArray(new Class[testClasses.size()]));
    return listener.getResult();
  }

  private String addSpockLangStarImport(String text) {
    return text + "\nimport spock.lang.*";
  }

  private List<Class> findTestClasses(List<Class> classes) {
    List<Class> testClasses = new ArrayList<Class>();
    for (Class clazz : classes)
      if (SpecUtil.isSpec(clazz) && !Modifier.isAbstract(clazz.getModifiers()))
        testClasses.add(clazz);
    return testClasses;
  }
}