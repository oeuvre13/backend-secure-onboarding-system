// @ExtendWith(MockitoExtension.class)
// public class JwtAuthFilterTest {

//     @InjectMocks
//     private JwtAuthFilter jwtAuthFilter;

//     @Mock
//     private JwtUtil jwtUtil;

//     @Mock
//     private SecurityUtil securityUtil;

//     @Mock
//     private UserDetailsService userDetailsService;

//     @BeforeEach
//     void setUp() {
//         jwtAuthFilter = new JwtAuthFilter(jwtUtil, securityUtil, userDetailsService);
//     }

//     @Test
//     void shouldAuthenticateValidToken() throws ServletException, IOException {
//         // Setup mock request/response/filterChain
//         MockHttpServletRequest request = new MockHttpServletRequest();
//         request.addHeader("Authorization", "Bearer validtoken");
//         MockHttpServletResponse response = new MockHttpServletResponse();
//         FilterChain filterChain = mock(FilterChain.class);

//         when(jwtUtil.validateToken("validtoken")).thenReturn(true);
//         when(jwtUtil.getEmailFromToken("validtoken")).thenReturn("test@example.com");

//         UserDetails userDetails = new org.springframework.security.core.userdetails.User(
//                 "test@example.com", "password", Collections.emptyList());

//         when(userDetailsService.loadUserByUsername("test@example.com")).thenReturn(userDetails);

//         jwtAuthFilter.doFilterInternal(request, response, filterChain);

//         verify(filterChain).doFilter(request, response);
//     }
// }
