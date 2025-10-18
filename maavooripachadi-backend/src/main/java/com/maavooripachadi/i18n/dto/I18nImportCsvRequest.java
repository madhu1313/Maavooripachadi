package com.maavooripachadi.i18n.dto;


import jakarta.validation.constraints.NotBlank;


/**
 * Accept raw CSV content (UTF-8). Columns: namespace,key,locale,text,tags,approved
 */
public record I18nImportCsvRequest(@NotBlank String csv) {}