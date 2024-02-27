package utils;

import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

import java.util.concurrent.ThreadLocalRandom;

public class EventCodeGenerator implements IdentifierGenerator {
    static final char[] validCharacters = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789".toCharArray();
    // I, 1, O, and 0 omitted for UX (user experience)
    public static final int codeLength = 6;
    // 32^6 = 1,073,741,824 combinations

    public static String generateID(){
        int maxRandomBound = validCharacters.length;
        ThreadLocalRandom random = ThreadLocalRandom.current();
        StringBuilder stringBuilder = new StringBuilder();

        for(int i = 0; i < codeLength; i++){
            int charIndex = random.nextInt(0, maxRandomBound);
            char character = validCharacters[charIndex];
            stringBuilder.append(character);
        }

        return stringBuilder.toString();
    }

    @Override
    public Object generate(SharedSessionContractImplementor session, Object object) {
        return generateID();
    }
}