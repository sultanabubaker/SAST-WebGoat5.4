
package org.owasp.webgoat.session;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


/***************************************************************************************************
 * 
 * 
 * This file is part of WebGoat, an Open Web Application Security Project utility. For details,
 * please see http://www.owasp.org/
 * 
 * Copyright (c) 2002 - 2007 Bruce Mayhew
 * 
 * This program is free software; you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program; if
 * not, write to the Free Software Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 * 
 * Getting Source ==============
 * 
 * Source for this application is maintained at code.google.com, a repository for free software
 * projects.
 * 
 * For details, please see http://code.google.com/p/webgoat/
 * 
 * @author Bruce Mayhew <a href="http://code.google.com/p/webgoat">WebGoat</a>
 * @created October 29, 2003
 */

public class UserTracker
{

	private static UserTracker instance;

	// FIXME: persist this somehow!

	private static HashMap<String, HashMap<String, LessonTracker>> storage = new HashMap<String, HashMap<String, LessonTracker>>();
	

	/**
	 * Constructor for the UserTracker object
	 */
	private UserTracker()
	{
	}

	/**
	 * Gets the completed attribute of the UserTracker object
	 * 
	 * @param userName
	 *            Description of the Parameter
	 * @return The completed value
	 */
	public int getCompleted(String userName)
	{

		HashMap usermap = getUserMap(userName);

		Iterator i = usermap.entrySet().iterator();

		int count = 0;

		while (i.hasNext())
		{

			Map.Entry entry = (Map.Entry) i.next();

			int value = ((Integer) entry.getValue()).intValue();

			if (value > 5)
			{
				count++;
			}

		}

		return count;
	}

	/**
	 * Gets the users attribute of the UserTracker object
	 * 
	 * @return The users value
	 */
	public Collection getUsers()
	{
		return storage.keySet();
	}

	public Collection<String> getAllUsers(String roleName)
	{
        //Removed call to catalina MemoryUserDatabase which prevents application server portability.
        return new ArrayList<String>();
	}

	public void deleteUser(String user)
	{
        //Removed call to catalina MemoryUserDatabase which prevents application server portability.
	}

	/**
	 * Gets the lessonTracker attribute of the UserTracker object
	 * 
	 * @param screen
	 *            Description of the Parameter
	 * @param userName
	 *            Description of the Parameter
	 * @return The lessonTracker value
	 */
	public LessonTracker getLessonTracker(WebSession s, Screen screen)
	{
		return getLessonTracker(s, s.getUserName(), screen);
	}

	public LessonTracker getLessonTracker(WebSession s, String user, Screen screen)
	{
		HashMap<String, LessonTracker> usermap = getUserMap(user);
		LessonTracker tracker = (LessonTracker) usermap.get(screen.getTitle());
		if (tracker == null)
		{
			// Creates a new lesson tracker, if one does not exist on disk.
			tracker = LessonTracker.load(s, user, screen);
			usermap.put(screen.getTitle(), tracker);
		}
		// System.out.println( "User: [" + userName + "] UserTracker:getLessonTracker() LTH " +
		// tracker.hashCode() + " for " + screen );
		return tracker;
	}

	/**
	 * Gets the status attribute of the UserTracker object
	 * 
	 * @param screen
	 *            Description of the Parameter
	 * @param userName
	 *            Description of the Parameter
	 * @return The status value
	 */
	public String getStatus(WebSession s, Screen screen)
	{
		return ("User [" + s.getUserName() + "] has accessed " + screen + " UserTracker:getStatus()LTH = " + getLessonTracker(
																																s,
																																screen)
				.hashCode());
	}

	/**
	 * Gets the userMap attribute of the UserTracker object
	 * 
	 * @param userName
	 *            Description of the Parameter
	 * @return The userMap value
	 */
	private HashMap<String, LessonTracker> getUserMap(String userName)
	{

		HashMap<String, LessonTracker> usermap = storage.get(userName);

		if (usermap == null)
		{

			usermap = new HashMap<String, LessonTracker>();

			storage.put(userName, usermap);

		}

		return (usermap);
	}

	/**
	 * Description of the Method
	 * 
	 * @return Description of the Return Value
	 */
	public static synchronized UserTracker instance()
	{

		if (instance == null)
		{

			instance = new UserTracker();

		}

		return instance;
	}

	/**
	 * Description of the Method
	 * 
	 * @param screen
	 *            Description of the Parameter
	 * @param s
	 *            Description of the Parameter
	 */
	public void update(WebSession s, Screen screen)
	{

		LessonTracker tracker = getLessonTracker(s, screen);

		// System.out.println( "User [" + s.getUserName() + "] TRACKER: updating " + screen +
		// " LTH " + tracker.hashCode() );
		tracker.store(s, screen);

		HashMap<String, LessonTracker> usermap = getUserMap(s.getUserName());
		usermap.put(screen.getTitle(), tracker);

	}

}
