package es.upm.miw.betca_tpv_spring.business_controllers;

import es.upm.miw.betca_tpv_spring.documents.Tax;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class ShoppingLine {
    private Tax tax;
    private BigDecimal percentageApplied;
    private BigDecimal taxableAmount;
    private BigDecimal vat;

    public ShoppingLine(Tax tax, BigDecimal percentageApplied, BigDecimal totalAmount) {
        this.tax = tax;
        this.percentageApplied = percentageApplied;
        this.taxableAmount = totalAmount.divide(BigDecimal.ONE.add(percentageApplied), 4, RoundingMode.HALF_UP);
        this.vat = totalAmount.subtract(taxableAmount);
    }

    public Tax getTax() {
        return tax;
    }

    public BigDecimal getPercentageApplied() {
        return percentageApplied;
    }

    public BigDecimal getTaxableAmount() {
        return taxableAmount;
    }

    public BigDecimal getVat() {
        return vat;
    }

    @Override
    public String toString() {
        return "ShoppingLine{" +
                "tax=" + tax +
                ", percentageApplied=" + percentageApplied +
                ", taxableAmount=" + taxableAmount +
                ", vat=" + vat +
                '}';
    }
}
