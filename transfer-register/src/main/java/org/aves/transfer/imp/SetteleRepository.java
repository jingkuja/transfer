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
import org.aves.transfer.bean.Settles;

/**
 * @author nikin
 * 
 */
public class SetteleRepository {

	private static final Logger logger = Logger
			.getLogger(SetteleRepository.class.getName());

	private static final String initSequenceNumber = "insert into idgenerator(scheme,id) values('settles',1001)";

	private static final String nextSequenceNumber = "select id from idgenerator where scheme='settles' ";

	private static final String updateSequenceNumber = "update idgenerator set id=? where scheme='settles' ";

	private static final String insertSettles = "insert into settles(id,merchantid,contractNum,rebate,rebatea,signer,approver,approvetime,starttime,endtime,createtime,status,title,paysrvCode,paysrvAN,paysrvAltAN,paysrvKey,paysrvAgent,excon) "
			+ "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

	private static final String updateSettles = "update settles set contractNum=?, signer=? "
			+ "where id=?";

	private static final String suspSettles = "update settles set status=? "
			+ "where id=?";

	private static final String approve = "update settles set status=? ,approver=?,approvetime=? "
			+ "where id=?";

	private static final String updateSettlestime = "update settles set starttime=?,endtime=? "
			+ "where id=?";

	private static final String settlesbyid = "select id,merchantid,contractNum,rebate,rebatea,signer,approver,approvetime,starttime,endtime,createtime,status,title,paysrvCode,paysrvAN,paysrvAltAN,paysrvKey,paysrvAgent,excon from settles   where id=?  and status!='0'";

	private static final String settlesbyAid = "select id,merchantid,contractNum,rebate,rebatea,signer,approver,approvetime,starttime,endtime,createtime,status,title,paysrvCode,paysrvAN,paysrvAltAN,paysrvKey,paysrvAgent,excon from settles   where contractNum=?  and status!='0'";

	private static final String settlesbyAida = "select id,merchantid,contractNum,rebate,rebatea,signer,approver,approvetime,starttime,endtime,createtime,status,title,paysrvCode,paysrvAN,paysrvAltAN,paysrvKey,paysrvAgent,excon from settles   where contractNum=? ";

	private static final String settlesbymid = "select id,merchantid,contractNum,rebate,rebatea,signer,approver,approvetime,starttime,endtime,createtime,status,title,paysrvCode,paysrvAN,paysrvAltAN,paysrvKey,paysrvAgent,excon from settles  "
			+ " where merchantid=?  and status!='0'  AND(starttime <? AND endtime > ?) ";

	private static final String settlesbymidnt = "select id,merchantid,contractNum,rebate,rebatea,signer,approver,approvetime,starttime,endtime,createtime,status,title,paysrvCode,paysrvAN,paysrvAltAN,paysrvKey,paysrvAgent,excon from settles  "
			+ " where merchantid=?  and status!='0'  ";

	private static final String listSettles = " select id,merchantid,contractNum,rebate,rebatea,signer,approver,approvetime,starttime,endtime,createtime,status,title,paysrvCode,paysrvAN,paysrvAltAN,paysrvKey,paysrvAgent,excon from settles    where 1=1 ";
	private static final String totalSettles = "select count(*) from settles   where 1=1  ";

	private static final String totalsettlesbytime = "select count(*) from settles   where status=?    AND((starttime >= ? AND starttime <?) or (endtime >?  AND endtime <=? )) and merchantid=? and paysrvCode=?";

	private DataSource dataSource;

	/**
	 *
	 */
	public SetteleRepository() {
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

	public String insertSettles(Settles settles) throws SQLException {
		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			PreparedStatement pstmt = conn.prepareStatement(insertSettles);
			pstmt.setString(1, settles.getId());
			pstmt.setString(2, settles.getMerchantid());
			pstmt.setString(3, settles.getContractNum());
			pstmt.setString(4, settles.getRebate());
			pstmt.setString(5, settles.getRebatea());
			pstmt.setString(6, settles.getSigner());
			pstmt.setString(7, settles.getApprover());
			pstmt.setString(8, settles.getApprovetime());
			pstmt.setString(9, settles.getStarttime());
			pstmt.setString(10, settles.getEndtime());
			pstmt.setString(11, getNow());
			pstmt.setString(12, "1");
			pstmt.setString(13, settles.getTitle());
			pstmt.setString(14, settles.getPaysrvCode());
			pstmt.setString(15, settles.getPaysrvAN());
			pstmt.setString(16, settles.getPaysrvAltAN());
			pstmt.setString(17, settles.getPaysrvKey());
			pstmt.setString(18, settles.getPaysrvAgent());
			pstmt.setString(19, settles.getExcon());

			int affected = pstmt.executeUpdate();
			pstmt.close();
			if (affected > 0)
				return "1";
		} finally {
			JdbcKit.close(conn, null, null);
		}
		return null;
	}

