package com.bloodbowlclub.auth.use_cases;

import com.bloodbowlclub.auth.domain.user_account.DraftUserAccount;
import com.bloodbowlclub.auth.domain.user_account.commands.LostLoginCommand;
import com.bloodbowlclub.auth.io.repositories.LostLoginTokenEntity;
import com.bloodbowlclub.auth.io.repositories.LostLoginTokenRepository;
import com.bloodbowlclub.auth.io.security.filters.JwtRequestFilter;
import com.bloodbowlclub.lib.Command;
import com.bloodbowlclub.lib.domain.AggregateRoot;
import com.bloodbowlclub.lib.domain.events.AbstractEventDispatcher;
import com.bloodbowlclub.lib.persistance.event_store.EventStore;
import com.bloodbowlclub.lib.services.email_service.AbstractEmailService;
import com.bloodbowlclub.lib.services.result.Result;
import com.bloodbowlclub.lib.services.result.ResultMap;
import com.bloodbowlclub.lib.use_cases.CommandHandler;
import jakarta.mail.MessagingException;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import java.util.Optional;

@Component("lostLoginCommandHandler")
public class LostPasswordCommandHandler extends CommandHandler {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(LostPasswordCommandHandler.class);
    private final LostLoginTokenRepository lostLoginTokenRepository;
    @Qualifier("emailService")
    private final AbstractEmailService mailService;

    public LostPasswordCommandHandler(@Qualifier("EventStore") EventStore eventStore,
                                      @Qualifier("eventDispatcher") AbstractEventDispatcher businessDispatcher,
                                      MessageSource messageSource,
                                      LostLoginTokenRepository lostLoginTokenRepository,
                                      AbstractEmailService mailService
    ) {
        super(eventStore, businessDispatcher, messageSource);
        this.lostLoginTokenRepository = lostLoginTokenRepository;
        this.mailService = mailService;
    }

    @Override
    public ResultMap<Void> handle(Command command) {
        LostLoginCommand cmd = (LostLoginCommand) command;
        Result<AggregateRoot> foundUserAccount = eventStore.findUser(cmd.getUsername());
        if (foundUserAccount.isFailure()) {
            return ResultMap.success(null);
        }

        DraftUserAccount userAccount = (DraftUserAccount) foundUserAccount.getValue();

        Optional<LostLoginTokenEntity> existingTokenSearch = lostLoginTokenRepository.findByUsername(cmd.getUsername());
        if (existingTokenSearch.isPresent()) {
            return ResultMap.success(null);
        }

        LostLoginTokenEntity token = new LostLoginTokenEntity(((LostLoginCommand) command).getUsername());
        lostLoginTokenRepository.save(token);

        String recoverUrl = "https://bloodbowlclub.com/reset_password?token="+token.getToken();
        try {
            mailService.sendLostPasswordEmail(
                    userAccount.getEmail().toString(),
                    userAccount.getId(),
                    recoverUrl
            );
        } catch (MessagingException exc) {
            logger.error("exception in email sending : {}", exc.toString());
        }

        return ResultMap.success(null);
    }
}
