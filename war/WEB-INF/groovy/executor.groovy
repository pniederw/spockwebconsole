import groovy.io.GroovyPrintStream
import org.codehaus.groovy.control.MultipleCompilationErrorsException
import spockwebconsole.ScriptRunner

def scriptText = request.getParameter("script") ?: "'The received script was null.'"

def rawOutput = new ByteArrayOutputStream()
def output = new GroovyPrintStream(rawOutput)
def rawStacktrace = new ByteArrayOutputStream()
def stacktrace = new GroovyPrintStream(rawStacktrace)

// TODO: need to set thread context class loader to sth. other than app class loader (but not null because null is ignored)

def result = ""
try {
  result = new ScriptRunner().run(scriptText)
} catch (MultipleCompilationErrorsException e) {
	stacktrace.append(e.message - 'startup failed, Script1.groovy: ')
} catch (Throwable t) {
	sanitizeStacktrace(t)
	def cause = t
	while (cause = cause?.cause) {
		sanitizeStacktrace(cause)
	}
	t.printStackTrace(stacktrace)
}

response.contentType = "application/json"

out.println """{
	executionResult: "${escape(result)}",
 	outputText: "${escape(rawOutput)}",
 	stacktraceText: "${escape(rawStacktrace)}"
}"""

def escape(object) {
	object.toString().replaceAll(/\n/, /\\\n/).replaceAll(/"/, /\\"/)
}

def sanitizeStacktrace(t) {
    def filtered = [
        'com.google.', 'org.mortbay.',
        'java.', 'javax.', 'sun.', 
        'groovy.', 'org.codehaus.groovy.',
        'groovyx.gaelyk.', 'executor'
	]
    def trace = t.getStackTrace()
    def newTrace = []
    trace.each { stackTraceElement -> 
        if (filtered.every { !stackTraceElement.className.startsWith(it) }) {
            newTrace << stackTraceElement
        }
    }
    def clean = newTrace.toArray(newTrace as StackTraceElement[])
    t.stackTrace = clean
}