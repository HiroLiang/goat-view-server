package com.hiro.core.model.assemblies.carrier;

import com.hiro.core.CoreApplication;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest(classes = CoreApplication.class)
public class PojoTest {

    @Test
    void TestGoods() {
        Obj1 obj1 = new Obj1();
        Goods<?> goods1 = new Goods<>(obj1);
        var reference1 = goods1.get();
        log.info("obj1 - reference1: {}, {}", obj1, reference1);
        assertEquals(obj1, reference1);
        Goods<?> goods2 = new Goods<>(new Obj2());
        assertNotEquals(reference1, goods2.get());
    }

    @Data
    public static class Obj1 {
        private String name = "1";
    }

    @Data
    public static class Obj2 {
        private String name = "2";
    }

}
