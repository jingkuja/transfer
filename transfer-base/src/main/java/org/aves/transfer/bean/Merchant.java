/**
 * 
 */
package org.aves.transfer.bean;

/**
 * @author kuja
 * 
 */
public class Merchant {
	private String id;
	// 商户名称
	private String mname;
	// 商户编号
	private String mcode;
	// 经营地址
	private String address;
	// 联系人
	private String contactor;
	// 联系电话
	private String phone;
	// 转账开户银行
	private String kfbank;
	// 转账开户银行账号
	private String kfbaacount;

	// 转账开户银行账户名
	private String accountname;

	// 转账模板 0 公对公 1 代发代扣
	private String tplate;
	
	// 1 行内公对公 0 其他
	private String transign;

	// 创建时间
	private String createtime;
	
	// 创建时间
	private String status;
	

	private String email;
	
	
	
	

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the transign
	 */
	public String getTransign() {
		return transign;
	}

	/**
	 * @param transign
	 *            the transign to set
	 */
	public void setTransign(String transign) {
		this.transign = transign;
	}

	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @param email
	 *            the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	public String getMname() {
		return mname;
	}

	public void setMname(String mname) {
		this.mname = mname;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getContactor() {
		return contactor;
	}

	public void setContactor(String contactor) {
		this.contactor = contactor;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getKfbank() {
		return kfbank;
	}

	public void setKfbank(String kfbank) {
		this.kfbank = kfbank;
	}

	public String getKfbaacount() {
		return kfbaacount;
	}

	public void setKfbaacount(String kfbaacount) {
		this.kfbaacount = kfbaacount;
	}

	public String getMcode() {
		return mcode;
	}

	public void setMcode(String mcode) {
		this.mcode = mcode;
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
	 * @return the acountname
	 */
	public String getAccountname() {
		return accountname;
	}

	/**
	 * @param acountname
	 *            the acountname to set
	 */
	public void setAccountname(String acountname) {
		this.accountname = acountname;
	}

	/**
	 * @return the tplate
	 */
	public String getTplate() {
		return tplate;
	}

	/**
	 * @param tplate
	 *            the tplate to set
	 */
	public void setTplate(String tplate) {
		this.tplate = tplate;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Merchant [mname=" + mname + ", mcode=" + mcode + ", address="
				+ address + ", contactor=" + contactor + ", phone=" + phone
				+ ", kfbank=" + kfbank + ", kfbaacount=" + kfbaacount
				+ ", createtime=" + createtime + "]";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((mcode == null) ? 0 : mcode.hashCode());
		result = prime * result + ((mname == null) ? 0 : mname.hashCode());
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
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		Merchant other = (Merchant) obj;
		if (mcode == null) {
			if (other.mcode != null)
				return false;
		} else if (!mcode.equals(other.mcode))
			return false;
		if (mname == null) {
			if (other.mname != null)
				return false;
		} else if (!mname.equals(other.mname))
			return false;
		return true;
	}

}
