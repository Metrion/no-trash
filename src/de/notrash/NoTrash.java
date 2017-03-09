package de.markware.notrash;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import de.markware.toolbox.utils.MMIO;

public class NoTrash {
	private static final int NTHREDS = 10;

	public static void main(String[] args) {
		String dateFormat = "yyyy-MM-dd HH:mm:ss,SSS";
		SimpleDateFormat dfISO = new SimpleDateFormat(dateFormat);
		StringBuffer report = new StringBuffer();
		Properties properties = new Properties();

		try {
			properties.load(new FileInputStream("notrash.properties"));
		} catch (FileNotFoundException e1) {
			throw new RuntimeException("cannot find file 'notrash.properties'", e1);

		} catch (IOException e1) {
			throw new RuntimeException("cannot read file 'notrash.properties'", e1);
		}

		final String folderPath = properties.getProperty("de.notrash.folders");
		String fileName = properties.getProperty("de.notrash.filename");

		Date startDate = new Date();
		long startDateInMs = startDate.getTime();
		report.append(MessageFormat.format("Start: {0}",
				dfISO.format(startDate))+"\n");

		final String[] folders = folderPath.split(";");

		final ExecutorService executor = Executors.newFixedThreadPool(NTHREDS);
		List<Future<Map<String, List<String>>>> list = new ArrayList<>();

		for (int i = 0; i < folders.length; i++) {
			final String folder = folders[i];
			 if (!(new File(folder)).isDirectory()) {
				 System.err.println("Supplied directory does not exist.");
				 return;
			 }
			 else
			 {
				Callable<Map<String, List<String>>> worker = new TrashScannerCallable(
						new File(folder));
				Future<Map<String, List<String>>> submit = executor.submit(worker);
				list.add(submit);
			 }
		}

		Map<String, List<String>> trashList = new HashMap<>();
		
		// now retrieve the result
		for (Future<Map<String, List<String>>> future : list) {
			try {
				trashList.putAll(future.get());
				report.append(new Date());
			} catch (InterruptedException e) {
				System.err.println("thread was interrupted " + e.getMessage());
			} catch (ExecutionException e) {
				System.err.println("cannot execute thread " + e.getMessage());
			}
		}

		for (List<String> trash : trashList.values()) {
			if (trash.size() > 1) {
				report.append("--\n");
				for (String file : trash) {
					report.append(file+"\n");
				}
			}
		}
		
		report.append("--\n");

		Date endDate = new Date();
		report.append(MessageFormat.format("End: {0}",
				dfISO.format(endDate))+"\n");
		report.append(MessageFormat.format("Duration [s]: {0}",
				(endDate.getTime() - startDateInMs) / 60)+"\n");
		
		// save report into current folder 
		MMIO.save(new File(System.getProperty("user.dir") + "\\" + fileName + ".txt"), report.toString());
		
		executor.shutdown();
	}
}
