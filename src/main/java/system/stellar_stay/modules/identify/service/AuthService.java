package system.stellar_stay.modules.identify.service;

public interface AuthService {
        String login(String email, String password);
        void logout(String accessToken);
        void refreshToken(String refreshToken);
}
