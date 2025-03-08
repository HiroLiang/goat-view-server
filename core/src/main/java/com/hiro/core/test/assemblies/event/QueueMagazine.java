package com.hiro.core.test.assemblies.event;

import com.hiro.core.test.components.firearm.Magazine;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class QueueMagazine<T> implements Magazine<T> {

    private final Queue<T> queue = new ConcurrentLinkedQueue<>();

    @Override
    public Magazine<T> loadRound(T round) {
        queue.offer(round);
        return this;
    }

    @Override
    public Magazine<T> loadRounds(List<T> rounds) {
        for (T round : rounds) {
            queue.offer(round);
        }
        return this;
    }

    @Override
    public T feed() {
        return queue.poll();
    }

    @Override
    public boolean isEmpty() {
        return queue.isEmpty();
    }
}
