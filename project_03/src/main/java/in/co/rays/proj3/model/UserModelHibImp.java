package in.co.rays.proj3.model;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

import in.co.rays.proj3.dto.UserDTO;
import in.co.rays.proj3.exception.ApplicationException;
import in.co.rays.proj3.exception.DuplicateRecordException;
import in.co.rays.proj3.exception.RecordNotFoundException;
import in.co.rays.proj3.util.EmailBuilder;
import in.co.rays.proj3.util.EmailMessage;
import in.co.rays.proj3.util.EmailUtility;
import in.co.rays.proj3.util.HibDataSource;

public class UserModelHibImp implements UserModelInt {

	@Override
	public long add(UserDTO dto) throws ApplicationException, DuplicateRecordException {

		Session session = HibDataSource.getSession();

		Transaction tx = null;

		UserDTO existDto = null;
		existDto = findByLogin(dto.getLogin());
		if (existDto != null) {
			throw new DuplicateRecordException("login id already exist");
		}

		try {
			long pk = 0;
			tx = session.beginTransaction();

			session.save(dto);

			tx.commit();

		} catch (HibernateException e) {
			e.printStackTrace();

			if (tx != null) {
				tx.rollback();

			}
			throw new ApplicationException("Exception in User Add " + e.getMessage());

		} finally {
			session.close();

		}

		return dto.getId();
	}

	@Override
	public void delete(UserDTO dto) throws ApplicationException {

		Session session = null;

		Transaction tx = null;

		try {

			session = HibDataSource.getSession();
			tx = session.beginTransaction();

			session.delete(dto);

			tx.commit();

		} catch (HibernateException e) {
			if (tx != null) {
				tx.rollback();
			}
			throw new ApplicationException("Exception in User Delete" + e.getMessage());
		} finally {
			session.close();
		}

	}

	@Override
	public void update(UserDTO dto) throws ApplicationException, DuplicateRecordException {

		Session session = null;
		Transaction tx = null;

		UserDTO existDto = findByLogin(dto.getLogin());
		// Check if updated LoginId already exist
		if (existDto != null && existDto.getId() != dto.getId()) {
			throw new DuplicateRecordException("LoginId is already exist");
		}

		try {
			session = HibDataSource.getSession();
			tx = session.beginTransaction();

			session.update(dto);

			tx.commit();

		} catch (HibernateException e) {

			if (tx != null) {
				tx.rollback();

			}
			throw new ApplicationException("Exception in User update" + e.getMessage());

		} finally {
			session.close();
		}

	}

	@Override
	public UserDTO findByPK(long pk) throws ApplicationException {

		Session session = null;

		UserDTO dto = null;

		try {
			session = HibDataSource.getSession();

			dto = (UserDTO) session.get(UserDTO.class, pk);

		} catch (HibernateException e) {
			e.printStackTrace();
			throw new ApplicationException("Exception : Exception in getting User by pk");

		} finally {
			session.close();
		}

		return dto;
	}

	@Override
	public UserDTO findByLogin(String login) throws ApplicationException {

		Session session = null;

		UserDTO dto = null;

		try {

			session = HibDataSource.getSession();
			Criteria criteria = session.createCriteria(UserDTO.class);
			criteria.add(Restrictions.eq("login", login));
			List list = criteria.list();
			if (list.size() == 1) {
				dto = (UserDTO) list.get(0);
			}

		} catch (HibernateException e) {
			e.printStackTrace();
			throw new ApplicationException("Exception : Exception in getting User by login");

		} finally {
			session.close();
		}

		return dto;
	}

	@Override
	public List list() throws ApplicationException {
		return list(0, 0);
	}

	@Override
	public List list(int pageNo, int pageSize) throws ApplicationException {

		Session session = null;

		List list = null;

		try {

			session = HibDataSource.getSession();
			Criteria criteria = session.createCriteria(UserDTO.class);

			if (pageSize > 0) {
				pageNo = (pageNo - 1) * pageSize;
				criteria.setFirstResult(pageNo);
				criteria.setMaxResults(pageSize);

				list = criteria.list();

			}

		} catch (HibernateException e) {
			e.printStackTrace();
			throw new ApplicationException("Exception : Exception in getting User by list ");
		} finally {
			session.close();
		}

		return list;
	}

	@Override
	public List search(UserDTO dto, int pageNo, int pageSize) throws ApplicationException {

		Session session = null;

		ArrayList<UserDTO> list = null;

		try {
			session = HibDataSource.getSession();
			Criteria criteria = session.createCriteria(UserDTO.class);

			if (dto != null) {

				if (dto.getId() != null) {
					criteria.add(Restrictions.like("id", dto.getId()));

				}
				if (dto.getFirstName() != null && dto.getFirstName().length() > 0) {
					criteria.add(Restrictions.ilike("firstName", dto.getFirstName() + "%"));

				}
				if (dto.getLastName() != null && dto.getLastName().length() > 0) {
					criteria.add(Restrictions.ilike("lastName", dto.getLastName() + "%"));

				}
				if (dto.getLogin() != null && dto.getLogin().length() > 0) {
					criteria.add(Restrictions.like("login", dto.getLogin() + "%"));
				}
				if (dto.getPassword() != null && dto.getPassword().length() > 0) {
					criteria.add(Restrictions.like("password", dto.getPassword() + "%"));
				}
				if (dto.getGender() != null && dto.getGender().length() > 0) {
					criteria.add(Restrictions.like("gender", dto.getGender() + "%"));
				}
				if (dto.getDob() != null && dto.getDob().getDate() > 0) {
					criteria.add(Restrictions.eq("dob", dto.getDob()));
				}
				if (dto.getLastLogin() != null && dto.getLastLogin().getTime() > 0) {
					criteria.add(Restrictions.eq("lastLogin", dto.getLastLogin()));
				}
				if (dto.getRoleId() > 0) {
					criteria.add(Restrictions.eq("roleId", dto.getRoleId()));
				}
				if (dto.getUnSuccessfullLogin() > 0) {
					criteria.add(Restrictions.eq("unSuccessfulLogin", dto.getUnSuccessfullLogin()));
				}
			}
			// if pageSize is greater than 0
			if (pageSize > 0) {
				pageNo = (pageNo - 1) * pageSize;
				criteria.setFirstResult(pageNo);
				criteria.setMaxResults(pageSize);
			}

			list = (ArrayList<UserDTO>) criteria.list();

		} catch (HibernateException e) {
			throw new ApplicationException("Exception : Exception in getting User by search ");

		} finally {
			session.close();
		}
		return list;
	}

