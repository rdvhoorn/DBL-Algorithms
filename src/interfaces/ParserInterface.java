package interfaces;

import models.InputRecord;
import models.OutputRecord;

import java.io.IOException;
import java.io.OutputStream;

public interface ParserInterface {
    /**
     * Parse input to program structure.
     *
     * @param source {@link Readable}
     * @return InputRecord
     * @throws NullPointerException if {@code source == null}
     * @throws IOException if read error occurs
     */
    InputRecord input(Readable source) throws NullPointerException, IOException;

    /**
     * Serialize internal program structure towards output stream.
     *
     * @param record {@link OutputRecord}
     * @param stream {@link OutputStream}
     * @throws NullPointerException if {@code record == null || stream == null}
     */
    void output(OutputRecord record, OutputStream stream) throws NullPointerException;
}
