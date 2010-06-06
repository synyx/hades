package org.synyx.hades.sample.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.synyx.hades.sample.dao.UserDaoCustom;
import org.synyx.hades.sample.domain.User;


/**
 * Class with the implementation of the custom DAO code. Uses JDBC in this case.
 * For basic programatic setup see {@link UserDaoImpl} for examples.
 * <p>
 * As you need to hand the instance a {@link javax.sql.DataSource} or
 * {@link org.springframework.jdbc.core.simple.SimpleJdbcTemplate} you manually
 * need to declare it as Spring bean:
 * 
 * <pre>
 * &lt;hades:dao-config base-package=&quot;org.synyx.hades.sample.dao&quot; /&gt;
 * 
 * &lt;bean id=&quot;userDaoImpl&quot; class=&quot;...UserDaoJdbcImpl&quot;&gt;
 *   &lt;property name=&quot;dataSource&quot; ref=&quot;dataSource&quot; /&gt;
 * &lt;/bean&gt;
 * </pre>
 * 
 * Using {@code userDaoImpl} will cause the DAO instance get this bean injected
 * for custom DAO logic as the default postfix for custom DAO instances is
 * {@code Impl}.
 * 
 * @author Oliver Gierke - gierke@synyx.de
 */
public class UserDaoJdbcImpl extends JdbcDaoSupport implements UserDaoCustom {

    private static final String COMPLICATED_SQL = "SELECT * FROM User";


    /*
     * (non-Javadoc)
     * 
     * @see org.synyx.hades.sample.dao.UserDaoCustom#myCustomBatchOperation()
     */
    public List<User> myCustomBatchOperation() {

        return getJdbcTemplate().query(COMPLICATED_SQL, new UserRowMapper());
    }

    private static class UserRowMapper implements ParameterizedRowMapper<User> {

        /*
         * (non-Javadoc)
         * 
         * @see
         * org.springframework.jdbc.core.simple.ParameterizedRowMapper#mapRow
         * (java.sql.ResultSet, int)
         */
        public User mapRow(ResultSet rs, int rowNum) throws SQLException {

            User user = new User();
            user.setId(rs.getLong("id"));
            user.setUsername(rs.getString("username"));
            user.setLastname(rs.getString("lastname"));
            user.setFirstname(rs.getString("firstname"));

            return user;
        }
    }
}
