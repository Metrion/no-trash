package de.notrash.util;
import java.io.File;

public class MMFileTypeFilter implements java.io.FilenameFilter {
	private String fileType;

	public MMFileTypeFilter(String fileType) {
		this.fileType = fileType;
	}

	public boolean accept(File dir, String name) {
		return name.endsWith(fileType);
	}
}