package org.someth2say.taijitu.config2;

import java.util.List;

public interface TaijituConfig  {
	List<ComparisonConfig> getComparisons();

	int getThreads();

	String getConsoleLog();

	String getFileLog();

	String getOutputFolder();

	Boolean isUseScanClassPath();

}
