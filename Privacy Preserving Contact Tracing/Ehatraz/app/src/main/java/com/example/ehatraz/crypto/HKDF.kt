/**
 * Copyright (C) 2013-2016 Open Whisper Systems
 *
 * Licensed according to the LICENSE file in this repository.
 *
 */

import java.io.ByteArrayOutputStream
import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

public class HKDF {
    public fun deriveSecrets(
        inputKeyMaterial: ByteArray?,
        info: ByteArray,
        outputLength: Int
    ): ByteArray {
        val salt = ByteArray(HASH_OUTPUT_SIZE)
        return deriveSecrets(inputKeyMaterial, salt, info, outputLength)
    }

    fun deriveSecrets(
        inputKeyMaterial: ByteArray?,
        salt: ByteArray?,
        info: ByteArray?,
        outputLength: Int
    ): ByteArray {
        val prk = extract(salt, inputKeyMaterial)
        return expand(prk, info, outputLength)
    }

    private fun extract(
        salt: ByteArray?,
        inputKeyMaterial: ByteArray?
    ): ByteArray {
        return try {
            val mac = Mac.getInstance("HmacSHA256")
            mac.init(SecretKeySpec(salt, "HmacSHA256"))
            mac.doFinal(inputKeyMaterial)
        } catch (e: NoSuchAlgorithmException) {
            throw AssertionError(e)
        } catch (e: InvalidKeyException) {
            throw AssertionError(e)
        }
    }

    private fun expand(
        prk: ByteArray,
        info: ByteArray?,
        outputSize: Int
    ): ByteArray {
        return try {
            val iterations =
                Math.ceil(outputSize.toDouble() / HASH_OUTPUT_SIZE.toDouble()).toInt()
            var mixin: ByteArray? = ByteArray(0)
            val results = ByteArrayOutputStream()
            var remainingBytes = outputSize
            for (i in 1 until iterations + 1) {
                val mac = Mac.getInstance("HmacSHA256")
                mac.init(SecretKeySpec(prk, "HmacSHA256"))
                mac.update(mixin)
                if (info != null) {
                    mac.update(info)
                }
                mac.update(i.toByte())
                val stepResult = mac.doFinal()
                val stepSize = Math.min(remainingBytes, stepResult.size)
                results.write(stepResult, 0, stepSize)
                mixin = stepResult
                remainingBytes -= stepSize
            }
            results.toByteArray()
        } catch (e: NoSuchAlgorithmException) {
            throw AssertionError(e)
        } catch (e: InvalidKeyException) {
            throw AssertionError(e)
        }
    }

    companion object {
        private const val HASH_OUTPUT_SIZE = 16
    }
}