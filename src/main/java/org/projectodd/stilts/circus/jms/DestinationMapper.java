package org.projectodd.stilts.circus.jms;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Session;

import org.projectodd.stilts.spi.Headers;

public interface DestinationMapper {
    
    /** For subscriptions. */
    DestinationSpec map(Session session, String destinationName, Headers headers) throws JMSException;
    
    /** For sending. */
    Destination map(Session session, String destinationName) throws JMSException;

}