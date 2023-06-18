package ru.itgroup.intouch.service.account;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import model.account.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import ru.itgroup.intouch.config.email.EmailContents;
import ru.itgroup.intouch.exceptions.NoEmailFoundException;
import ru.itgroup.intouch.exceptions.NoUserRegisteredException;
import ru.itgroup.intouch.repository.UserRepository;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CredentialsRenewalService {
    @Value("${EMAIL_FROM}")
    private String from;
    private final JavaMailSender mailSender;
    private final UserRepository userRepository;
    @Value("${user.hash-expiry-min}")
    private long expiryMinutes;
    private final SpringTemplateEngine templateEngine;

    public void setNewPassword(String linkId, String password) {
        Optional<User> firstByEmail = userRepository.findFirstByHash(linkId);
        if (firstByEmail.isEmpty()) {
            throw new NoUserRegisteredException("Не удалось изменить пароль. Запрос на изменение пароля не найден");
        }
        firstByEmail.get().setPassword(password);
        userRepository.save(firstByEmail.get());
    }

    public void sendLetter(String to, EmailContents contents, boolean addLinkId) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(
                message,
                MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                StandardCharsets.UTF_8.name()
        );
        Context context = new Context();
        Optional<User> optUser = userRepository.findFirstByEmailEqualsAndIsDeletedEquals(to, false);
        if (optUser.isEmpty()) {
            throw new NoEmailFoundException("Пользователь с адресом \"" + to + "\" не зарегистрирован");
        }
        User user = optUser.get();
        helper.setFrom(from);
        helper.setTo(to);
        helper.setSubject(contents.getSubject().toUpperCase(Locale.ROOT));

        String userHash = "";
        if (addLinkId) {
            userHash = UUID.randomUUID().toString();
            user.setHash(userHash);
            user.setHashExpiryTime(LocalDateTime.now().plusMinutes(expiryMinutes));
            userRepository.save(user);
        }

        context.setVariables(Map.of(
                "subject", contents.getSubject(),
                "name", user.getFirstName(),
                "url", contents.getUrl() + userHash)
        );
        String html = templateEngine.process("letter.html", context);
        helper.setText(html, true);
        mailSender.send(message);
    }

    @Scheduled(fixedDelayString = "${delete-hash.delay-ms}")
    @Transactional
    public void deleteHashWithDelay() {
        userRepository.clearHashAfterExpiry(LocalDateTime.now());
    }


}
