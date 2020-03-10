package com.lmsnet.box;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.logging.Level;

import com.box.sdk.BoxAPIException;
import com.box.sdk.BoxFile;
import com.box.sdk.BoxFileVersion;
import com.box.sdk.BoxFolder;
import com.box.sdk.BoxItem;


// 2020.02.05 - Steven Rieger : Created program to export files from Box to a local drive

public class BoxFileExportThreaded extends Thread
{

	private BoxFileExport boxFileExport;
	private Path localPath;
	private BoxFolder aBoxFolder;
	
	public BoxFileExportThreaded( Path localPath, BoxFileExport aBoxFileExportOBJ, BoxFolder aBoxFolder )
	{
		super( localPath.toString() );
		this.localPath = localPath;
		this.aBoxFolder = aBoxFolder;
		this.boxFileExport = aBoxFileExportOBJ;
	}
	@Override
	public void run()
	{
		boxFileExport.LMSLogger.log( Level.INFO, "BoxFileExportThreaded run called - " + Thread.currentThread().getName() );
		try
		{
			Thread.sleep( 1000 );
			// Copy Files
			copyFiles( this.aBoxFolder, this.localPath );
		} catch ( InterruptedException e )
		{
			System.out.println( "Thread Interupted!" );
			e.printStackTrace();
		}
		boxFileExport.LMSLogger.log( Level.INFO, "BoxFileExportThreaded run end - " + Thread.currentThread().getName() );
	}


	public void copyFiles( BoxFolder aBoxFolder, Path localFolderPath )
	{
		try
		{
			boxFileExport.LMSLogger.log( Level.INFO, "Processing: " + localFolderPath.toString() );

			// Loop through the files and folders
			for (BoxItem.Info itemInfo : aBoxFolder.getChildren() )
			{
				if( itemInfo instanceof BoxFile.Info )
				{
					copyFile( itemInfo, localFolderPath );
				}
				else if( itemInfo instanceof BoxFolder.Info )
				{
					//  Type it as a BoxFolder.Info
					BoxFolder.Info folderInfo = (BoxFolder.Info) itemInfo;
					String aFolderName = folderInfo.getName();
					// This line makes the recursion work
					Path aFolderLocation = localFolderPath.resolve( Paths.get( aFolderName ) );
					if( !Files.exists( aFolderLocation ) )
					{
						boxFileExport.LMSLogger.log( Level.INFO, "Creating: " + aFolderLocation );
						aFolderLocation = Files.createDirectory( aFolderLocation );
					}
					// Recursive call with current folder
					copyFiles( folderInfo.getResource(), aFolderLocation );
				}
			}
		} catch ( final BoxAPIException | IOException e )
		{
			boxFileExport.LMSLogger.log( Level.SEVERE, "An error occurred: " + e.getMessage() );
		}
	}

	public void copyFile( BoxItem.Info fileInfo, Path localFolderPath )
	{
		try
		{
			// Download the current file
			downloadFile( fileInfo, localFolderPath );

			// If there are any versions, download the version files.
			BoxFile aBoxFile = new BoxFile( boxFileExport.getBoxService().getBoxAPIConnection(), fileInfo.getID() );
			List<BoxFileVersion> fileVersions = (List<BoxFileVersion>) aBoxFile.getVersions();
			for (BoxFileVersion aFileVersion : fileVersions)
			{
				downloadFile( aFileVersion, localFolderPath );
			}
		} catch ( Exception e )
		{
			boxFileExport.LMSLogger.log( Level.SEVERE, "An error occurred: " + e.getMessage() );
		}
	}

	public void downloadFile( BoxFileVersion aFileVersion, Path localFolderPath )
	{
		FileOutputStream stream;
		String localFilePath;

		try
		{
			// Format the modified date from this version of the file to be used in the name of the downloaded file.
			String pattern = "yyyy-MM-dd";
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat( pattern );
			String dateStr = simpleDateFormat.format( aFileVersion.getModifiedAt() );

			// Break up the file name to name and extension
			String fileName = aFileVersion.getName();
			int dot = fileName.lastIndexOf( "." );
			String fileExtension = fileName.substring( dot + 1 );
			fileName = fileName.substring( 0, dot );

			// Build the path and file name to be downloaded
			localFilePath = localFolderPath.resolve( Paths.get( fileName + " " + dateStr + "." + fileExtension ) ).toAbsolutePath().toString();

			// If local file exists
			File localPathFile = new File( localFilePath );
			if( localPathFile.exists() )
			{
				// Fetch the local existing file date
				long localFileDate = localPathFile.lastModified();
				// Fetch the BoxVersionFile modified date. Verify the versioned file has a modified date...
				long boxFileDate = ( aFileVersion.getModifiedAt() != null ) ? aFileVersion.getModifiedAt().getTime() : System.currentTimeMillis();

				// if box file date is older or equal than local file date
				if( boxFileDate <= localFileDate )
					return;
			}
			stream = new FileOutputStream( localFilePath );
			boxFileExport.LMSLogger.log( Level.INFO, "Downloading: " + localFilePath );
			// BoxFile boxFile = new BoxFile( boxService.getBoxAPIConnection(), aFileVersion.getID() );
			aFileVersion.download( stream );
			stream.close();
		} catch ( Exception e )
		{
			boxFileExport.LMSLogger.log( Level.SEVERE, "An error occurred: " + e.getMessage() );
		}
	}

	public void downloadFile( BoxItem.Info fileInfo, Path localFolderPath )
	{
		FileOutputStream stream;
		String localFilePath;
		BoxFile boxFile;

		try
		{
			// Build the path and file name to be downloaded
			localFilePath = localFolderPath.resolve( Paths.get( fileInfo.getName() ) ).toAbsolutePath().toString();
			// Fetch the BoxFile to get dates
			boxFile = new BoxFile( boxFileExport.getBoxService().getBoxAPIConnection(), fileInfo.getID() );
			// If local file exists
			File localPathFile = new File( localFilePath );
			if( localPathFile.exists() )
			{
				// Fetch the local existing file date
				long localFileDate = localPathFile.lastModified();

				// Fetch the Box file info to get dates
				BoxFile.Info boxFileInfo = boxFile.getInfo();
				// Fetch the modified date. Verify it does have one. Some do not for some reason...
				long boxFileDate = ( boxFileInfo.getModifiedAt() != null ) ? boxFileInfo.getModifiedAt().getTime() : System.currentTimeMillis();

				// if box file date is older or equal than local file date
				if( boxFileDate <= localFileDate )
					return;
			}
			stream = new FileOutputStream( localFilePath );
			boxFileExport.LMSLogger.log( Level.INFO, "Downloading: " + localFilePath );
			boxFile.download( stream );
			stream.close();
		} catch ( Exception e )
		{
			boxFileExport.LMSLogger.log( Level.SEVERE, "An error occurred: " + e.getMessage() );
		}
	}
}
