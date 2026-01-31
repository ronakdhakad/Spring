package com.spring.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.spring.model.User;

@Repository
public class UserImp implements UserDao{
	
	JdbcTemplate jdbcTemplate;

	public UserImp(JdbcTemplate jdbcTemplate) {
//		super();
		this.jdbcTemplate = jdbcTemplate;
	}
	
	Map<String,Object> map = new HashMap<String, Object>();
	
	RowMapper<User> rm = new RowMapper<User>() {
		
		@Override
		public User mapRow(ResultSet rs, int rowNum) throws SQLException {
			User user =new User();
			user.setUid(rs.getInt(1));
			user.setUsername(rs.getString(2));
			user.setEmail(rs.getString(3));
			user.setAddress(rs.getString(4));
			return user;
		}
	};
	
	@Override
	public void addUser(User user) {
		String query="insert into JdbcTemplate1(uid,username,email,address) values (?,?,?,?)";
		jdbcTemplate.update(query,user.getUid(),user.getUsername(),user.getEmail(),user.getAddress());
	}
	@Override
	public void deleteUser(int id) {
		String query="delete from JdbcTemplate1 where uid=?";
		jdbcTemplate.update(query,id);
	}
		
	@Override
	public User getUser(int id) {
		String query="select * from JdbcTemplate1 where uid =?";
		return jdbcTemplate.queryForObject(query, rm , id);
		
	}
	@Override
	public void updateUser(User user) {
		String query="update JdbcTemplate1 set username=?,email=?, address=? where uid=?";
		jdbcTemplate.update(query,user.getUsername(),user.getEmail(),user.getAddress(),user.getUid());
		
	}
	
}
