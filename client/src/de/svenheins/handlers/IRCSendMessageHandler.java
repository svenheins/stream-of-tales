/* Copyright (C) Sven Heins - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * author: Sven Heins
 * date: 05.07.2015
 */
package de.svenheins.handlers;

import java.util.StringTokenizer;

public class IRCSendMessageHandler {
	 String lineToServer;
	   String command = "",
	          param1 = "",
		  message = "";

	   public IRCSendMessageHandler(String lineToServer)
	   {
	      this.lineToServer = lineToServer;

	      init_all();
	   }

	   private void init_all()
	   {
	      if (lineToServer.startsWith("/"))
	      {
	         StringTokenizer st = new StringTokenizer(lineToServer, " \r\n");
		 int totalTokens = st.countTokens();
		 if (totalTokens >= 3)
		 {
		    String temp = st.nextToken();

	            // command
		    temp = temp.toUpperCase();
		    temp = temp.substring(1);
		    command = temp;

	            // param1
	            temp = st.nextToken();
		    param1 = temp;

	            // message
		    temp = st.nextToken("\n");
		    if (temp.startsWith(" "))
		       temp = temp.substring(1);

		    message = temp;
		 }
		 else if (totalTokens == 2)
		 {
		    String temp = st.nextToken();

	            // command
		    temp = temp.toUpperCase();
		    temp = temp.substring(1);
		    command = temp;

	            // param1
		    temp = st.nextToken();
		    param1 = temp;
		 }
		 else
		 {
		    // not a valid command
		 }
	      }
	      else
	      {
	         // not a command
	      }
	   }

	   public String getCommand()
	   {
	      return command;
	   }

	   public String getParam1()
	   {
	      return param1;
	   }

	   public String getMessage()
	   {
	      return message;
	   }
}
