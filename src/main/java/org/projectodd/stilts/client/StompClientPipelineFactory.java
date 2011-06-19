package org.projectodd.stilts.client;

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.projectodd.stilts.logging.Logger;
import org.projectodd.stilts.protocol.DebugHandler;
import org.projectodd.stilts.protocol.StompFrameDecoder;
import org.projectodd.stilts.protocol.StompFrameEncoder;
import org.projectodd.stilts.protocol.StompMessageDecoder;
import org.projectodd.stilts.protocol.StompMessageEncoder;
import org.projectodd.stilts.protocol.client.ClientContext;
import org.projectodd.stilts.protocol.client.ClientMessageHandler;
import org.projectodd.stilts.protocol.client.ClientReceiptHandler;
import org.projectodd.stilts.protocol.client.ConnectedHandler;

public class StompClientPipelineFactory implements ChannelPipelineFactory {

    public StompClientPipelineFactory(AbstractStompClient client, ClientContext clientContext) {
        this.client = client;
        this.clientContext = clientContext;
    }

    @Override
    public ChannelPipeline getPipeline() throws Exception {
        ChannelPipeline pipeline = Channels.pipeline();

        pipeline.addLast( "debug-head", new DebugHandler( log( "DEBUG.head" ) ) );

        pipeline.addLast( "stomp-frame-decoder", new StompFrameDecoder( log( "frame.decoder" ) ) );
        pipeline.addLast( "stomp-frame-encoder", new StompFrameEncoder( log( "frame.encoder" ) ) );
        pipeline.addLast( "debug-frame-encoders", new DebugHandler( log( "DEBUG.frame-encoders" ) ) );

        pipeline.addLast( "stomp-client-connect", new ConnectedHandler( clientContext ) );
        pipeline.addLast( "stomp-client-receipt", new ClientReceiptHandler( clientContext ) );

        pipeline.addLast( "stomp-message-encoder", new StompMessageEncoder( log( "message.encoder" ) ) );
        pipeline.addLast( "stomp-message-decoder", new StompMessageDecoder( log( "message.decoder" ), new ClientStompMessageFactory( this.client ) ) );
        pipeline.addLast( "debug-message-encoders", new DebugHandler( log( "DEBUG.message-encoders" ) ) );

        pipeline.addLast( "stomp-client-message-handler", new ClientMessageHandler( clientContext ) );

        return pipeline;
    }

    Logger log(String name) {
        return this.clientContext.getLoggerManager().getLogger( "pipeline.stomp." + name );
    }

    private AbstractStompClient client;
    private ClientContext clientContext;

}