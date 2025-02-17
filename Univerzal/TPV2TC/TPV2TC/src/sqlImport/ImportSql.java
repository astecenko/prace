package sqlImport;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import com.tpv.connect.LogFile;;

public class ImportSql {

	public static String Import2TPV_hlav(Connection conn, String string) {
		
	
		System.out.println("Test");
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("INSERT INTO tpv_c2t_import_hlav (nazev, soubor, klic_cfg, stav,changenum)" +
				"VALUES ('TC2TPV','neco',2,'N','01')");
		//stringBuilder.append(table);
		String sql;
		sql = stringBuilder.toString();
		
	
		Statement statement;
		try {
			statement = conn.createStatement();
	
			int rs =statement.executeUpdate(sql);
		System.out.println(rs);
		//System.out.println(rs);
	String key = null;
		/*if ( rs.next() ) {
		    // Retrieve the auto generated key(s).
		     key = rs.getString("klic_imp_hlav");
		}*/
		return key=GetIdentity(conn);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
private static String GetIdentity(Connection conn) throws SQLException {
		String key=null;
		
		
		PreparedStatement ps = conn.prepareStatement("SELECT @@IDENTITY");
    	//ps.setString(1, "F");
    	//ResultSet rs = ps.executeQuery();
    	ResultSet rs= ps.executeQuery();
    	
    	while (rs.next()) {
    		//System.out.println(rs.getString(2));
    		int klic=rs.getInt(1);
    
		
    		System.out.println("klic_imp_hlav="+klic);
    		key=String.valueOf(klic);
    		
    	}

		return key;
	}

	public static void SetIdentity_Insert(Connection conn,String table, boolean set) throws SQLException {
		String sql="SET IDENTITY_INSERT "+table;
		if(set)
			sql+=" ON";
		else 
			sql+=" OFF";
		
		System.out.println(sql);
		Statement statement = conn.createStatement();
 		int rs =statement.executeUpdate(sql);
		System.out.println(rs);
	}
	
	
	public static void Import2TPV(Connection conn,String table, String Value)  {
		
		
		System.out.println("Test");
	/*	StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("INSERT INTO ");
		stringBuilder.append(table);*/
		String sql="INSERT INTO "+table+Value+");";
		LogFile.expoertlog("ImportSql:88 \n",table);
		//sql = stringBuilder.toString();
		LogFile.makelog("Import Dialog", sql);
		System.out.println(sql);
		
		try {
			Statement statement = conn.createStatement();
			int rs =statement.executeUpdate(sql);
		
		
			System.out.println(rs);
		
		}catch (SQLException ex) {
			String mess="Unexpected exception : " + ex.toString() + ", sqlstate = " + ex.getSQLState();
			System.out.println(mess);
			LogFile.makelog("Import Dialog", mess);
			LogFile.expoertlog("Import Tc do ERP nebyl uspesny: \n",mess);
			
			System.exit(1);
			
		}
	//	LogFile.makelog("Import Dialog: ", "Soubor naimportovan");
		
	}
	public static void UpdateC_Artiklu(Connection conn,String table, String Value, String condition )  {
		String sql=""+table+" " +Value;//"UPDATE "+table+" SET TC_STATUS="+Value +" Where PORADI="+condition;
		LogFile.expoertlog("Update Tc do ERP: \n",sql);
		System.out.println(sql);
		try {
		Statement statement = conn.createStatement();
		int rs =statement.executeUpdate(sql);
	
		System.out.println(rs);
		
		}catch (SQLException ex) {
			String mess="Unexpected exception : " + ex.toString() + ", sqlstate = " + ex.getSQLState();
			System.out.println(mess);
			LogFile.makelog("Update_C_artiklu", mess);
			LogFile.expoertlog("Update Tc do ERP nebyl uspesny: \n",sql);
			
			//System.exit(1);
			
		}
		
	}
public static void Delete_status1(Connection conn,String table)  {
		
		
		System.out.println("Test");
	/*	StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("INSERT INTO ");
		stringBuilder.append(table);*/
		String sql="Delete "+table+" where DIALOG_STATUS=1";
		//sql = stringBuilder.toString();
		System.out.println(sql);
		try {
		Statement statement = conn.createStatement();
		int rs =statement.executeUpdate(sql);
	
		//int rs =statement.executeUpdate("DELETE FROM D3000S.TC_D3000S WHERE PORADI="+i);
		System.out.println(rs);
		
		}catch (SQLException ex) {
			String mess="Unexpected exception : " + ex.toString() + ", sqlstate = " + ex.getSQLState();
			System.out.println(mess);
			LogFile.makelog("Delete Dialog", mess);
			
			LogFile.expoertlog("Delete Tc do Dialog3000 nebyl uspesny: \n",mess);
			System.exit(1);
			
		}
	//	LogFile.makelog("Import Dialog: ", "Soubor naimportovan");
		
	}
	
}
