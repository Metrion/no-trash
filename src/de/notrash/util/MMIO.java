package de.notrash.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.text.Format;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.w3c.dom.Document;

public class MMIO {

	final static String fileSeparator = System.getProperty("file.separator");

	public static void save(String path, String fileName, String content) {
		File file = new File(path + fileName);
		try {
			Writer wf = new FileWriter(file);
			Writer bw = new BufferedWriter(wf);
			PrintWriter pw = new PrintWriter(bw);
			pw.println(content);
			pw.close();
		} catch (IOException e) {
			System.err.println(e.getMessage());
			e.getStackTrace();
		}
	}

	public static void save(File file, String content) {
		try {
			Writer wf = new FileWriter(file);
			Writer bw = new BufferedWriter(wf);
			PrintWriter pw = new PrintWriter(bw);
			pw.println(content);
			pw.close();
		} catch (IOException e) {
			System.err.println(e.getMessage());
			e.getStackTrace();
		}
	}

	public static void addLines(String fileName, ArrayList<String> data) {
		try {
			DataOutputStream dos = new DataOutputStream(new FileOutputStream(
					fileName));
			for (Iterator<String> it = data.iterator(); it.hasNext();) {
				dos.writeUTF((String) it.next());
			}
			dos.close();
		} catch (IOException e) {
			System.err.println(e.getMessage());
			e.getStackTrace();
		}
	}

	public static ArrayList<String> getLines(String fileName) {
		ArrayList<String> lines = new ArrayList<String>();
		try {
			DataInputStream dis = new DataInputStream(new FileInputStream(
					fileName));
			String line;
			try {
				while (dis.available() > 0) {
					line = dis.readUTF();
					lines.add(line);
				}
			} catch (EOFException e) {
				System.err.println(e.getMessage());
				e.getStackTrace();
			}
			dis.close();
		} catch (FileNotFoundException e) {
			System.err.println(e.getMessage());
			e.getStackTrace();
		} catch (IOException e) {
			System.err.println(e.getMessage());
			e.getStackTrace();
		}
		return lines;
	}

	public static void replaceFileName(File dir, File file, String oldChar,
			String newChar) {
		if (file.getName().contains(oldChar)) {
			File oldFile = file;
			String newFileName = oldFile.getName().replace(oldChar, newChar);
			File newFile = new File(dir + fileSeparator + newFileName);
			@SuppressWarnings("unused")
			boolean b = oldFile.renameTo(newFile);
			/**
			 * debug
			 */
			/*
			 * boolean b = oldFile.renameTo(newFile); if(b)
			 * System.out.println("success"); else System.err.println("error");
			 */
			System.out.println(file);
		}
	}

	public static void findSpecialCharacters(File dir) {
		File[] files = dir.listFiles();
		if (files != null) {
			for (int i = 0; i < files.length; i++) {
				if (files[i].isDirectory()) {
					if (files[i].getName().contains("÷")) {
						System.out.println(files[i]);
					}
					if (files[i].getName().contains("³")) {
						System.out.println(files[i]);
					}
					if (files[i].getName().contains("õ")) {
						System.out.println(files[i]);
					}
					if (files[i].getName().contains("▄")) {
						System.out.println(files[i]);
					}
					findSpecialCharacters(files[i]);
				} else {

					String[] specialChar = { "▄", "õ", "÷", "³" };

					for (int j = 0; j < specialChar.length; j++) {
						/**
						 * ÷ = oe; ³ = ue; õ = ae; ▄ = Ue
						 */
						if (files[i].getName().contains(specialChar[j])) {
							if (files[i].getName().contains("÷")) {
								replaceFileName(dir, files[i], "÷", "oe");
							}
							if (files[i].getName().contains("³")) {
								replaceFileName(dir, files[i], "³", "ue");
							}
							if (files[i].getName().contains("õ")) {
								replaceFileName(dir, files[i], "õ", "ae");
							}
							if (files[i].getName().contains("▄")) {
								replaceFileName(dir, files[i], "▄", "Ue");
							}
						}
					}
				}
			}
		}
	}

