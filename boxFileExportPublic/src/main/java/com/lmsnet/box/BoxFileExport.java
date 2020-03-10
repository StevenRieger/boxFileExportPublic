package com.lmsnet.box;


import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import com.box.sdk.BoxConfig;
import com.box.sdk.BoxFolder;
import com.lmsnet.box.properties.ApplicationProperties;
import com.lmsnet.box.service.BoxService;
import com.lmsnet.box.utils.BoxUtils;

// 2020.02.05 - Steven Rieger : Created program to export files from Box to a local drive

public class BoxFileExport
{

	//  Application Specific configurations like source folder in box and destination folder on your network
	private final String	APPLICATION_PROPERTIES_FILE	= "application.properties";
	//  Box security config from your application config in Box API dev page
	private final String	BOX_CONFIG_FILE				= "box_config.json";
	//  A log file to record good, bad and ugly
	public final Logger		LMSLogger					= Logger.getLogger( "BoxFileExport" );
	private FileHandler	fileHandler	= null;
	
	private BoxService	boxService;

	public static void main( final String[] args )
	{
		// Create a new object
		BoxFileExport boxFileExport = new BoxFileExport();
		try
		{
			// Setup Logger
			boxFileExport.fileHandler = new FileHandler( "BoxFileExport-Logger.log", true );
			boxFileExport.LMSLogger.addHandler( boxFileExport.fileHandler );
			boxFileExport.LMSLogger.setLevel( Level.ALL );
			boxFileExport.fileHandler.setLevel( Level.ALL );
			boxFileExport.fileHandler.setFormatter( new SimpleFormatter() );

			boxFileExport.LMSLogger.log( Level.CONFIG, "Logger Instantiated!" );
			boxFileExport.LMSLogger.log( Level.INFO, "Begin BoxAPIFileExport Main!" );

			// Fetch all the configurations for this application
			final ApplicationProperties applicationProperties = new ApplicationProperties( boxFileExport.APPLICATION_PROPERTIES_FILE );

			// Get logged in as the admin user defined in the application
			boxFileExport.boxService = new BoxService( applicationProperties.getUserId(), BoxConfig.readFrom( BoxUtils.getFileReader( boxFileExport.BOX_CONFIG_FILE ) ) );

			// Fetch the root folder from config file
			final BoxFolder rootFolder = boxFileExport.boxService.getFolderByName( applicationProperties.getRootFolderName() );

			ExecutorService executor = Executors.newFixedThreadPool( applicationProperties.getNumberOfThreads() );
			// Loop through all the folders from the root folder
			for (final BoxFolder aBoxFolder : boxFileExport.boxService.getSubFolders( rootFolder ))
			{
				
				// Set the destination path to the folder being processed
				Path destinationDirectory = Paths.get( applicationProperties.getDestinationFolderName() ).toAbsolutePath();

				// Get the folder name and see if it exists. If not, create it
				// These should be Root level folders
				// Root folders should be property number only...
				String aFolderName = aBoxFolder.getInfo().getName();

				Path localFolderPath = destinationDirectory.resolve( Paths.get( aFolderName ) );
				if( !Files.exists( localFolderPath ) )
				{
					localFolderPath = Files.createDirectory( localFolderPath );
				}

				Thread copyThread = new BoxFileExportThreaded( localFolderPath, boxFileExport, aBoxFolder );
				executor.execute( copyThread );
				Thread.sleep( 5000 );
			}
			executor.shutdown();
			while( !executor.isTerminated() )
			{
				Thread.sleep( 1000 );
			}
			boxFileExport.LMSLogger.log( Level.INFO, "Program Complete!" );
		} catch ( final Exception e )
		{
			boxFileExport.LMSLogger.log( Level.SEVERE, "An error occurred: " + e.getMessage() );
		}
	}

	public BoxService getBoxService()
	{
		return this.boxService;
	}

	public void setBoxService( BoxService boxService )
	{
		this.boxService = boxService;
	}
}
