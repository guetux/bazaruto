package ch.bazaruto.storage;

import java.io.FileNotFoundException;
import java.io.InputStream;

public interface Storage {

	public String[] list(String path);
	public boolean exists(String path);
	public boolean isDirectory(String path);
	public long lastModified(String path);
	public long length(String path);
	public InputStream open(String path) throws FileNotFoundException;
}
