package com.github.black0nion.blackonionbot.oauth;

import com.github.black0nion.blackonionbot.misc.exception.OAuthUserNotFoundException;
import com.github.black0nion.blackonionbot.utils.ThrowableFunction;

public interface OAuthUserLoader extends ThrowableFunction<Long, OAuthUser, OAuthUserNotFoundException> {}