package nz.govt.linz.service

import org.apache.commons.codec.binary.Base16
import org.slf4j.LoggerFactory
import java.math.BigInteger
import java.security.MessageDigest
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

class LegacyEncryptionService {
    var logger = LoggerFactory.getLogger(LegacyEncryptionService::class.java)!!

    private val hashAlgorithm = "MD5"
    private val algorithm = "RC4"
    private val keyLengthBits = 40

    /**
     * Encrypt the supplied password, using a key derived from the username
     */
    fun encrypt(username: String, password: String): String {
        // username is encryption key
        val key = deriveKey(username)

        val cipher = Cipher.getInstance(algorithm)
        cipher.init(Cipher.ENCRYPT_MODE, SecretKeySpec(key, algorithm))

        val encrypted = cipher.doFinal(password.toByteArray())

        return String.format("%0${encrypted.size * 2}x", BigInteger(1, encrypted))
    }

    /**
     * Decrypt the supplied encrypted password, using a key derived from the username
     */
    fun decrypt(username: String, encryptedPassword: String): String {
        // username is encryption key
        val key = deriveKey(username)

        val cipher = Cipher.getInstance(algorithm)
        cipher.init(Cipher.DECRYPT_MODE, SecretKeySpec(key, algorithm))

        val decrypted = cipher.doFinal(Base16().decode(encryptedPassword.uppercase()))

        return decrypted.toString(Charsets.UTF_8)
    }

    /**
     * Derive a 40-bit RC4 encryption key from the supplied username. This is nothing more than the first 40 bits
     * of an MD5 hash of the username
     */
    private fun deriveKey(username: String): ByteArray {
        // username is the encryption key
        val hash = MessageDigest.getInstance(hashAlgorithm)
        hash.update(username.toByteArray())
        val hashValue = hash.digest()

        val keyLength = keyLengthBits / 8
        logger.debug(String.format("MD5 Hash: %032x", BigInteger(1, hashValue)))

        val derivedKey = hashValue.copyOfRange(0, keyLength)
        logger.debug(String.format("Derived Key: %010x", BigInteger(1, derivedKey)))
        return derivedKey
    }
}
