package org.effective_mobile.task_management_system.enums;

import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.UserType;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public abstract class EnumWithIdJpaType<Value, T extends ValuableEnum<Value>> implements UserType<T> {

    protected abstract Value getValueFromResult(ResultSet rs) throws SQLException;

    protected abstract void setValue(PreparedStatement st, T value, int index) throws SQLException;

    @Override
    public boolean equals(T x, T y) {
        return x == y;
    }

    @Override
    public int hashCode(T x) {
        return x == null ? 0 : x.hashCode();
    }

    @Override
    public T nullSafeGet(
        ResultSet rs,
        int position,
        SharedSessionContractImplementor session,
        Object owner
    ) throws SQLException {
        Value value = getValueFromResult(rs);
        if(rs.wasNull()) {
            return null;
        }
        for(T enumeration : returnedClass().getEnumConstants()) {
            if(value == enumeration.getValue()) {
                return enumeration;
            }
        }
        throw new IllegalStateException("There is no '%s' for value = '%s'".formatted(returnedClass().getSimpleName(), value));
    }

    @Override
    public void nullSafeSet(
        PreparedStatement st,
        T value,
        int index,
        SharedSessionContractImplementor session
    ) throws SQLException {
        if (value == null) {
            st.setNull(index, getSqlType());
        } else {
            setValue(st, value, index);
        }
    }


    @Override
    public T deepCopy(T value) {
        return value;
    }

    @Override
    public boolean isMutable() {
        return false;
    }

    @Override
    public Serializable disassemble(T value) {
        throw unsupported("disassemble");
//        return value;
    }

    @Override
    public T assemble(Serializable cached, Object owner) {
        throw unsupported("assemble");
//        return (T) owner;
    }

    private UnsupportedOperationException unsupported(String methodName) {
        return new UnsupportedOperationException(("%s#%s").formatted(this.getClass().getSimpleName(), methodName));
    }
}