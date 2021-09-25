package com.spring.toby.template;

import com.spring.toby.User;
import org.springframework.dao.EmptyResultDataAccessException;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TemplateUserDao {
  private DataSource dataSource;
  private JdbcContext jdbcContext;

  public void setDataSource(DataSource dataSource) {
    this.dataSource = dataSource;
  }

  public void setJdbcContext(JdbcContext jdbcContext) {
    this.jdbcContext = jdbcContext;
  }

  public void add(final User user) throws SQLException {
    this.jdbcContext.workWithStatementStrategy(new StatementStrategy() {
      @Override
      public PreparedStatement makePreparedStatement(Connection c) throws SQLException {
        PreparedStatement ps = c.prepareStatement("insert into users(id, name, password) values (?,?,?)");
        ps.setString(1, user.getId());
        ps.setString(2, user.getName());
        ps.setString(3, user.getPassword());

        return ps;
      }
    });
  }

  public void deleteAll() throws SQLException {
    this.jdbcContext.executeSql("delete from users");
  }

  public User get(String id) throws ClassNotFoundException, SQLException, EmptyResultDataAccessException {
    Connection c = dataSource.getConnection();

    PreparedStatement ps = c.prepareStatement("select * from users where id = ?");
    ps.setString(1, id);

    ResultSet rs = ps.executeQuery();
    User user = null;
    if (rs.next()) {
      user = new User();
      user.setId(rs.getString("id"));
      user.setName(rs.getString("name"));
      user.setPassword(rs.getString("password"));
    }

    rs.close();
    ps.close();
    c.close();

    if (user == null) throw new EmptyResultDataAccessException(1);

    return user;
  }

  public int getCount() throws SQLException {
    Connection c = dataSource.getConnection();

    PreparedStatement ps = c.prepareStatement("select count(1) from users");

    ResultSet rs = ps.executeQuery();
    rs.next();
    int count = rs.getInt(1);

    rs.close();
    ps.close();
    c.close();

    return count;
  }
}
