/**
 * BillRepository.java
 * Created on Dec 2, 2013 9:21:04 PM
 * Copyright (c) 2012-2014 Aves Team of Sichuan Abacus Co.,Ltd. All rights reserved.
 */
package org.aves.transfer.temp;

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
import org.aves.transfer.bean.Bill;

/**
 * @author nikin
 * 
 */
public class TempRepository {

	private static final Logger logger = Logger.getLogger(TempRepository.class
			.getName());

	private static final String initSequenceNumber = "insert into idgenerator(scheme,id) values('bill',1001)";

	private static final String nextSequenceNumber = "select id from idgenerator where scheme='bill' ";

	private static final String updateSequenceNumber = "update idgenerator set id=? where scheme='bill' ";

	private static final String insertBill = "insert into bill(id,merid,terminal,postradeNo,tradetype,tradetime,sum,mfee,fee,rate,ratea,income,transfermer,createtime,status,sdesc,paysrvCode) "
			+ "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

	private static final String resettlebill = "update bill set sdesc=?, status=?,createtime=?,mfee=?,fee=?,rate=?,ratea=?,income=?,transfermer=?,paysrvCode=?  "
			+ "where id=?";

	private static final String settlebill = "update bill set sdesc=?, status=?,createtime=?, "
			+ "where id=?";

	private static final String billbyid = "select id,merid,terminal,postradeNo,tradetype,tradetime,sum,mfee,fee,rate,ratea,income,transfermer,createtime,status,sdesc,paysrvCode from bill  where id=?  ";

	private static final String billbytradno = "select id,merid,terminal,postradeNo,tradetype,tradetime,sum,mfee,fee,rate,ratea,income,transfermer,createtime,status,sdesc,paysrvCode from bill where postradeNo=?  ";

	private static final String listbills = " select id,merid,terminal,postradeNo,tradetype,tradetime,sum,mfee,fee,rate,ratea,income,transfermer,createtime,status,sdesc,paysrvCode from bill   where 1=1 ";
	private static final String totalBills = "select count(*) from bill   where 1=1  ";

	private DataSource dataSource;

	/**
	 *
	 */
	public TempRepository() {
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

	public String insertBill(Bill bill) throws SQLException {
		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			PreparedStatement pstmt = conn.prepareStatement(insertBill);
			pstmt.setString(1, bill.getId());
			pstmt.setString(2, bill.getMerid());
			pstmt.setString(3, bill.getTerminal());
			pstmt.setString(4, bill.getPostradeNo());
			pstmt.setString(5, bill.getTradetype());
			pstmt.setString(6, bill.getTradetime());
			pstmt.setString(7, bill.getSum());
			pstmt.setString(8, bill.getMfee());
			pstmt.setString(9, bill.getFee());
			pstmt.setString(10, bill.getRate());
			pstmt.setString(11, bill.getRatea());
			pstmt.setString(12, bill.getIncome());
			pstmt.setString(13, bill.getTransfermer());
			pstmt.setString(14, bill.getCreatetime());
			pstmt.setString(15, bill.getStatus());
			pstmt.setString(16, bill.getSdesc());
			pstmt.setString(17, bill.getPaysrvCode());
			int affected = pstmt.executeUpdate();
			pstmt.close();
			if (affected > 0)
				return "1";
		} finally {
			JdbcKit.close(conn, null, null);
		}
		return null;
	}

