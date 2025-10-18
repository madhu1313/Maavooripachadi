package com.maavooripachadi.i18n.dto;


import java.util.Map;


public record I18nBundleResponse(String namespace, String locale, Map<String,String> entries) {}