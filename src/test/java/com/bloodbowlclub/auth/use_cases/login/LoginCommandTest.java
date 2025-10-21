package com.bloodbowlclub.auth.use_cases.login;

import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
public class LoginCommandTest {

//    @Autowired
//    private UserRepository userRepo;
//
//    @Autowired
//    private MessageSource msgSource;
//
//    private final EventDispatcher dispatcher = new EventDispatcher();
//
//    private LoginCommandHandler handler;
//
//    private void initDb() {
//        handler = new LoginCommandHandler(userRepo, msgSource, dispatcher);
//        ActiveUserAccount user = new ActiveUserAccount(
//                "01K6E2X28KHA8P9CRZ3P326JF2",
//               "Bagouze",
//               "bertrand.begouin@gmail.com",
//               "$2a$12$agzLjevvLRcSEM2/0zD1f.xVZ9ukK.xS4GWxKBkdrEFwhHUViIpxy"// encrypted g17b92hk
//        );
////        userRepo.save(user);
//    }
//
//    @Test
//    public void test_login_not_existing() {
//        /*
//        When trying to login with an unknown user
//        Then the login should fail with an error message
//        * */
//        initDb();
//        Result<CommandResult> handling = handler.handle(new LoginCommand("unknown_user", "password"));
//        Assertions.assertTrue(handling.isFailure());
//        String expectedError = msgSource.getMessage("user_account.not_existing", null, LocaleContextHolder.getLocale());
//        Assertions.assertEquals(expectedError, handling.getErrorMessage());
//    }

}
