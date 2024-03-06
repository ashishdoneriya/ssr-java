package com.csetutorials.ssj.services;

import com.csetutorials.ssj.Main;
import com.csetutorials.ssj.exceptions.FileSystemException;
import com.csetutorials.ssj.utils.StringUtils;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

@Service
public class FileService {

	public String getString(String path) {
		return getString(new File(path));
	}

	public String getString(File file) {
		try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
			StringBuilder stringBuilder = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				stringBuilder.append(line);
				stringBuilder.append('\n');
			}
			int length = stringBuilder.length();
			if (length < 1) {
				return "";
			}
			// delete the last new line separator
			stringBuilder.deleteCharAt(stringBuilder.length() - 1);
			return stringBuilder.toString();
		} catch (IOException e) {
			throw new FileSystemException("Problem while reading the file - " + file.getAbsolutePath(), e);
		}
	}

	public void write(String path, String content) {
		File file = new File(path);
		mkdirs(file.getParentFile());
		try (PrintWriter out = new PrintWriter(file)) {
			out.print(content);
			out.flush();
		} catch (IOException e) {
			throw new FileSystemException("Problem while writing to the file - " + path, e);
		}
	}

	public void mkdirs(File dir) {
		if (!dir.exists() && !dir.mkdirs()) {
			throw new FileSystemException("Couldn't create dir - " + dir.getAbsolutePath());
		}
	}

	public List<File> getFilesRecursively(String dirPath) {
		File dir = new File(dirPath);
		if (listFiles(dir).isEmpty()) {
			return new ArrayList<>(1);
		}
		Stack<File> stack = new Stack<>();
		List<File> list = new ArrayList<>(1);
		stack.push(dir);
		while (!stack.isEmpty()) {
			File file = stack.pop();
			if (file.isFile()) {
				list.add(file);
			} else if (file.isDirectory()) {
				for (File temp : listFiles(file)) {
					stack.push(temp);
				}
			}
		}
		return list;
	}

	public void copyDirRecursively(String srcDirPath, String destPath) {
		List<File> files = getFilesRecursively(srcDirPath);
		Path pathBase = Paths.get(srcDirPath);
		for (File srcFile : files) {
			Path pathAbsolute = Paths.get(srcFile.getAbsolutePath());
			Path pathRelative = pathBase.relativize(pathAbsolute);
			String relativePath = pathRelative.toString();
			String targetPath = new File(destPath).getAbsolutePath() + File.separator + relativePath;
			File targetFile = new File(targetPath);
			mkdirs(targetFile.getParentFile());
			copyFile(srcFile, targetFile);
		}
	}

	public void copyFile(File source, File dest) {
		mkdirs(dest.getParentFile());
		try {
			Files.deleteIfExists(dest.toPath());
		} catch (IOException e) {
			throw new FileSystemException("Couldn't clean path [" + dest.getAbsolutePath() + "] for copying [" + source.getAbsolutePath() + "]");
		}

		try (InputStream is = new FileInputStream(source); OutputStream os = new FileOutputStream(dest)) {
			byte[] buffer = new byte[1024];
			int length;
			while ((length = is.read(buffer)) > 0) {
				os.write(buffer, 0, length);
			}
		} catch (IOException e) {
			throw new FileSystemException("Problem while copying the file from [ " + source.getAbsolutePath() + "] to [ " + dest.getAbsolutePath() +" ]", e);
		}
	}

	public String getResourceContent(String fileName) {
		try {
			return StringUtils.getString(Main.class.getResourceAsStream("/" + fileName));
		} catch (IOException e) {
			throw new FileSystemException("Problem while fetching the content of resouce [ " + fileName + " ]", e);
		}
	}

	public void deleteDir(File dir) {
		if (dir.isDirectory()) {
			for (File file : listFiles(dir))
				deleteDir(file);
		}
		if (!dir.delete())
			throw new FileSystemException("Failed to delete file: " + dir);
	}

	public List<File> listFiles(String dirPath) {
		return listFiles(new File(dirPath));
	}

	public List<File> listFiles(File dir) {
		List<File> list = new ArrayList<>(1);
		if (dir == null || !dir.exists() || !dir.isDirectory()) {
			return list;
		}
		File[] files = dir.listFiles();
		if (files == null) {
			return list;
		}
		return Arrays.asList(files);
	}

}
