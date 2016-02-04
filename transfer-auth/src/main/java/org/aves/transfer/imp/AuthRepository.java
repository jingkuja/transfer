/**
 * MerchantRepository.java
 * Created on Dec 2, 2013 9:21:04 PM
 * Copyright (c) 2012-2014 Aves Team of Sichuan Abacus Co.,Ltd. All rights reserved.
 */
package org.aves.transfer.imp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sql.DataSource;

import org.aves.transfer.JdbcKit;
import org.aves.transfer.bean.Account;

/**
 * @author nikin
 * 
 */
public class AuthRepository {

	private static final Logger logger = Logger.getLogger(AuthRepository.class
			.getName());

	private static final String initSequenceNumber = "insert into idgenerator(scheme,id) values('auth',1001)";

	private static final String nextSequenceNumber = "select id from idgenerator where scheme='auth' ";

	private static final String updateSequenceNumber = "update idgenerator set id=? where scheme='auth' ";

	private static final String insertAuth = "insert into auth(id,account,password,aliasname,aliasid,auth,extra,email,createtime,status) "
			+ "values(?,?,?,?,?,?,?,?,?,?)";

	private static final String updateAuth = "update auth set aliasname=?, extra=?,email=? "
			+ "where id=?";

	private static final String resetAuth = "update auth set password=? "
			+ "where id=?";

	private static final String suspendAuth = "update auth set status=? "
			+ "where id=?";

	private static final String Authbyid = "select id,account,password,aliasname,aliasid,auth,extra,email,createtime,status from auth   where id=? ";

	private static final String Authbyacct = "select id,account,password,aliasname,aliasid,auth,extra,email,createtime,status from auth  where account=?    and status='1'";

	private static final String listAuth = " select id,account,password,aliasname,aliasid,auth,extra,email,createtime,status from auth   where  status='1' ";
	private static final String totalAuth = "select count(*) from auth   where  status='1'";

	private DataSource dataSource;

	/**
	 *
	 */
	public AuthRepository() {
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public DataSource getDataSource() {
		return dataSource;
	}

	public String initSequenceNumber() throws SQLException {
		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			PreparedStatement pstmt = conn.prepareStatement(initSequenceNumber);
			int affected = pstmt.executeUpdate();
			pstmt.close();
			if (affected > 0)
				return "1";
		} catch (SQLException e) {
			e.printStackTrace();
			logger.log(Level.SEVERE, e.getMessage(), e);
		} finally {
			JdbcKit.close(conn, null, null);
		}
		return null;
	}

