package com.maavooripachadi.i18n;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


import java.util.List;
import java.util.Optional;


public interface I18nStringRepository extends JpaRepository<I18nString, Long> {
    List<I18nString> findByNamespaceAndLocale(String namespace, String locale);


    @Query("SELECT i FROM I18nString i WHERE i.namespace = :ns AND i.locale IN :locales")
    List<I18nString> findByNamespaceAndLocales(String ns, List<String> locales);


    Optional<I18nString> findByNamespaceAndKeyAndLocale(String namespace, String key, String locale);


    @Query("SELECT i FROM I18nString i WHERE i.namespace = :ns AND i.key = :k AND i.locale IN :locales")
    List<I18nString> findByNsKeyAndLocales(String ns, String k, List<String> locales);
}