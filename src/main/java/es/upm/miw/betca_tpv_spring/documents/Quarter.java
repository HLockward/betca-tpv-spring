package es.upm.miw.betca_tpv_spring.documents;

import java.time.LocalDateTime;

public enum Quarter {
    Q1, Q2, Q3, Q4;

    public Quarter getQuarterFromDate(LocalDateTime date) {
        int quarter = (date.getMonthValue() / 3) + 1;
        switch (quarter) {
            case 1:
                return Q1;
            case 2:
                return Q2;
            case 3:
                return Q3;
            case 4:
                return Q4;
            default:
                return null;
        }
    }
}

