package spockwebconsole;

import java.util.*;

import org.codehaus.groovy.control.*;
import org.codehaus.groovy.control.io.StringReaderSource;

import groovy.lang.GroovyClassLoader;

public class ScriptCompiler {
  public List<Class> compile(String scriptText) throws CompilationFailedException {
    ActualScriptCompiler actual = new ActualScriptCompiler();
    return actual.compile(scriptText);
  }
}

// cannot use GCL as-is because we need a StringReaderSource
// only reason why we inherit from GCL is that ClassCollector has protected constructor
@SuppressWarnings("unchecked")
class ActualScriptCompiler extends GroovyClassLoader {
  List<Class> compile(String scriptText) throws CompilationFailedException {
    CompilationUnit unit = new CompilationUnit(this);
    SourceUnit su = new SourceUnit("Script1.groovy", new StringReaderSource(scriptText, unit.getConfiguration()),
        unit.getConfiguration(), unit.getClassLoader(), unit.getErrorCollector());
    unit.addSource(su);

    ClassCollector collector = createCollector(unit, su);
    unit.setClassgenCallback(collector);
    unit.compile(Phases.CLASS_GENERATION);
    return (List)collector.getLoadedClasses();
  }
}
