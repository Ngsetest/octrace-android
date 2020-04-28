package org.opencovidtrace.octrace.utils

import android.annotation.SuppressLint
import org.bouncycastle.jce.provider.BouncyCastleProvider
import java.io.UnsupportedEncodingException
import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException
import java.security.Security
import javax.crypto.*
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec


object AESEncryptor {
    private const val cipherInstance ="AES/CBC/PKCS5Padding"

    @SuppressLint("GetInstance")
    fun encrypt(input: ByteArray, keyBytes: ByteArray): ByteArray? {
        Security.addProvider(BouncyCastleProvider())

        try {
            val skey = SecretKeySpec(keyBytes, "AES")

            synchronized(Cipher::class.java) {
                val cipher = Cipher.getInstance(cipherInstance)
                cipher.init(Cipher.ENCRYPT_MODE, skey, generateIV())
                return cipher.doFinal(input)
            }
        } catch (uee: UnsupportedEncodingException) {
            uee.printStackTrace()
        } catch (ibse: IllegalBlockSizeException) {
            ibse.printStackTrace()
        } catch (bpe: BadPaddingException) {
            bpe.printStackTrace()
        } catch (ike: InvalidKeyException) {
            ike.printStackTrace()
        } catch (nspe: NoSuchPaddingException) {
            nspe.printStackTrace()
        } catch (nsae: NoSuchAlgorithmException) {
            nsae.printStackTrace()
        } catch (e: ShortBufferException) {
            e.printStackTrace()
        }

        return null
    }

    @SuppressLint("GetInstance")
    fun decryptWithAES(bytesToDecrypt: ByteArray, keyBytes: ByteArray): ByteArray? {
        Security.addProvider(BouncyCastleProvider())

        try {
            val skey = SecretKeySpec(keyBytes, "AES")

            synchronized(Cipher::class.java) {
                val cipher = Cipher.getInstance(cipherInstance)
                cipher.init(Cipher.DECRYPT_MODE, skey, generateIV())

                val plainText = ByteArray(cipher.getOutputSize(bytesToDecrypt.size))
                var ptLength = cipher.update(bytesToDecrypt, 0, bytesToDecrypt.size, plainText, 0)
                ptLength += cipher.doFinal(plainText, ptLength)
                val decryptedString = String(plainText)
                return decryptedString.trim { it <= ' ' }.toByteArray()
            }
        } catch (uee: UnsupportedEncodingException) {
            uee.printStackTrace()
        } catch (ibse: IllegalBlockSizeException) {
            ibse.printStackTrace()
        } catch (bpe: BadPaddingException) {
            bpe.printStackTrace()
        } catch (ike: InvalidKeyException) {
            ike.printStackTrace()
        } catch (nspe: NoSuchPaddingException) {
            nspe.printStackTrace()
        } catch (nsae: NoSuchAlgorithmException) {
            nsae.printStackTrace()
        } catch (e: ShortBufferException) {
            e.printStackTrace()
        }

        return null
    }

    private fun generateIV(): IvParameterSpec = IvParameterSpec(ByteArray(16))
}