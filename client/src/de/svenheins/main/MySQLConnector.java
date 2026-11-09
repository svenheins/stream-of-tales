/* Copyright (C) Sven Heins - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * author: Sven Heins
 * date: 05.07.2015
 */
package de.svenheins.main;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MySQLConnector {
	private String driver;
	private String hostName;
	private String userName;
	private String password;
	private String table;
	
	public MySQLConnector(String driver, String hostName, String userName, String password, String table) {
		this.setDriver(driver);
		this.setHostName(hostName);
		this.setUserName(userName);
		this.setPassword(password);
		this.setTable(table);
	}
	
	public void insert(String[] colName, String[] value) {
		Connection con;
		try {
			String driver = this.getDriver();
			Class.forName(driver);
			con = DriverManager.getConnection(this.getHostName(), this.getUserName(), this.getPassword());
			String colNameText = "";
			for (int i = 0; i < colName.length; i++) {
				if (i < colName.length-1) {
					// not the last one
					colNameText = colNameText + colName[i]+", ";
				} else {
					// last element
					 colNameText = colNameText + colName[i];
				}
			}
			String valueText = "";
			for (int i = 0; i < value.length; i++) {
				if (i < value.length-1) {
					// not the last one
					valueText = valueText + "'"+value[i]+"', ";
				} else {
					// last element
					valueText = valueText + "'"+value[i]+"'";
				}
			}
			PreparedStatement stmt = con.prepareStatement(
					  "INSERT INTO "+this.getTable()+" ("+colNameText+") VALUES ("+valueText+")");

			boolean results = stmt.execute();
			con.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	public void delete(String colName, String value) {
		Connection con;
		try {
			String driver = this.getDriver();
			Class.forName(driver);
			con = DriverManager.getConnection(this.getHostName(), this.getUserName(), this.getPassword());
			PreparedStatement stmt = con.prepareStatement(
					  "DELETE FROM "+this.getTable()+" WHERE "+colName+"='"+value+"'");

			boolean results = stmt.execute();
			con.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public boolean contains(String colName, String value) {
		boolean retValue = false;
		Connection con;
		try {
			String driver = this.getDriver();
			Class.forName(driver);
			con = DriverManager.getConnection(this.getHostName(), this.getUserName(), this.getPassword());
			
			PreparedStatement stmt = con.prepareStatement(
					  "SELECT * FROM "+this.getTable()+" WHERE "+colName+"='"+value+"' LIMIT 1");

			ResultSet results = stmt.executeQuery();
//			try {
//				results.last();
////			    size = results.getRow();
////			    results.beforeFirst();
//				retValue = true;
//			}
//			catch(Exception ex) {
//			    retValue = false;
//			}
			
			if(!results.next()) retValue = false;
			else retValue = true;

			con.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return retValue;
	}
	
	public void update(String idColName, String idColValue, String[] colName, String[] value) {
		Connection con;
		try {
			String driver = this.getDriver();
			Class.forName(driver);
			con = DriverManager.getConnection(this.getHostName(), this.getUserName(), this.getPassword());
			String setNameText = "";
			for (int i = 0; i < colName.length; i++) {
				if (i < colName.length-1) {
					// not the last one
					setNameText = setNameText + colName[i]+"="+value[i]+", ";
				} else {
					// last element
					setNameText = setNameText + colName[i]+"="+value[i];
				}
			}
			
			PreparedStatement stmt = con.prepareStatement(
					  "UPDATE "+this.getTable()+" SET "+setNameText+" WHERE "+idColName+"='"+idColValue+"';");

			boolean results = stmt.execute();
			con.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public String[] getData(String idColName, String idColValue, String[] colNames) {
		String[] retValues = new String[colNames.length];
		Connection con;
		try {
			String driver = this.getDriver();
			Class.forName(driver);
			con = DriverManager.getConnection(this.getHostName(), this.getUserName(), this.getPassword());
			PreparedStatement stmt = con.prepareStatement(
					  "SELECT * FROM "+this.getTable()+" WHERE "+idColName+"='"+idColValue+"';");
			
			ResultSet rs = stmt.executeQuery();
		    try {
		        while ( rs.next() ) {
		            int numColumns = rs.getMetaData().getColumnCount();
		            for ( int i = 1 ; i <= numColumns ; i++ ) {
		               // Column numbers start at 1.
		               // Also there are many methods on the result set to return
		               //  the column as a particular type. Refer to the Sun documentation
		               //  for the list of valid conversions.
//		               System.out.println( "COLUMN " + i + " "+rs.getMetaData().getColumnName(i)+" = " + rs.getObject(i) );
		               for (int j = 0; j < colNames.length; j++) {
		            	   if (rs.getMetaData().getColumnName(i).equals(colNames[j])) {
		            		   retValues[j] = ""+ rs.getObject(i);
		            		   break;
		            	   }
		               }
		            }
		        }
		    } finally {
		        try { rs.close(); } catch (Throwable ignore) { /* Propagate the original exception
		instead of this one that you may want just logged */ }
		    }
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return retValues;
	}
	
	
	public String getDriver() {
		return driver;
	}

	public void setDriver(String driver) {
		this.driver = driver;
	}

	public String getHostName() {
		return hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getTable() {
		return table;
	}

	public void setTable(String table) {
		this.table = table;
	}
	
	
	
}
