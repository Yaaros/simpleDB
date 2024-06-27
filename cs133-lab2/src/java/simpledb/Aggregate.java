package simpledb;

import java.util.*;

/**
 * The Aggregation operator that computes an aggregate (e.g., sum, avg, max,
 * min). Note that we only support aggregates over a single column, grouped by a
 * single column.
 */
public class Aggregate extends Operator {

    private static final long serialVersionUID = 1L;
    private DbIterator child;
    private int afield;
    private int gfield;
    private Aggregator.Op aop;
    private Aggregator aggregator;
    private DbIterator aggIterator;
    /**
     * Constructor.
     * 
     * Implementation hint: depending on the type of afield, you will want to
     * construct an {@link IntegerAggregator} or {@link StringAggregator} to help
     * you with your implementation of fetchNext().
     * 
     * 
     * @param child
     *            The DbIterator that is feeding us tuples.
     * @param afield
     *            The column over which we are computing an aggregate.
     * @param gfield
     *            The column over which we are grouping the result, or -1 if
     *            there is no grouping
     * @param aop
     *            The aggregation operator to use
     */
    public Aggregate(DbIterator child, int afield, int gfield, Aggregator.Op aop) {
	// some code goes here
        this.child = child;
        this.afield = afield;
        this.gfield = gfield;
        this.aop = aop;
        if (child.getTupleDesc().getFieldType(afield) == Type.INT_TYPE) {
            this.aggregator = new IntegerAggregator(gfield, gfield == Aggregator.NO_GROUPING ? null : child.getTupleDesc().getFieldType(gfield), afield, aop);
        } else if (child.getTupleDesc().getFieldType(afield) == Type.STRING_TYPE) {
            this.aggregator = new StringAggregator(gfield, gfield == Aggregator.NO_GROUPING ? null : child.getTupleDesc().getFieldType(gfield), afield, aop);
        }
    }

    /**
     * @return If this aggregate is accompanied by a groupby, return the groupby
     *         field index in the <b>INPUT</b> tuples. If not, return
     *         {@link simpledb.Aggregator#NO_GROUPING}
     * */
    public int groupField() {
	    // some code goes here
	    return gfield;
    }

    /**
     * @return If this aggregate is accompanied by a group by, return the name
     *         of the groupby field in the <b>OUTPUT</b> tuples If not, return
     *         null;
     * */
    public String groupFieldName() {
	    // some code goes here
        if (gfield == Aggregator.NO_GROUPING) {
            return null;
        } else {
            return child.getTupleDesc().getFieldName(gfield);
        }

    }

    /**
     * @return the aggregate field
     * */
    public int aggregateField() {
	    // some code goes here
	    return afield;
    }

    /**
     * @return return the name of the aggregate field in the <b>OUTPUT</b>
     *         tuples
     * */
    public String aggregateFieldName() {
	    // some code goes here
	    return child.getTupleDesc().getFieldName(afield);
    }

    /**
     * @return return the aggregate operator
     * */
    public Aggregator.Op aggregateOp() {
	    // some code goes here
	    return aop;
    }

    public static String nameOfAggregatorOp(Aggregator.Op aop) {
	return aop.toString();
    }

    public void open() throws NoSuchElementException, DbException, TransactionAbortedException {
        super.open();
        child.open();
        while (child.hasNext()) {
            aggregator.mergeTupleIntoGroup(child.next());
        }
        aggIterator = aggregator.iterator();
        aggIterator.open();
    }
    /**
     * Returns the next tuple. If there is a group by field, then the first
     * field is the field by which we are grouping, and the second field is the
     * result of computing the aggregate, If there is no group by field, then
     * the result tuple should contain one field representing the result of the
     * aggregate. Should return null if there are no more tuples.
     *
     * Hint: think about how many tuples you have to process from the child operator
     * before this method can return its first tuple.
     * Hint: notice that you each Aggregator class has an iterator() method
     */
    protected Tuple fetchNext() throws TransactionAbortedException, DbException {
        if (aggIterator.hasNext()) {
            return aggIterator.next();
        } else {
            return null;
        }
    }

    public void rewind() throws DbException, TransactionAbortedException {
        aggIterator.rewind();
    }

    /**
     * Returns the TupleDesc of this Aggregate. If there is no group by field,
     * this will have one field - the aggregate column. If there is a group by
     * field, the first field will be the group by field, and the second will be
     * the aggregate value column.
     * 
     * The name of an aggregate column should be informative. For example:
     * "aggName(aop) (child_td.getFieldName(afield))" where aop and afield are
     * given in the constructor, and child_td is the TupleDesc of the child
     * iterator.
     */
    public TupleDesc getTupleDesc() {
        TupleDesc td = child.getTupleDesc();
        Type[] typeAr;
        String[] fieldAr;

        if (gfield == Aggregator.NO_GROUPING) {
            typeAr = new Type[1];
            fieldAr = new String[1];
            typeAr[0] = td.getFieldType(afield);
            fieldAr[0] = td.getFieldName(afield) + "(" + aop.toString() + ")";
        } else {
            typeAr = new Type[2];
            fieldAr = new String[2];
            typeAr[0] = td.getFieldType(gfield);
            fieldAr[0] = td.getFieldName(gfield);
            typeAr[1] = td.getFieldType(afield);
            fieldAr[1] = td.getFieldName(afield) + "(" + aop.toString() + ")";
        }

        return new TupleDesc(typeAr, fieldAr);
    }
    public void close() {
        super.close();
        child.close();
        aggIterator.close();
    }
    /**
     * See Operator.java for additional notes
     */
    @Override
    public DbIterator[] getChildren() {
        return new DbIterator[]{this.child};
    }

    /**
     * See Operator.java for additional notes
     */

    public void setChildren(DbIterator[] children) {
        if (children[0] != null) {
            this.child = children[0];
        }
    }
    
}
