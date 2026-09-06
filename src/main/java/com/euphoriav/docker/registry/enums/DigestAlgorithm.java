package com.euphoriav.docker.registry.enums;

import com.euphoriav.docker.registry.exception.InvalidRequestException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum DigestAlgorithm {
    SHA_256("sha256") {
        @Override
        public MessageDigest getDigest() throws NoSuchAlgorithmException {
            return MessageDigest.getInstance("SHA-256");
        }
    },
    SHA_512("sha512") {
        @Override
        public MessageDigest getDigest() throws NoSuchAlgorithmException {
            return MessageDigest.getInstance("SHA-512");
        }
    };

    private final String algorithm;

    public abstract MessageDigest getDigest() throws NoSuchAlgorithmException;

    public static DigestAlgorithm fromDigest(String digest) {
        return Arrays.stream(DigestAlgorithm.values()).filter(digestAlgorithm -> digestAlgorithm.getAlgorithm().equals(digest.substring(0, 6)))
                .findFirst()
                .orElseThrow(() -> new InvalidRequestException("invalid digest algorithm", ErrorCode.DIGEST_INVALID));
    }
}
