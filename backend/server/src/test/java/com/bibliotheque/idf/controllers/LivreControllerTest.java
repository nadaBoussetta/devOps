@ExtendWith(MockitoExtension.class)
class LivreControllerTest {

    @Mock
    private LivreService livreService;

    @InjectMocks
    private LivreController livreController;

    @Test
    void rechercherLivre() {
        when(livreService.rechercherLivre("java"))
                .thenReturn(List.of());

        ResponseEntity<?> response =
                livreController.rechercherLivre("java");

        assertEquals(200, response.getStatusCodeValue());
    }
}