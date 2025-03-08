package com.hiro.core.test.components.platform;

public interface LifeCycle {

    boolean isAlive();

    void destroy();

    void beforeCreate();

    void afterCreate();

    void beforeDestroy();

    void afterDestroy();

}
