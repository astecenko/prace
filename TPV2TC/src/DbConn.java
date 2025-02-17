
import tc4tpv.CSVUtils;
import tc4tpv.CSVRead;
import tc4tpv.LogFile;
import tc4tpv.ReadProperty;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.sql.Connection;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

//******************************************************************************//
//		popis argumentu															//
//		arg 0 == typ akce (EXPORT);TC2Helios									//
//		arg 1 == configuracni soubor 											//
//		arg 2 == typ exportu (NAKUP)											//
//		arg 3 == custom option 													//
//      arg 4 == dimenze_update;ineko;pro importHCV zde importi soubor			//
//      arg 5 == typ sql server - pokud neni vyplnen MSSQL 						//
//		exampl : EXPORT ReadVyroba.csv VYROBA klic_postaveni='S'				//
//******************************************************************************//

public class DbConn {

	static boolean cutom_select=true;
	static boolean update_HCV=false;
    static String where_custom;
    static boolean ineko_zalozit=false;
    static boolean ineko_HCV=false;
    
	static String basePath = new File("").getAbsolutePath();
	public static void main(String[] args)
		    throws Exception
		  {
		    String pole = "";
		    boolean dimenze_update=false;
		    String timeStamp = new SimpleDateFormat("yyyy-MM-dd_HHmmss").format(new Timestamp(System.currentTimeMillis()));
		    
		    if (args.length<=4 ||args[4].isEmpty())
		    {
		    	dimenze_update=false;
		    	LogFile.makelog(timeStamp, "arg[4]=isEmpty");
		    }
		    else if (args[4].equals("dimenze_update"))
		    {
		    	dimenze_update=true;
		    	LogFile.makelog(timeStamp, "arg[4]="+args[4]);
		    }
		    else if(args[4].equals("ineko"))
		    {
		    	ineko_zalozit=true;
		    	LogFile.makelog(timeStamp, "arg[4]="+args[4]);
		    }
		    else if(args[4].equals("ineko_HCV"))
		    {
		    	ineko_HCV=true;
		    	LogFile.makelog(timeStamp, "arg[4]="+args[4]);
		    }
		    else
		    	dimenze_update=false;
		    
		   String timeStamp_read = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Timestamp(System.currentTimeMillis()));
		  
		   if(ineko_HCV || ineko_zalozit)
			   timeStamp_read = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(new Timestamp(System.currentTimeMillis()));//pro InEko
		    
		  
		    LogFile.makelog(timeStamp_read, "Start Connection");
		    String timelast = ReadProperty.LastDateMod(timeStamp_read,args[2]);
		   
		    timeStamp = "import" + timeStamp;
		   //pokud chceme cist z vyce tabulek pro ka�dem spu�t�n� zvolime jin� vstupn� soubor
		    LogFile.makelog(timeStamp, "arg[1]="+args[1]);
		    if (args[1].isEmpty())
		    	ReadProperty.GetProperty("java_ReadProperty.csv");
		    else 
		    	ReadProperty.GetProperty(args[1]);
		    
		    LogFile.makelog(timeStamp, "number="+ReadProperty.num_attr);
		    
	
		    	
		    if (args.length<=3||args[3].isEmpty() )
		    	cutom_select=false;
		    else if ( args[3].equals("0"))
		    {
		    	cutom_select=false;
		    	LogFile.makelog(timeStamp, "arg[3]="+args[3]);
		    }else if(args[3].equals("HCV_update"))
		    {
		    	update_HCV=true;
		    }
		    else 
		    {	where_custom=args[3];
		    	LogFile.makelog(timeStamp, "arg[3]="+args[3]);
		    }
		    
		    
		    LogFile.makelog(timeStamp, "cutom_select="+cutom_select);
		    
