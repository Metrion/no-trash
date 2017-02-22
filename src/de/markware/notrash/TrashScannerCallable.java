package de.markware.notrash;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

public class TrashScannerCallable implements Callable<Map<String, List<String>>> {
    private static MessageDigest md;
    private Map<String, List<String>> lists = new HashMap<>();
    private File directory;
    
    static {
        try {
            md = MessageDigest.getInstance("SHA-512");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("cannot initialize SHA-512 hash function", e);
        }
    }

    public TrashScannerCallable(File aDirectory) {
    	directory = aDirectory;
	}
    
    @Override
    public Map<String, List<String>> call() throws Exception {
    	return find();
    }
    
    private Map<String, List<String>> find() {
		String ROOT = directory.getAbsolutePath();
		final FileVisitor<Path> fileProcessor = new ProcessFile();
		try {
			Files.walkFileTree(Paths.get(ROOT), fileProcessor);
		} catch (IOException e) {
			throw new RuntimeException("cannot walk file tree ", e);
		}
        return lists;
    }
    
	private class ProcessFile extends SimpleFileVisitor<Path> {
		
		@Override
		public FileVisitResult visitFile(Path aFile, BasicFileAttributes aAttrs)
				throws IOException {
			
			// System.out.println("Processing file:" + aFile);
            try {
                FileInputStream fin = new FileInputStream(aFile.toFile());
                byte data[] = new byte[(int) aFile.toFile().length()];
                fin.read(data);
                fin.close();
                String hash = new BigInteger(1, md.digest(data)).toString(16);
                List<String> list = lists.get(hash);
                if (list == null) {
                    list = new LinkedList<String>();
                    lists.put(hash, list);
                }
                list.add(aFile.toFile().getAbsolutePath());
            } catch (IOException e) {
                throw new RuntimeException("cannot read file " + aFile.toFile().getAbsolutePath(), e);
            }
		
			return FileVisitResult.CONTINUE;
		}		
	}
}