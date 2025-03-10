package com.hiro.core.model.parts.automation;

import com.hiro.core.model.enumeration.RunningState;

public interface Continuous {

    void start();

    void pause();

    void stop();

    void restart();

    void destroy();

    RunningState getState();

}
