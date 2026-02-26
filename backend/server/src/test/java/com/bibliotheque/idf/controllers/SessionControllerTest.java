package devOps.controllers;

import devOps.services.SessionService; // adapte selon tes vraies dépendances
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests de base du SessionController")
class SessionControllerTest {

    @Mock
    private SessionService sessionService; // adapte au vrai service injecté

    @InjectMocks
    private SessionController sessionController;

    @Test
    @DisplayName("Le controller doit être instanciable et non null")
    void controllerShouldBeInstantiable() {
        assertThat(sessionController).isNotNull();
    }

    @Test
    @DisplayName("La classe SessionController doit être chargée correctement")
    void classShouldLoad() {
        assertThatCode(() -> Class.forName("devOps.controllers.SessionController"))
                .doesNotThrowAnyException();
    }
}