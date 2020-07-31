import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;

/**
 * 
 */

/**
 * Christopher Brislin 26 Jul 2020 JUserRegistration
 */
public class DatabaseConnector {

	Connection conn;
	ResultSet rs;

	private PreparedStatement checkIfEmailExists;
	private PreparedStatement grantUser;
	private PreparedStatement createUser;
	private PreparedStatement createUserDataTable;
	private PreparedStatement addToUserTable;
	private PreparedStatement newPword;
	private PreparedStatement newEmail;
	private PreparedStatement removeUser;
	private PreparedStatement removeUserTable;
	private PreparedStatement deleteUser;
	private PreparedStatement updateNewEmail;
	// private PreparedStatement checkIfUUIDExists;
	//Properties properties = new Properties();

	public void connect(String driver, String url, String port, String dbname, String user, String password) {

		String connectionString = driver + url + ":" + port + "/" + dbname;

		try {
			//properties.put("sslMode", SSLModeType.DISABLED);
			conn = DriverManager.getConnection(connectionString+"?verifyServerCertificate=false&useSSL=false&requireSSL=false&allowPublicKeyRetrieval=true", user, password);
			if(Main.DEBUG)System.out.println(conn.isValid(0));
			//if(Main.DEBUG)System.
			conn.setAutoCommit(false);

			checkIfEmailExists = conn
					.prepareStatement("select count(*) as rowcount from Logbook.UserTable where Email= ? or UUID= ?;");
			// Statements for creating user
			createUser = conn.prepareStatement("create user ? @'%' IDENTIFIED BY ? ;");
			addToUserTable = conn
					.prepareStatement("insert into Logbook.UserTable (UUID, Email, DateAdded)" + "values (?,?,?)");
			grantUser = conn.prepareStatement("grant select, insert, delete, update on Logbook.* to ? @'%';");
			// Statement for new/reset password
			newPword = conn.prepareStatement("alter user ? @'%' identified by ?;");
			// Statement for new email
			newEmail = conn.prepareStatement("rename user ? to ?;");
			updateNewEmail = conn.prepareStatement("update Logbook.UserTable set Email=? where UUID=?;");

			// Statements for removing accounts
			removeUser = conn.prepareStatement("drop user ? @'%';");
			removeUserTable = conn.prepareStatement("drop table Logbook.? ;");
			deleteUser = conn.prepareStatement("DELETE FROM Logbook.UserTable WHERE UUID= ? ;");

		} catch (SQLException ex) {
			ex.printStackTrace();

		}

	}

	public boolean connStatus() {
		try {
			return conn.isValid(0);
		} catch (SQLException ex) {
			System.out.println(ex);
			return false;
		}
	}

	public boolean connClose() {
		if (conn != null) {
			try {
				conn.setAutoCommit(true);
				conn.close();
			} catch (SQLException ex) {
				System.err.print(ex);
			}
			return true;
		} else {
			return false;
		}

	}

	public boolean createNewUser(String uuid, String emailAddr, String password) {
		boolean createSuccess = false;

		try {

			checkIfEmailExists.setString(1, emailAddr);
			checkIfEmailExists.setString(2, uuid);
			rs = checkIfEmailExists.executeQuery();
			rs.next();
			if (rs.getInt("rowcount") == 0) {

				String tableName = "Logbook.user" + uuid;
				String sql = ("create table " + tableName + "(" + "ENTRY_DATE varchar(50)," + "AC_TYPE varchar(100),"
						+ "AC_REGO varchar(50)," + "PIC varchar(100)," + "OTHER_CREW varchar(100),"
						+ "DESCIPTION varchar(255)," + "DEPARTURE_PORT varchar(100)," + "ARRIVAL_PORT varchar(100),"
						+ "OUT_TIME varchar(20)," + "OFF_TIME varchar(20)," + "ON_TIME varchar(20),"
						+ "IN_TIME varchar(20)," + "TOTAL_TIME_DAY varchar(20)," + "TOTAL_TIME_NIGHT varchar(20),"
						+ "IF_TIME varchar(20)," + "SE tinyint(1)," + "ME tinyint(1)," + "ICUS varchar(20),"
						+ "DUAL_CREW tinyint(1)," + "COMMAND tinyint(1)," + "CO_PILOT varchar(1),"
						+ "APPROACH varchar(20)" + ");");

				createUserDataTable = conn.prepareStatement(sql);
				createUser.setString(1, emailAddr);
				createUser.setString(2, password);
				addToUserTable.setString(1, uuid);
				addToUserTable.setString(2, emailAddr);
				addToUserTable.setString(3, Instant.now().toString());
				grantUser.setString(1, emailAddr);

				createUser.executeUpdate();
				addToUserTable.executeUpdate();
				grantUser.executeUpdate();
				grantUser.execute("flush privileges;");
				createUserDataTable.execute();
				createSuccess = true; // This doesn't check anything yet...
				createUser.close();
				createUserDataTable.close();
				addToUserTable.close();
				grantUser.close();
				checkIfEmailExists.close();

			} else {
				System.out.println("user already exists.");
				createSuccess = false;
			}

		} catch (SQLException ex) {
			createSuccess = false;
			ex.printStackTrace();
			;
		}

		return createSuccess;
	}

	public boolean newPassword(String newPwd, String emailAddr, String uuid) {
		boolean success = false;
		try {
			checkIfEmailExists.setString(1, emailAddr);
			checkIfEmailExists.setString(2, uuid);
			rs = checkIfEmailExists.executeQuery();
			rs.next();
			if (rs.getInt("rowcount") != 0) {
				newPword.setString(1, emailAddr);
				newPword.setString(2, newPwd);
				newPword.executeUpdate();
				success = true;
				checkIfEmailExists.close();
				newPword.close();
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
			success = false;
		}

		return success;
	}

	public boolean renameUser(String curEmail, String oldEmail, String uuid) {
		boolean success = false;
		try {
			newEmail.setString(1, oldEmail);
			newEmail.setString(2, curEmail);
			updateNewEmail.setString(1, curEmail);
			updateNewEmail.setString(2, uuid);
			updateNewEmail.executeUpdate();
			newEmail.execute();
			success = true;
			updateNewEmail.close();
			newEmail.close();

		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		return success;
	}

	public boolean removeUser(String emailAddr, String uuid) {
		boolean success = false;
		try {
			checkIfEmailExists.setString(1, emailAddr);
			checkIfEmailExists.setString(2, uuid);
			rs = checkIfEmailExists.executeQuery();
			rs.next();
			if (rs.getInt("rowcount") != 0) {

				String sql = "drop table Logbook.user" + uuid + " ;";
				deleteUser.setString(1, uuid);
				// removeUserTable.setString(1, "user" + uuid);
				removeUser.setString(1, emailAddr);

				deleteUser.execute();
				removeUserTable.execute(sql);
				removeUser.execute();
				success = true;
				checkIfEmailExists.close();
				deleteUser.close();
				removeUser.close();

			}

		} catch (SQLException ex) {
			ex.printStackTrace();
			success = false;
		}

		return success;
	}

}
