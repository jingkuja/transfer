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
import org.aves.transfer.bean.Merchant;

/**
 * @author nikin
 * 
 */
public class MerchantRepository {

	private static final Logger logger = Logger
			.getLogger(MerchantRepository.class.getName());

	private static final String initSequenceNumber = "insert into idgenerator(scheme,id) values('merchant',1001)";

	private static final String nextSequenceNumber = "select id from idgenerator where scheme='merchant' ";

	private static final String updateSequenceNumber = "update idgenerator set id=? where scheme='merchant' ";

	private static final String insertMerchant = "insert into merchant(id,mname,mcode,address,contactor,phone,accountname,kfbank,kfbaacount,tplate,transign,email,createtime,status) "
			+ "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

	private static final String updateMerchant = "update merchant set mname=?, mcode=?,address=?,contactor=?,phone=?,accountname=?,kfbank=?,kfbaacount=?,tplate=?,transign=?,email=?"
			+ "where id=?";

	private static final String suspendMerchant = "update merchant set status=? "
			+ "where id=?";

	private static final String merchantbyid = "select id,mname,mcode,address,contactor,phone,accountname,kfbank,kfbaacount,tplate,transign,email,createtime from merchant   where id=? ";

	private static final String merchantbycode = "select id,mname,mcode,address,contactor,phone,accountname,kfbank,kfbaacount,tplate,transign,email,createtime from merchant   where mcode=?    and status='1'";

	private static final String listMerchant = " select id,mname,mcode,address,contactor,phone,accountname,kfbank,kfbaacount,tplate,transign,email,createtime  from merchant   where 1=1    and status='1' ";
	private static final String totalMerchant = "select count(*) from merchant   where 1=1  and status='1'";

	private DataSource dataSource;

	/**
	 *
	 */
	public MerchantRepository() {
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

	public String insertMerchant(Merchant merchant) throws SQLException {
		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			PreparedStatement pstmt = conn.prepareStatement(insertMerchant);
			pstmt.setString(1, merchant.getId());
			pstmt.setString(2, merchant.getMname());
			pstmt.setString(3, merchant.getMcode());
			pstmt.setString(4, merchant.getAddress());
			pstmt.setString(5, merchant.getContactor());
			pstmt.setString(6, merchant.getPhone());
			pstmt.setString(7, merchant.getAccountname());
			pstmt.setString(8, merchant.getKfbank());
			pstmt.setString(9, merchant.getKfbaacount());
			pstmt.setString(10, merchant.getTplate());
			pstmt.setString(11, merchant.getTransign());
			pstmt.setString(12, merchant.getEmail());
			pstmt.setString(13, getNow());
			pstmt.setString(14, "1");
			int affected = pstmt.executeUpdate();
			pstmt.close();
			if (affected > 0)
				return "1";
		} finally {
			JdbcKit.close(conn, null, null);
		}
		return null;
	}

