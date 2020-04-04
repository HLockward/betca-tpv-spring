package es.upm.miw.betca_tpv_spring.dtos;

import es.upm.miw.betca_tpv_spring.documents.Quarter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class QuarterVATDto {

    private Quarter quarter;
    private List<TaxDto> taxes;

    public QuarterVATDto(Quarter quarter, TaxDto general, TaxDto reduced, TaxDto superReduced) {
        this.quarter = quarter;
        taxes = new ArrayList<>(Arrays.asList(general, reduced, superReduced));
    }

    public Quarter getQuarter() {
        return quarter;
    }

    public List<TaxDto> getTaxes() {
        return taxes;
    }

    @Override
    public String toString() {
        return "QuarterVATDto{" +
                "quarter=" + quarter +
                ", taxes=" + taxes +
                '}';
    }
}
