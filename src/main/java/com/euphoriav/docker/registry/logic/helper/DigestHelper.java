package com.euphoriav.docker.registry.logic.helper;

import com.euphoriav.docker.registry.enums.DigestAlgorithm;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.DigestInputStream;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.regex.Pattern;

@Component
public class DigestHelper {

    private static final Pattern DIGEST_PATTERN = Pattern.compile("sha256:[a-f0-9]{64}|sha512:[a-f0-9]{128}");

    public String calculateDigest(InputStream inputStream, DigestAlgorithm digestAlgorithm) throws NoSuchAlgorithmException, IOException {
        var digest = digestAlgorithm.getDigest();

        try (DigestInputStream digestIn = new DigestInputStream(inputStream, digest)) {
            digestIn.transferTo(OutputStream.nullOutputStream());
        }

        return digestAlgorithm.getAlgorithm() + ":" + HexFormat.of().formatHex(digest.digest());
    }

    public boolean isDigest(String reference) {
        return DIGEST_PATTERN.matcher(reference).matches();
    }
}
