package devOps.dtos;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class HoraireDTOTest {

    @Test
    void shouldCreateHoraireWithAllArgsConstructor() {
        HoraireDTO dto = new HoraireDTO(
                12L,
                "Mardi",
                "10:00",
                "18:30"
        );

        assertThat(dto.getId()).isEqualTo(12L);
        assertThat(dto.getJourSemaine()).isEqualTo("Mardi");
        assertThat(dto.getHeureOuverture()).isEqualTo("10:00");
        assertThat(dto.getHeureFermeture()).isEqualTo("18:30");
    }

    @Test
    void shouldCreateWithNoArgsConstructorAndSetFields() {
        HoraireDTO dto = new HoraireDTO();
        
        dto.setId(8L);
        dto.setJourSemaine("Samedi");
        dto.setHeureOuverture("09:30");
        dto.setHeureFermeture("17:00");

        assertThat(dto.getId()).isEqualTo(8L);
        assertThat(dto.getJourSemaine()).isEqualTo("Samedi");
        assertThat(dto.getHeureOuverture()).matches("\\d{2}:\\d{2}");
        assertThat(dto.getHeureFermeture()).matches("\\d{2}:\\d{2}");
    }

    @Test
    void timesCanBeNullOrEmpty() {
        HoraireDTO dto = new HoraireDTO(3L, "Dimanche", null, null);
        
        assertThat(dto.getHeureOuverture()).isNull();
        assertThat(dto.getHeureFermeture()).isNull();
    }

    @Test
    void shouldHaveWorkingEqualsAndHashCode() {
        HoraireDTO a = new HoraireDTO(1L, "Lundi", "08:00", "20:00");
        HoraireDTO b = new HoraireDTO(1L, "Lundi", "08:00", "20:00");
        HoraireDTO c = new HoraireDTO(1L, "Lundi", "09:00", "19:00");

        assertThat(a).isEqualTo(b);
        assertThat(a.hashCode()).isEqualTo(b.hashCode());
        assertThat(a).isNotEqualTo(c);
    }

    @Test
    void toStringShouldContainAllFields() {
        HoraireDTO dto = new HoraireDTO(4L, "Vendredi", "13:00", "19:00");
        String str = dto.toString();

        assertThat(str).contains("id=4");
        assertThat(str).contains("jourSemaine=Vendredi");
        assertThat(str).contains("heureOuverture=13:00");
        assertThat(str).contains("heureFermeture=19:00");
    }
}