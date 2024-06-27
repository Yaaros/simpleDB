package simpledb;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * HeapFile is an implementation of a DbFile that stores a collection of tuples
 * in no particular order. Tuples are stored on pages, each of which is a fixed
 * size, and the file is simply a collection of those pages. HeapFile works
 * closely with HeapPage. The format of HeapPages is described in the HeapPage
 * constructor.
 * 
 * @see HeapPage#HeapPage
 * @author Sam Madden
 */
public class HeapFile implements DbFile {
    private File file;

    private TupleDesc td;

    /**
     * Constructs a heap file backed by the specified file.
     * 
     * @param f
     *            the file that stores the on-disk backing store for this heap
     *            file.
     */
    public HeapFile(File f, TupleDesc td) {

        // some code goes here
        this.file = f;
        this.td = td;
    }

    /**
     * Returns the File backing this HeapFile on disk.
     * 
     * @return the File backing this HeapFile on disk.
     */
    public File getFile() {
        // some code goes here
        return file;
    }


    /**
     * Returns the TupleDesc of the table stored in this DbFile.
     *
     * @return TupleDesc of this DbFile.
     */
    public TupleDesc getTupleDesc() {
        // some code goes here
        return td;
    }

    /**
     * Returns an ID uniquely identifying this HeapFile. Implementation note:
     * you will need to generate this tableid somewhere to ensure that each
     * HeapFile has a "unique id," and that you always return the same value for
     * a particular HeapFile. We suggest hashing the absolute file name of the
     * file underlying the heapfile, i.e. f.getAbsoluteFile().hashCode().
     * 
     * @return an ID uniquely identifying this HeapFile.
     */
    public int getId() {
        // some code goes here
        //这里就是看了DbFile接口里面的文档注释才看出来的。
        return file.getAbsoluteFile().hashCode();
    }



    // see DbFile.java for javadocs
    public Page readPage(PageId pid) {
        try (RandomAccessFile raf = new RandomAccessFile(file, "r")) {
            int pageSize = BufferPool.getPageSize();
            byte[] pageData = new byte[pageSize];
            raf.seek((long) pid.getPageNumber() * pageSize);
            raf.readFully(pageData);
            return new HeapPage((HeapPageId) pid, pageData);
        } catch (IOException e) {
            e.printStackTrace();
        }
        throw new IllegalArgumentException("readPage: Unable to read the page from disk.");
    }

    // see DbFile.java for javadocs
    public void writePage(Page page) throws IOException {
        // some code goes here
        // not necessary for lab1
    }

    /**
     * Returns the number of pages in this HeapFile.
     */
    public int numPages() {
        // some code goes here
        long fileSize = file.length();
        int pageSize = BufferPool.getPageSize();
        return (int) Math.ceil((double) fileSize / pageSize);
    }

    // see DbFile.java for javadocs
    public DbFileIterator iterator(TransactionId tid) {
        return new DbFileIterator() {
            private int currentPageIndex = 0;
            private Iterator<Tuple> currentIterator = null;

            public void open() throws DbException, TransactionAbortedException {
                currentPageIndex = 0;
                currentIterator = getIteratorForPage(currentPageIndex);
            }

            public boolean hasNext() throws DbException, TransactionAbortedException {
                if (currentIterator == null) {
                    throw new IllegalStateException("currentIterator is null");
                }

                if (currentIterator.hasNext()) {
                    return true;
                } else {
                    while (currentPageIndex < numPages() - 1) {
                        currentPageIndex++;
                        currentIterator = getIteratorForPage(currentPageIndex);
                        if (currentIterator.hasNext()) {
                            return true;
                        }
                    }
                    return false;
                }
            }

            public Tuple next() throws DbException, TransactionAbortedException, NoSuchElementException {
                if (currentIterator == null || !currentIterator.hasNext()) {
                    throw new IllegalStateException("currentIterator is null");
                }
                return currentIterator.next();
            }

            public void rewind() throws DbException, TransactionAbortedException {
                close();
                open();
            }

            public void close() {
                currentIterator = null;
                currentPageIndex = 0;
            }

            private Iterator<Tuple> getIteratorForPage(int pageIndex) throws TransactionAbortedException, DbException {
                PageId pageId = new HeapPageId(getId(), pageIndex);
                Page page = Database.getBufferPool().getPage(tid, pageId, Permissions.READ_ONLY);
                return ((HeapPage) page).iterator();
            }
        };
    }



    // see DbFile.java for javadocs
    public ArrayList<Page> insertTuple(TransactionId tid, Tuple t)
            throws DbException, IOException, TransactionAbortedException {
        // some code goes here
        return null;
        // not necessary for lab1
    }

    // see DbFile.java for javadocs
    public ArrayList<Page> deleteTuple(TransactionId tid, Tuple t) throws DbException,
            TransactionAbortedException {
        // some code goes here
        return null;
        // not necessary for lab1
    }

}

