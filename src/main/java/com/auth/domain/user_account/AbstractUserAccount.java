package com.auth.domain.user_account;

import com.auth.domain.user_account.values.*;
import com.shared.domain.AggregateRoot;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import org.apache.commons.lang3.LocaleUtils;

import java.util.Date;
import java.util.List;
import java.util.Locale;

public abstract class AbstractUserAccount extends AggregateRoot {
}