	public String updateSettles(Settles settles) throws SQLException {
		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			PreparedStatement pstmt = conn.prepareStatement(updateSettles);
			pstmt.setString(1, settles.getContractNum());
			pstmt.setString(2, settles.getSigner());
			int affected = pstmt.executeUpdate();
			pstmt.close();
			if (affected > 0)
				return "1";
		} finally {
			JdbcKit.close(conn, null, null);
		}
		return null;
	}

	public String approve(Settles settles) throws SQLException {
		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			PreparedStatement pstmt = conn.prepareStatement(approve);
			pstmt.setString(1, settles.getApprover());
			pstmt.setString(2, settles.getApprovetime());
			pstmt.setString(3, settles.getId());
			int affected = pstmt.executeUpdate();
			pstmt.close();
			if (affected > 0)
				return "1";
		} finally {
			JdbcKit.close(conn, null, null);
		}
		return null;
	}

	public String updateSettlestime(Settles settles) throws SQLException {
		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			PreparedStatement pstmt = conn.prepareStatement(updateSettlestime);
			pstmt.setString(1, settles.getStarttime());
			pstmt.setString(2, settles.getEndtime());
			pstmt.setString(3, settles.getId());
			int affected = pstmt.executeUpdate();
			pstmt.close();
			if (affected > 0)
				return "1";
		} finally {
			JdbcKit.close(conn, null, null);
		}
		return null;
	}

	public String suspSettles(String id) throws SQLException {
		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			PreparedStatement pstmt = conn.prepareStatement(suspSettles);
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

	public Settles settlesbyAid(String aid, boolean all) {
		Settles settles = new Settles();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			conn = dataSource.getConnection();
			if (all)
				pstmt = conn.prepareStatement(settlesbyAida);
			else
				pstmt = conn.prepareStatement(settlesbyAid);
			pstmt.setString(1, aid);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				settles.setId(rs.getString(1));
				settles.setMerchantid(rs.getString(2));
				settles.setContractNum(rs.getString(3));
				settles.setRebate(rs.getString(4));
				settles.setRebatea(rs.getString(5));
				settles.setSigner(rs.getString(6));
				settles.setApprover(rs.getString(7));
				settles.setApprovetime(rs.getString(8));
				settles.setStarttime(rs.getString(9));
				settles.setEndtime(rs.getString(10));
				settles.setCreatetime(rs.getString(11));
				settles.setStatus(rs.getString(12));
				settles.setTitle(rs.getString(13));
				settles.setPaysrvCode(rs.getString(14));
				settles.setPaysrvAN(rs.getString(15));
				settles.setPaysrvAltAN(rs.getString(16));
				settles.setPaysrvKey(rs.getString(17));
				settles.setPaysrvAgent(rs.getString(18));
				settles.setExcon(rs.getString(19));

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
		return settles;
	}

	public Settles settlesbyid(String id) {
		Settles settles = new Settles();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			conn = dataSource.getConnection();
			pstmt = conn.prepareStatement(settlesbyid);
			pstmt.setString(1, id);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				settles.setId(rs.getString(1));
				settles.setMerchantid(rs.getString(2));
				settles.setContractNum(rs.getString(3));
				settles.setRebate(rs.getString(4));
				settles.setRebatea(rs.getString(5));
				settles.setSigner(rs.getString(6));
				settles.setApprover(rs.getString(7));
				settles.setApprovetime(rs.getString(8));
				settles.setStarttime(rs.getString(9));
				settles.setEndtime(rs.getString(10));
				settles.setCreatetime(rs.getString(11));
				settles.setStatus(rs.getString(12));
				settles.setTitle(rs.getString(13));
				settles.setPaysrvCode(rs.getString(14));
				settles.setPaysrvAN(rs.getString(15));
				settles.setPaysrvAltAN(rs.getString(16));
				settles.setPaysrvKey(rs.getString(17));
				settles.setPaysrvAgent(rs.getString(18));
				settles.setExcon(rs.getString(19));

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
		return settles;
	}

	public Settles settlesbymid(Map<String, String> conditon) {
		Settles settles = new Settles();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String now = getNow();
		try {
			conn = dataSource.getConnection();
			pstmt = conn.prepareStatement(settlesbymid);
			pstmt.setString(1, conditon.get("mid"));
			pstmt.setString(2, now);
			pstmt.setString(3, now);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				settles.setId(rs.getString(1));
				settles.setMerchantid(rs.getString(2));
				settles.setContractNum(rs.getString(3));
				settles.setRebate(rs.getString(4));
				settles.setRebatea(rs.getString(5));
				settles.setSigner(rs.getString(6));
				settles.setApprover(rs.getString(7));
				settles.setApprovetime(rs.getString(8));
				settles.setStarttime(rs.getString(9));
				settles.setEndtime(rs.getString(10));
				settles.setCreatetime(rs.getString(11));
				settles.setStatus(rs.getString(12));
				settles.setTitle(rs.getString(13));
				settles.setPaysrvCode(rs.getString(14));
				settles.setPaysrvAN(rs.getString(15));
				settles.setPaysrvAltAN(rs.getString(16));
				settles.setPaysrvKey(rs.getString(17));
				settles.setPaysrvAgent(rs.getString(18));
				settles.setExcon(rs.getString(19));
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
		return settles;
	}

	public List<Settles> settlesbymidnt(Map<String, String> conditon) {
		List<Settles> lsettles = new ArrayList<Settles>();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			conn = dataSource.getConnection();
			pstmt = conn.prepareStatement(settlesbymidnt);
			pstmt.setString(1, conditon.get("mid"));
			rs = pstmt.executeQuery();
			while (rs.next()) {
				Settles settles = new Settles();
				settles.setId(rs.getString(1));
				settles.setMerchantid(rs.getString(2));
				settles.setContractNum(rs.getString(3));
				settles.setRebate(rs.getString(4));
				settles.setRebatea(rs.getString(5));
				settles.setSigner(rs.getString(6));
				settles.setApprover(rs.getString(7));
				settles.setApprovetime(rs.getString(8));
				settles.setStarttime(rs.getString(9));
				settles.setEndtime(rs.getString(10));
				settles.setCreatetime(rs.getString(11));
				settles.setStatus(rs.getString(12));
				settles.setTitle(rs.getString(13));
				settles.setPaysrvCode(rs.getString(14));
				settles.setPaysrvAN(rs.getString(15));
				settles.setPaysrvAltAN(rs.getString(16));
				settles.setPaysrvKey(rs.getString(17));
				settles.setPaysrvAgent(rs.getString(18));
				settles.setExcon(rs.getString(19));
				lsettles.add(settles);
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
		return lsettles;
	}

	public List<Map<String, Object>> listSettles(Map<String, String> para,
			int start, int limit) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Connection conn = null;
		Statement pstmt = null;
		ResultSet rs = null;
		try {
			conn = dataSource.getConnection();
			StringBuffer sb = new StringBuffer(listSettles);
			if (!getStirng(para.get("merchantid")).equals(""))
				sb.append(" AND merchantid = '"
						+ getStirng(para.get("merchantid")) + "'  ");
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
				String merchantid = rs.getString(2);
				String contractNum = rs.getString(3);
				String rebate = rs.getString(4);
				String rebatea = rs.getString(5);
				String signer = rs.getString(6);
				String approver = rs.getString(7);
				String approvetime = rs.getString(8);
				String starttime = rs.getString(9);
				String endtime = rs.getString(10);
				String createtime = rs.getString(11);
				String status = rs.getString(12);
				String title = rs.getString(13);
				String paysrvCode = rs.getString(14);
				String paysrvAN = rs.getString(15);
				String paysrvAltAN = rs.getString(16);
				String paysrvKey = rs.getString(17);
				String paysrvAgent = rs.getString(18);
				String excon = rs.getString(19);

				Map<String, Object> cm = new HashMap<String, Object>();
				cm.put("id", id);
				cm.put("merchantid", merchantid);
				cm.put("contractNum", contractNum);
				cm.put("rebate", rebate);
				cm.put("rebatea", rebatea);
				cm.put("signer", signer);
				cm.put("approver", approver);
				cm.put("approvetime", approvetime);
				cm.put("starttime", starttime);
				cm.put("endtime", endtime);
				cm.put("createtime", createtime);
				cm.put("status", status);
				cm.put("title", title);
				cm.put("paysrvCode", paysrvCode);
				cm.put("paysrvAN", paysrvAN);
				cm.put("paysrvAltAN", paysrvAltAN);
				cm.put("paysrvKey", paysrvKey);
				cm.put("paysrvAgent", paysrvAgent);
				cm.put("excon", excon);
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

	public int totalSettles(Map<String, String> para) {
		Connection conn = null;
		Statement pstmt = null;
		ResultSet rs = null;
		int count = 0;
		try {
			conn = dataSource.getConnection();
			StringBuffer sb = new StringBuffer(totalSettles);
			if (!getStirng(para.get("merchantid")).equals(""))
				sb.append(" AND merchantid = '"
						+ getStirng(para.get("merchantid")) + "'  ");
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

	public int totalsettlesbytime(Settles settles) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		int count = 0;
		try {
			conn = dataSource.getConnection();
			pstmt = conn.prepareStatement(totalsettlesbytime);
			pstmt.setString(1, "1");
			pstmt.setString(2, settles.getStarttime());
			pstmt.setString(3, settles.getEndtime());
			pstmt.setString(4, settles.getStarttime());
			pstmt.setString(5, settles.getEndtime());
			pstmt.setString(6, settles.getMerchantid());
			pstmt.setString(7, settles.getPaysrvCode());
			rs = pstmt.executeQuery();
			while (rs.next()) {
				count = rs.getInt(1);
			}
		} catch (SQLException e) {
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
