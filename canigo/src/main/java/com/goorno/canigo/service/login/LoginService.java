package com.goorno.canigo.service.login;

import com.goorno.canigo.dto.login.LoginRequestDTO;
import com.goorno.canigo.dto.login.LoginResponseDTO;

public interface LoginService {
	
	LoginResponseDTO login(LoginRequestDTO loginRequestDTO);

}
