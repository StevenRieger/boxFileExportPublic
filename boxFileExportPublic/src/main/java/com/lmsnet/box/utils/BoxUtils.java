package com.lmsnet.box.utils;

import com.lmsnet.box.BoxFileExport;
import com.lmsnet.box.exception.BoxException;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;

public class BoxUtils
{
	public static Reader getFileReader(final String fileName) throws BoxException
	{
		try
		{
			final ClassLoader classLoader = BoxFileExport.class.getClassLoader();
			return new FileReader(new File(classLoader.getResource(fileName).getFile()));
		}
		catch (final Exception e)
		{
			throw new BoxException("Unable to read file: " + fileName);
		}
	}
}
