package karol.przygoda.mp3player.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.SAXException;

public class MetaDataCollector {
    public MetaDataCollector() {
    }

    public Songs getMetaData(File file) throws IOException, TikaException, SAXException {
        BodyContentHandler handler = new BodyContentHandler();
        Metadata metadata = new Metadata();
        FileInputStream inputStream = new FileInputStream(file);
        ParseContext parseContext = new ParseContext();
        AutoDetectParser parser = new AutoDetectParser();
        parser.parse(inputStream, handler, metadata, parseContext);
        inputStream.close();
        String title = metadata.get("title");
        String artist = metadata.get("xmpDM:artist");
        if (title == null || title.isEmpty()) {
            title = "unknown";
        }

        if (artist == null || artist.isEmpty()) {
            artist = "unknown";
        }

        return new Songs(title, artist, file.getPath());
    }
}
