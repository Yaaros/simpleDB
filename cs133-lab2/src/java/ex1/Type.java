package ex1;

import simpledb.Field;
import simpledb.IntField;
import simpledb.StringField;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.text.ParseException;

/**
 * Class representing a type in SimpleDB.
 * Types are static objects defined by this class; hence, the Type
 * constructor is private.
 */
public enum Type implements Serializable {
    INT_TYPE() {
        @Override
        public int getSizeInBytes() {
            return 4;
        }

        @Override
        public simpledb.Field parse(DataInputStream dis) throws ParseException {
            try {
                return new IntField(dis.readInt());
            }  catch (IOException e) {
                throw new ParseException("couldn't parse", 0);
            }
        }

    }, STRING_TYPE() {
        @Override
        public int getSizeInBytes() {
            return STRING_LEN+4;
        }

        @Override
        public simpledb.Field parse(DataInputStream dis) throws ParseException {
            try {
                int strLen = dis.readInt();
                byte bs[] = new byte[strLen];
                dis.read(bs);
                dis.skipBytes(STRING_LEN-strLen);
                return new StringField(new String(bs), STRING_LEN);
            } catch (IOException e) {
                throw new ParseException("couldn't parse", 0);
            }
        }
    };
    
    public static final int STRING_LEN = 128;

  /**
   * @return the number of bytes required to store a field of this type.
   */
    public abstract int getSizeInBytes();

  /**
   * @return a Field object of the same type as this object that has contents
   *   read from the specified DataInputStream.
   * @param dis The input stream to read from
   * @throws ParseException if the data read from the input stream is not
   *   of the appropriate type.
   */
    public abstract Field parse(DataInputStream dis) throws ParseException;

}
