package com.auth;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@DataJpaTest
@ExtendWith(SpringExtension.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserJpaEntityRepositoryTest {

//    @Autowired
//    private TestEntityManager entityManager;
//
//    @Autowired
//    private UserRepository userRepository;
//
//    private UserJpaEntity buildUser(int id) {
//        UserJpaEntity user = new UserJpaEntity();
//        user.setUserId("01E48SD97BMWHAW82D229T0C7K");
//        user.setCoachName("testCoach" + id);
//        user.setEmail("test" + id + "@example.com");
//        user.setPassword("password");
//        user.setIsActive(true);
//        user.setCreatedAt(new Date());
//        user.setUpdatedAt(new Date());
//        user.setLang("fr_FR");
//        user.setRoles(List.of(UserRole.SIMPLE_USER.toString()));
//        return user;
//    }
//
//    @Test
//    @Transactional
//    public void testFindByCoachNameSucceed() {
//        // Créer un utilisateur de test
//        UserJpaEntity user = new UserJpaEntity();
//        user.setUserId("01E48SD97BMWHAW82D229T0C7K");
//        user.setCoachName("testCoach");
//        user.setEmail("test@example.com");
//        user.setPassword("password");
//        user.setIsActive(true);
//        user.setCreatedAt(new Date());
//        user.setUpdatedAt(new Date());
//        user.setLang("fr_FR");
//        user.setRoles(List.of(UserRole.SIMPLE_USER.toString()));
//
//        // Sauvegarder l'utilisateur dans la base de données de test
//        entityManager.persist(user);
//        entityManager.flush();
//
//        // Récupérer l'utilisateur par son coachName
//        Optional<UserJpaEntity> foundUser = userRepository.findByUsername("testCoach");
//
//        // Vérifier que l'utilisateur a été trouvé et que les détails sont corrects
//        assertNotNull(foundUser.get());
//        assertEquals("testCoach", foundUser.get().getUsername());
//        assertEquals("test@example.com", foundUser.get().getEmail());
//    }
//
//
//    @Test
//    @Transactional
//    public void testFindByCoachNameNotFoundReturnsNull() {
//        // Récupérer l'utilisateur par son coachName
//        Optional<UserJpaEntity> foundUser = userRepository.findByUsername("testCoach");
//        assertFalse(foundUser.isPresent());
//    }
//
//    @Test
//    @Transactional
//    public void testListAllUsersSucceed() {
//        UserJpaEntity user_1 = new UserJpaEntity();
//        user_1.setUserId("01E48SD97BMWHAW82D229T0C7K");
//        user_1.setCoachName("testCoach");
//        user_1.setEmail("test@example.com");
//        user_1.setPassword("password");
//        user_1.setIsActive(true);
//        user_1.setCreatedAt(new Date());
//        user_1.setUpdatedAt(new Date());
//        user_1.setLang("fr_FR");
//        user_1.setRoles(List.of(UserRole.SIMPLE_USER.toString()));
//
//        // Sauvegarder l'utilisateur dans la base de données de test
//        entityManager.persist(user_1);
//        entityManager.flush();
//
//        UserJpaEntity user_2 = new UserJpaEntity();
//        user_2.setUserId("01E48SD97BMWHAW82D229T0C7L");
//        user_2.setCoachName("testCoach_2");
//        user_2.setEmail("test2@example.com");
//        user_2.setPassword("password");
//        user_2.setIsActive(true);
//        user_2.setCreatedAt(new Date());
//        user_2.setUpdatedAt(new Date());
//        user_2.setLang("fr_FR");
//        user_2.setRoles(List.of(UserRole.SIMPLE_USER.toString()));
//
//        // Sauvegarder l'utilisateur dans la base de données de test
//        entityManager.persist(user_2);
//        entityManager.flush();
//
//        List<UserJpaEntity> all_users = userRepository.findAll();
//        assertNotNull(all_users);
//        assertEquals(2, all_users.size());
//        assertEquals("testCoach", all_users.get(0).getUsername());
//        assertEquals("testCoach_2", all_users.get(1).getUsername());
//    }
//
//    @Test
//    @Transactional
//    public void TestInsertUserSucceed() {
//        UserJpaEntity user = this.buildUser(1);
//        // Sauvegarder l'utilisateur dans la base de données de test
//        userRepository.save(user);
//
//        List<UserJpaEntity> all_users = userRepository.findAll();
//        assertNotNull(all_users);
//        assertEquals(1, all_users.size());
//        assertEquals("testCoach1", all_users.get(0).getUsername());
//    }
//
//    @Test
//    @Transactional
//    public void TestRetrieveDomainUserByNameSucceed() {
//        UserJpaEntity user = this.buildUser(1);
//        // Sauvegarder l'utilisateur dans la base de données de test
//        userRepository.save(user);
//
//        Assertions.assertTrue(false);
////        Optional<ActiveUserAccount> found = userRepository.findDomainUserByName("testCoach1");
////        assertNotNull(found);
////        Assertions.assertTrue(found.isPresent());
////        assertEquals("testCoach1", found.get().getUsername().toString());
//    }
//
}