	public String updateSequenceNumber(int id) throws SQLException {
		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			PreparedStatement pstmt = conn
					.prepareStatement(updateSequenceNumber);
			pstmt.setInt(1, id);
			int affected = pstmt.executeUpdate();
			pstmt.close();
			if (affected > 0)
				return "1";
		} catch (SQLException e) {
			e.printStackTrace();
			logger.log(Level.SEVERE, e.getMessage(), e);
		} finally {
			JdbcKit.close(conn, null, null);
		}
		return null;
	}

	public Integer nextSequenceNumber() {
		Integer id = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			conn = dataSource.getConnection();
			pstmt = conn.prepareStatement(nextSequenceNumber);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				id = rs.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			logger.log(Level.SEVERE, e.getMessage(), e);
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (Exception e) {

			}
			try {
				if (pstmt != null) {
					pstmt.close();
				}
			} catch (Exception e) {

			}
			JdbcKit.close(conn, null, null);
		}
		return id;
	}

	public String insertAuth(Account account) throws SQLException {
		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			PreparedStatement pstmt = conn.prepareStatement(insertAuth);
			pstmt.setString(1, account.getId());
			pstmt.setString(2, account.getAccount());
			pstmt.setString(3, account.getPassword());
			pstmt.setString(4, account.getAliasname());
			pstmt.setString(5, account.getAliasid());
			pstmt.setString(6, account.getAuth());
			pstmt.setString(7, account.getExtra());
			pstmt.setString(8, account.getEmail());
			pstmt.setString(9, getNow());
			pstmt.setString(10, "1");
			int affected = pstmt.executeUpdate();
			pstmt.close();
			if (affected > 0)
				return "1";
		} finally {
			JdbcKit.close(conn, null, null);
		}
		return null;
	}

	public String updateAuth(Account account) throws SQLException {
		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			PreparedStatement pstmt = conn.prepareStatement(updateAuth);
			pstmt.setString(1, account.getAliasname());
			pstmt.setString(2, account.getExtra());
			pstmt.setString(3, account.getEmail());
			pstmt.setString(4, account.getId());
			int affected = pstmt.executeUpdate();
			pstmt.close();
			if (affected > 0)
				return "1";
		} finally {
			JdbcKit.close(conn, null, null);
		}
		return null;
	}

	public String resetAuth(String password, String id) throws SQLException {
		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			PreparedStatement pstmt = conn.prepareStatement(resetAuth);
			pstmt.setString(1, password);
			pstmt.setString(2, id);
			int affected = pstmt.executeUpdate();
			pstmt.close();
			if (affected > 0)
				return "1";
		} finally {
			JdbcKit.close(conn, null, null);
		}
		return null;
	}

	public String suspendMerchant(String id) throws SQLException {
		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			PreparedStatement pstmt = conn.prepareStatement(suspendAuth);
			pstmt.setString(1, "0");
			pstmt.setString(2, id);
			int affected = pstmt.executeUpdate();
			pstmt.close();
			if (affected > 0)
				return "1";
		} finally {
			JdbcKit.close(conn, null, null);
		}
		return null;
	}

	public Account Authbyid(String id) {
		Account account = new Account();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			conn = dataSource.getConnection();
			pstmt = conn.prepareStatement(Authbyid);
			pstmt.setString(1, id);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				account.setId(rs.getString(1));
				account.setAccount(rs.getString(2));
				account.setPassword(rs.getString(3));
				account.setAliasname(rs.getString(4));
				account.setAliasid(rs.getString(5));
				account.setAuth(rs.getString(6));
				account.setExtra(rs.getString(7));
				account.setEmail(rs.getString(8));
				account.setCreatetime(rs.getString(9));
				account.setStatus(rs.getString(10));
			}
		} catch (SQLException e) {
			e.printStackTrace();
			logger.log(Level.SEVERE, e.getMessage(), e);
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (Exception e) {

			}
			try {
				if (pstmt != null) {
					pstmt.close();
				}
			} catch (Exception e) {

			}
			JdbcKit.close(conn, null, null);
		}
		return account;
	}

	public Account Authbyacct(String acct) {
		Account account = new Account();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			conn = dataSource.getConnection();
			pstmt = conn.prepareStatement(Authbyacct);
			pstmt.setString(1, acct);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				account.setId(rs.getString(1));
				account.setAccount(rs.getString(2));
				account.setPassword(rs.getString(3));
				account.setAliasname(rs.getString(4));
				account.setAliasid(rs.getString(5));
				account.setAuth(rs.getString(6));
				account.setExtra(rs.getString(7));
				account.setEmail(rs.getString(8));
				account.setCreatetime(rs.getString(9));
				account.setStatus(rs.getString(10));
			}
		} catch (SQLException e) {
			e.printStackTrace();
			logger.log(Level.SEVERE, e.getMessage(), e);
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (Exception e) {

			}
			try {
				if (pstmt != null) {
					pstmt.close();
				}
			} catch (Exception e) {

			}
			JdbcKit.close(conn, null, null);
		}
		return account;
	}

	public List<Map<String, Object>> listAuth(Map<String, String> para,
			int start, int limit) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Connection conn = null;
		Statement pstmt = null;
		ResultSet rs = null;
		try {
			conn = dataSource.getConnection();
			StringBuffer sb = new StringBuffer(listAuth);
			if (!getStirng(para.get("merid")).equals(""))
				sb.append("  AND aliasid=  '" + getStirng(para.get("merid"))
						+ "'");
			pstmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			pstmt.setMaxRows(start + limit);
			sb.append("order by id ");
			rs = pstmt.executeQuery(sb.toString());
			rs.beforeFirst();
			if (start != 0)
				rs.absolute(start);
			while (rs.next()) {

				String id = rs.getString(1);
				String account = rs.getString(2);
				String password = rs.getString(3);
				String aliasname = rs.getString(4);
				String aliasid = rs.getString(5);
				String auth = rs.getString(6);
				String extra = rs.getString(7);
				String email = rs.getString(8);
				String createtime = rs.getString(9);
				String status = rs.getString(10);

				Map<String, Object> cm = new HashMap<String, Object>();
				cm.put("id", id);
				cm.put("account", account);
				cm.put("aliasname", aliasname);
				cm.put("aliasid", aliasid);
				cm.put("auth", auth);
				cm.put("extra", extra);
				cm.put("email", email);
				cm.put("createtime", createtime);
				cm.put("status", status);

				list.add(cm);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			logger.log(Level.SEVERE, e.getMessage(), e);
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (Exception e) {

			}
			try {
				if (pstmt != null) {
					pstmt.close();
				}
			} catch (Exception e) {

			}
			JdbcKit.close(conn, null, null);
		}
		return list;
	}

	public int totalAuth(Map<String, String> para) {
		Connection conn = null;
		Statement pstmt = null;
		ResultSet rs = null;
		int count = 0;
		try {
			conn = dataSource.getConnection();
			StringBuffer sb = new StringBuffer(totalAuth);
			if (!getStirng(para.get("merid")).equals(""))
				sb.append("  AND aliasid=  '" + getStirng(para.get("merid"))
						+ "'");
			pstmt = conn.createStatement();
			rs = pstmt.executeQuery(sb.toString());
			while (rs.next()) {
				count = rs.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			logger.log(Level.SEVERE, e.getMessage(), e);
			return 1;
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (Exception e) {

			}
			try {
				if (pstmt != null) {
					pstmt.close();
				}
			} catch (Exception e) {

			}
			JdbcKit.close(conn, null, null);
		}
		return count;
	}

	private String getStirng(String ob) {
		if (ob == null)
			return "";
		else
			return ob;
	}

	private String getNow() {
		Date nowday = new Date();
		SimpleDateFormat sm = new SimpleDateFormat("yyyy-MM-dd");

		return sm.format(nowday);
	}
}
