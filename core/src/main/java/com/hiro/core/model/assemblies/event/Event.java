package com.hiro.core.model.assemblies.event;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

@Slf4j
@Data
public abstract class Event {

    private final String id = UUID.randomUUID().toString();

    private final EventService eventService;

    private final Type type;

    protected Event(EventService eventService, Type type) {
        this.eventService = eventService;
        this.type = type;
    }

    /**
     * 1. Try to execute event, even if it's a rollback event.
     * 2. Must record success events ( if needed to send ) to let undo can know what events success.
     */
    public void execute() {
        try {
            switch (this.type) {
                case PROCESS:
                    eventService.process(this);
                    break;
                case ROLLBACK:
                    eventService.rollback(this);
                    break;
            }
        } catch (Exception e) {
            log.error("Event: [{} - {} - {}] execute failed... Try to trigger undo method.",
                    this.getClass().getSimpleName(), this.type, this.id, e);
            this.undoSucceeded();
        }
    }

    /**
     * Process event ( Might contain other events )
     */
    abstract void process();

    /**
     * If Event success but caller failed ( the event who sending this event ),
     * process rollback method to undo process()
     */
    abstract void rollback();

    /**
     * If process or rollback failed, undo success events.
     * ( process and rollback are rapped with transaction. Just need to send opposite events )
     */
    abstract void undoSucceeded();

    /**
     * To record current event trigger type
     */
    public enum Type {
        ROLLBACK,
        PROCESS
    }
}
