package com.hiro.core.platform;

import com.hiro.core.model.assemblies.postman.PostNetwork;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@Qualifier("Generic")
public class GenericNetwork extends PostNetwork {


}
