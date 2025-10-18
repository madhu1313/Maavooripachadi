package com.maavooripachadi.engage;


import java.util.Map;


/** Very small {{var}} placeholder renderer. Not for untrusted HTML. */
public class TemplateRenderer {
    public String render(String template, Map<String, Object> vars){
        if (template == null) return null;
        if (vars == null || vars.isEmpty()) return template;
        String out = template;
        for (Map.Entry<String, Object> e : vars.entrySet()) {
            String key = "{{" + e.getKey() + "}}";
            out = out.replace(key, e.getValue() == null ? "" : String.valueOf(e.getValue()));
        }
        return out;
    }
}