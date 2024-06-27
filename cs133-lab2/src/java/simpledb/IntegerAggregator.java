package simpledb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Computes some aggregate over a set of IntFields.
 */
public class IntegerAggregator implements Aggregator {

    private static final long serialVersionUID = 1L;
    private int gbfield;
    private Type gbfieldtype;
    private int afield;
    private Op operator;
    private HashMap<Field, Integer> aggregateResults;
    private HashMap<Field, Integer> countResults;
    /**
     * Aggregate constructor
     * 
     * @param gbfield
     *            the 0-based index of the group-by field in the tuple, or
     *            NO_GROUPING if there is no grouping
     * @param gbfieldtype
     *            the type of the group by field (e.g., Type.INT_TYPE), or null
     *            if there is no grouping
     * @param afield
     *            the 0-based index of the aggregate field in the tuple
     * @param operator
     *            the aggregation operator
     */

    public IntegerAggregator(int gbfield, Type gbfieldtype, int afield, Op operator) {
        this.gbfield = gbfield;
        this.gbfieldtype = gbfieldtype;
        this.afield = afield;
        this.operator = operator;
        this.aggregateResults = new HashMap<>();
        this.countResults = new HashMap<>();
        if (gbfield == NO_GROUPING) {
            // Initialize default grouping
            aggregateResults.put(null, initialValue());
            countResults.put(null, 0);
        }
        // some code goes here
    }
    private Integer initialValue() {
        if (operator == Op.MIN) {
            return Integer.MAX_VALUE;
        } else if (operator == Op.MAX) {
            return Integer.MIN_VALUE;
        } else {
            return 0;
        }
    }
    /**
     * Merge a new tuple into the aggregate, grouping as indicated in the
     * constructor. See Aggregator.java for more.
     * 
     * @param tup
     *            the Tuple containing an aggregate field and a group-by field
     */
    public void mergeTupleIntoGroup(Tuple tup) {
        Field groupVal = (gbfield == NO_GROUPING) ? null : tup.getField(gbfield);
        int aggregateVal = ((IntField) tup.getField(afield)).getValue();

        aggregateResults.putIfAbsent(groupVal, initialValue());
        countResults.putIfAbsent(groupVal, 0);

        int newAggregateValue = aggregateVal;
        int newCountValue = countResults.get(groupVal) + 1;
        switch (operator) {
            case MIN:
                newAggregateValue = Math.min(aggregateResults.get(groupVal), aggregateVal);
                break;
            case MAX:
                newAggregateValue = Math.max(aggregateResults.get(groupVal), aggregateVal);
                break;
            case SUM:
            case AVG:
                newAggregateValue = aggregateResults.get(groupVal) + aggregateVal;
                break;
            case COUNT:
                newAggregateValue = newCountValue;
                break;
            default:
                throw new UnsupportedOperationException("Unsupported operation " + operator);
        }

        aggregateResults.put(groupVal, newAggregateValue);
        countResults.put(groupVal, newCountValue);
    }

    /**
     * Returns a DbIterator over group aggregate results.
     * 
     * @return a DbIterator whose tuples are the pair (groupVal, aggregateVal)
     *         if using group, or a single (aggregateVal) if no grouping. The
     *         aggregateVal is determined by the type of aggregate specified in
     *         the constructor.
     */

    public DbIterator iterator() {
        ArrayList<Tuple> tuples = new ArrayList<>();
        TupleDesc td = (gbfield == NO_GROUPING) ? new TupleDesc(new Type[]{Type.INT_TYPE})
                : new TupleDesc(new Type[]{gbfieldtype, Type.INT_TYPE});

        for (Map.Entry<Field, Integer> entry : aggregateResults.entrySet()) {
            int aggregateVal = entry.getValue();
            if (operator == Op.AVG) {
                aggregateVal /= countResults.get(entry.getKey());
            }
            Tuple tuple = new Tuple(td);
            if (gbfield == NO_GROUPING) {
                tuple.setField(0, new IntField(aggregateVal));
            } else {
                tuple.setField(0, entry.getKey());
                tuple.setField(1, new IntField(aggregateVal));
            }
            tuples.add(tuple);
        }

        return new TupleIterator(td, tuples);
    }

}
