package simpledb;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;

/**
 * Interface for values of fields in tuples in SimpleDB.
 */
public interface Field extends Serializable{
    /**
     * Write the bytes representing this field to the specified
     * DataOutputStream.
     * @see DataOutputStream
     * @param dos The DataOutputStream to write to.
     */
    void serialize(DataOutputStream dos) throws IOException;

    /**
     * Compare the value of this field object to the passed in value.
     * Example usage: foo.compare(op, value) is interpreted as "foo op value"
     * @param op The operator
     * @param value The value to compare this Field to
     * @return Whether or not the comparison yields true.
     */
    public boolean compare(Predicate.Op op, Field value);

    /**
     * Returns the type of this field (see {@link Type#INT_TYPE} or {@link Type#STRING_TYPE}
     * @return type of this field
     */
    public Type getType();
    
    /**
     * Hash code.
     * Different Field objects representing the same value should probably
     * return the same hashCode.
     */
    public int hashCode();
    public boolean equals(Object field);

    public String toString();
}
