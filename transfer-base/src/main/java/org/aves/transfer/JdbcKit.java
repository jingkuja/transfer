/**
 * JdbcKit.java
 * Created by Steven Chen on Dec 19, 2012 9:15:03 PM
 * Copyright (c) 2012 Passplie Co.,Ltd. All rights reserved.
 */
package org.aves.transfer;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

/**
 * @author Steven Chen
 * 
 */
public abstract class JdbcKit {

	public static void commit(Connection conn) {
		try {
			if (conn != null) {
				conn.commit();
			}
		} catch (SQLException e) {
		}
	}

	public static void rollback(Connection conn) {
		try {
			if (conn != null) {
				conn.rollback();
			}
		} catch (SQLException e) {
		}
	}

	public static void close(Connection conn, PreparedStatement pstmt,
			ResultSet rs) {
		close(rs);
		close(pstmt);
		close(conn);
	}

	public static void close(ResultSet rs) {
		try {
			if (rs != null) {
				rs.close();
			}
		} catch (SQLException e) {
		}

	}

	public static void close(PreparedStatement pstmt) {
		try {
			if (pstmt != null) {
				pstmt.close();
			}
		} catch (SQLException e) {
		}

	}

	public static void close(Connection conn) {
		try {
			if (conn != null) {
				conn.setAutoCommit(true);
				conn.close();
			}
		} catch (SQLException e) {
		}

	}

	public static java.sql.Timestamp toSQLTimestamp(Date date) {
		if (date == null)
			return null;
		return new java.sql.Timestamp(date.getTime());
	}

	public static java.sql.Date toSQLDate(Date date) {
		if (date == null)
			return null;
		return new java.sql.Date(date.getTime());
	}

	public static Boolean getIntegerAsBoolean(ResultSet rs, int index)
			throws SQLException {
		Integer value = rs.getInt(index);
		if (rs.wasNull())
			return false;
		if (value == 0)
			return false;
		return true;
	}

	public static void setInteger(PreparedStatement pstmt, int index,
			Integer value) throws SQLException {
		if (value == null)
			pstmt.setNull(index, java.sql.Types.INTEGER);
		else
			pstmt.setInt(index, value);
	}

	public static void setDouble(PreparedStatement pstmt, int index,
			Double value) throws SQLException {
		if (value == null)
			pstmt.setNull(index, java.sql.Types.DOUBLE);
		else
			pstmt.setDouble(index, value);
	}

	public static void setLong(PreparedStatement pstmt, int index, Long value)
			throws SQLException {
		if (value == null)
			pstmt.setNull(index, java.sql.Types.INTEGER);
		else
			pstmt.setLong(index, value);
	}

	public static void setBigDecimal(PreparedStatement pstmt, int index,
			BigDecimal value) throws SQLException {
		if (value == null)
			pstmt.setNull(index, java.sql.Types.NUMERIC);
		else
			pstmt.setBigDecimal(index, value);
	}

}
