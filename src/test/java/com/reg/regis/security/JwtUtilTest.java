// public class JwtUtilTest {

//     private JwtUtil jwtUtil;

//     @BeforeEach
//     void setUp() {
//         jwtUtil = new JwtUtil();
//         ReflectionTestUtils.setField(jwtUtil, "jwtSecret", "TestSecretKeyTestSecretKeyTestSecretKey");
//         ReflectionTestUtils.setField(jwtUtil, "jwtExpirationMs", 3600000); // 1 hour
//     }

//     @Test
//     void shouldGenerateAndValidateToken() {
//         String email = "test@example.com";
//         String token = jwtUtil.generateToken(email);

//         assertNotNull(token);
//         assertTrue(jwtUtil.validateToken(token));
//         assertEquals(email, jwtUtil.getEmailFromToken(token));
//     }
// }
