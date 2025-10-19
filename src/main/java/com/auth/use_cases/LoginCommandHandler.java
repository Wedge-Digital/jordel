package com.auth.use_cases;

import com.auth.domain.user_account.commands.LoginCommand;
import com.lib.services.Result;
import com.shared.use_cases.CommandResult;
import org.springframework.stereotype.Component;


@Component
public class LoginCommandHandler {
//    private final UserRepository userRepository;
//    private final MessageSource msgSource;
//    private final EventDispatcher domainDispatcher;
//
//    public LoginCommandHandler(UserRepository userRepository, MessageSource msgSource, EventDispatcher domainDispatcher) {
//        this.userRepository = userRepository;
//        this.msgSource = msgSource;
//        this.domainDispatcher = domainDispatcher;
//    }
//

    public Result<CommandResult> handle(LoginCommand command) {
        return null;
//        Optional<ActiveUserAccount> userSearch = userRepository.findDomainUserByName(command.getUsername());
//        if (userSearch.isEmpty()) {
//            String errorMessage =msgSource.getMessage("user_account.not_existing", null, LocaleContextHolder.getLocale());
//            return Result.failure(errorMessage);
//        }
//
//        ActiveUserAccount user = userSearch.get();
//        boolean loginResult = user.login(command.getPassword());
//        if (loginResult) {
//            domainDispatcher.dispatch(user.getDomainEvents());
//            return Result.success(null);
//        }
//
//        String errorMessage =msgSource.getMessage("user_account.bad_credentials", new String[]{user.getUsername().toString()}, LocaleContextHolder.getLocale());
//        return Result.failure(errorMessage);
    }
}
