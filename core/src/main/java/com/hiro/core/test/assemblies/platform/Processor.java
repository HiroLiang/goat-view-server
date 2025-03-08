package com.hiro.core.test.assemblies.platform;

import com.hiro.core.test.components.firearm.Magazine;
import com.hiro.core.test.components.firearm.Receiver;

public abstract class Processor<T> implements Receiver<T> {

    /**
     * Implement about Processor
     */
    private volatile boolean isProcessing = false;

    private volatile boolean pause = false;

    public boolean isProcessing() {
        return isProcessing;
    }

    public boolean isPaused() {
        return pause;
    }

    public void pause() {
        pause = true;
    }

    public void resume() {
        pause = false;
    }

    /**
     * Implement of Receiver
     */
    protected Magazine<T> magazine;

    protected boolean loaded;

    @Override
    public void load(Magazine<T> magazine) {
        this.magazine = magazine;
        loaded = true;
    }

    @Override
    public void eject() {
        this.magazine = null;
        loaded = false;
    }

    @Override
    public Magazine<T> change(Magazine<T> magazine) {
        if (magazine == null || !magazine.isEmpty()) {
            load(magazine);
            return null;
        } else {
            Magazine<T> restMagazine = this.magazine;
            load(magazine);
            return restMagazine;
        }
    }

}
