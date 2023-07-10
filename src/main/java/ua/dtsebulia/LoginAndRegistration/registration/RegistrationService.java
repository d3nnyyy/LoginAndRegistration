package ua.dtsebulia.LoginAndRegistration.registration;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ua.dtsebulia.LoginAndRegistration.appuser.AppUser;
import ua.dtsebulia.LoginAndRegistration.appuser.AppUserRole;
import ua.dtsebulia.LoginAndRegistration.appuser.AppUserService;

@Service
@AllArgsConstructor
public class RegistrationService {

    private final EmailValidator emailValidator;
    private final AppUserService appUserService;

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
}