	public static String prettyFormat(String input, int indent) {
		try {
			Source xmlInput = new StreamSource(new StringReader(input));
			StringWriter stringWriter = new StringWriter();
			StreamResult xmlOutput = new StreamResult(stringWriter);
			Transformer transformer = (Transformer) TransformerFactory
					.newInstance().newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty(
					"{http://xml.apache.org/xslt}indent-amount",
					String.valueOf(indent));
			transformer.transform(xmlInput, xmlOutput);
			return xmlOutput.getWriter().toString();
		} catch (Exception e) {
			throw new RuntimeException(e); // simple exception handling, please
											// review it
		}
	}

	public static String prettyFormat(String input) {
		return prettyFormat(input, 2);
	}

	public static boolean validateXml(String xsdPath, String xsdFileName,
			String xmlErrorFile) {
		if (MMString.isNull(xsdPath) || MMString.isNull(xsdFileName))
			return false;
		else {
			try {
				SchemaFactory schemaFactory = SchemaFactory
						.newInstance("http://www.w3.org/2001/XMLSchema");
				Source schemaSource = new StreamSource(new File(xsdPath
						+ xsdFileName));
				Schema schema = schemaFactory.newSchema(schemaSource);
				Validator validator = schema.newValidator();
				validator.validate(new StreamSource(new StringReader(
						xmlErrorFile)));
				System.out.println("Xml is valid.");
				return true;
			} catch (Exception e) {
				StringBuffer errorMsg = new StringBuffer();
				errorMsg.append("\n ---------------------------- ");
				errorMsg.append("\n msg: [" + e.getMessage() + "]");
				errorMsg.append("\nXml is invalid.");
				errorMsg.append("\n ---------------------------- ");
				System.err.println(errorMsg.toString());
			}
			return false;
		}
	}

	public static final long getFolderSize(File dir) {
		long size = 0;
		for (File file : dir.listFiles()) {
			if (file.isFile())
				size = file.length();
			else
				size = getFolderSize(file);
		}
		return size;
	}

	public static long getTotalFileSpace(File folder, String fileType, long tfs) {
		long totalFileSpace = tfs;

		FilenameFilter filter = new MMFileTypeFilter(fileType);
		String[] filteredFolder = folder.list(filter);

		File[] files = folder.listFiles();

		if (filteredFolder != null) {
			for (File file : files) {
				if (file.isFile()
						&& filter.accept(file, folder.getAbsolutePath()
								+ fileSeparator + file.getName())) {
					totalFileSpace += file.length();
					System.out.println("X " + folder.getAbsolutePath()
							+ fileSeparator + file.getName() + " length: "
							+ file.length() + " total: " + totalFileSpace);
				} else {
					System.out.println("Y " + folder.getAbsolutePath()
							+ fileSeparator + file.getName() + " length: "
							+ file.length() + " total: " + totalFileSpace);
					getTotalFileSpace(new File(folder.getAbsolutePath()
							+ fileSeparator + file.getName()), fileType,
							totalFileSpace);
				}
			}
		}
		return totalFileSpace;
	}

	public static String getFileSpaceSummary(long totalFileSpace) {
		if (totalFileSpace < 1048576l) {
			return ("Total File Space [kB | KiB]: " + totalFileSpace);
		} else if (totalFileSpace >= 1048576l) {
			return ("Total File Space [MB | MiB]: " + totalFileSpace / 1024 / 1024);
		} else if (totalFileSpace < 1073741824l) {
			return ("Total File Space [GB | GiB]: " + totalFileSpace / 1024 / 1024 / 1024);
		} else {
			return ("Total File Space [TB | TiB]: " + totalFileSpace / 1024
					/ 1024 / 1024 / 1024);
		}
	}

	public static void printWindowsCommand(String command) throws Exception {
		System.out.println("Windows command: " + command);
		String line;
		Process process = Runtime.getRuntime().exec("cmd /c " + command);
		Reader r = new InputStreamReader(process.getInputStream());
		BufferedReader in = new BufferedReader(r);
		while ((line = in.readLine()) != null)
			System.out.println(line);
		in.close();
	}

	public static boolean isWindowsSystem() {
		String osName = System.getProperty("os.name").toLowerCase();
		return osName.indexOf("windows") >= 0;
	}

	public static boolean isLinuxSystem() {
		String osName = System.getProperty("os.name").toLowerCase();
		return osName.indexOf("linux") >= 0;
	}
}
