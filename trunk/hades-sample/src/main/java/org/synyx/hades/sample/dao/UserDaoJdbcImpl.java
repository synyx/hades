package org.synyx.hades.sample.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcDaoSupport;
import org.synyx.hades.sample.domain.User;


/**
 * Class with the implementation of the custom DAO code. Uses JDBC in this case.
 * 
 * @author Oliver Gierke - gierke@synyx.de
 */
public class UserDaoJdbcImpl extends SimpleJdbcDaoSupport implements
        UserDaoCustom {

    private static final String COMPLICATED_SQL = "SELECT * FROM Users";


    /*
     * (non-Javadoc)
     * 
     * @see org.synyx.hades.sample.dao.UserDaoCustom#myCustomBatchOperation()
     */
    public List<User> myCustomBatchOperation() {

        return getSimpleJdbcTemplate().query(COMPLICATED_SQL,
                new UserRowMapper());
    }

    private class UserRowMapper implements ParameterizedRowMapper<User> {

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

            return user;
        }
    }
}
