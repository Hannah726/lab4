package simpledb;

import java.io.IOException;

/**
 * The delete operator. Delete reads tuples from its child operator and removes
 * them from the table they belong to.
 */
public class Delete extends Operator {

    private static final long serialVersionUID = 1L;
    private TransactionId tid;
    private DbIterator child;
    private TupleDesc td;
    private boolean hasAccessed;
    private int count;

    private static final TupleDesc SCHEMA = new TupleDesc(new Type[] { Type.INT_TYPE });
    /**
     * Constructor specifying the transaction that this delete belongs to as
     * well as the child to read from.
     * 
     * @param t
     *            The transaction this delete runs in
     * @param child
     *            The child operator from which to read tuples for deletion
     */
    public Delete(TransactionId t, DbIterator child) {
        // some code goes here
        tid = t;
        this.child = child;
        count=0;
        td = new TupleDesc(new Type[]{Type.INT_TYPE}, new String[]{null});
    }

    public TupleDesc getTupleDesc() {
        // some code goes here
        return td;
    }

    public void open() throws DbException, TransactionAbortedException {
        // some code goes here
        child.open();
        super.open();
        hasAccessed = false;
        while (child.hasNext()) {
            Tuple next = child.next();
            Database.getBufferPool().deleteTuple(tid, next);
            count++;
        }
    }

    public void close() {
        // some code goes here
        super.close();
        child.close();
    }

    public void rewind() throws DbException, TransactionAbortedException {
        // some code goes here
        hasAccessed = false;
    }

    /**
     * Deletes tuples as they are read from the child operator. Deletes are
     * processed via the buffer pool (which can be accessed via the
     * Database.getBufferPool() method.
     * 
     * @return A 1-field tuple containing the number of deleted records.
     * @see Database#getBufferPool
     * @see BufferPool#deleteTuple
     */
    protected Tuple fetchNext() throws TransactionAbortedException, DbException {
        // some code goes here
        if (hasAccessed) {
            return null;
        }
        hasAccessed = true;
        Tuple deleted_num=new Tuple(getTupleDesc());
        deleted_num.setField(0,new IntField(count));
        return deleted_num;
    }

    @Override
    public DbIterator[] getChildren() {
        // some code goes here
        return new DbIterator[]{ child };
    }

    @Override
    public void setChildren(DbIterator[] children) {
        // some code goes here
        if (children.length != 1) {
        	throw new IllegalArgumentException("Expected one new child.");
        }
        child = children[0];
    }

}
