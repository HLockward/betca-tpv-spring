package es.upm.miw.betca_tpv_spring.business_services;

import es.upm.miw.betca_tpv_spring.TestConfig;
import es.upm.miw.betca_tpv_spring.exceptions.FileException;
import es.upm.miw.betca_tpv_spring.exceptions.PdfException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertThrows;

@TestConfig
public class FileServiceIT {

    @Autowired
    private FileService fileService;

    @Test
    void testReadFileError() {
        assertThrows(FileException.class, () -> fileService.read("%-.,$"));
    }
}
