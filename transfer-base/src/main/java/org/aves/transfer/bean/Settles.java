/**
 *  2013-6-4  下午3:22:33  Settles.java
 */
package org.aves.transfer.bean;

/**
 * @author nikin
 * 
 */
public class Settles {
	private String id;
	// 商户编号
	private String merchantid;
	private String contractNum;
	// 商户费率
	private String rebate;
	// 支付宝费率
	private String rebatea;
	private String signer;

	private String title;
	private String paysrvCode;
	private String paysrvAN;
	private String paysrvAltAN;
	private String paysrvKey;
	private String paysrvAgent;

	private String excon;

	private String approver;
	private String approvetime;
	private String starttime;
	private String endtime;
	// 0 废弃 1 暂停执行 2激活状态 3 待核准
	private String status;
	private String createtime;

	/**
	 * @return the rebatea
	 */
	public String getRebatea() {
		return rebatea;
	}

	/**
	 * @param rebatea
	 *            the rebatea to set
	 */
	public void setRebatea(String rebatea) {
		this.rebatea = rebatea;
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

	/**
	 * @return the merchantid
	 */
	public String getMerchantid() {
		return merchantid;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getPaysrvCode() {
		return paysrvCode;
	}

	public void setPaysrvCode(String paysrvCode) {
		this.paysrvCode = paysrvCode;
	}

	public String getExcon() {
		return excon;
	}

	public void setExcon(String excon) {
		this.excon = excon;
	}

	public String getPaysrvAN() {
		return paysrvAN;
	}

	public void setPaysrvAN(String paysrvAN) {
		this.paysrvAN = paysrvAN;
	}

	public String getPaysrvAltAN() {
		return paysrvAltAN;
	}

	public void setPaysrvAltAN(String paysrvAltAN) {
		this.paysrvAltAN = paysrvAltAN;
	}

	public String getPaysrvKey() {
		return paysrvKey;
	}

	public void setPaysrvKey(String paysrvKey) {
		this.paysrvKey = paysrvKey;
	}

	public String getPaysrvAgent() {
		return paysrvAgent;
	}

	public void setPaysrvAgent(String paysrvAgent) {
		this.paysrvAgent = paysrvAgent;
	}

	/**
	 * @param merchantid
	 *            the merchantid to set
	 */
	public void setMerchantid(String merchantid) {
		this.merchantid = merchantid;
	}

	/**
	 * @return the contractNum
	 */
	public String getContractNum() {
		return contractNum;
	}

	/**
	 * @param contractNum
	 *            the contractNum to set
	 */
	public void setContractNum(String contractNum) {
		this.contractNum = contractNum;
	}

	/**
	 * @return the rebate
	 */
	public String getRebate() {
		return rebate;
	}

	/**
	 * @param rebate
	 *            the rebate to set
	 */
	public void setRebate(String rebate) {
		this.rebate = rebate;
	}

	/**
	 * @return the signer
	 */
	public String getSigner() {
		return signer;
	}

	/**
	 * @param signer
	 *            the signer to set
	 */
	public void setSigner(String signer) {
		this.signer = signer;
	}

	/**
	 * @return the approver
	 */
	public String getApprover() {
		return approver;
	}

	/**
	 * @param approver
	 *            the approver to set
	 */
	public void setApprover(String approver) {
		this.approver = approver;
	}

	/**
	 * @return the approvetime
	 */
	public String getApprovetime() {
		return approvetime;
	}

	/**
	 * @param approvetime
	 *            the approvetime to set
	 */
	public void setApprovetime(String approvetime) {
		this.approvetime = approvetime;
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
	 * @return the starttime
	 */
	public String getStarttime() {
		return starttime;
	}

	/**
	 * @param starttime
	 *            the starttime to set
	 */
	public void setStarttime(String starttime) {
		this.starttime = starttime;
	}

	/**
	 * @return the endtime
	 */
	public String getEndtime() {
		return endtime;
	}

	/**
	 * @param endtime
	 *            the endtime to set
	 */
	public void setEndtime(String endtime) {
		this.endtime = endtime;
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Settles [id=" + id + ", merchantid=" + merchantid
				+ ", contractNum=" + contractNum + ", rebate=" + rebate
				+ ", signer=" + signer + ", approver=" + approver
				+ ", approvetime=" + approvetime + ", createtime=" + createtime
				+ ", starttime=" + starttime + ", endtime=" + endtime
				+ ", status=" + status + "]";
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
				+ ((contractNum == null) ? 0 : contractNum.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result
				+ ((merchantid == null) ? 0 : merchantid.hashCode());
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
		Settles other = (Settles) obj;
		if (contractNum == null) {
			if (other.contractNum != null)
				return false;
		} else if (!contractNum.equals(other.contractNum))
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
		return true;
	}

}
