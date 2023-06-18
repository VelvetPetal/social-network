package ru.itgroup.intouch.service.account;

import com.github.cage.GCage;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import model.account.Account;
import model.account.User;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.itgroup.intouch.client.StorageServiceClient;
import ru.itgroup.intouch.dto.CaptchaDto;
import ru.itgroup.intouch.dto.ImageDTO;
import ru.itgroup.intouch.dto.RegistrationDto;
import ru.itgroup.intouch.dto.UploadPhotoDto;
import ru.itgroup.intouch.exceptions.CaptchaNotValidException;
import ru.itgroup.intouch.exceptions.ServiceUnavailableException;
import ru.itgroup.intouch.exceptions.UserAlreadyRegisteredException;
import ru.itgroup.intouch.mapper.UserMapper;
import ru.itgroup.intouch.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RegistrationService {
    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final StorageServiceClient storageServiceClient;

    public void registerNewUser(RegistrationDto registrationDto) {
        if (!Objects.equals(registrationDto.getCaptchaCode(), registrationDto.getCaptchaSecret())) {
            throw new CaptchaNotValidException("Капча введена неверно.");
        }
        Optional<User> firstByEmail = userRepository
                .findFirstByEmailEqualsAndIsDeletedEquals(registrationDto.getEmail(), false);
        if (firstByEmail.isPresent() && !firstByEmail.get().isDeleted()) {
            throw new UserAlreadyRegisteredException("Пользователь с адресом \"" +
                    registrationDto.getEmail() + "\" уже зарегистрирован.");
        }
        Account accountEntity = userMapper.registrationDto2AccountEntity(registrationDto);
        accountEntity.setCreatedOn(LocalDateTime.now());
        accountEntity.setRegDate(LocalDateTime.now());
        accountEntity.setUpdateOn(LocalDateTime.now());
        userRepository.save(accountEntity);
    }


    public CaptchaDto generateCaptcha() {
        GCage gCage = new GCage();
        String token = gCage.getTokenGenerator().next();
        byte[] image = gCage.draw(token);
        MultipartFile multipartImage = new MockMultipartFile(token, image);
        UploadPhotoDto uploadPhotoDto = new UploadPhotoDto();
        ImageDTO imageDTO;
        uploadPhotoDto.setMultipartFile(multipartImage);
        try {
            imageDTO = storageServiceClient.feignUploadPhoto(uploadPhotoDto);
        } catch (FeignException e){
            throw new ServiceUnavailableException("Не удалось получить капчу");
        }

        return new CaptchaDto(token, imageDTO.getPhotoPath());
    }
}

