package json;

import java.io.IOException;
import java.io.InputStream;

public abstract class JSONReader {

    protected InputStream inStrm;
    protected String encd;

    protected JSONReader(InputStream inStrm, String encd) {
        this.inStrm = inStrm;
        this.encd = encd;
    }

    /**
     * Read a JSON data from the underlying stream.
     */
    public abstract String read();

    /**
     * Close the reader and its underlying stream.
     */
    public void close() {
        try {
            this.inStrm.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}