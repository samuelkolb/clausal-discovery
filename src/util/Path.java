package util;

import basic.FileUtil;

import java.io.File;

/**
 * Created by samuelkolb on 06/06/15.
 *
 * @author Samuel Kolb
 */
public enum Path {

	IDP("/executable/mac/idp/bin/idp");

	private final String relativePath;

	Path(String relativePath) {
		this.relativePath = relativePath;
	}

	public File getFile() {
		return FileUtil.getLocalFile(getClass().getResource("/executable/mac/idp/bin/idp"));
	}

	public String getRelativePath() {
		return relativePath;
	}

	public String getFullPath() {
		return getFile().getAbsolutePath();
	}
}
