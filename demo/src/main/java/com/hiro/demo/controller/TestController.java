package com.hiro.demo.controller;

import com.hiro.demo.environment.FireHill;
import com.hiro.demo.environment.events.Burned;
import com.hiro.demo.subject.Caster;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
public class TestController {

    public static void main(String[] args) {
        var caster = new Caster();

        var env = new FireHill()
                .addSubject(caster, Caster.class)
                .reload(new Burned());

        log.info("Caster: {}", caster);
        env.trigger();
        log.info("Caster: {}", caster);

    }

}
