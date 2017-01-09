package com.keksipurkki;

import java.io.IOException;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;
import java.util.Date;
import java.util.HashMap;

import org.springframework.data.jpa.repository.JpaRepository;
 
/**
 * 
 * The repository class for currency operations
 *
 * Handles in-memory database hydration and rate updates from
 * http://api.fixer.io/latest.
 *
 */
public interface CurrencyRepository extends JpaRepository<Currency, String> {

  public static final Currency BASE_CURRENCY = new Currency("EUR", 1.0d);

  /* @todo: publish the retrieval date to the frontend */
  public static Date LAST_UPDATED = new Date();

  /* Loads the current exchange rates from disk */
  default public void loadRatesFromJSON() throws IOException {

    final String filename = "currency-rates.json";

    /* Clear all existing data*/
    findAll().forEach(c -> {
      delete(c);
    });

    ObjectMapper mapper = new ObjectMapper();

    Resource resource = new ClassPathResource(filename);
    Map<String,Object> rates = mapper.readValue(resource.getFile(), Map.class);
    rates = (Map<String,Object>)rates.get("rates");

    save(BASE_CURRENCY);

    for (Map.Entry<String,Object> rate : rates.entrySet()) {
      save(new Currency(rate.getKey(), (Double)rate.getValue()));
    }

  }

  /* @todo: update rates as described in http://fixer.io/ every day  */
  public static void loadRates() {

    String resource = "http://api.fixer.io/latest";
  
  }
 
}
