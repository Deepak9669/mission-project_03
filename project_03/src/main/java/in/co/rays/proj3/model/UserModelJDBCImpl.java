package in.co.rays.proj3.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.mysql.cj.protocol.Resultset;
import com.mysql.cj.xdevapi.PreparableStatement;

import in.co.rays.proj3.dto.UserDTO;
import in.co.rays.proj3.exception.ApplicationException;
import in.co.rays.proj3.exception.DatabaseException;
import in.co.rays.proj3.exception.DuplicateRecordException;
import in.co.rays.proj3.exception.RecordNotFoundException;
import in.co.rays.proj3.util.JDBCDataSource;

public class UserModelJDBCImpl implements UserModelInt {

	public long nextPk() throws DatabaseException {

		Connection con = null;

		long pk = 0;

		try {

			con = JDBCDataSource.getConnection();
			PreparedStatement ps = con.prepareStatement("select max(id) from st_USER");
			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				pk = rs.getLong(1);
			}

		} catch (Exception e) {
			throw new DatabaseException("Database Exception" + e);
		} finally {
			JDBCDataSource.closeConnection(con);

		}

		return pk + 1;

	}

	@Override
	public long add(UserDTO dto) throws ApplicationException, DuplicateRecordException {

		Connection con = null;

		long pk = 0;

		try {
			con = JDBCDataSource.getConnection();
			con.setAutoCommit(false);
			pk = nextPk();
			PreparedStatement ps = con
					.prepareStatement("insert into ST_USERdto values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
			ps.setLong(1, pk);
			ps.setString(2, dto.getFirstName());
			ps.setString(3, dto.getLastName());
			ps.setString(4, dto.getGender());
			ps.setLong(5, dto.getRoleId());
			ps.setDate(6, new java.sql.Date(dto.getDob().getTime()));
			ps.setString(7, dto.getMobileNo());
			ps.setString(8, dto.getLogin());
			ps.setString(9, dto.getPassword());
			ps.setString(10, dto.getConfirmPassword());
			ps.setTimestamp(11, dto.getLastLogin());
			ps.setInt(12, dto.getUnSuccessfullLogin());
			ps.setString(13, dto.getLoginIP());
			ps.setString(14, dto.getRegisteredIP());
			ps.setString(15, dto.getCreatedBy());
			ps.setString(16, dto.getModifiedBy());
			ps.setTimestamp(17, dto.getCreatedDatetime());
			ps.setTimestamp(18, dto.getModifiedDatetime());
			ps.executeUpdate();

			con.commit();
			con.close();

		} catch (Exception e) {
			e.printStackTrace();
			try {
				con.rollback();

			} catch (Exception e2) {
				e2.printStackTrace();
				e.printStackTrace();
				throw new ApplicationException("exception: add rollback exception:" + e2.getMessage());

			}
			throw new ApplicationException("Exception : Exception in add User");
		} finally {
			JDBCDataSource.closeConnection(con);
		}
		return pk;
	}

	@Override
	public void delete(UserDTO dto) throws ApplicationException {
		Connection con = null;
		try {
			con = JDBCDataSource.getConnection();
			con.setAutoCommit(false);
			PreparedStatement ps = con.prepareStatement("delete from ST_USER where id=?");
			ps.setLong(1, dto.getId());
			ps.executeUpdate();
			con.commit();
			ps.close();

		} catch (Exception e) {
			try {
				con.rollback();
			} catch (Exception ex) {
				throw new ApplicationException("Exception : Delete rollback exception " + ex.getMessage());
			}
			throw new ApplicationException("Exception : Exception in delete User");
		} finally {
			JDBCDataSource.closeConnection(con);
		}

	}

	@Override
	public void update(UserDTO dto) throws ApplicationException, DuplicateRecordException {
		Connection con = null;
		PreparedStatement ps = null;
		UserDTO dtoExist = findByLogin(dto.getLogin());
		// Check if updated LoginId already exist
		if (dtoExist != null && !(dtoExist.getId() == dto.getId())) {
			throw new DuplicateRecordException("LoginId is already exist");
		}
		try {
			con = JDBCDataSource.getConnection();
			ps = con.prepareStatement(
					"update ST_USER set FIRST_NAME=?,LAST_NAME=?,LOGIN=?,PASSWORD=?,CONFIRMPASSWORD=?,DOB=?,MOBILE_NO=?,ROLE_ID=?,UNSUCCESSFUL_LOGIN=?,GENDER=?,LAST_LOGIN=?,REGISTERED_IP=?,LAST_LOGIN_IP=?,CREATED_BY=?,MODIFIED_BY=?,CREATED_DATETIME=?,MODIFIED_DATETIME=? WHERE ID=?");

			ps.setString(1, dto.getFirstName());
			ps.setString(2, dto.getLastName());
			ps.setString(3, dto.getLogin());
			ps.setString(4, dto.getPassword());
			ps.setString(5, dto.getConfirmPassword());
			ps.setDate(6, new java.sql.Date(dto.getDob().getTime()));
			ps.setString(7, dto.getMobileNo());
			ps.setLong(8, dto.getRoleId());
			ps.setInt(9, dto.getUnSuccessfullLogin());
			ps.setString(10, dto.getGender());
			ps.setTimestamp(11, dto.getLastLogin());
			ps.setString(12, dto.getRegisteredIP());
			ps.setString(13, dto.getLoginIP());
			ps.setString(14, dto.getCreatedBy());
			ps.setString(15, dto.getModifiedBy());
			ps.setTimestamp(16, dto.getCreatedDatetime());
			ps.setTimestamp(17, dto.getModifiedDatetime());
			ps.setLong(18, dto.getId());
			ps.executeUpdate();
			con.commit();
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
			try {
				con.rollback();
			} catch (Exception ex) {
				throw new ApplicationException("Exception : Delete rollback exception " + ex.getMessage());
			}
			throw new ApplicationException("Exception in updating User ");
		} finally {
			JDBCDataSource.closeConnection(con);
		}

	}

	@Override
	public UserDTO findByPK(long pk) throws ApplicationException {
		Connection con = null;
		PreparedStatement ps = null;
		UserDTO dto = null;
		try {
			con = JDBCDataSource.getConnection();
			ps = con.prepareStatement("select * from ST_USER where id=?");
			ps.setLong(1, pk);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				dto = new UserDTO();
				dto.setId(rs.getLong(1));
				dto.setFirstName(rs.getString(2));
				dto.setLastName(rs.getString(3));
				dto.setGender(rs.getString(4));
				dto.setRoleId(rs.getLong(5));
				dto.setDob(rs.getDate(6));
				dto.setMobileNo(rs.getString(7));
				dto.setLogin(rs.getString(8));
				dto.setPassword(rs.getString(9));
				dto.setConfirmPassword(rs.getString(10));
				dto.setLastLogin(rs.getTimestamp(11));
				dto.setUnSuccessfullLogin(rs.getInt(12));
				dto.setLoginIP(rs.getString(13));
				dto.setRegisteredIP(rs.getString(14));
				dto.setCreatedBy(rs.getString(15));
				dto.setModifiedBy(rs.getString(16));
				dto.setCreatedDatetime(rs.getTimestamp(17));
				dto.setModifiedDatetime(rs.getTimestamp(18));
			}
			rs.close();
		} catch (Exception e) {
			e.printStackTrace();
			throw new ApplicationException("Exception : Exception in getting User by pk");
		} finally {
			JDBCDataSource.closeConnection(con);
		}
		return dto;
	}

	@Override
	public UserDTO findByLogin(String login) throws ApplicationException {
		Connection con = null;
		PreparedStatement ps = null;
		UserDTO dto = null;
		try {
			con = JDBCDataSource.getConnection();
			ps = con.prepareStatement("select * from ST_USER where LOGIN=?");
			ps.setString(1, login);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				dto = new UserDTO();
				dto.setId(rs.getLong(1));
				dto.setFirstName(rs.getString(2));
				dto.setLastName(rs.getString(3));
				dto.setGender(rs.getString(4));
				dto.setRoleId(rs.getLong(5));
				dto.setDob(rs.getDate(6));
				dto.setMobileNo(rs.getString(7));
				dto.setLogin(rs.getString(8));
				dto.setPassword(rs.getString(9));
				dto.setConfirmPassword(rs.getString(10));
				dto.setLastLogin(rs.getTimestamp(11));
				dto.setUnSuccessfullLogin(rs.getInt(12));
				dto.setLoginIP(rs.getString(13));
				dto.setRegisteredIP(rs.getString(14));
				dto.setCreatedBy(rs.getString(15));
				dto.setModifiedBy(rs.getString(16));
				dto.setCreatedDatetime(rs.getTimestamp(17));
				dto.setModifiedDatetime(rs.getTimestamp(18));
			}
			rs.close();
		} catch (Exception e) {
			e.printStackTrace();
			throw new ApplicationException("Exception : Exception in getting User by login");
		} finally {
			JDBCDataSource.closeConnection(con);
		}

		return dto;
	}

	@Override
	public List list() throws ApplicationException {
		return list(0, 0);
	}

	@Override
	public List list(int pageNo, int pageSize) throws ApplicationException {
		Connection con = null;
		PreparedStatement ps = null;
		ArrayList array = null;
		UserDTO dto = null;
		StringBuffer sql = new StringBuffer("select * from ST_USER where 1=1");
		if (pageSize > 0) {
			pageNo = (pageNo - 1) * pageSize;
			sql.append("limit" + pageNo + "," + pageSize);
		}
		try {
			con = JDBCDataSource.getConnection();
			ps = con.prepareStatement(sql.toString());
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				dto = new UserDTO();
				dto.setId(rs.getLong(1));
				dto.setFirstName(rs.getString(2));
				dto.setLastName(rs.getString(3));
				dto.setGender(rs.getString(4));
				dto.setRoleId(rs.getLong(5));
				dto.setDob(rs.getDate(6));
				dto.setMobileNo(rs.getString(7));
				dto.setLogin(rs.getString(8));
				dto.setPassword(rs.getString(9));
				dto.setConfirmPassword(rs.getString(10));
				dto.setLastLogin(rs.getTimestamp(11));
				dto.setUnSuccessfullLogin(rs.getInt(12));
				dto.setLoginIP(rs.getString(13));
				dto.setRegisteredIP(rs.getString(14));
				dto.setCreatedBy(rs.getString(15));
				dto.setModifiedBy(rs.getString(16));
				dto.setCreatedDatetime(rs.getTimestamp(17));
				dto.setModifiedDatetime(rs.getTimestamp(18));
				array.add(dto);
			}
			rs.close();
		} catch (Exception e) {
			throw new ApplicationException("Exception : Exception in getting list of users");
		} finally {
			JDBCDataSource.closeConnection(con);
		}

		return array;
	}

	@Override
	public List search(UserDTO dto, int pageNo, int pageSize) throws ApplicationException {
		
		Connection con = null;
		PreparedStatement ps = null;
		ArrayList array = null;
		StringBuffer sql = new StringBuffer("select * from ST_USER where 1=1");
		if (dto != null) {
			if (dto.getId() > 0) {
				sql.append(" AND ID = " + dto.getId());
			}
			if (dto.getFirstName() != null && dto.getFirstName().length() > 0) {
				sql.append(" AND FIRSTNAME like '" + dto.getFirstName() + "%'");
			}
			if (dto.getLastName() != null && dto.getLastName().length() > 0) {
				sql.append(" AND LASTNAME like '" + dto.getLastName() + "%'");
			}
			if (dto.getLogin() != null && dto.getLogin().length() > 0) {
				sql.append(" AND LOGIN like '" + dto.getLogin() + "%'");
			}
			if (dto.getPassword() != null && dto.getPassword().length() > 0) {
				sql.append(" AND PASSWORD like '" + dto.getPassword() + "%'");
			}
			if (dto.getDob() != null && dto.getDob().getDate() > 0) {
				sql.append(" AND DOB = " + dto.getGender());
			}
			if (dto.getMobileNo() != null && dto.getMobileNo().length() > 0) {
				sql.append(" AND MOBILENO = " + dto.getMobileNo());
			}
			if (dto.getRoleId() > 0) {
				sql.append(" AND ROLEID = " + dto.getRoleId());
			}
			if (dto.getUnSuccessfullLogin() > 0) {
				sql.append(" AND UNSUCCESSFULLOGIN = " + dto.getUnSuccessfullLogin());
			}
			if (dto.getGender() != null && dto.getGender().length() > 0) {
				sql.append(" AND GENDER like '" + dto.getGender() + "%'");
			}
			if (dto.getLastLogin() != null && dto.getLastLogin().getTime() > 0) {
				sql.append(" AND LASTLOGIN = " + dto.getLastLogin());
			}
			if (dto.getRegisteredIP() != null && dto.getRegisteredIP().length() > 0) {
				sql.append(" AND REGISTEREDIP like '" + dto.getRegisteredIP() + "%'");
			}
			if (dto.getLoginIP() != null && dto.getLoginIP().length() > 0) {
				sql.append(" AND LOGINIP like '" + dto.getLoginIP() + "%'");
			}
		}
		if (pageSize > 0) {
			pageNo = (pageNo - 1) * pageSize;
			sql.append("limit" + pageNo + "," + pageSize);
		}
		array = new ArrayList();
		try {
			con = JDBCDataSource.getConnection();
			ps = con.prepareStatement(sql.toString());
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				dto = new UserDTO();
				dto.setId(rs.getLong(1));
				dto.setFirstName(rs.getString(2));
				dto.setLastName(rs.getString(3));
				dto.setGender(rs.getString(4));
				dto.setRoleId(rs.getLong(5));
				dto.setDob(rs.getDate(6));
				dto.setMobileNo(rs.getString(7));
				dto.setLogin(rs.getString(8));
				dto.setPassword(rs.getString(9));
				dto.setConfirmPassword(rs.getString(10));
				dto.setLastLogin(rs.getTimestamp(11));	
				dto.setUnSuccessfullLogin(rs.getInt(12));	
				dto.setLoginIP(rs.getString(13));
				dto.setRegisteredIP(rs.getString(14));
				dto.setCreatedBy(rs.getString(15));
				dto.setModifiedBy(rs.getString(16));
				dto.setCreatedDatetime(rs.getTimestamp(17));
				dto.setModifiedDatetime(rs.getTimestamp(18));
				array.add(dto);
			}
			rs.close();

		} catch (Exception e) {
			throw new ApplicationException("Exception : Exception in search user");
		} finally {
			JDBCDataSource.closeConnection(con);
		}


		return array;
	}

	

	@Override
	public List search(UserDTO dto) throws ApplicationException {

		return search(dto, 0, 0);
	}
	

	@Override
	public boolean changePassword(long id, String newPassword, String oldPassword)
			throws ApplicationException, RecordNotFoundException {
		return false;
	}

	@Override
	public UserDTO authenticate(String login, String password) throws ApplicationException {
		UserDTO dto = null;
		Connection con = null;
		StringBuffer sql = new StringBuffer("select * from ST_USER where login=? and password=?");
		try {
			con = JDBCDataSource.getConnection();
			PreparedStatement pstmt = con.prepareStatement(sql.toString());
			pstmt.setString(1, login);
			pstmt.setString(2, password);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				dto = new UserDTO();
				dto.setId(rs.getLong(1));
				dto.setFirstName(rs.getString(2));
				dto.setLastName(rs.getString(3));
				dto.setGender(rs.getString(4));
				dto.setRoleId(rs.getLong(5));
				dto.setDob(rs.getDate(6));
				dto.setMobileNo(rs.getString(7));
				dto.setLogin(rs.getString(8));
				dto.setPassword(rs.getString(9));
				dto.setConfirmPassword(rs.getString(10));
				dto.setLastLogin(rs.getTimestamp(11));	
				dto.setUnSuccessfullLogin(rs.getInt(12));	
				dto.setLoginIP(rs.getString(13));
				dto.setRegisteredIP(rs.getString(14));
				dto.setCreatedBy(rs.getString(15));
				dto.setModifiedBy(rs.getString(16));
				dto.setCreatedDatetime(rs.getTimestamp(17));
				dto.setModifiedDatetime(rs.getTimestamp(18));

			}
		} catch (Exception e) {
			throw new ApplicationException("Exception : Exception in get roles");

		} finally {
			JDBCDataSource.closeConnection(con);
		}

		return dto;
	}

	

	@Override
	public boolean forgetPassword(String login) throws ApplicationException, RecordNotFoundException {
		return false;
	}

	@Override
	public boolean resetPassword(UserDTO dto) throws ApplicationException, RecordNotFoundException {
		return false;
	}

	@Override
	public long registerUser(UserDTO dto) throws ApplicationException, DuplicateRecordException {
		return 0;
	}

	@Override
	public List getRoles(UserDTO dto) throws ApplicationException {
		return null;
	}

}
