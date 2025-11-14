package com.maavooripachadi.engage;

import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class TemplateRendererTest {

    private final TemplateRenderer renderer = new TemplateRenderer();

    @Test
    void returnsNullWhenTemplateIsNull() {
        assertNull(renderer.render(null, Map.of("name", "Madhu")));
    }

    @Test
    void returnsTemplateWhenVarsEmpty() {
        String template = "Hello {{name}}";
        assertEquals(template, renderer.render(template, Collections.emptyMap()));
    }

    @Test
    void replacesPlaceholdersWithMatchingValues() {
        String template = "Hello {{name}}, order {{order}} is ready";
        String rendered = renderer.render(template, Map.of(
                "name", "Madhu",
                "order", 42
        ));

        assertEquals("Hello Madhu, order 42 is ready", rendered);
    }

    @Test
    void replacesNullValuesWithEmptyString() {
        String template = "Hi {{name}}!";
        Map<String, Object> vars = new java.util.HashMap<>();
        vars.put("name", null);
        String rendered = renderer.render(template, vars);

        assertEquals("Hi !", rendered);
    }
}
