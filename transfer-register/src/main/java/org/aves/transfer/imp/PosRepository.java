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
import org.aves.transfer.bean.MerchantDevice;

/**
 * @author nikin
 * 
 */
public class PosRepository {

	private static final Logger logger = Logger.getLogger(PosRepository.class
			.getName());

	private static final String initSequenceNumber = "insert into idgenerator(scheme,id) values('merchantdevice',1001)";

	private static final String nextSequenceNumber = "select id from idgenerator where scheme='merchantdevice' ";

	private static final String updateSequenceNumber = "update idgenerator set id=? where scheme='merchantdevice' ";

	private static final String insertMerchantDevice = "insert into merchantdevice(id,merchantid,posnum,deviceNum,createtime,status) "
			+ "values(?,?,?,?,?,?)";

	private static final String upsetMerchantDevice = "update merchantdevice set posnum=?, deviceNum=? "
			+ "where id=?";

	private static final String suspendMerchantDevice = "update merchantdevice set status=? "
			+ "where id=?";

	private static final String merchantDevicebyid = "select id,merchantid,posnum,deviceNum,createtime,status from merchantdevice   where id=?  and status='1'";

	private static final String merchantDevicebymid = "select id,merchantid,posnum,deviceNum,createtime,status from merchantdevice  where merchantid=?    and status='1'";

	private static final String listMerchantDevice = " select id,merchantid,posnum,deviceNum,createtime,status from merchantdevice    where 1=1    and status='1' ";
	private static final String totalMerchantDevice = "select count(*) from merchantdevice   where 1=1  and status='1'";

	private static final String totalMerchantDeviceBynum = "select count(*) from merchantdevice   where  posnum=?  and  merchantid=? and  status='1'";

	private DataSource dataSource;

	/**
	 *
	 */
	public PosRepository() {
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

	public String insertMerchantDevice(MerchantDevice merchantDevice)
			throws SQLException {
		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			PreparedStatement pstmt = conn
					.prepareStatement(insertMerchantDevice);
			pstmt.setString(1, merchantDevice.getId());
			pstmt.setString(2, merchantDevice.getMerchantid());
			pstmt.setString(3, merchantDevice.getPosnum());
			pstmt.setString(4, merchantDevice.getDeviceNum());
			pstmt.setString(5, getNow());
			pstmt.setString(6, "1");
			int affected = pstmt.executeUpdate();
			pstmt.close();
			if (affected > 0)
				return "1";
		} finally {
			JdbcKit.close(conn, null, null);
		}
		return null;
	}

	public String updateMerchantDevice(MerchantDevice merchantDevice)
			throws SQLException {
		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			PreparedStatement pstmt = conn
					.prepareStatement(upsetMerchantDevice);
			pstmt.setString(1, merchantDevice.getPosnum());
			pstmt.setString(2, merchantDevice.getDeviceNum());
			int affected = pstmt.executeUpdate();
			pstmt.close();
			if (affected > 0)
				return "1";
		} finally {
			JdbcKit.close(conn, null, null);
		}
		return null;
	}

	public String suspendMerchantDevice(String id) throws SQLException {
		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			PreparedStatement pstmt = conn
					.prepareStatement(suspendMerchantDevice);
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

	public MerchantDevice merchantDevicebyid(String id) {
		MerchantDevice merchantDevice = new MerchantDevice();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			conn = dataSource.getConnection();
			pstmt = conn.prepareStatement(merchantDevicebyid);
			pstmt.setString(1, id);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				merchantDevice.setId(rs.getString(1));
				merchantDevice.setMerchantid(rs.getString(2));
				merchantDevice.setPosnum(rs.getString(3));
				merchantDevice.setDeviceNum(rs.getString(4));
				merchantDevice.setCreatetime(rs.getString(5));
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
		return merchantDevice;
	}

	public List<MerchantDevice> merchantDevicebymid(String mcode) {
		List<MerchantDevice> list = new ArrayList<MerchantDevice>();

		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			conn = dataSource.getConnection();
			pstmt = conn.prepareStatement(merchantDevicebymid);
			pstmt.setString(1, mcode);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				MerchantDevice merchantDevice = new MerchantDevice();
				merchantDevice.setId(rs.getString(1));
				merchantDevice.setMerchantid(rs.getString(2));
				merchantDevice.setPosnum(rs.getString(3));
				merchantDevice.setDeviceNum(rs.getString(4));
				merchantDevice.setCreatetime(rs.getString(5));
				list.add(merchantDevice);
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

	public List<Map<String, Object>> listMerchantDevice(
			Map<String, String> para, int start, int limit) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Connection conn = null;
		Statement pstmt = null;
		ResultSet rs = null;
		try {
			conn = dataSource.getConnection();
			StringBuffer sb = new StringBuffer(listMerchantDevice);
			if (!getStirng(para.get("merchantid")).equals(""))
				sb.append(" AND merchantid = '"
						+ getStirng(para.get("merchantid")) + "'  ");
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
				String posnum = rs.getString(3);
				String deviceNum = rs.getString(4);
				String createtime = rs.getString(5);
				Map<String, Object> cm = new HashMap<String, Object>();
				cm.put("id", id);
				cm.put("merchantid", merchantid);
				cm.put("posnum", posnum);
				cm.put("deviceNum", deviceNum);
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

	public int totalMerchantDevice(Map<String, String> para) {
		Connection conn = null;
		Statement pstmt = null;
		ResultSet rs = null;
		int count = 0;
		try {
			conn = dataSource.getConnection();
			StringBuffer sb = new StringBuffer(totalMerchantDevice);
			if (!getStirng(para.get("merchantid")).equals(""))
				sb.append(" AND merchantid = '"
						+ getStirng(para.get("merchantid")) + "'  ");
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

	public int totalMerchantDeviceBynum(String posnum, String merchantid) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		int count = 0;
		try {
			conn = dataSource.getConnection();
			pstmt = conn.prepareStatement(totalMerchantDeviceBynum);
			pstmt.setString(1, posnum);
			pstmt.setString(2, merchantid);

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
