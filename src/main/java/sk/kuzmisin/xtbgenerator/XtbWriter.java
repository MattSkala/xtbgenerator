package sk.kuzmisin.xtbgenerator;

import com.google.common.base.CaseFormat;
import com.google.javascript.jscomp.JsMessage;

import java.io.IOException;
import java.io.Writer;
import java.lang.RuntimeException;
import java.util.Iterator;
import java.util.Map;

abstract class XtbWriter {

    protected final String EOL = "\n";

    protected final String INDENT = "\t";

    protected Writer writer;

    protected Map<String, JsMessage> messages;

    protected String lang;

    public XtbWriter(Writer writer, String lang, Map<String, JsMessage> messages) {
        this.writer = writer;
        this.messages = messages;
        this.lang = lang;
    }

    abstract public void write() throws IOException;

    protected void writeFooter() throws IOException {
        writer.append("</translationbundle>");
    }

    protected void writeMessages() throws IOException {
        Iterator<JsMessage> iterator = messages.values().iterator();
        while (iterator.hasNext()) {
            JsMessage message = iterator.next();

            if (message.getDesc() == null) {
                throw new RuntimeException("Message desc cannot be empty: " + message.getKey() + " in " + message.getSourceName());
            }

            writer.append(
                INDENT +
                "<translation id=\"" + message.getId() + "\" " +
                        "key=\"" + message.getKey() + "\" " +
                        "source=\"" + message.getSourceName() + "\" " +
                        "desc=\"" + escapeDesc(message.getDesc()) + "\"" +
                        ">"
            );

            for (CharSequence part : message.parts()) {
                if (part instanceof JsMessage.PlaceholderReference) {
                    String name = CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, ((JsMessage.PlaceholderReference) part).getName());
                    writer.append("<ph name=\"" + name + "\" />");
                } else {
                    writer.append(escape(part));
                }
            }

            writer.append(
                "</translation>" +
                EOL
            );
        }
    }

    protected String escape(CharSequence value) {
        return value.toString().
            replace("&", "&amp;").
            replace("<", "&lt;").
            replace(">", "&gt;");
    }

    protected String escapeDesc(CharSequence value) {
        return value.toString().
            replace("&", "&amp;").
            replace("\"", "&quot;");
    }
}
