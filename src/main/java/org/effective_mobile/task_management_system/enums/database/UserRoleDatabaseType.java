package org.effective_mobile.task_management_system.enums.database;

import org.effective_mobile.task_management_system.enums.EnumWithIdJpaType;
import org.effective_mobile.task_management_system.enums.UserRole;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

public class UserRoleDatabaseType extends EnumWithIdJpaType<String, UserRole> {

    @Override
    public int getSqlType() {
        return Types.VARCHAR;
    }

    @Override
    public Class<UserRole> returnedClass() {
        return UserRole.class;
    }

    @Override
    protected String getValueFromResult(ResultSet rs) throws SQLException {
        return rs.getString(2);
    }

    @Override
    protected void setValue(PreparedStatement st, UserRole value, int index) throws SQLException {
        st.setString(index, value.getValue());
    }
}