package com.csetutorials.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import com.csetutorials.Main;

public class FileUtils {

	public static String getString(String path) throws IOException {
		return getString(new File(path));
	}

	public static String getString(File file) throws IOException {
		try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
			StringBuilder stringBuilder = new StringBuilder();
			String line = null;
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
		}
	}

	public static void write(String path, String content) throws FileNotFoundException {
		File file = new File(path);
		file.getParentFile().mkdirs();
		try (PrintWriter out = new PrintWriter(file)) {
			out.print(content);
			out.flush();
		}
	}

	public static List<File> getFilesRecursively(String dirPath) {
		File dir = new File(dirPath);
		if (!dir.exists() || dir.list().length == 0) {
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
				for (File temp : file.listFiles()) {
					stack.push(temp);
				}
			}
		}
		return list;
	}

	public static void copyDirRecursively(String srcDirPath, String destPath) throws IOException {
		List<File> files = getFilesRecursively(srcDirPath);
		Path pathBase = Paths.get(srcDirPath);
		for (File srcFile : files) {
			Path pathAbsolute = Paths.get(srcFile.getAbsolutePath());
			Path pathRelative = pathBase.relativize(pathAbsolute);
			String relativePath = pathRelative.toString();
			String targetPath = new File(destPath).getAbsolutePath() + File.separator + relativePath;
			File targetFile = new File(targetPath);
			targetFile.getParentFile().mkdirs();
			copyFile(srcFile, targetFile);
		}
	}

	public static void copyFile(File source, File dest) throws IOException {
		dest.getParentFile().mkdirs();
		if (dest.exists()) {
			dest.delete();
		}
		try (InputStream is = new FileInputStream(source); OutputStream os = new FileOutputStream(dest)) {
			byte[] buffer = new byte[1024];
			int length;
			while ((length = is.read(buffer)) > 0) {
				os.write(buffer, 0, length);
			}
		}
	}

	public static String getResourceContent(String fileName) throws IOException {
		return StringUtils.getString(Main.class.getResourceAsStream("/" + fileName));
	}

}
