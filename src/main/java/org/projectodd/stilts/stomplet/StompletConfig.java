package org.projectodd.stilts.stomplet;

public interface StompletConfig {
    
    StompletContext getStompletContext();
    
    String getProperty(String name);
    String[] getPropertyNames();
}