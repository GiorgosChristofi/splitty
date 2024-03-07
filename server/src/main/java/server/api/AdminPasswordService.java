package server.api;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

@Service
public class AdminPasswordService {
    static final char[] passwordChars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray();
    static final int passwordLength = 20;
    String password = "";
    public AdminPasswordService(){}

    /**
     * generate a random password
     * @return the randomly generated password
     */
    private String generatePassword() {
        Random random = new Random();
        for (int i = 0; i < passwordLength; i++) {
            password += passwordChars[random.nextInt(passwordChars.length)];
        }
        return password;
    }
    private static final Logger log = LoggerFactory.getLogger(AdminPasswordService.class);

    /**
     * initialise and log in the console the randomly generated password
     * @return a log of the password for an admin to check
     */
    @Bean
    CommandLineRunner initPassword() {
        return args -> {
            this.password = generatePassword();
            log.info(password);
        };
    }

    /**
     * check if this is the correct password
     * @param inputPassword the password we check it is correct
     * @return whether the password is matching or not, boolean
     */
    public boolean passwordChecker(String inputPassword){
        if(inputPassword.equals(this.password))return true;
        else return false;
    }
}
