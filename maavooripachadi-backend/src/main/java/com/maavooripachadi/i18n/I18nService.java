package com.maavooripachadi.i18n;


import com.maavooripachadi.i18n.dto.I18nUpsertRequest;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.io.IOException;
import java.io.StringReader;
import java.util.*;


@Service
public class I18nService {
  private final I18nStringRepository repo;


  public I18nService(I18nStringRepository repo) { this.repo = repo; }


  /**
   * Returns a bundle map with fallback chain: e.g., "en-IN" -> ["en-IN", "en", "default"].
   */
  @Transactional(readOnly = true)
  public Map<String, String> getBundle(String namespace, String locale){
    List<String> chain = buildFallbackChain(locale);
    List<I18nString> list = repo.findByNamespaceAndLocales(namespace, chain);
    Map<String, String> out = new LinkedHashMap<>();
// Apply from least specific to most specific so later entries override earlier.
    Collections.reverse(chain);
    for (String loc : chain){
      for (I18nString i : list){
        if (loc.equalsIgnoreCase(i.getLocale()) && (i.getApproved() == null || i.getApproved())){
          out.put(i.getKey(), i.getText());
        }
      }
    }
    return out;
  }


  @Transactional
  public I18nString upsert(I18nUpsertRequest req){
    var existing = repo.findByNamespaceAndKeyAndLocale(req.namespace(), req.key(), req.locale()).orElse(null);
    I18nString row = (existing == null) ? new I18nString() : existing;
    row.setNamespace(req.namespace());
    row.setKey(req.key());
    row.setLocale(req.locale());
    row.setText(req.text());
    row.setTags(req.tags());
    row.setApproved(req.approved() == null ? Boolean.TRUE : req.approved());
    row.setChecksum(Integer.toHexString(Objects.hash(req.text(), req.tags(), req.locale())));
    return repo.save(row);
  }


  @Transactional
  public void delete(String namespace, String key, String locale){
    repo.findByNamespaceAndKeyAndLocale(namespace, key, locale).ifPresent(repo::delete);
  }


  @Transactional
  public int importCsv(String csv) throws IOException {
    int imported = 0;
    try (CSVParser parser = CSVFormat.DEFAULT
            .withSkipHeaderRecord()
.parse(new StringReader(csv))) {
        for (CSVRecord r : parser) {
String ns = r.get("namespace");
String key = r.get("key");
String loc = r.get("locale");
String text = r.get("text");
String tags = r.isMapped("tags") ? r.get("tags") : null;
Boolean approved = r.isMapped("approved") && !r.get("approved").isBlank() ? Boolean.valueOf(r.get("approved")) : Boolean.TRUE;
var req = new I18nUpsertRequest(ns, key, loc, text, tags, approved);
upsert(req);
imported++;
        }
        }
        return imported;
}


@Transactional(readOnly = true)
public String exportCsv(String namespace, String locale){
  StringBuilder sb = new StringBuilder();
  sb.append("namespace,key,locale,text,tags,approved\n");
  List<I18nString> list;
  if (namespace != null && locale != null) {
    list = repo.findByNamespaceAndLocale(namespace, locale);
  } else if (namespace != null) {
    list = repo.findByNamespaceAndLocales(namespace, List.of(locale == null ? "default" : locale));
  } else {
    list = repo.findAll();
  }
  for (I18nString i : list){
    sb.append(escape(i.getNamespace())).append(',')
            .append(escape(i.getKey())).append(',')
            .append(escape(i.getLocale())).append(',')
            .append(escape(i.getText())).append(',')
            .append(escape(i.getTags())).append(',')
            .append(i.getApproved() == null ? "" : i.getApproved()).append('\n');
  }
  return sb.toString();
}


private String escape(String v){
  if (v == null) return "";
  if (v.contains(",") || v.contains("\n") || v.contains("\"")){
    return '"' + v.replace("\"", "\"\"") + '"';
  }
  return v;
}


private List<String> buildFallbackChain(String locale){
  if (locale == null || locale.isBlank()) return List.of("default");
  if (locale.contains("-")){
    String[] parts = locale.split("-");
    return List.of("default", parts[0], locale);
  }
  return List.of("default", locale);
}
}