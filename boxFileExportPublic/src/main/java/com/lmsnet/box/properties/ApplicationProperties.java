package com.lmsnet.box.properties;

import com.lmsnet.box.exception.BoxException;
import com.lmsnet.box.utils.BoxUtils;

import java.io.IOException;
import java.util.Properties;

public class ApplicationProperties
{
	private String developerAccessToken;
	private String userId;
	private String rootFolderName;
	private String editorFolderNameEndingWith;
	private String viewerUploaderGroupNames;
	private String editorGroupNames;
	private String destinationFolderName;
	private int numberOfThreads;

	public ApplicationProperties(final String fileName) throws BoxException
	{
		try
		{
			final Properties properties = new Properties();
			properties.load(BoxUtils.getFileReader(fileName));

			developerAccessToken = properties.getProperty( "developer_access_token" );
			userId = properties.getProperty( "user_id" );
			rootFolderName = properties.getProperty( "root_folder_name" );
			editorFolderNameEndingWith = properties.getProperty( "editor_folder_name_ending_with" );
			viewerUploaderGroupNames = properties.getProperty( "viewer_uploader_group_names" );
			editorGroupNames = properties.getProperty( "editor_group_names" );
			destinationFolderName = properties.getProperty( "destination_folder_name" );
			numberOfThreads = Integer.parseInt( properties.getProperty( "number_of_threads" ) );
		}
		catch (final IOException ex)
		{
			throw new BoxException("An error occurred loading box properties");
		}
	}

	public String getDeveloperAccessToken()
	{
		return developerAccessToken;
	}

	public void setDeveloperAccessToken(final String developerAccessToken)
	{
		this.developerAccessToken = developerAccessToken;
	}

	public String getUserId()
	{
		return userId;
	}

	public void setUserId(final String userId)
	{
		this.userId = userId;
	}

	public String getRootFolderName()
	{
		return rootFolderName;
	}

	public void setRootFolderName(final String rootFolderName)
	{
		this.rootFolderName = rootFolderName;
	}

	public String getEditorFolderNameEndingWith()
	{
		return editorFolderNameEndingWith;
	}

	public void setEditorFolderNameEndingWith(final String editorFolderNameEndingWith)
	{
		this.editorFolderNameEndingWith = editorFolderNameEndingWith;
	}

	public String getviewerUploaderGroupNames()
	{
		return viewerUploaderGroupNames;
	}

	public void setviewerUploaderGroupNames(final String viewerUploaderGroupNames)
	{
		this.viewerUploaderGroupNames = viewerUploaderGroupNames;
	}

	
	public String getEditorGroupNames()
	{
		return editorGroupNames;
	}

	
	public void setEditorGroupNames( String editorGroupNames )
	{
		this.editorGroupNames = editorGroupNames;
	}

	
	public String getDestinationFolderName()
	{
		return this.destinationFolderName;
	}

	
	public void setDestinationFolderName( String destinationFolderName )
	{
		this.destinationFolderName = destinationFolderName;
	}

	
	public int getNumberOfThreads()
	{
		return this.numberOfThreads;
	}

	
	public void setNumberOfThreads( int numberOfThreads )
	{
		this.numberOfThreads = numberOfThreads;
	}
}
