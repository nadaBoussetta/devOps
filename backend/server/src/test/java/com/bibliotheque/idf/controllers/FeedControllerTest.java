@ExtendWith(MockitoExtension.class)
class FeedControllerTest {

    @Mock
    private FeedService feedService;

    @InjectMocks
    private FeedController feedController;

    @Test
    void getAllPosts() {
        when(feedService.getAllPosts()).thenReturn(List.of());

        ResponseEntity<?> response = feedController.getAllPosts();

        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    void getPostById() {
        when(feedService.getPostById(1L)).thenReturn(new PublicationDTO());

        ResponseEntity<?> response = feedController.getPostById(1L);

        assertEquals(200, response.getStatusCodeValue());
    }
}