/**
 *  2013-5-29  上午11:12:08  AuthServer.java
 */
package org.aves.transfer.imp;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.aves.transfer.AuthServerException;
import org.aves.transfer.api.IAuthServer;
import org.aves.transfer.bean.Account;

/**
 * @author nikin
 * 
 */
public class AuthServer implements IAuthServer {

	private AuthRepository authRepository;

	public void setAuthRepository(AuthRepository authRepository) {
		this.authRepository = authRepository;
	}

	@Override
	public String nextSequenceNumberForset() {
		try {
			Integer id = authRepository.nextSequenceNumber();
			if (null == id) {
				authRepository.initSequenceNumber();
				id = 1000;
			} else {
				authRepository.updateSequenceNumber(id + 1);
			}
			return String.format("%d", id);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public Account createAccount(Account acount) throws AuthServerException {
		try {
			Account account = acount;
			account.setStatus("1");
			account.setId(nextSequenceNumberForset());
			Date date = new Date();
			SimpleDateFormat sm = new SimpleDateFormat("yyyy-MM-dd");
			account.setCreatetime(sm.format(date));
			Account accountd = authRepository.Authbyacct(acount.getAccount());
			if (accountd.getId() != null) {
				throw new AuthServerException("该账号已被使用，请检查修改!");
			}
			authRepository.insertAuth(account);
			return account;
		} catch (Exception e) {
			e.printStackTrace();
			String msg = "该账号已被使用，请检查修改!";
			if (msg.equals(e.getMessage()))
				throw new AuthServerException(msg);
			else
				throw new AuthServerException("创建账号失败!");
		}
	}

	@Override
	public void ResetAccount(Account acount) throws AuthServerException {
		try {
			authRepository.resetAuth(acount.getPassword(), acount.getId());
		} catch (Exception e) {
			e.printStackTrace();
			throw new AuthServerException("重置密码出错!");
		}

	}

	@Override
	public List<Map<String, Object>> listAccounts(
			Map<String, String> condition, int start, int limit)
			throws AuthServerException {
		try {
			List<Map<String, Object>> ml = authRepository.listAuth(condition,
					start, limit);
			return ml;
		} catch (Exception e) {
			e.printStackTrace();
			throw new AuthServerException("获取账号列表失败!");
		}
	}

	@Override
	public int totalAccounts(Map<String, String> condition)
			throws AuthServerException {
		try {
			int i = authRepository.totalAuth(condition);
			return i;
		} catch (Exception e) {
			e.printStackTrace();
			throw new AuthServerException("获取总数失败!");
		}
	}

	@Override
	public Account AccountByAcct(String account) throws AuthServerException {
		try {
			Account acct = authRepository.Authbyacct(account);
			return acct;
		} catch (Exception e) {
			e.printStackTrace();
			throw new AuthServerException("获取账号信息出错!");
		}
	}

	@Override
	public Account AccountById(String id) throws AuthServerException {
		try {
			Account acct = authRepository.Authbyid(id);
			return acct;
		} catch (Exception e) {
			e.printStackTrace();
			throw new AuthServerException("获取账号信息出错!");
		}
	}

	@Override
	public void suspendAccount(Account account) throws AuthServerException {
		try {
			authRepository.suspendMerchant(account.getId());
		} catch (Exception e) {
			e.printStackTrace();
			throw new AuthServerException("涨停账号信息出错!");
		}

	}

}
