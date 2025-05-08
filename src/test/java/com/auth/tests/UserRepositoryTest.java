package com.auth.tests;

import com.auth.db_model.User;
import com.auth.db_model.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ExtendWith(SpringExtension.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testFindByCoachName() {
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
}
