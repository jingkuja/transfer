/**
 *  2013-6-3  下午3:07:47  MerchantDevice.java
 */
package org.aves.transfer.bean;

/**
 * @author nikin
 * 
 */
public class MerchantDevice {
	private String id;
	//商户编号
	private String merchantid;
	//pos标识号
	private String posnum;
	//保留编号
	private String deviceNum;	
	private String createtime;
	// 0 停用  1 启用  2注销
	private String status;

	/**
	 * @return the merchantid
	 */
	public String getMerchantid() {
		return merchantid;
	}

	/**
	 * @param merchantid
	 *            the merchantid to set
	 */
	public void setMerchantid(String merchantid) {
		this.merchantid = merchantid;
	}

	/**
	 * @return the posnum
	 */
	public String getPosnum() {
		return posnum;
	}

	/**
	 * @param posnum
	 *            the posnum to set
	 */
	public void setPosnum(String posnum) {
		this.posnum = posnum;
	}

	/**
	 * @return the deviceNum
	 */
	public String getDeviceNum() {
		return deviceNum;
	}

	/**
	 * @param deviceNum
	 *            the deviceNum to set
	 */
	public void setDeviceNum(String deviceNum) {
		this.deviceNum = deviceNum;
	}

	/**
	 * @return the createtime
	 */
	public String getCreatetime() {
		return createtime;
	}

	/**
	 * @param createtime
	 *            the createtime to set
	 */
	public void setCreatetime(String createtime) {
		this.createtime = createtime;
	}

	
	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @param status
	 *            the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "MerchantDevice [merchantid=" + merchantid + ", posnum="
				+ posnum + ", deviceNum=" + deviceNum+", createtime=" + createtime + ", status=" + status + "]";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((deviceNum == null) ? 0 : deviceNum.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result
				+ ((merchantid == null) ? 0 : merchantid.hashCode());
		result = prime * result + ((posnum == null) ? 0 : posnum.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MerchantDevice other = (MerchantDevice) obj;
		if (deviceNum == null) {
			if (other.deviceNum != null)
				return false;
		} else if (!deviceNum.equals(other.deviceNum))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (merchantid == null) {
			if (other.merchantid != null)
				return false;
		} else if (!merchantid.equals(other.merchantid))
			return false;
		if (posnum == null) {
			if (other.posnum != null)
				return false;
		} else if (!posnum.equals(other.posnum))
			return false;
		return true;
	}

}
