package ch.bazaruto;

/* Custom facility for simple console logging */
public class Log {

	private static boolean isDebug() {
		try {
			Class mfClass = Class.forName("java.lang.management.ManagementFactory");
			Object mfInstance = mfClass.newInstance();
			Object rtMXBean = mfClass.getMethod("getRuntimeMXBean").invoke(mfInstance);
			Class rtClass = rtMXBean.getClass();
			Object inputArgs = rtClass.getMethod("getInputArguments").invoke(rtMXBean);
			return inputArgs.toString().indexOf("-agentlib:jdwp") > 0;
		} catch (Exception e) {
			return false;
		}
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