		    try
		    {
		    	String connectionUrl;
		    	int delka_argumentu =args.length;
		    	if (delka_argumentu < 6)
		    	{
		    		 Class.forName("net.sourceforge.jtds.jdbc.Driver");
				      connectionUrl = "jdbc:jtds:sqlserver://" + ReadProperty.attr[0] + "/" + ReadProperty.attr[1];
		    	}
		    	else if (args[5].isEmpty() )
		    	{
		    		 Class.forName("net.sourceforge.jtds.jdbc.Driver");
				      connectionUrl = "jdbc:jtds:sqlserver://" + ReadProperty.attr[0] + "/" + ReadProperty.attr[1];
		    	}
		    	else if(args[5].equals("MAPOS2TC") )
		    	 {
		    		 Class.forName("com.mysql.jdbc.Driver");  // initialise the driver
		    		 connectionUrl ="jdbc:mysql://" + ReadProperty.attr[0] + "/" + ReadProperty.attr[1];
		    		 
		    	 }else
		    	 {
		    		 Class.forName("net.sourceforge.jtds.jdbc.Driver");
				      connectionUrl = "jdbc:jtds:sqlserver://" + ReadProperty.attr[0] + "/" + ReadProperty.attr[1];
		    	 }
		      
		      DriverManager.setLoginTimeout(10);
		      Connection connection=null;
		     // Import2TC(connection, pole, timeStamp, timelast,false);
		      
		     ////////----ZAKOMENTOVAT PRO OTESTOVANI----////// 
		  connection = DriverManager.getConnection(connectionUrl, ReadProperty.attr[2], ReadProperty.attr[3]);
		      System.out.println("Connect");
		      LogFile.makelog(timeStamp, "Connection Complete");
		      if (args.length == 0 || args[0].equals("EXPORT") )
		      {
		    	  LogFile.makelog(timeStamp, "EXPORT Arg 0 "+ pole);
		    	  if( args[2].equals("NAKUP"))
		    	  {
		    		  LogFile.makelog(timeStamp, "EXPORT NAKUP");
		    		  Import2TC(connection, pole, timeStamp, timelast,true,dimenze_update);
		    	  }
		    	  else
		    	  {
		    		  LogFile.makelog(timeStamp, "EXPORT "+args[2]);
		    		  Import2TC(connection, pole, timeStamp, timelast,false,dimenze_update);
		    		  
		    	  }
		    	  if(ineko_HCV)
		    		  CallImportTC_HCV(pole, timeStamp,args[2]);
		    	  else
		    		  CallImportTC(pole, timeStamp,args[2]);
		        
		      }
		      else if(args[0].equals("TC2Helios"))
		      {
		    	  LogFile.makelog(timeStamp, "IMPORT 2 Heliso");
		    	  if(update_HCV)  
		        CallImportTPV(connection, "UPDATE [iNuvio001].[dbo].[HcvInEkoHeliosToTeamCenter] SET ", args[4], timelast);
		    	  else
		    		  CallImportTPV(connection, "[iNuvio001].[dbo].[HcvInEkoTeamCenterToHelios]", args[4], timelast);
		        
		      }
		      else if(args[0].equals("TC2ERP"))
		      {
		    	  LogFile.makelog(timeStamp, "IMPORT 2 ERP");
		    	  if(update_HCV)  
		        CallImportTPV(connection, "UPDATE "+ ReadProperty.attr[4]+" SET ", args[4], timelast);
		    	  else
		    		  CallImportTPV(connection,  ReadProperty.attr[4], args[4], timelast);
		        
		      }
		    	  
		      else
		      {
		    	  LogFile.makelog(timeStamp, "IMPORT 2 TPV");
		        CallImportTPV(connection, "tpv_c2t_import", args[0], timelast);
		        
		      }
		      	
		      
		    }
		    catch (Exception e)
		    {
		      System.err.println("Got an exception! ");
		      System.err.println(e.getMessage());
		      System.out.print(e);
		      LogFile.makelog(timeStamp, " Error:"+e.getMessage());
		    }
		  }
		  
		  private static void CallImportTPV(Connection conn, String table, String filecsv, String timeLast)
		    throws SQLException
		  {
			  if(update_HCV)
				  CSVRead.ReadCSV(table, filecsv, conn, true);
			  else 
				  CSVRead.ReadCSV(table, filecsv, conn, cutom_select);
		    LogFile.makelog("ted spustit read CSV", "");
		  }
		  
		  private static void Import2TC(Connection conn, String pole, String timeStamp, String timeLast,boolean nakupPol, boolean no_dimenze_update)
		    throws SQLException, FileNotFoundException
		  {
			  String last_update = new SimpleDateFormat("yyyy-MM-dd").format(new Date())+" 20:00:00";
			 
			  String where_time="";
			  if(no_dimenze_update)//OrderDate BETWEEN '1996-07-01' AND '1996-07-31'
				  where_time=ReadProperty.attr[6]+" BETWEEN "+"'"+timeLast+"'"+" AND "+"'"+last_update+"'";
			  else if(ineko_zalozit)
			  {
				  String[] splited = timeLast.split(" ");
				  where_time=ReadProperty.attr[6]+"=" + "'" +splited[0]  +"' and CasZmeny>'"+splited[1]+"'"; 
			  }  
			  else
				  where_time=ReadProperty.attr[6]+">" + "'" + timeLast + "'";
				  
			  PreparedStatement ps;
			  if (cutom_select) 
				{
					LogFile.makelog(timeStamp, "connection : "+ "select * from " + ReadProperty.attr[4]+" WHERE  "+where_custom +"and  "+ where_time);
					ps= conn.prepareStatement("select * from " + ReadProperty.attr[4]+" WHERE  "+where_custom +"and  "+ where_time);
				}			
				else if ( ReadProperty.attr[4].equals("dbo.vtp_tpvg_tc_zmeny"))
				{
					LogFile.makelog(timeStamp, "connection : "+ "select * from " + ReadProperty.attr[4]+" where changetype='KT' or changetype='K';");
					ps= conn.prepareStatement("select * from " + ReadProperty.attr[4]+" where changetype='KT' or changetype='K';");
				}
				else if ( ReadProperty.attr[4].equals("dbo.vtpg_TC_hutni"))
				{
					LogFile.makelog(timeStamp, "connection : "+ "select * from " + ReadProperty.attr[4]+" WHERE "+where_time+";");
					ps= conn.prepareStatement("select * from " + ReadProperty.attr[4]+" WHERE "+where_time+";");
				}
				else if ( ReadProperty.attr[4].equals("dbo.vtp_tpvg_tc_nakupovane_revize"))
				{
					LogFile.makelog(timeStamp, "connection : "+ "select * from " + ReadProperty.attr[4]+" WHERE  index_vykresu!='NULL' and index_vykresu != '00' and index_vykresu != '';");
					ps= conn.prepareStatement("select* from " + ReadProperty.attr[4]+" WHERE index_vykresu!='NULL' and index_vykresu != '00' and index_vykresu != '';");
				}
				else if (ineko_HCV)
				{
					LogFile.makelog(timeStamp, "connection : "+ "select * from " + ReadProperty.attr[4] +" WHERE Status=0");
					ps= conn.prepareStatement("select* from " + ReadProperty.attr[4]+" WHERE Status=0");
				}
				else if (nakupPol)
				{ 
					// upravo u Inge kv�li nastavov�n� atributu pro posledn� datum zmeny
					//LogFile.makelog(timeStamp, "connection : "+ "select * from " + ReadProperty.attr[4] + " WHERE lastamenddate>=" + "'" + timeLast + "'" +"or  lastamenddate IS NULL");
				    // ps= conn.prepareStatement("select * from " + ReadProperty.attr[4] + " WHERE lastamenddate>=" + "'" + timeLast + "'"+"or  lastamenddate IS NULL");
					LogFile.makelog(timeStamp, "connection : "+ "select * from " + ReadProperty.attr[4] + " WHERE "+ where_time +"or  "+ReadProperty.attr[6]+" IS NULL"); 
					ps= conn.prepareStatement("select * from " + ReadProperty.attr[4] + " WHERE "+where_time+"or  "+ReadProperty.attr[6]+" IS NULL");
				}
				
				else
				{ 
					LogFile.makelog(timeStamp, "connection : "+ "select * from " + ReadProperty.attr[4]+" WHERE  "+where_time);
					ps= conn.prepareStatement("select * from " + ReadProperty.attr[4]+" WHERE  "+where_time);
					
				}
				//System.out.println("select * from " + ReadProperty.attr[4] + " WHERE lastamenddate>" + "'" + timeLast + "'");
				    ResultSet rs = ps.executeQuery();
				    System.out.println(rs.getRow());
				    while (rs.next())
				    {
				     // int klic = rs.getInt(ReadProperty.attr[5]);
				    	 String klic = rs.getString(ReadProperty.attr[5]);
				      pole = pole + klic + "#";
				      // upravo u Inge kv�li nastavov�n� atributu pro posledn� datum zmeny
				     // for (int i = 6; i < ReadProperty.num_attr; i++)
				      for (int i = 7; i < ReadProperty.num_attr; i++)
				      {
				    	  
				        String temp = rs.getString(ReadProperty.attr[i]);
				        
				        
				        if (temp == null) {
				          pole = pole + " #";
				        } else if (temp.length() > 0) {
				        	temp=temp.replace("\n", "").replace("\r", " ");;
				          pole = pole + temp + "#";
				        } else {
				          pole = pole + " #";
				        }
				      }
				      Send2CSV(pole, timeStamp);
				      pole = "";
				    }
				    System.out.println(pole);
				    
				    pole = pole + "/0";
				    System.out.println("Connection Succesfull");
				    LogFile.makelog(timeStamp, " CreteaCSV_File: Complete");
			 }
		/*  private static void Fantom2TC(Connection conn, String pole, String timeStamp, String timeLast)
				    throws SQLException, FileNotFoundException
				  {
				    PreparedStatement ps = conn.prepareStatement("select * from " + ReadProperty.attr[4] + " WHERE lastamenddate>=" + "'" + timeLast + "'");
				    
				    System.out.println("select * from " + ReadProperty.attr[4] + " WHERE lastamenddate>" + "'" + timeLast + "'");
				    ResultSet rs = ps.executeQuery();
				    System.out.println(rs.getRow());
				    while (rs.next())
				    {
				      int klic = rs.getInt(ReadProperty.attr[5]);
				      pole = pole + klic + "#";
				      for (int i = 6; i < ReadProperty.num_attr; i++)
				      {
				        String temp = rs.getString(ReadProperty.attr[i]);
				        if (temp == null) {
				          pole = pole + " #";
				        } else if (temp.length() > 0) {
				          pole = pole + temp + "#";
				        } else {
				          pole = pole + " #";
				        }
				      }
				      Send2CSV(pole, timeStamp);
				      pole = "";
				    }
				    System.out.println(pole);
				    
				    pole = pole + "/0";
				    System.out.println("Connection Succesfull");
				    LogFile.makelog(timeStamp, " CreteaCSV_File: Complete");
				  }*/
		  
		  private static void Send2CSV(String pole, String csv)
		    throws FileNotFoundException
		  {
		    String csvFile = "C:\\SPLM\\Apps\\Import\\cvs\\" + csv + ".csv";
		    OutputStream os = new FileOutputStream(csvFile, true);
		    try
		    {
		      PrintWriter writer = new PrintWriter(new OutputStreamWriter(os, "UTF-8"));
		      
		      CSVUtils.writeLine(writer, Arrays.asList(new String[] { pole }));
		      
		      CreateCSV(csvFile);
		      writer.flush();
		      writer.close();
		    }
		    catch (IOException e)
		    {
		      CreateCSV(csvFile);
		      try
		      {
		    	  PrintWriter writer = new PrintWriter(new OutputStreamWriter(os, "UTF-8"));
		      }
		      catch (IOException e1)
		      {
		        PrintWriter writer;
		        e1.printStackTrace();
		      }
		    }
		  }
		  
		  private static void CreateCSV(String csvFile)
		  {
		    File f = new File(csvFile);
		    
		    f.getParentFile().mkdirs();
		    try
		    {
		      f.createNewFile();
		    }
		    catch (IOException e1)
		    {
		      e1.printStackTrace();
		    }
		  }
		  
		  private static void CallImportTC(String pole, String name, String typExport)
		    throws IOException
		  {
		    try
		    {
		      Process child = 
		        Runtime.getRuntime()
		        .exec("cmd /c start \"\" C:\\SPLM\\Apps\\Import\\ImportTC.bat " + name + " " + typExport+".xml");
		   
		      
		      OutputStream out = child.getOutputStream();
		      
		      out.write("cd C:/ /r/n".getBytes());
		      out.flush();
		      out.write("dir /r/n".getBytes());
		      out.close();
		    }
		    catch (IOException localIOException) {}
		  }
		  private static void CallImportTC_HCV(String pole, String name, String typExport)
				    throws IOException
				  {
				    try
				    {
				      Process child = 
				        Runtime.getRuntime()
				        .exec("cmd /c start \"\" C:\\SPLM\\Apps\\Import\\ImportTC_HCV.bat " + name + " " + typExport+".xml");
				   
				      
				      OutputStream out = child.getOutputStream();
				      
				      out.write("cd C:/ /r/n".getBytes());
				      out.flush();
				      out.write("dir /r/n".getBytes());
				      out.close();
				    }
				    catch (IOException localIOException) {}
				  }
}
