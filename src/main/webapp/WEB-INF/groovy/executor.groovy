import org.codehaus.groovy.control.MultipleCompilationErrorsException
import com.google.apphosting.api.ApiProxy
import spockwebconsole.ScriptRunner

static encoding = 'UTF-8'
static threadLocalOutErrStream = new ThreadLocal()
static delegatingOutStream = new ThreadLocalDelegatingStream(original: System.out, threadLocal: threadLocalOutErrStream)
static delegatingErrStream = new ThreadLocalDelegatingStream(original: System.err, threadLocal: threadLocalOutErrStream)
static systemOutStream = new PrintStream(delegatingOutStream, true, encoding)
static systemErrStream = new PrintStream(delegatingErrStream, true, encoding)

static globalRedirect = {
  System.setOut(systemOutStream)
  System.setErr(systemErrStream)
}()

class ThreadLocalDelegatingStream extends OutputStream {
  def original
  def threadLocal

  void write(int b) {
    (threadLocal.get() ?: original).write(b)
  }

  void write(byte[] b) {
    (threadLocal.get() ?: original).write(b)
  }

  void write(byte[] b, int off, int len) {
    (threadLocal.get() ?: original).write(b, off, len)
  }

  void flush() {
    (threadLocal.get() ?: original).flush()
  }

  void close() {
    (threadLocal.get() ?: original).close()
  }
}

def scriptText = request.getParameter("script") ?: "'The received script was null.'"

def stream = new ByteArrayOutputStream()

def stacktrace = new StringWriter()
def errWriter = new PrintWriter(stacktrace)

def result = ""
def env = ApiProxy.getCurrentEnvironment()
ApiProxy.clearEnvironmentForCurrentThread()
threadLocalOutErrStream.set(stream)
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
	t.printStackTrace(errWriter)
} finally {
  threadLocalOutErrStream.remove()
  ApiProxy.setEnvironmentForCurrentThread(env)
}

response.contentType = "application/json"

out.println """{
	executionResult: "${escape(result)}",
 	outputText: "${escape(stream.toString(encoding))}",
 	stacktraceText: "${escape(stacktrace)}"
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