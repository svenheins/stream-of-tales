/* Copyright (C) Sven Heins - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * author: Sven Heins
 * date: 05.07.2015
 */
package de.svenheins.handlers;

import java.util.StringTokenizer;

public class IRCGetMessageHandler {
	 String line;
	   String prefix     = "",
	          command    = "",
		  params     = "",
		  middle     = "",
		  trailing   = "",
	          servername = "",
		  nick       = "",
		  user       = "",
		  host       = "";

	   public IRCGetMessageHandler(String line)
	   {
	      this.line = line;

	      initTags();
	   }

	   /**
	    * Main initializing method!
	    */
	   private void initTags()
	   {
	      StringTokenizer st = new StringTokenizer(line, " \r\n");
	      int totalTokens = st.countTokens();

	      // At first let us grep prefix
	      if (line.startsWith(":") && totalTokens >= 3)
	      {
	         String temp = st.nextToken();
					 int index = temp.indexOf(":");
					 if (index != -1)
					    prefix = temp.substring(index + 1);
					 
					 // now let's grep command
					 temp = st.nextToken();
					 command = temp;
					 
					 // now let's grep params
					 temp = st.nextToken("\n");
					 params = temp;
	      }
	      else if (!line.startsWith(":") && totalTokens >= 2)
	      {
		 String temp = st.nextToken();
					 
		 // now let's grep command
		 command = temp;
					 
	        // now let's grep params
	        params = st.nextToken("\n");
	     }
	   }

	   public String getPrefix()
	   {
	      return prefix;
	   }

	   public String getCommand()
	   {
	      return command;
	   }

	   public String getParams()
	   {
	      return params;
	   }
		 
		 public String getServer()
		 {
		   if (!prefix.equals("")) // prefix is not empty
			 {
			 	 int index = prefix.indexOf("!");
				 if (index != -1)
				 {
				 	 String temp = prefix.substring(0, index);
					 servername = temp;
				 }
			 }
			 
			 return servername;
		 }
		 
		 public String getNick()
		 {
		   if (!prefix.equals("")) // prefix is not empty
			 {
			 	 int index = prefix.indexOf("!");
				 if (index != -1)
				 {
				 	 String temp = prefix.substring(0, index);
					 nick = temp;
				 }
			 }
			 
			 return nick;
		 }
		 
		 public String getUser()
		 {
		 	 if (!prefix.equals(""))
			 {
			 	 int exMark = prefix.indexOf("!");
				 int adMark = prefix.indexOf("@");
				 if (exMark != -1 && adMark != -1 && (adMark > exMark))
				 {
				 	 user = prefix.substring(exMark + 1, adMark);
				 }
			 }
			 
			 return user;
		 }
		 
		 public String getHost()
		 {
		 	 if (!prefix.equals(""))
			 {
				 int adMark = prefix.indexOf("@");
				 if (adMark != -1 && adMark >= 0)
				 {
				 	 host = prefix.substring(adMark + 1);
				 }
			 }		 
			 
			 return host;
		 }
		 
		 public String getTrailing()
		 {
		 	 if (!params.equals(""))
			 {
		 	   int index = params.indexOf(":");
			   if (index != -1 && index >= 0)
			   {
			 	   trailing = params.substring(index + 1);
			   }
			 }
			 
			 return trailing;
		 }
		 
		 public String getMiddle()
		 {
		 	  if (!params.equals(""))
				{
		 	    int index = params.indexOf(":");
				  if (index != -1 && index >= 0)
				  {
					  if (params.startsWith(" ") && index - 1 >= 1)
					    middle = params.substring(1, index - 1);
					  else
					    middle = params.substring(0, index - 1);
				  }
				}
				
				return middle;
		 }
}
