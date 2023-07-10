package ua.dtsebulia.LoginAndRegistration.registration;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.dtsebulia.LoginAndRegistration.appuser.AppUser;
import ua.dtsebulia.LoginAndRegistration.appuser.AppUserRole;
import ua.dtsebulia.LoginAndRegistration.appuser.AppUserService;
import ua.dtsebulia.LoginAndRegistration.registration.token.ConfirmationToken;
import ua.dtsebulia.LoginAndRegistration.registration.token.ConfirmationTokenService;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class RegistrationService {

    private final EmailValidator emailValidator;
    private final AppUserService appUserService;
    private final ConfirmationTokenService confirmationTokenService;

    public String register(RegistrationRequest request) {
        boolean isEmailValid = emailValidator.test(request.getEmail());
        if (!isEmailValid) {
            throw new IllegalStateException("email is not valid");
        }
        return appUserService.signUpUser(
                new AppUser(
                        request.getFirstName(),
                        request.getLastName(),
                        request.getEmail(),
                        request.getPassword(),
                        AppUserRole.USER
                )
        );
    }

    @Transactional
    public String confirmToken(String token) {
        ConfirmationToken confirmationToken = confirmationTokenService.getToken(token)
                .orElseThrow(
                        () -> new IllegalStateException("token not found")
                );
        if (confirmationToken.getConfirmedAt() != null) {
            throw new IllegalStateException("token already confirmed");
        }

        LocalDateTime expiredAt = confirmationToken.getExpiresAt();

        if (expiredAt.isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("token expired");
        }

        confirmationTokenService.setConfirmedAt(token);
        appUserService.enableAppUser(
                confirmationToken.getAppUser().getEmail()
        );
        return "confirmed";

    }
}
