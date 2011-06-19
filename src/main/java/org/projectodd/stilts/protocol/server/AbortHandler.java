package org.projectodd.stilts.protocol.server;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.projectodd.stilts.StompException;
import org.projectodd.stilts.protocol.StompFrame;
import org.projectodd.stilts.protocol.StompFrame.Command;
import org.projectodd.stilts.protocol.StompFrame.Header;
import org.projectodd.stilts.spi.StompProvider;

public class AbortHandler extends AbstractControlFrameHandler {

    public AbortHandler(StompProvider server, ConnectionContext context) {
        super( server, context, Command.ABORT );
    }

    public void handleControlFrame(ChannelHandlerContext channelContext, StompFrame frame) {
        String transactionId = frame.getHeader( Header.TRANSACTION );
        try {
            getStompConnection().abort( transactionId );
        } catch (StompException e) {
            sendError( channelContext, "Unable to abort transaction: " + e.getMessage(), frame );
        }
    }

}