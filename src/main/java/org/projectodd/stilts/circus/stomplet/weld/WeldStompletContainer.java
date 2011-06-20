package org.projectodd.stilts.circus.stomplet.weld;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;

import org.jboss.weld.bootstrap.WeldBootstrap;
import org.jboss.weld.bootstrap.api.Environments;
import org.jboss.weld.manager.BeanManagerImpl;
import org.projectodd.stilts.StompException;
import org.projectodd.stilts.circus.stomplet.NoSuchStompletException;
import org.projectodd.stilts.circus.stomplet.SimpleStompletContainer;
import org.projectodd.stilts.stomplet.Stomplet;

public class WeldStompletContainer extends SimpleStompletContainer {

    public WeldStompletContainer(boolean includeCore) {
        this.includeCore = includeCore;
    }

    public void addStomplet(String pattern, String className) throws StompException {
        try {
            Stomplet stomplet = newStomplet( className );
            addStomplet( pattern, stomplet );
        } catch (ClassNotFoundException e) {
            throw new StompException( e );
        }
    }

    protected Stomplet newStomplet(String className) throws ClassNotFoundException, NoSuchStompletException {

        Class<?> stompletImplClass = this.deployment.getClassLoader().loadClass( className );
        Set<Bean<?>> beans = this.beanManager.getBeans( stompletImplClass );

        if (beans.isEmpty()) {
            throw new NoSuchStompletException( className );
        }

        Bean<? extends Object> bean = this.beanManager.resolve( beans );

        CreationalContext<?> creationalContext = this.beanManager.createCreationalContext( bean );
        Stomplet stomplet = (Stomplet) beanManager.getReference( bean, stompletImplClass, creationalContext );

        return stomplet;
    }
    
    public void addBeanDeploymentArchive(CircusBeanDeploymentArchive archive) {
        this.archives.add( archive );
    }

    public void start() throws Exception {
        System.err.println( "++++++++++++++++" );
        System.err.println( "++++++++++++++++" );
        System.err.println( "++++++++++++++++" );
        super.start();
        System.err.println( "++++++++++++++++" );
        System.err.println( "++++++++++++++++" );
        System.err.println( "++++++++++++++++" );
        startWeld();
        System.err.println( "++++++++++++++++" );
        System.err.println( "++++++++++++++++" );
        System.err.println( "++++++++++++++++" );
    }

    protected void startWeld() {
        this.bootstrap = new WeldBootstrap();
        this.deployment = new CircusDeployment();

        this.aggregationArchive = new AggregatingBeanDeploymentArchive( "things", this.deployment.getClassLoader() );
        System.err.println( "$$$ aggregationArchive: " + this.aggregationArchive );
        for (CircusBeanDeploymentArchive each : this.archives) {
            this.aggregationArchive.addMemberArchive( each );
            //this.deployment.addArchive( each );
        }
        if (this.includeCore) {
            CoreStompletBeanDeploymentArchive core = new CoreStompletBeanDeploymentArchive();
            this.aggregationArchive.addMemberArchive( core );
            //this.deployment.addArchive( core );
        }
        this.deployment.addArchive( this.aggregationArchive );
        this.bootstrap.startContainer( Environments.SE, this.deployment );
        this.bootstrap.startInitialization();
        this.bootstrap.deployBeans();
        this.bootstrap.validateBeans();

        this.beanManager = this.bootstrap.getManager( this.aggregationArchive );
        System.err.println( "$$$ beanManager: " + this.beanManager );
        
        for (CircusBeanDeploymentArchive each : this.archives) {
            System.err.println( " ===> " + each + " // " + this.bootstrap.getManager( this.aggregationArchive ) );
        }
    }

    public void stop() throws Exception {
        stopWeld();
        super.stop();
    }

    protected void stopWeld() {
        this.bootstrap.shutdown();
    }

    private boolean includeCore;
    private CircusDeployment deployment;
    private List<CircusBeanDeploymentArchive> archives = new ArrayList<CircusBeanDeploymentArchive>();;
    private AggregatingBeanDeploymentArchive aggregationArchive;
    private WeldBootstrap bootstrap;
    private BeanManagerImpl beanManager;
}
