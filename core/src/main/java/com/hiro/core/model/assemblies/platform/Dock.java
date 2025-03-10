package com.hiro.core.model.assemblies.platform;

import com.hiro.core.exceptioin.GenericException;
import com.hiro.core.model.assemblies.postal.Postbox;
import com.hiro.core.model.enumeration.ErrorCode;
import com.hiro.core.model.enumeration.RunningState;
import com.hiro.core.model.parts.automation.Continuous;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class Dock implements Continuous {

    protected final Postbox postbox;

    protected RunningState state = RunningState.STOPPED;

    public Dock(Postbox postbox) {
        if (postbox == null) throw new GenericException(ErrorCode.DOCK_ERROR);
        this.postbox = postbox;
    }

    @Override
    public void start() {

    }

    @Override
    public void pause() {

    }

    @Override
    public void stop() {

    }

    @Override
    public void restart() {
        stop();
        start();
    }

    @Override
    public void destroy() {

    }

    @Override
    public RunningState getState() {
        return this.state;
    }

    protected abstract void init();

    private boolean isState(RunningState state) {
        if (this.state == null) throw new GenericException(ErrorCode.POSTAL_DESTROYED);
        return this.state.equals(state);
    }

}
