package tc4tpv;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.io.FileInputStream;

import sqlImport.ImportSql;
import com.tpv.connect.LogFile;;

public class CSVRead {


	public static String ReadCSV(String table, String arg, Connection conn, boolean GetArtikl) throws SQLException {

		String csvFile;
		if(GetArtikl)
			csvFile = "" + arg;
		else
			csvFile = "" + arg;
						//C:\SPLM\Apps\Export\Artikl_csv
		BufferedReader br = null;
		String line = "";
		boolean header = true;
		boolean podruhe = false;
		String cvsSplitBy = "#";
		String[] attr = new String[50];
		String value = null;
		
		String table_txt = table + " (";
		if(GetArtikl)
			table_txt =table;
		
		LogFile.expoertlog("Import Helios 39\n","OK");
		
		// String klic_imp_hlav=ImportSql.Import2TPV_hlav(conn, value);
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date(System.currentTimeMillis());
		String TC_TS=formatter.format(date).toString();

		LogFile.expoertlog("Import Helios 46\n","ok");
		try {

			br = new BufferedReader( new InputStreamReader(new FileInputStream(csvFile),"UTF8"));
			int radku=0;
			int typ=99;
			value = ("");
			while ((line = br.readLine()) != null) {
				attr = line.split(cvsSplitBy);
				
				//LogFile.expoertlog("Import Helios 54\n",line);
				System.out.println(""+attr.length);
				
				if (header) {
					
					for (int i = 0; i < attr.length-1; i++)// -2 proto�e posledn� znak TYP do exportu nechci
					{
						
						table_txt += (attr[i] + ", ");
					}	
					if (GetArtikl)
						System.out.println(""+attr[attr.length-1]);//table_txt +=(attr[attr.length-2] + ")");
					 
					else
						table_txt += (attr[attr.length-1] + ") \n VALUES (\n");// -2 proto�e posledn� znak TYP do exportu nechci
					System.out.println(table_txt);
				//	LogFile.expoertlog("Import Helios 75\n",table_txt);
					header = false;
				} else {
					// use comma as separator
					radku++;
					if (GetArtikl)
						table_txt=""+table;
					else if(podruhe)
						value += ("\n ), ( \n");
					
					for (int i = 0; i < attr.length-1; i++)// -2 proto�e posledn� znak TYP do exportu nechci
						{
						if (attr[i].equals(" "))
							value +="null,";
						else if(GetArtikl)
						{
							if(i==attr.length-2) value += (attr[i]+" ");
							else value += (""+attr[i] + ",");
							}
						else
							{
							//pro nastaveni varchar
							/*if(	i==4 ||
								//i==1 ||
								i==13 || 
								i==14 ||
								i==15 ||
								i==16 ||
								//i==17 ||
								//i==20||
								//i==21||
								i==22||
								i==23||
								i==24||
								i==31) value += (attr[i]+",");
							else if(i==typ)
								System.out.println("nic u typu");							
							else value += ("'"+attr[i] + "',");*/
							value += (attr[i]+",");
							}
						
						}
						//value += (attr[i]+", ");
					
					if (GetArtikl)
						value += (attr[attr.length-1])+";";
						//System.out.println("GetArtikl"+table_txt);//value += ("0,0,"+"'"+TC_TS+"'"+"\n Union All \n");
					else 
						value += (attr[attr.length-1]);
						//value += ("'"+attr[attr.length-1] + "'"+"\n Union All \n");// -2 proto�e posledn� znak TYP do exportu nechci
					//System.out.println(value);
					//ImportSql.SetIdentity_Insert(conn,table, true);
					//ImportSql.Import2TPV(conn, table_txt, value);
					//ImportSql.SetIdentity_Insert(conn,table, false);
					podruhe=true;
				}
			}
			
			LogFile.expoertlog("Import Mapos TABLE\n",table_txt);
			System.out.println(table_txt);
			System.out.println(value);
		//	LogFile.expoertlog("Import Helios VALUE \n",table_txt);
			LogFile.expoertlog("Import Mapos VALUE \n",value);
			if(GetArtikl)
			{
				ImportSql.UpdateC_Artiklu(conn, table_txt, value, "");
			}else
			{
				//value +="Select 0, 'END', 'END', 'KONEC_IMPORTU', 'END',1,null,null,null,null,null,null,null,null,null ,null ,null ,null ,null,null,null ,null ,null ,null ,'null ',null,'null '";
				//( 10,'DAA000982 ','DAA000979 ','n�boj ','01 ',1 ,null,null,null,null,null,null,null,'KR 50 - loupan� ',0.0000 ,0.4250 ,41.7500 ,7900.0000 ,'1.4301 ','NE ',0 ,0.0000 ,0.0000 ,0.0000 ,'0000026382 ',null,'alauber ')
				LogFile.expoertlog("Import Helios 143\n",table_txt);
				ImportSql.Import2TPV(conn, table_txt, value);
				//ImportSql.Import2TPV(conn, "D3000S.TC_D3000S (POZICE, IDV, ID,  NAZEV, REVIZE, MNOZSTVI)", " VALUES (0, 'END', 'END', 'KONEC_IMPORTU', 'END',1)");
			}
				

			
			
		
				
			String mess="pocet naimportovanych radku je "+radku;
					
			LogFile.expoertlog("Import Tc do ERP probehl uspesne \n",mess );
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			LogFile.expoertlog("Import Tc do ERP nebyl uspesny: \n",e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
			LogFile.expoertlog("Import Tc do ERP nebyl uspesny: \n",e.getMessage());
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
					LogFile.expoertlog("Import Tc do ERP nebyl uspesny: \n",e.getMessage());
				}
			}
		}
		return value;
	}
	public static String ReadCSV4Artikl(String table, String arg, Connection conn) throws SQLException {
		return "null";
	}

}