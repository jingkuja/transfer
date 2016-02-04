/**
 *  2013-6-8  下午4:51:42  Bill.java
 */
package org.aves.transfer.bean;

/**
 * @author nikin
 * 
 */
public class Bill {
	private String id;
	// 商户编号
	private String merid;
	// pos机编号
	private String terminal;
	// 交易序号
	private String postradeNo;
	// 交易时间
	private String tradetime;
	// 交易类型 pay refund
	private String tradetype;
	// 金额
	private String sum;

	// 商户手续费
	private String mfee;

	// 支付手续费
	private String fee;

	// 当时所办费率
	private String rate;

	// 当时所办支付宝费率
	private String ratea;

	// 收入
	private String income;

	// 划给商户金额
	private String transfermer;

	// 0清分未成功 1，清分成功
	private String status;

	// 失败简介
	private String sdesc;

	private String paysrvCode;

	private String createtime;

	public String getPaysrvCode() {
		return paysrvCode;
	}

	public void setPaysrvCode(String paysrvCode) {
		this.paysrvCode = paysrvCode;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getSdesc() {
		return sdesc;
	}

	public void setSdesc(String sdesc) {
		this.sdesc = sdesc;
	}

	/**
	 * @return the rate
	 */
	public String getRate() {
		return rate;
	}

	/**
	 * @param rate
	 *            the rate to set
	 */
	public void setRate(String rate) {
		this.rate = rate;
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
	 * @return the merid
	 */
	public String getMerid() {
		return merid;
	}

	/**
	 * @param merid
	 *            the merid to set
	 */
	public void setMerid(String merid) {
		this.merid = merid;
	}

	/**
	 * @return the transfermer
	 */
	public String getTransfermer() {
		return transfermer;
	}

	/**
	 * @param transfermer
	 *            the transfermer to set
	 */
	public void setTransfermer(String transfermer) {
		this.transfermer = transfermer;
	}

	/**
	 * @return the createtdate
	 */
	public String getCreatetime() {
		return createtime;
	}

	/**
	 * @param createtdate
	 *            the createtdate to set
	 */
	public void setCreatetime(String createtime) {
		this.createtime = createtime;
	}

	/**
	 * @return the terminal
	 */
	public String getTerminal() {
		return terminal;
	}

	/**
	 * @param terminal
	 *            the terminal to set
	 */
	public void setTerminal(String terminal) {
		this.terminal = terminal;
	}

	/**
	 * @return the tradetime
	 */
	public String getTradetime() {
		return tradetime;
	}

	/**
	 * @param tradetime
	 *            the tradetime to set
	 */
	public void setTradetime(String tradetime) {
		this.tradetime = tradetime;
	}

	/**
	 * @return the tradetype
	 */
	public String getTradetype() {
		return tradetype;
	}

	/**
	 * @param tradetype
	 *            the tradetype to set
	 */
	public void setTradetype(String tradetype) {
		this.tradetype = tradetype;
	}

	/**
	 * @return the sum
	 */
	public String getSum() {
		return sum;
	}

	/**
	 * @param sum
	 *            the sum to set
	 */
	public void setSum(String sum) {
		this.sum = sum;
	}

	public String getRatea() {
		return ratea;
	}

	public void setRatea(String ratea) {
		this.ratea = ratea;
	}

	/**
	 * @return the fee
	 */
	public String getFee() {
		return fee;
	}

	/**
	 * @param fee
	 *            the fee to set
	 */
	public void setFee(String fee) {
		this.fee = fee;
	}

	/**
	 * @return the postradeNo
	 */
	public String getPostradeNo() {
		return postradeNo;
	}

	/**
	 * @param postradeNo
	 *            the postradeNo to set
	 */
	public void setPostradeNo(String postradeNo) {
		this.postradeNo = postradeNo;
	}

	/**
	 * @return the mfee
	 */
	public String getMfee() {
		return mfee;
	}

	/**
	 * @param mfee
	 *            the mfee to set
	 */
	public void setMfee(String mfee) {
		this.mfee = mfee;
	}

	/**
	 * @return the income
	 */
	public String getIncome() {
		return income;
	}

	/**
	 * @param income
	 *            the income to set
	 */
	public void setIncome(String income) {
		this.income = income;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Billitem [id=" + id + ", merid=" + merid + ", transfermer="
				+ transfermer + ", mfee=" + mfee + ", rate=" + rate
				+ ", income=" + income + ", createtime=" + createtime
				+ ", terminal=" + terminal + "]";
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
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((merid == null) ? 0 : merid.hashCode());
		result = prime * result
				+ ((postradeNo == null) ? 0 : postradeNo.hashCode());
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
		Bill other = (Bill) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (merid == null) {
			if (other.merid != null)
				return false;
		} else if (!merid.equals(other.merid))
			return false;
		if (postradeNo == null) {
			if (other.postradeNo != null)
				return false;
		} else if (!postradeNo.equals(other.postradeNo))
			return false;
		return true;
	}

}
