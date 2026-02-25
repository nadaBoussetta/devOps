@ExtendWith(MockitoExtension.class)
class NotationControllerTest {

    @Mock
    private NotationService notationService;

    @InjectMocks
    private NotationController notationController;

    @Test
    void getNotationsByBibliotheque() {
        when(notationService.getNotationsByBibliotheque(1L))
                .thenReturn(List.of());

        ResponseEntity<?> response =
                notationController.getNotationsByBibliotheque(1L);

        assertEquals(200, response.getStatusCodeValue());
    }
}