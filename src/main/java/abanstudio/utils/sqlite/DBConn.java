package abanstudio.utils.sqlite;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * @author akyasnaushad
 */
public class DBConn
{
	String libname = null;
	Connection conn = null;
	String filename = null;
	ResultSet rs = null;
	PreparedStatement pst = null;
	String sql = null;

	public DBConn(String library, String file)
	{
		libname = library;
		filename = file;
	}

	public void openConn()
	{
		try {
			Class.forName(libname);
			conn = DriverManager.getConnection(filename);
		} catch (ClassNotFoundException | SQLException e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
	}

	public void closeConn()
	{
		//http://stackoverflow.com/questions/2225221/closing-database-connections-in-java
		if (rs != null) {
			try {
				rs.close();
			} catch (SQLException e) { /* ignored */}
		}
		if (pst != null) {
			try {
				pst.close();
			} catch (SQLException e) { /* ignored */}
		}
		if (conn != null) {
			try {
				conn.close();
			} catch (SQLException e) { /* ignored */}
		}
	}

	public void setSQL(String s)
	{
		sql = s;
	}

	public void prepStatement()
	{
		try {
			pst = conn.prepareStatement(sql);
		} catch (SQLException ex) {
			Logger.getLogger(DBConn.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	public void executeN()
	{
		try {
			if (pst.execute()) {
				//JOptionPane.showMessageDialog(null,"way");
			}
		} catch (SQLException ex) {
			Logger.getLogger(DBConn.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	public ResultSet executeQ()
	{
		try {
			rs = pst.executeQuery();
		} catch (SQLException ex) {
			Logger.getLogger(DBConn.class.getName()).log(Level.SEVERE, null, ex);
		}
		return rs;
	}

	public void pstSetString(int rowno, String id)
	{
		try {
			pst.setString(rowno, id);
		} catch (SQLException ex) {
			Logger.getLogger(DBConn.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	public void pstSetBoolean(int rowno, boolean b)
	{
		try {
			pst.setBoolean(rowno, b);
		} catch (SQLException ex) {
			Logger.getLogger(DBConn.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	public void pstSetInt(int rowno, int id)
	{
		try {
			pst.setInt(rowno, id);
		} catch (SQLException ex) {
			Logger.getLogger(DBConn.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	public void pstSetDouble(int rowno, double id)
	{
		try {
			pst.setDouble(rowno, id);
		} catch (SQLException ex) {
			Logger.getLogger(DBConn.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
}
