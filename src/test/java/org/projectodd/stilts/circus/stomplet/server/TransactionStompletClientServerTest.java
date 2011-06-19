package org.projectodd.stilts.circus.stomplet.server;

import static org.junit.Assert.*;

import org.junit.Test;
import org.projectodd.stilts.StompMessages;
import org.projectodd.stilts.client.ClientSubscription;
import org.projectodd.stilts.client.ClientTransaction;
import org.projectodd.stilts.logging.SimpleLoggerManager.Level;

public class TransactionStompletClientServerTest extends AbstractStompletClientServerTest {

    static {
        SERVER_ROOT_LEVEL = Level.NONE;
        CLIENT_ROOT_LEVEL = Level.NONE;
    }

    @Test
    public void testClientTransaction() throws Exception {
        client.connect();

        ClientSubscription subscription1 = client.subscribe( "/topics/foo" ).withMessageHandler( accumulator( "one" ) ).start();
        ClientSubscription subscription2 = client.subscribe( "/topics/foo" ).withMessageHandler( accumulator( "two" ) ).start();

        ClientTransaction tx = client.begin();

        for (int i = 0; i < 10; ++i) {
            tx.send( StompMessages.createStompMessage( "/topics/foo", "What? " + i ) );
        }

        Thread.sleep( 1000 );

        assertTrue( accumulator( "one" ).isEmpty() );
        assertTrue( accumulator( "two" ).isEmpty() );

        tx.commit();

        Thread.sleep( 1000 );

        subscription1.unsubscribe();
        subscription2.unsubscribe();

        client.disconnect();

        assertEquals( 10, accumulator( "one" ).size() );
        assertEquals( 10, accumulator( "two" ).size() );
        
        System.err.println( accumulator("one").messageIds() );
        System.err.println( accumulator("two").messageIds() );
    }

    @Test
    public void testClientTransactionAborted() throws Exception {
        client.connect();

        ClientSubscription subscription1 = client.subscribe( "/topics/foo" ).withMessageHandler( accumulator( "one" ) ).start();
        ClientSubscription subscription2 = client.subscribe( "/topics/foo" ).withMessageHandler( accumulator( "two" ) ).start();

        ClientTransaction tx = client.begin();

        for (int i = 0; i < 10; ++i) {
            tx.send( StompMessages.createStompMessage( "/topics/foo", "What? " + i ) );
        }

        Thread.sleep( 1000 );

        tx.abort();

        Thread.sleep( 1000 );

        subscription1.unsubscribe();
        subscription2.unsubscribe();

        client.disconnect();

        assertTrue( accumulator( "one" ).isEmpty() );
        assertTrue( accumulator( "two" ).isEmpty() );
        
    }
}