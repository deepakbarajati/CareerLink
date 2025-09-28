package com.careerLink.user_service.service;

import com.careerLink.user_service.dto.LoginRequestDto;
import com.careerLink.user_service.dto.SignUpRequestDto;
import com.careerLink.user_service.dto.UserDto;
import com.careerLink.user_service.entity.User;
import com.careerLink.user_service.exception.BadRequestException;
import com.careerLink.user_service.exception.ResourceNotFoundException;
import com.careerLink.user_service.repository.UserRepository;
import com.careerLink.user_service.utils.PasswordUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final JwtService jwtService;

    public UserDto signUp(SignUpRequestDto signUpRequestDto) {

        boolean exists =userRepository.existsByEmail(signUpRequestDto.getEmail());
        if(exists){
            throw new BadRequestException("User Already Exists,cannot signup again");
        }

        User user=modelMapper.map(signUpRequestDto,User.class);
        user.setPassword(PasswordUtil.hashPassword(signUpRequestDto.getPassword()));

        User savedUser = userRepository.save(user);
        return modelMapper.map(savedUser, UserDto.class);
    }

    public String login(LoginRequestDto loginRequestDto) {
        User user =userRepository.findByEmail(loginRequestDto.getEmail())
                .orElseThrow(()-> new ResourceNotFoundException("User Not found with email: "+loginRequestDto.getEmail()));

        boolean isPasswordMatch=PasswordUtil.checkPassword(loginRequestDto.getPassword(),user.getPassword());

        if(!isPasswordMatch){
            throw new BadRequestException("Incorrect Password");
        }

        return jwtService.generateAccessToken(user);
    }
}
