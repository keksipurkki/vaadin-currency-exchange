package com.keksipurkki;

import java.math.BigDecimal;
import java.math.MathContext;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
/**
 *
 * A domain object representing currency.
 *
 * A currency consists of a symbol (EUR, SEK, USD...) and 
 * a rate relative to base currency (which is Euro).
 *
 */
public class Currency {

  @Id
  private String symbol;

  private BigDecimal rate;

  protected Currency() {
  }

  public Currency(String symbol, Number rate) {
    this.symbol = symbol;
    this.rate = new BigDecimal((Double)rate);
  }

  public String getSymbol() {
    return symbol;
  }

  public BigDecimal getRate() {
    return rate;
  }

  /* convert quantity amount of currency `from` to currency `to` */
  public static BigDecimal convert(BigDecimal quantity, Currency from, Currency to) {

    BigDecimal conversionFactor = to.getRate().divide(from.getRate(), MathContext.DECIMAL128);
    return quantity.multiply(conversionFactor, MathContext.DECIMAL128);

  }

  @Override
  public String toString() {
    return String.format("Currency[symbol='%s', rate=%f]", symbol, rate);
  }

}
