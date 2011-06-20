/*
 * Copyright 2008-2011 Red Hat, Inc, and individual contributors.
 * 
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 * 
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.projectodd.stilts.circus.server;

import java.util.HashSet;

import javax.jms.XAConnection;
import javax.jms.XAConnectionFactory;

import org.hornetq.api.core.TransportConfiguration;
import org.hornetq.core.config.Configuration;
import org.hornetq.core.config.impl.ConfigurationImpl;
import org.hornetq.core.remoting.impl.invm.InVMAcceptorFactory;
import org.hornetq.core.remoting.impl.invm.InVMConnectorFactory;
import org.hornetq.core.server.HornetQServer;
import org.hornetq.core.server.HornetQServers;
import org.hornetq.jms.client.HornetQXAConnectionFactory;
import org.hornetq.jms.server.impl.JMSServerManagerImpl;
import org.projectodd.stilts.circus.jms.server.JMSCircusServer;

public class HornetQCircusServer extends JMSCircusServer {

    public HornetQCircusServer() {
        super();
    }
    
    public HornetQCircusServer(int port) {
        super( port );
    }
    
    public void start() throws Throwable {
        startHornetQ();
        super.start();
    }
    
    protected void startHornetQ() throws Exception {
        Configuration config = getConfiguration();
        HornetQServer hornetQServer = HornetQServers.newHornetQServer( config ); 
        this.jmsManager = new JMSServerManagerImpl( hornetQServer );
        this.jmsManager.start();
        XAConnectionFactory cf = new HornetQXAConnectionFactory( false, new TransportConfiguration(InVMConnectorFactory.class.getName()));
        XAConnection connection = cf.createXAConnection();
        connection.start();
        setConnection( connection );
    }
    
    public void stop() throws Throwable {
        super.stop();
        stopHornetQ();
    }
    
    protected void stopHornetQ() throws Exception {
        this.jmsManager.stop();
    }
    
    public void addQueue(String name) throws Exception {
        this.jmsManager.createQueue( false, name, null, false );
    }
    
    public void addTopic(String name) throws Exception {
        this.jmsManager.createTopic( false, name );
    }
    
    protected Configuration getConfiguration() {
        ConfigurationImpl config = new ConfigurationImpl();
        
        HashSet<TransportConfiguration> transports = new HashSet<TransportConfiguration>();
        transports.add(new TransportConfiguration(InVMAcceptorFactory.class.getName()));
        config.setAcceptorConfigurations(transports);
        config.setPersistenceEnabled( false );
        config.setSecurityEnabled( false );
        return config;
    }

    private JMSServerManagerImpl jmsManager;


}