	public String resettlebill(Bill bill) throws SQLException {
		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			PreparedStatement pstmt = conn.prepareStatement(resettlebill);
			pstmt.setString(1, bill.getSdesc());
			pstmt.setString(2, bill.getStatus());
			pstmt.setString(3, bill.getCreatetime());
			pstmt.setString(4, bill.getMfee());
			pstmt.setString(5, bill.getFee());
			pstmt.setString(6, bill.getRate());
			pstmt.setString(7, bill.getRatea());
			pstmt.setString(8, bill.getIncome());
			pstmt.setString(9, bill.getTransfermer());
			pstmt.setString(10, bill.getPaysrvCode());
			pstmt.setString(11, bill.getId());
			int affected = pstmt.executeUpdate();
			pstmt.close();
			if (affected > 0)
				return "1";
		} finally {
			JdbcKit.close(conn, null, null);
		}
		return null;
	}

	public String settlebill(Bill bill) throws SQLException {
		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			PreparedStatement pstmt = conn.prepareStatement(settlebill);
			pstmt.setString(1, bill.getSdesc());
			pstmt.setString(2, bill.getStatus());
			pstmt.setString(3, bill.getCreatetime());
			pstmt.setString(4, bill.getId());

			int affected = pstmt.executeUpdate();
			pstmt.close();
			if (affected > 0)
				return "1";
		} finally {
			JdbcKit.close(conn, null, null);
		}
		return null;
	}

	public Bill billbyid(String id) {
		Bill bill = new Bill();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			conn = dataSource.getConnection();
			pstmt = conn.prepareStatement(billbyid);
			pstmt.setString(1, id);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				bill.setId(rs.getString(1));
				bill.setMerid(rs.getString(2));
				bill.setTerminal(rs.getString(3));
				bill.setPostradeNo(rs.getString(4));
				bill.setTradetype(rs.getString(5));
				bill.setTradetime(rs.getString(6));
				bill.setSum(rs.getString(7));
				bill.setMfee(rs.getString(8));
				bill.setFee(rs.getString(9));
				bill.setRate(rs.getString(10));
				bill.setRatea(rs.getString(11));
				bill.setIncome(rs.getString(12));
				bill.setTransfermer(rs.getString(13));
				bill.setCreatetime(rs.getString(14));
				bill.setStatus(rs.getString(15));
				bill.setSdesc(rs.getString(16));
				bill.setPaysrvCode(rs.getString(17));

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
		return bill;
	}

	public Bill billbytradno(String tradeno) {
		Bill bill = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			conn = dataSource.getConnection();
			pstmt = conn.prepareStatement(billbytradno);
			pstmt.setString(1, tradeno);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				bill = new Bill();
				bill.setId(rs.getString(1));
				bill.setMerid(rs.getString(2));
				bill.setTerminal(rs.getString(3));
				bill.setPostradeNo(rs.getString(4));
				bill.setTradetype(rs.getString(5));
				bill.setTradetime(rs.getString(6));
				bill.setSum(rs.getString(7));
				bill.setMfee(rs.getString(8));
				bill.setFee(rs.getString(9));
				bill.setRate(rs.getString(10));
				bill.setRatea(rs.getString(11));
				bill.setIncome(rs.getString(12));
				bill.setTransfermer(rs.getString(13));
				bill.setCreatetime(rs.getString(14));
				bill.setStatus(rs.getString(15));
				bill.setSdesc(rs.getString(16));
				bill.setPaysrvCode(rs.getString(17));
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
		return bill;
	}

	public List<Map<String, Object>> listbills(Map<String, String> para,
			int start, int limit) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Connection conn = null;
		Statement pstmt = null;
		ResultSet rs = null;
		try {
			conn = dataSource.getConnection();
			StringBuffer sb = new StringBuffer(listbills);
			if (!getStirng(para.get("mname")).equals(""))
				sb.append("AND  merid in (select id from merchant where mname like '%"
						+ getStirng(para.get("mname")) + "%')");
			if (!getStirng(para.get("merid")).equals(""))
				sb.append(" AND  merid ='" + getStirng(para.get("merid"))
						+ "' ");
			if (!getStirng(para.get("sdate")).equals(""))
				sb.append(" AND createtime = '" + getStirng(para.get("sdate"))
						+ "'  ");
			if (!getStirng(para.get("mdate")).equals(""))
				sb.append(" AND createtime <= '" + getStirng(para.get("mdate"))
						+ "'  ");
			if (!getStirng(para.get("fdate")).equals(""))
				sb.append(" AND createtime >= '" + getStirng(para.get("fdate"))
						+ "'  ");
			if (!getStirng(para.get("status")).equals(""))
				sb.append(" AND status = '" + getStirng(para.get("status"))
						+ "'  ");
			pstmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			pstmt.setMaxRows(start + limit);
			sb.append("order by createtime desc");
			rs = pstmt.executeQuery(sb.toString());
			rs.beforeFirst();
			if (start != 0)
				rs.absolute(start);
			while (rs.next()) {
				String id = rs.getString(1);
				String merid = rs.getString(2);
				String terminal = rs.getString(3);
				String postradeNo = rs.getString(4);
				String tradetype = rs.getString(5);
				String tradetime = rs.getString(6);
				String sum = rs.getString(7);
				String mfee = rs.getString(8);
				String fee = rs.getString(9);
				String rate = rs.getString(10);
				String ratea = rs.getString(11);
				String income = rs.getString(12);
				String transfermer = rs.getString(13);
				String createtime = rs.getString(14);
				String status = rs.getString(15);
				String desc = rs.getString(16);
				String payCode = rs.getString(17);

				Map<String, Object> cm = new HashMap<String, Object>();
				cm.put("id", id);
				cm.put("merid", merid);
				cm.put("terminal", terminal);
				cm.put("postradeNo", postradeNo);
				cm.put("tradetype", tradetype);
				cm.put("tradetime", tradetime);
				cm.put("sum", sum);
				cm.put("mfee", mfee);
				cm.put("fee", fee);
				cm.put("rate", rate);
				cm.put("ratea", ratea);
				cm.put("income", income);
				cm.put("transfermer", transfermer);
				cm.put("createtime", createtime);
				cm.put("status", status);
				cm.put("desc", desc);
				cm.put("payCode", payCode);

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

	public int totalBills(Map<String, String> para) {
		Connection conn = null;
		Statement pstmt = null;
		ResultSet rs = null;
		int count = 0;
		try {
			conn = dataSource.getConnection();
			StringBuffer sb = new StringBuffer(totalBills);
			if (!getStirng(para.get("mname")).equals(""))
				sb.append("AND  merid in (select id from merchant where mname like '%"
						+ getStirng(para.get("mname")) + "%')");
			if (!getStirng(para.get("merid")).equals(""))
				sb.append(" AND  merid ='" + getStirng(para.get("merid"))
						+ "' ");
			if (!getStirng(para.get("sdate")).equals(""))
				sb.append(" AND createtime = '" + getStirng(para.get("sdate"))
						+ "'  ");
			if (!getStirng(para.get("mdate")).equals(""))
				sb.append(" AND createtime <= '" + getStirng(para.get("mdate"))
						+ "'  ");
			if (!getStirng(para.get("fdate")).equals(""))
				sb.append(" AND createtime >= '" + getStirng(para.get("fdate"))
						+ "'  ");
			if (!getStirng(para.get("status")).equals(""))
				sb.append(" AND status = '" + getStirng(para.get("status"))
						+ "'  ");
			pstmt = conn.createStatement();
			rs = pstmt.executeQuery(sb.toString());
			while (rs.next()) {
				count = rs.getInt(1);
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
