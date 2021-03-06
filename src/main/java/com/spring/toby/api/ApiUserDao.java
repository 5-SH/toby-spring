package com.spring.toby.api;

import com.spring.toby.User;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class ApiUserDao {
  private JdbcTemplate jdbcTemplate;
  private RowMapper<User> userRowMapper = new RowMapper<User>() {
    @Override
    public User mapRow(ResultSet rs, int rowNum) throws SQLException {
      User user = new User();
      user.setId(rs.getString("id"));
      user.setName(rs.getString("name"));
      user.setPassword(rs.getString("password"));
      return user;
    }
  };

  public void setDataSource(DataSource dataSource) {
    jdbcTemplate = new JdbcTemplate(dataSource);
  }

  public void add(final User user) throws SQLException {
    jdbcTemplate.update("insert into users(id, name, password) values(?,?,?)",
            user.getId(), user.getName(), user.getPassword());
  }

  public void deleteAll() throws SQLException {
    jdbcTemplate.update("delete from users");
  }

  public User get(String id) throws ClassNotFoundException, SQLException, EmptyResultDataAccessException {
    return this.jdbcTemplate.queryForObject("select * from users where id = ?",
            new Object[]{id},
            userRowMapper
            );
  }

  public List<User> getAll() {
    return this.jdbcTemplate.query("select * from users order by id", userRowMapper);
  }

  public int getCount() throws SQLException {
    return jdbcTemplate.queryForInt("select count(1) from users");
  }
}
