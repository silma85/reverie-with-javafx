package it.vermilionsands.reverie.configuration;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class ApplicationConfigurationTest {
    
    @Test
    void testLoadController() {
        
        ApplicationConfiguration c = new ApplicationConfiguration();
        assertThrows(ExceptionInInitializerError.class, () -> c.loadController("/gui/TestPane.fxml"));

    }
}
