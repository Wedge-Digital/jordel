package com.auth.tests;

import com.auth.persistance.User;
import com.auth.persistance.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ExtendWith(SpringExtension.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    private User buildUser(int id) {
        User user = new User();
        user.setId("01E48SD97BMWHAW82D229T0C7K");
        user.setCoachName("testCoach" + id);
        user.setEmail("test" + id + "@example.com");
        user.setPassword("password");
        user.setIsActive(true);
        user.setCreatedAt(new Date());
        user.setUpdatedAt(new Date());
        return user;
    }

    @Test
    public void testFindByCoachNameSucceed() {
        // Créer un utilisateur de test
        User user = new User();
        user.setId("01E48SD97BMWHAW82D229T0C7K");
        user.setCoachName("testCoach");
        user.setEmail("test@example.com");
        user.setPassword("password");
        user.setIsActive(true);
        user.setCreatedAt(new Date());
        user.setUpdatedAt(new Date());

        // Sauvegarder l'utilisateur dans la base de données de test
        entityManager.persist(user);
        entityManager.flush();

        // Récupérer l'utilisateur par son coachName
        User foundUser = userRepository.findByCoachName("testCoach");

        // Vérifier que l'utilisateur a été trouvé et que les détails sont corrects
        assertNotNull(foundUser);
        assertEquals("testCoach", foundUser.getCoachName());
        assertEquals("test@example.com", foundUser.getEmail());
    }


    @Test
    public void testFindByCoachNameNotFoundReturnsNull() {
        // Récupérer l'utilisateur par son coachName
        User foundUser = userRepository.findByCoachName("testCoach");
        assertNull(foundUser);
    }

    @Test
    public void testListAllUsersSucceed() {
        User user_1 = new User();
        user_1.setId("01E48SD97BMWHAW82D229T0C7K");
        user_1.setCoachName("testCoach");
        user_1.setEmail("test@example.com");
        user_1.setPassword("password");
        user_1.setIsActive(true);
        user_1.setCreatedAt(new Date());
        user_1.setUpdatedAt(new Date());

        // Sauvegarder l'utilisateur dans la base de données de test
        entityManager.persist(user_1);
        entityManager.flush();

        User user_2 = new User();
        user_2.setId("01E48SD97BMWHAW82D229T0C7L");
        user_2.setCoachName("testCoach_2");
        user_2.setEmail("test2@example.com");
        user_2.setPassword("password");
        user_2.setIsActive(true);
        user_2.setCreatedAt(new Date());
        user_2.setUpdatedAt(new Date());

        // Sauvegarder l'utilisateur dans la base de données de test
        entityManager.persist(user_2);
        entityManager.flush();

        List<User> all_users = userRepository.findAll();
        assertNotNull(all_users);
        assertEquals(2, all_users.size());
        assertEquals("testCoach", all_users.get(0).getCoachName());
        assertEquals("testCoach_2", all_users.get(1).getCoachName());
    }



    @Test
    public void TestInsertUserSucceed() {
        User user = this.buildUser(1);
        // Sauvegarder l'utilisateur dans la base de données de test
        userRepository.save(user);

        List<User> all_users = userRepository.findAll();
        assertNotNull(all_users);
        assertEquals(1, all_users.size());
        assertEquals("testCoach1", all_users.get(0).getCoachName());
    }

}
