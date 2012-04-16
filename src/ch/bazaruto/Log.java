package ch.bazaruto;

/* Custom facility for simple console logging */
public class Log {

	private static boolean isDebug() {
		return java.lang.management.ManagementFactory.getRuntimeMXBean().
			    getInputArguments().toString().indexOf("-agentlib:jdwp") > 0;
	}

	public static void info(String msg) {
		if (isDebug()) {
			System.out.println(msg);
		}
	}
	
	public static void error(String msg) {
		if (isDebug()) {
			System.err.println(msg);
		}
	}
}