	@Override
	public List search(UserDTO dto) throws ApplicationException {
		return search(dto, 0, 0);
	}

	@Override
	public boolean changePassword(long id, String newPassword, String oldPassword)
			throws ApplicationException, RecordNotFoundException {

		boolean flag = true;

		UserDTO dtoExist = null;

		dtoExist = findByPK(id);

		if (dtoExist != null && dtoExist.getPassword().equals(oldPassword)) {
			dtoExist.setPassword(newPassword);

			try {
				update(dtoExist);
			} catch (DuplicateRecordException e) {
				throw new ApplicationException("LoginId is lready exist");
			}
			flag = true;

		} else {
			throw new RecordNotFoundException("Login not exist");
		}

		HashMap<String, String> map = new HashMap<String, String>();

		map.put("logiin", dtoExist.getLogin());
		map.put("password", dtoExist.getPassword());
		map.put("firstName", dtoExist.getFirstName());
		map.put("lastName", dtoExist.getLastName());

		String message = EmailBuilder.getChangePasswordMessage(map);

		EmailMessage msg = new EmailMessage();

		msg.setTo(dtoExist.getLogin());
		msg.setSubject("Password has been changed Successfully.");
		msg.setMessage(message);
		msg.setMessageType(EmailMessage.HTML_MSG);

		EmailUtility.sendMail(msg);

		return flag;
	}

	@Override
	public UserDTO authenticate(String login, String password) throws ApplicationException {

		Session session = null;

		UserDTO dto = null;

		session = HibDataSource.getSession();

		Query q = session.createQuery("from UserDTO where login=? and password=?");

		q.setString(0, login);
		q.setString(1, password);

		List list = q.list();

		if (list.size() > 0) {

			dto = (UserDTO) list.get(0);

		} else {
			dto = null;

		}

		return dto;
	}

	@Override
	public boolean forgetPassword(String login) throws ApplicationException, RecordNotFoundException {

		UserDTO userData = findByLogin(login);

		boolean flag = true;

		if (userData == null) {
			throw new RecordNotFoundException("Password has been changed Successfully.");

		}
		HashMap<String, String> map = new HashMap<String, String>();

		map.put("login", login);
		map.put("password", userData.getPassword());
		map.put("firstName", userData.getFirstName());
		map.put("lastName", userData.getLastName());

		String message = EmailBuilder.getForgetPasswordMessage(map);

		EmailMessage msg = new EmailMessage();

		msg.setTo(login);
		msg.setSubject("SUNARYS ORS Password reset");
		msg.setMessage(message);
		msg.setMessageType(EmailMessage.HTML_MSG);

		EmailUtility.sendMail(msg);

		flag = true;

		return flag;

	}

	@Override
	public boolean resetPassword(UserDTO dto) throws ApplicationException, RecordNotFoundException {

		String newPassword = String.valueOf(new Date().getTime()).substring(0, 4);
		UserDTO userData = findByPK(dto.getId());
		userData.setPassword(newPassword);

		try {
			update(userData);

		} catch (DuplicateRecordException e) {
			return false;
		}

		HashMap<String, String> map = new HashMap<String, String>();
		map.put("login", dto.getLogin());
		map.put("password", dto.getPassword());

		String message = EmailBuilder.getForgetPasswordMessage(map);

		EmailMessage msg = new EmailMessage();

		msg.setTo(dto.getLogin());
		msg.setSubject("Password has been reset");
		msg.setMessage(message);
		msg.setMessageType(EmailMessage.HTML_MSG);

		EmailUtility.sendMail(msg);

		return true;
	}

	@Override
	public long registerUser(UserDTO dto) throws ApplicationException, DuplicateRecordException {

		long pk = add(dto);

		HashMap<String, String> map = new HashMap<String, String>();
		map.put("login", dto.getLogin());
		map.put("password", dto.getPassword());

		String message = EmailBuilder.getUserRegistrationMessage(map);

		EmailMessage msg = new EmailMessage();

		msg.setTo(dto.getLogin());
		msg.setSubject("Registration is successful for ORS Project SUNRAYS Technologies");
		msg.setMessage(message);
		msg.setMessageType(EmailMessage.HTML_MSG);

		EmailUtility.sendMail(msg);

		return pk;
	}

	@Override
	public List getRoles(UserDTO dto) throws ApplicationException {

		return null;

	}

}