	public String updateMerchant(Merchant merchant) throws SQLException {
		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			PreparedStatement pstmt = conn.prepareStatement(updateMerchant);
			pstmt.setString(1, merchant.getMname());
			pstmt.setString(2, merchant.getMcode());
			pstmt.setString(3, merchant.getAddress());
			pstmt.setString(4, merchant.getContactor());
			pstmt.setString(5, merchant.getPhone());
			pstmt.setString(6, merchant.getAccountname());
			pstmt.setString(7, merchant.getKfbank());
			pstmt.setString(8, merchant.getKfbaacount());
			pstmt.setString(9, merchant.getTplate());
			pstmt.setString(10, merchant.getTransign());
			pstmt.setString(11, merchant.getEmail());
			pstmt.setString(12, merchant.getId());
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
			PreparedStatement pstmt = conn.prepareStatement(suspendMerchant);
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

	public Merchant merchantbyid(String id) {
		Merchant merchant = new Merchant();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			conn = dataSource.getConnection();
			pstmt = conn.prepareStatement(merchantbyid);
			pstmt.setString(1, id);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				merchant.setId(rs.getString(1));
				merchant.setMname(rs.getString(2));
				merchant.setMcode(rs.getString(3));
				merchant.setAddress(rs.getString(4));
				merchant.setContactor(rs.getString(5));
				merchant.setPhone(rs.getString(6));
				merchant.setAccountname(rs.getString(7));
				merchant.setKfbank(rs.getString(8));
				merchant.setKfbaacount(rs.getString(9));
				merchant.setTplate(rs.getString(10));
				merchant.setTransign(rs.getString(11));
				merchant.setEmail(rs.getString(12));
				merchant.setCreatetime(rs.getString(13));
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
		return merchant;
	}

	public Merchant merchantbycode(String mcode) {
		Merchant merchant = new Merchant();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			conn = dataSource.getConnection();
			pstmt = conn.prepareStatement(merchantbycode);
			pstmt.setString(1, mcode);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				merchant.setId(rs.getString(1));
				merchant.setMname(rs.getString(2));
				merchant.setMcode(rs.getString(3));
				merchant.setAddress(rs.getString(4));
				merchant.setContactor(rs.getString(5));
				merchant.setPhone(rs.getString(6));
				merchant.setAccountname(rs.getString(7));
				merchant.setKfbank(rs.getString(8));
				merchant.setKfbaacount(rs.getString(9));
				merchant.setTplate(rs.getString(10));
				merchant.setTransign(rs.getString(11));
				merchant.setEmail(rs.getString(12));
				merchant.setCreatetime(rs.getString(13));
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
		return merchant;
	}

	public List<Map<String, Object>> listMerchant(Map<String, String> para,
			int start, int limit) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Connection conn = null;
		Statement pstmt = null;
		ResultSet rs = null;
		try {
			conn = dataSource.getConnection();
			StringBuffer sb = new StringBuffer(listMerchant);
			if (!getStirng(para.get("mname")).equals(""))
				sb.append("  AND (mname like CONCAT('%','"
						+ getStirng(para.get("mname"))
						+ "','%' ) or mcode like CONCAT('%','"
						+ getStirng(para.get("mname")) + "','%' ) )   ");
			pstmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			pstmt.setMaxRows(start + limit);
			sb.append("order by mcode ");
			rs = pstmt.executeQuery(sb.toString());
			rs.beforeFirst();
			if (start != 0)
				rs.absolute(start);
			while (rs.next()) {
				String id = rs.getString(1);
				String mname = rs.getString(2);
				String mcode = rs.getString(3);
				String address = rs.getString(4);
				String contactor = rs.getString(5);
				String phone = rs.getString(6);
				String accountname = rs.getString(7);
				String kfbank = rs.getString(8);
				String kfbaacount = rs.getString(9);
				String tplate = rs.getString(10);
				String transign = rs.getString(11);
				String email = rs.getString(12);
				String createtime = rs.getString(13);

				Map<String, Object> cm = new HashMap<String, Object>();
				cm.put("id", id);
				cm.put("mname", mname);
				cm.put("mcode", mcode);
				cm.put("address", address);
				cm.put("contactor", contactor);
				cm.put("phone", phone);
				cm.put("accountname", accountname);
				cm.put("kfbank", kfbank);
				cm.put("kfbaacount", kfbaacount);
				cm.put("tplate", tplate);
				cm.put("transign", transign);
				cm.put("email", email);
				cm.put("createtime", createtime);

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

	public int totalMerchant(Map<String, String> para) {
		Connection conn = null;
		Statement pstmt = null;
		ResultSet rs = null;
		int count = 0;
		try {
			conn = dataSource.getConnection();
			StringBuffer sb = new StringBuffer(totalMerchant);
			if (!getStirng(para.get("mname")).equals(""))
				sb.append("  AND (mname like CONCAT('%','"
						+ getStirng(para.get("mname"))
						+ "','%' ) or mcode like CONCAT('%','"
						+ getStirng(para.get("mname")) + "','%' ) )   ");
			if (!getStirng(para.get("lcode")).equals(""))
				sb.append("  AND mcode = '" + getStirng(para.get("lcode"))
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
