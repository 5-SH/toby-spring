package com.spring.toby.independent;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class UserDaoJdbc implements UserDao {
  private JdbcTemplate jdbcTemplate;
  private RowMapper<User> userRowMapper = new RowMapper<User>() {
    @Override
    public User mapRow(ResultSet rs, int rowNum) throws SQLException {
      User user = new User();
      user.setId(rs.getString("id"));
      user.setName(rs.getString("name"));
      user.setPassword(rs.getString("password"));
      user.setLevel(Level.valueOf(rs.getInt("level")));
      user.setLogin(rs.getInt("login"));
      user.setRecommend(rs.getInt("recommend"));
      user.setEmail(rs.getString("email"));
      return user;
    }
  };

  public void setDataSource(DataSource dataSource) {
    jdbcTemplate = new JdbcTemplate(dataSource);
  }

  public int add(final User user) {
    return this.jdbcTemplate.update("insert into users(id, name, password, level, login, recommend, email) " +
                    "values(?,?,?,?,?,?,?)", user.getId(), user.getName(), user.getPassword(),
            user.getLevel().intValue(), user.getLogin(), user.getRecommend(), user.getEmail());
  }

  public int deleteAll() {
    return jdbcTemplate.update("delete from users");
  }

  public User get(String id) {
    return this.jdbcTemplate.queryForObject("select * from users where id = ?",
            new Object[]{id},
            userRowMapper
    );
  }

  public List<User> getAll() {
    return this.jdbcTemplate.query("select * from users order by id", userRowMapper);
  }

  public void update(User user) {
    this.jdbcTemplate.update("update users set name=?, password=?, level=?, login=?, recommend=?, email=? where id=?",
            user.getName(), user.getPassword(),
            user.getLevel().intValue(), user.getLogin(),
            user.getRecommend(), user.getEmail(),
            user.getId());
  }

  public int getCount() {
    return jdbcTemplate.queryForInt("select count(1) from users");
  }
}
