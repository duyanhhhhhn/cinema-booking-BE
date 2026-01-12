package CinemaBooking.Group2.models;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import CinemaBooking.Group2.ultis.StringValue;

public final class DbConnection {
	private static DbConnection _instance=null;
	private DbConnection() {
	}
	public static DbConnection Instance() {
		if(_instance==null) {
			_instance=new DbConnection();
		}
		return _instance;
	}
	public JdbcTemplate getDb() {
		try {
			DriverManagerDataSource dataSource = new DriverManagerDataSource();
			dataSource.setDriverClassName(StringValue.DRIVER_CLASSNAME);
			dataSource.setUrl(StringValue.URL);
			dataSource.setUsername(StringValue.USER_SQL);
			dataSource.setPassword(StringValue.PWD_SQL);
			JdbcTemplate db = new JdbcTemplate(dataSource);
			return db;
		}
		catch (Exception e) {
			// TODO: handle exception
			System.out.print(e);
		}
		return null;
	}
}
