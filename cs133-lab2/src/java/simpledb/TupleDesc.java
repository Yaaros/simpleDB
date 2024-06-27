package simpledb;

import java.io.Serializable;
import java.util.*;

/**
 * TupleDesc describes the schema of a tuple.
 */
public class TupleDesc implements Serializable,Iterable<TupleDesc.TDItem> {
    protected List<TDItem> list = new LinkedList<>();
    /**
     * A help class to facilitate organizing the information of each field
     * */
    public static class TDItem implements Serializable {

        private static final long serialVersionUID = 1L;
        /**
         * The type of the field
         * */
        public final Type fieldType;
        
        /**
         * The name of the field
         * */
        public final String fieldName;

        public TDItem(Type t, String n) {
            this.fieldName = n;
            this.fieldType = t;
        }

        public TDItem(Type fieldType) {
            this.fieldType = fieldType;
            this.fieldName = "Undefined";
        }

        public String toString() {
            return "{" + fieldName + "(" + fieldType + ")" + "}";
        }

    }

    /**
     * @return
     *        An iterator which iterates over all the field TDItems
     *        that are included in this TupleDesc
     * */
    public Iterator<TDItem> iterator() {
        // some code goes here
        return list.iterator();
    }

    private static final long serialVersionUID = 1L;

    /**
     * Create a new TupleDesc with typeAr.length fields with fields of the
     * specified types, with associated named fields.
     *
     * @param typeAr
     *            array specifying the number of and types of fields in this
     *            TupleDesc. It must contain at least one entry.
     * @param fieldAr
     *            array specifying the names of the fields. Note that names may
     *            be null.
     */
    public TupleDesc(Type[] typeAr, String[] fieldAr) throws IllegalArgumentException {
        if(typeAr.length != fieldAr.length)throw new IllegalArgumentException("typeAr.length is not equals to fieldAr");
        // some code goes here
        for(int i = 0;i<typeAr.length;i++){
            TDItem t;
            if(fieldAr[i]==null) t = new TDItem(typeAr[i]);
            else t = new TDItem(typeAr[i],fieldAr[i]);
            list.add(t);
        }
    }

    /**
     * Constructor. Create a new tuple desc with typeAr.length fields with
     * fields of the specified types, with anonymous (unnamed) fields.
     * 
     * @param typeAr
     *            array specifying the number of and types of fields in this
     *            TupleDesc. It must contain at least one entry.
     */
    public TupleDesc(Type[] typeAr) {
        // some code goes here
        for (Type t : typeAr) {
            TDItem i = new TDItem(t);
            list.add(i);
        }
    }

    public TupleDesc() {
    }

    /**
     * @return the number of fields in this TupleDesc
     */
    public int numFields() {
        // some code goes here
        return list.size();
    }

    /**
     * Gets the (possibly null) field name of the ith field of this TupleDesc.
     * 
     * @param i
     *            index of the field name to return. It must be a valid index.
     * @return the name of the ith field
     * @throws NoSuchElementException
     *             if i is not a valid field reference.
     */
    public String getFieldName(int i) throws NoSuchElementException {
        // some code goes here
        return list.get(i).fieldName;
    }

    /**
     * Gets the type of the ith field of this TupleDesc.
     * 
     * @param i
     *            The index of the field to get the type of. It must be a valid
     *            index.
     * @return the type of the ith field
     * @throws NoSuchElementException
     *             if i is not a valid field reference.
     */
    public Type getFieldType(int i) throws NoSuchElementException {
        // some code goes here
        return list.get(i).fieldType;
    }

    /**
     * Find the index of the field with a given name.
     * No match if name is null.
     * 
     * @param name
     *            name of the field.
     * @return the index of the field that is first to have the given name.
     * @throws NoSuchElementException
     *             if no field with a matching name is found.
     */
    public int fieldNameToIndex(String name) throws NoSuchElementException {
        // some code goes here
        for (int i = 0; i < list.size(); i++) {
            if(list.get(i).fieldName.equals(name)){
                return i;
            }
        }
        throw new NoSuchElementException("No such element in TupleDesc!");
    }

    /**
     * @return The size (in bytes) of tuples corresponding to this TupleDesc.
     *         Note that tuples from a given TupleDesc are of a fixed size.
     * @see Type#getSizeInBytes
     */
    public int getSizeInBytes() {
        // some code goes here
        int toReturn = 0;
        for (TDItem t : list) {
            toReturn+=t.fieldType.getSizeInBytes();
        }
        return toReturn;
    }

    /**
     * Merge two TupleDescs into one, with td1.numFields + td2.numFields fields,
     * with the first td1.numFields coming from td1 and the remaining from td2.
     * 
     * @param td1
     *            The TupleDesc with the first fields of the new TupleDesc
     * @param td2
     *            The TupleDesc with the last fields of the TupleDesc
     * @return the new TupleDesc
     */
    public static TupleDesc merge(TupleDesc td1, TupleDesc td2) {
        // some code goes here
        TupleDesc tdc = new TupleDesc();
        for (TDItem t : td1) {
            tdc.list.add(t);
        }
        for (TDItem t : td2) {
            tdc.list.add(t);
        }
        return tdc;
    }

    /**
     * Compares the specified object with this TupleDesc for equality. Two
     * TupleDescs are considered equal if they have the same number of items
     * and if the i-th type in this TupleDesc is equal to the i-th type in o
     * for every i. It does not matter if the field names are equal.
     * 
     * @param o
     *            the Object to be compared for equality with this TupleDesc.
     * @return true if the object is equal to this TupleDesc.
     */

    public boolean equals(Object o) {
        //some code goes here
        if (this == o) return true; //内存指针指向同一处返回true；
        if (!(o instanceof TupleDesc)) return false;//类型不一样返回false;
        TupleDesc that = (TupleDesc) o;   //类型一样进行强转
        if (this.list.size() != that.list.size()) return false;//内部链表长度不同返回false;
        for (int i = 0; i < this.list.size(); i++) {
            //我们只关心Type是否一致
            if (!this.list.get(i).fieldType.equals(that.list.get(i).fieldType)) return false;
        }
        return true;
    }



    public int hashCode() {
        // If you want to use TupleDesc as keys for HashMap, implement this so
        // that equal objects have equals hashCode() results

        //直接返回内部链表的哈希码就行，这东西就是套壳链表
        return Objects.hashCode(list);
    }

    /**
     * Returns a String describing this descriptor. It should be of the form
     * "fieldName[0](fieldType[0]), ..., fieldName[M](fieldType[M])", although
     * the exact format does not matter.
     * 
     * @return String describing this descriptor.
     */
    public String toString() {
        // some code goes here
        StringBuilder stb = new StringBuilder();
        stb.append("[");
        for (TDItem t : list) {
            stb.append(t).append(",").append(" ");
        }
        stb.delete(stb.length()-2,stb.length());
        stb.append("]");
        return stb.toString();
    }
}
