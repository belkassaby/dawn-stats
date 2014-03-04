package uk.ac.diamond.modulestats.updater.logger;

import java.io.IOException;
import java.io.PrintStream;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class MyLogger {

	private Logger loggerInfo;
	private Logger loggerError;
	private PrintStream stdout;
	private PrintStream stderr;

	public MyLogger() {
		// initialize logging to go to rolling log file
		LogManager logManager = LogManager.getLogManager();
		logManager.reset();
		// log file max size 10K, 3 rolling files, append-on-open
		Handler fileHandler = null;
		try {
			fileHandler = new FileHandler("log", 10000, 3, true);
		} catch (SecurityException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		fileHandler.setFormatter(new SimpleFormatter());
		Logger.getLogger("").addHandler(fileHandler);

		// preserve old stdout/stderr streams in case they might be useful
		setStdout(System.out);
		setStderr(System.err);

		// now rebind stdout/stderr to logger
		LoggingOutputStream los;

		loggerInfo = Logger.getLogger("stdout");
		los = new LoggingOutputStream(loggerInfo, StdOutErrLevel.STDOUT);
		System.setOut(new PrintStream(los, true));

		loggerError = Logger.getLogger("stderr");
		los = new LoggingOutputStream(loggerError, StdOutErrLevel.STDERR);
		System.setErr(new PrintStream(los, true));
	}

	public void info(String msg) {
		loggerInfo.info(msg);
	}

	public void error(String msg) {
		loggerError.info(msg);
	}

	public PrintStream getStdout() {
		return stdout;
	}

	public void setStdout(PrintStream stdout) {
		this.stdout = stdout;
	}

	public PrintStream getStderr() {
		return stderr;
	}

	public void setStderr(PrintStream stderr) {
		this.stderr = stderr;
	}
}
