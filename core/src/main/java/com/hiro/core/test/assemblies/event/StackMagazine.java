package com.hiro.core.test.assemblies.event;

import com.hiro.core.test.components.firearm.Magazine;

import java.util.Deque;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;

public class StackMagazine<T> implements Magazine<T> {

    private final Deque<T> stack = new ConcurrentLinkedDeque<>();

    @Override
    public Magazine<T> loadRound(T round) {
        stack.push(round);
        return this;
    }

    @Override
    public Magazine<T> loadRounds(List<T> rounds) {
        for (T round : rounds) {
            stack.push(round);
        }
        return this;
    }

    @Override
    public T feed() {
        return stack.poll();
    }

    @Override
    public boolean isEmpty() {
        return stack.isEmpty();
    }
}
