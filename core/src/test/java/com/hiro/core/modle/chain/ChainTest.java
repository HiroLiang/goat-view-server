package com.hiro.core.modle.chain;

import com.hiro.core.CoreApplication;
import com.hiro.core.model.unit.chain.Chainable;
import com.hiro.core.model.unit.chain.GenericChainable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest(classes = CoreApplication.class)
public class ChainTest {

    @Test
    void normalChain() {
        final DemoChainable demo1 = new DemoChainable(1);
        final DemoChainable demo2 = new DemoChainable(2);
        final DemoChainable demo3 = new DemoChainable(3);

        List<Chainable<?>> chain1 = List.of(demo1, demo2, demo3);
        log.info("chain1: {}", chain1);
        List<Chainable<?>> chain2 = demo1.chain(demo2).chain(demo3).get();
        log.info("chain2: {}", chain2);

        checkLists(chain1, chain2);
    }

    @Test
    void ifChain() {
        final DemoChainable demo1 = new DemoChainable(1);
        final DemoChainable demo2 = new DemoChainable(2);
        final DemoChainable demo3 = new DemoChainable(3);

        List<Chainable<?>> chain1 = List.of(demo1, demo3);
        log.info("chain if 1: {}", chain1);
        List<Chainable<?>> chain2 = demo1.chainIf(demo2, () -> false).chain(demo3).get();
        log.info("chain if 2: {}", chain2);

        checkLists(chain1, chain2);
    }

    void checkLists(List<Chainable<?>> chain1, List<Chainable<?>> chain2) {
        assertEquals(chain1.size(), chain2.size());

        for (int i = 0; i < chain1.size(); i++) {
            assertEquals(chain1.get(i), chain2.get(i));
        }
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    @ToString(callSuper = true)
    @AllArgsConstructor
    public static class DemoChainable extends GenericChainable {
        private int number;
    }


}
