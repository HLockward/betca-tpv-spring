package es.upm.miw.betca_tpv_spring.dtos;

import es.upm.miw.betca_tpv_spring.documents.Tax;

import java.math.BigDecimal;

public class TaxDto {

    private Tax tax;
    private double percentageApplied;
    private BigDecimal taxableAmount;
    private BigDecimal vat;

    public TaxDto(Tax tax, double percentageApplied) {
        this.tax = tax;
        this.percentageApplied = percentageApplied;
        this.taxableAmount = new BigDecimal(0);
        this.vat = new BigDecimal(0);
    }

    public Tax getTax() {
        return tax;
    }

    public void setTax(Tax tax) {
        this.tax = tax;
    }

    public double getPercentageApplied() {
        return percentageApplied;
    }

    public BigDecimal getTaxableAmount() {
        return taxableAmount;
    }

    public void setTaxableAmount(BigDecimal taxableAmount) {
        this.taxableAmount = taxableAmount;
    }

    public BigDecimal getVat() {
        return vat;
    }

    public void setVat(BigDecimal vat) {
        this.vat = vat;
    }

    @Override
    public String toString() {
        return "TaxDto{" +
                "tax=" + tax +
                ", percentageApplied=" + percentageApplied +
                ", taxableAmount=" + taxableAmount +
                ", vat=" + vat +
                '}';
    }
}