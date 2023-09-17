package com.example.users53;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;


@RunWith(SpringRunner.class) // чтобы использовать специальный раннер для тестирования Spring
@DataJpaTest // не запускать приложение целиком, а только часть связанную с JPA
public class DatabaseTest {
    @Autowired
    private UserRepository userRepository;

    @Test
    public void testUserCreation() throws Exception {
        // создайте пользователя
        User user = new User();
        // добавьте ему имя и емэйл
        user.setName("Max");
        user.setEmail("max@gmail.com");
        // сохраните его через userRepository
        userRepository.save(user);
        // убедитесь что его идентификатор стал равен 1L
        assertEquals((long) user.getId(), 1L);

    }

    // напишите тестовый метод
    // создайте и сохраните двух пользователей
    // убедитесь что количество пользователей равно 2
    // userRepository.count()
    @Test
    public void testMultipleUsersCreation()
    {
        User u1 = new User();
        u1.setName("Max");
        u1.setEmail("max@gmail.com");

        User u2 = new User();
        u2.setName("Masha");
        u2.setEmail("masha@yahoo.com");

        userRepository.save(u1);
        userRepository.save(u2);

        assertEquals(userRepository.count(), 2L);
    }
}
