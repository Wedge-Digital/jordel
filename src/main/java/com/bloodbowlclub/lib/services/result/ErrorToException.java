package com.bloodbowlclub.lib.services.result;

import com.bloodbowlclub.lib.services.result.exceptions.*;

import java.util.HashMap;

public class ErrorToException {
    private static final HashMap<ErrorCode, Class<? extends ResultException>> _map = new HashMap<>();
    static {
        _map.put(ErrorCode.NOT_FOUND, NotFound.class);
        _map.put(ErrorCode.BAD_REQUEST, BadRequest.class);
        _map.put(ErrorCode.UNAUTHORIZED, Unauthorized.class);
        _map.put(ErrorCode.UNPROCESSABLE_ENTITY, ResultUnprocessableEntityException.class);
//        _map.put(ErrorCode.FORBIDDEN,
//        _map.put(ErrorCode.ALREADY_EXISTS,
//                CONFLICT,
//                UNKNOWN_ERROR,
//                INVALID_CREDENTIALS,
//                EXPIRED_TOKEN,
//                INVALID_TOKEN,
//                PERMISSION_DENIED,
//                INTERNAL_SERVER_ERROR,
//                UNKNOWN_ERROR_CODE;
    }

    public static Class<? extends ResultException> get(ErrorCode code) {
        return _map.get(code);
    }
}
