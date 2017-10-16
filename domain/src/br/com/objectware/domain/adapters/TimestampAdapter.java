package br.com.objectware.domain.adapters;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * Timestamp adaptor class (used in xml parsing)
 * 
 * @author Luciano M. Christofoletti
 * @since 12/Apr/2015
 * 
 * Useful docs:
 *     http://stackoverflow.com/questions/4216745/java-string-to-date-conversion
 *     http://stackoverflow.com/questions/2519432/jaxb-unmarshal-timestamp
 */
public class TimestampAdapter extends XmlAdapter<String, Timestamp> {
    
    /** Timestamp adapter used in xml parsing */
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    
    @Override
    public String marshal(Timestamp timestamp) throws Exception {
        return TimestampAdapter.dateFormat.format(timestamp);
    }
    
    @Override
    public Timestamp unmarshal(String timestamp) throws ParseException {
        return new Timestamp(TimestampAdapter.dateFormat.parse(timestamp).getTime());
    }
    
}
