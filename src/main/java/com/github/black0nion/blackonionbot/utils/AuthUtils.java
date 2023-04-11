package com.github.black0nion.blackonionbot.utils;

import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class AuthUtils {

	private static final Logger logger = LoggerFactory.getLogger(AuthUtils.class);

	public static final String PUBLIC_KEY_LOCATION = "files/public_key.pem";
	public static final String PRIVATE_KEY_LOCATION = "files/private_key.pem";
	// 7 days in seconds
	public static final int JWT_VALID_FOR = 60 * 60 * 24 * 7;

	private AuthUtils() {}

	private static byte[] parsePEMFile(File pemFile) throws IOException {
		if (!pemFile.isFile() || !pemFile.exists()) {
			throw new FileNotFoundException(String.format("The file '%s' doesn't exist.", pemFile.getAbsolutePath()));
		}
		byte[] content;
		try (PemReader reader = new PemReader(new FileReader(pemFile))) {
			PemObject pemObject = reader.readPemObject();
			content = pemObject.getContent();
		}
		return content;
	}

	private static PublicKey getPublicKey(byte[] keyBytes, String algorithm) {
		PublicKey publicKey = null;
		try {
			KeyFactory kf = KeyFactory.getInstance(algorithm);
			EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
			publicKey = kf.generatePublic(keySpec);
		} catch (NoSuchAlgorithmException e) {
			logger.error("Could not reconstruct the public key, the given algorithm could not be found.", e);
		} catch (InvalidKeySpecException e) {
			logger.error("Could not reconstruct the public key", e);
		}

		return publicKey;
	}

	private static PrivateKey getPrivateKey(byte[] keyBytes, String algorithm) {
		PrivateKey privateKey = null;
		try {
			KeyFactory kf = KeyFactory.getInstance(algorithm);
			EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
			privateKey = kf.generatePrivate(keySpec);
		} catch (NoSuchAlgorithmException e) {
			logger.error("Could not reconstruct the private key, the given algorithm could not be found.", e);
		} catch (InvalidKeySpecException e) {
			logger.error("Could not reconstruct the private key", e);
		}

		return privateKey;
	}

	public static PublicKey readPublicKeyFromFile(String filepath, String algorithm) throws IOException {
		byte[] bytes = AuthUtils.parsePEMFile(new File(filepath));
		return AuthUtils.getPublicKey(bytes, algorithm);
	}

	public static PrivateKey readPrivateKeyFromFile(String filepath, String algorithm) throws IOException {
		byte[] bytes = AuthUtils.parsePEMFile(new File(filepath));
		return AuthUtils.getPrivateKey(bytes, algorithm);
	}
}