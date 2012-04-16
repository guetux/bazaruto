package ch.bazaruto;

import java.lang.reflect.Method;

/* Custom facility for simple console logging */
public class Log {

	private static boolean isDebug() {
		try {
			return System.getenv("DEBUG").equals("True");
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	private static void logOrPrintInfo(String msg) {
		try {
			Class log = Class.forName("android.util.Log");
			Method d = log.getDeclaredMethod("d", String.class, String.class);
			d.invoke(null, "LOG", msg);
		} catch (Exception e) {
			System.out.println(msg);
		}
	}
	
	private static void logOrPrintError(String msg) {
		try {
			Class log = Class.forName("android.util.Log");
			Method w = log.getDeclaredMethod("w", String.class, String.class);
			w.invoke(null, "ERROR", msg);
		} catch (Exception e) {
			System.out.println(msg);
		}
	}
	
	public static void info(String msg) {
		if (isDebug()) {
			logOrPrintInfo(msg);
		}
	}
	
	public static void error(String msg) {
		if (isDebug()) {
			logOrPrintError(msg);
		}
	}
}
