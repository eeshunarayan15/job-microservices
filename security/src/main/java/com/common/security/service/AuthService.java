package com.common.security.service;

import com.common.security.dto.UserCredentialDto;
import com.common.security.entity.UserCredential;

public interface AuthService {
  UserCredential createUser(UserCredentialDto userCredentialDto);
}
