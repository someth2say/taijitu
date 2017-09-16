package org.someth2say.taijitu.query.queryactions;

import org.someth2say.taijitu.query.objects.ObjectArray;

import java.io.IOException;
import java.io.OutputStream;

/**
 * @author Jordi Sola
 */
public class OutputStreamQueryActions<T extends ObjectArray> implements QueryActions<T> {
    private final OutputStream outputStream;
    private final String recordSeparator;
    private boolean firstStep = true;

    private String[] columnDescriptions;

    public OutputStreamQueryActions(OutputStream out) {
        this.outputStream = out;
        this.recordSeparator = "\n";
    }

    public OutputStreamQueryActions(OutputStream out, String _recordSeparator) {
        this.outputStream = out;
        this.recordSeparator = _recordSeparator;
    }

    @Override
    public void step(ObjectArray currentRecord) throws QueryActionsException {
        try {
            if (this.firstStep) {
                this.firstStep = false;
                this.writeHeaders();
            }

            String e = currentRecord.toString().concat(this.recordSeparator);
            this.outputStream.write(e.getBytes());
        } catch (IOException e) {
            throw new QueryActionsException("Can't write to output stream.", e);
        }
    }

    private void writeHeaders() throws IOException {
        boolean first = true;
        StringBuilder sb = new StringBuilder();
        for (String columnDescription : columnDescriptions) {
            if (first) {
                first = false;
            } else {
                sb.append(",");
            }
            sb.append(columnDescription);
        }

        sb.append(this.recordSeparator);
        this.outputStream.write(sb.toString().getBytes());
    }

    @Override
    public void start(String[] columnDescriptions) throws QueryActionsException {
        this.columnDescriptions = columnDescriptions;
    }

    public String[] getColumnDescriptions() {
        return columnDescriptions;
    }

    @Override
    public void end() throws QueryActionsException {
        // Nothing to do
    }
}
