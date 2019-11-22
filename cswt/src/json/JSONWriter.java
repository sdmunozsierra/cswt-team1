package json;

import java.io.IOException;
import java.io.OutputStream;

public abstract class JSONWriter {

    protected OutputStream outStrm;
    protected String encd;

    protected JSONWriter(OutputStream outStrm, String encd) {
        this.outStrm = outStrm;
        this.encd = encd;
    }

    /**
     * Write the JSON data to the underlying stream.
     * 
     * @param json
     *            The JSON data to be written.
     */
    public abstract void write(String json);

    /**
     * Close the writer and its underlying stream.
     */
    public void close() {
        try {
            this.outStrm.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}