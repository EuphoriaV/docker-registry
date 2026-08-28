package com.euphoriav.docker.registry.logic.helper;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.regex.Pattern;

@Component
public class DigestHelper {

    private static final Pattern DIGEST_PATTERN = Pattern.compile("sha256:[a-f0-9]{64}");

    public String calculateDigest(InputStream inputStream) throws NoSuchAlgorithmException, IOException {
        var digest = MessageDigest.getInstance("SHA-256");

        try (DigestInputStream digestIn = new DigestInputStream(inputStream, digest)) {
            digestIn.transferTo(OutputStream.nullOutputStream());
        }

        return "sha256:" + HexFormat.of().formatHex(digest.digest());
    }

    public boolean isDigest(String reference) {
        return DIGEST_PATTERN.matcher(reference).matches();
    }
}
