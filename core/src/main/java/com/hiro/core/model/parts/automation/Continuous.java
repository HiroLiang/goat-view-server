package com.hiro.core.model.parts.automation;

import com.hiro.core.model.enumeration.RunningState;

/**
 * A continuous class:
 * 1. Keep running since start
 * 2. Can be restarted after being stopped or paused
 * 3. Unusable after destroyed
 */
public interface Continuous {

    /**
     * Start the routine
     */
    void start();

    /**
     * Pause process: don't care about if the process stopped
     */
    void pause();

    /**
     * Stop process: would wait for process stopping
     */
    void stop();

    /**
     * Stop and Start
     */
    void restart();

    /**
     * Stop and release all resources
     */
    void destroy();

    /**
     * Let other class to known state of this
     * @return RunningState
     */
    RunningState getState();

}
