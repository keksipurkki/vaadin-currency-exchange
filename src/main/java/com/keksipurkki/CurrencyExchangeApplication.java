/**
 * 
 * A simple Spring/Vaadin single-page app I wrote
 * to teach myself Java.
 *
 * The app is a basic currency exchange calculator. 
 *
 * @author Elias Toivanen <elkku.tolkku@gmail.com>
 *
 */

package com.keksipurkki;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class CurrencyExchangeApplication {

  /* Start the Spring Application */
  public static void main(String[] args) {
    SpringApplication.run(CurrencyExchangeApplication.class, args);
  }

  /**
   *
   * Fill in-memory database with currency data after the 
   * server boots.
   *
   */
  @Bean
  public CommandLineRunner loadData(CurrencyRepository repo) {

    return (args) -> {

      try {

        repo.loadRatesFromJSON(); 

      } catch (java.io.IOException e) {

        System.out.println(e.getMessage());

      }
    
    };
  
  }

}
