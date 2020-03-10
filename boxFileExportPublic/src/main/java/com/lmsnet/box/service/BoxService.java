package com.lmsnet.box.service;

import com.box.sdk.BoxAPIConnection;
import com.box.sdk.BoxAPIException;
import com.box.sdk.BoxCollaboration;
import com.box.sdk.BoxConfig;
import com.box.sdk.BoxDeveloperEditionAPIConnection;
import com.box.sdk.BoxFolder;
import com.box.sdk.BoxGroup;
import com.box.sdk.BoxItem;
import com.box.sdk.BoxUser;
import com.lmsnet.box.exception.BoxException;

import java.util.ArrayList;
import java.util.List;

public class BoxService
{
	private BoxAPIConnection boxAPIConnection;

	public BoxService(final String accessToken) throws BoxException
	{
		try
		{
			this.boxAPIConnection = new BoxAPIConnection(accessToken);
		}
		catch (final BoxAPIException e)
		{
			throw new BoxException("Unable to authenticate using the developer access token: " + e.getMessage(), e);
		}
	}

	public BoxService(final String userId, final BoxConfig boxConfig) throws BoxException
	{
		try
		{
			this.boxAPIConnection = BoxDeveloperEditionAPIConnection.getAppUserConnection(userId, boxConfig);
		}
		catch (final BoxAPIException e)
		{
			throw new BoxException("Unable to authenticate using the box config file: " + e.getMessage(), e);
		}
	}

	public BoxFolder getRootFolder()
	{
		return BoxFolder.getRootFolder(boxAPIConnection);
	}

	public BoxFolder getFolderByName(final String name) throws BoxException
	{
		return getFolderByName(getRootFolder(), name);
	}

	public BoxFolder getFolderByName(final BoxFolder parentFolder, final String name) throws BoxException
	{
		for (final BoxItem.Info itemInfo : parentFolder)
		{
			if (itemInfo instanceof BoxFolder.Info && itemInfo.getName().equalsIgnoreCase(name))
			{
				return (BoxFolder) itemInfo.getResource();
			}
		}

		throw new BoxException(String.format("Unable to find folder in [%s] with name [%s]", parentFolder.getInfo().getName(), name));
	}

	public BoxFolder getFolderByNameEndsWith(final BoxFolder folder, final String name) throws BoxException
	{
		for (final BoxItem.Info itemInfo : folder)
		{
			if (itemInfo instanceof BoxFolder.Info && itemInfo.getName().toLowerCase().endsWith(name.toLowerCase()))
			{
				return (BoxFolder) itemInfo.getResource();
			}
		}

		throw new BoxException(String.format("Unable to find folder in [%s] with name ending in [%s]", folder.getInfo().getName(), name));
	}

	public List<BoxFolder> getSubFolders(final BoxFolder folder)
	{
		final List<BoxFolder> subFolders = new ArrayList<>();

		for (final BoxItem.Info itemInfo : folder)
		{
			if (itemInfo instanceof BoxFolder.Info)
			{
				subFolders.add((BoxFolder) itemInfo.getResource());
			}
		}

		return subFolders;
	}

	public BoxCollaboration.Info getFolderEditor(final BoxFolder folder) throws BoxException
	{
		for (final BoxCollaboration.Info collaboration : folder.getCollaborations())
		{
			if (collaboration.getRole().equals(BoxCollaboration.Role.EDITOR))
			{
				return collaboration;
			}
		}

		throw new BoxException(String.format("Unable to find editor for folder [%s]", folder.getInfo().getName()));
	}

	public BoxUser getBoxUser(final String userId) throws BoxException
	{
		try
		{
		    return new BoxUser(this.boxAPIConnection, userId );
		}
		catch (final BoxAPIException e)
		{
		    throw new BoxException(String.format("Unable to find user with id [%s]", userId), e);
		}
	}
	

	public BoxUser getBoxUserByName(final String userName ) throws BoxException
	{
	    BoxUser aBoxUser = null;
	    try
	    {
		Iterable<BoxUser.Info> users = BoxUser.getAllEnterpriseUsers( this.boxAPIConnection, userName );
		for( BoxUser.Info aUser : users )
		    aBoxUser = new BoxUser(this.boxAPIConnection, aUser.getID() );
		
		return aBoxUser;
	    }
	    catch (final BoxAPIException e)
	    {
		throw new BoxException(String.format("Unable to find user with id [%s]", userName ), e);
	    }
	}
	
	public BoxGroup getBoxGroupByName(final String groupName) throws BoxException
	{
		for (final BoxGroup.Info groupInfo : BoxGroup.getAllGroups(this.boxAPIConnection))
		{
			if (groupInfo.getName().equalsIgnoreCase(groupName))
			{
				return groupInfo.getResource();
			}
		}

		throw new BoxException(String.format("Unable to find group with name [%s]", groupName));
	}

	public BoxCollaboration createBoxCollaboration( final String folderID ) throws BoxException
	{
		try
		{
		    return new BoxCollaboration( this.boxAPIConnection, folderID );
		}
		catch (final BoxAPIException e)
		{
		    throw new BoxException(String.format( "Unable to create collaboration for folderID [%s]", folderID ), e);
		}
	}
	
	public BoxAPIConnection getBoxAPIConnection()
	{
		return this.boxAPIConnection;
	}


}
