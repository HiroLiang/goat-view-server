package com.hiro.demo.subject;

import com.hiro.core.model.subject.Subject;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Setter
@Getter
@ToString
@Slf4j
@EqualsAndHashCode(callSuper = true)
public class Caster extends Subject {

    private int life = 100;

}
