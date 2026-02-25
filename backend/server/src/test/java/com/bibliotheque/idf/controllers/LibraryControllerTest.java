@ExtendWith(MockitoExtension.class)
class LibraryControllerTest {

    @Mock
    private LibraryService libraryService;

    @InjectMocks
    private LibraryController libraryController;

    @Test
    void getAllBibliotheques() {
        when(libraryService.getAllBibliotheques()).thenReturn(List.of());

        ResponseEntity<?> response = libraryController.getAllBibliotheques();

        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    void getBibliothequeById() {
        when(libraryService.getBibliothequeById(1L))
                .thenReturn(new LibraryResponseDTO());

        ResponseEntity<?> response = libraryController.getBibliothequeById(1L);

        assertEquals(200, response.getStatusCodeValue());
    }
}