package com.tinytotrend.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/debug")
public class MappingDebugController {

    @Autowired
    private RequestMappingHandlerMapping handlerMapping;

    @GetMapping("/mappings")
    public Map<String, Set<String>> getMappings() {
        return handlerMapping.getHandlerMethods().entrySet().stream()
                .flatMap(e -> {
                    var info = e.getKey();
                    var patternsCond = info.getPatternsCondition();
                    var methodsCond = info.getMethodsCondition();

                    var patterns = (patternsCond != null) ? patternsCond.getPatterns() : Set.of("/");
                    var methods = (methodsCond != null && !methodsCond.getMethods().isEmpty())
                            ? methodsCond.getMethods().stream().map(Object::toString).collect(Collectors.toSet())
                            : Set.of("ALL");

                    return patterns.stream().map(p -> Map.entry(p, methods));
                })
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a,b)->a));
    }
}
