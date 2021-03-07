package com.example.ehatraz.crypto


import java.nio.ByteBuffer
import java.nio.LongBuffer
import java.security.SecureRandom;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec
import javax.crypto.spec.IvParameterSpec;


fun byteArrtoLongArr(byteArray: ByteArray):MutableList<Long>{
    return byteArray.toMutableList().map { it.toLong() }.toMutableList()
}


fun longArrToByteArr(longArr:MutableList<Long>):ByteArray{
    return longArr.map { it.toByte() }.toByteArray()
}
fun main(args: Array<String>) {
//fun generateTEK(){
    val TEK = getTEK();

    var bb=ByteBuffer.wrap(TEK)
    var lb = bb.asLongBuffer()
    /* For whatever reason the same keys are being generated */

    println("Here is the TEK: ${TEK.contentToString()} and ${TEK.contentToString().toCharArray()} ${byteArrtoLongArr(TEK)}")

    val RPIK = getKey(TEK,
        "EN-RPIK".toByteArray(Charsets.UTF_8),
        16)
    println("Here is the RPIK: ${RPIK.dk.contentToString()}");

    val AEMK = getKey(TEK,
        "EN-AEMK".toByteArray(Charsets.UTF_8),
        16)
    println("Here is the AEMK: ${AEMK.dk.contentToString()}");

    val RPI = getRPI(RPIK);
    println("Here is the RPI for this 10 minutes ${RPI.contentToString()}");

    val AEM = getAEM("sth to encrypt", AEMK.dk, RPI);
    println("Here is the AEM for this person ${AEM.contentToString()}");
}

fun getTEK(): ByteArray{
    val random = SecureRandom();
    val TEK = ByteArray(16);
    random.nextBytes(TEK);
    return TEK;
}

fun getKey(tek: ByteArray, info: ByteArray, outputLength: Int): Time_HKDF {
    return Time_HKDF(HKDF().deriveSecrets(tek, info, outputLength))
}

fun getRPI(rpik: Time_HKDF): ByteArray{
    /* Time is an issue that will be fixed with android */
    /* Java has no unsigned bytes, it could be done manually later */
    val time = ((rpik.time/600000)%144).toByte()
    val pad:ByteArray = byteArrayOf(0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0)
    val padded_data: ByteArray = "EN-RPI".toByteArray(Charsets.UTF_8) + pad + time

    val key: SecretKey = SecretKeySpec(rpik.dk, "AES")
    val cipher = Cipher.getInstance("AES/ECB/NoPadding")
    cipher.init(Cipher.ENCRYPT_MODE, key)

    return cipher.doFinal(padded_data)
}

fun getAEM(distance: String, AEMK: ByteArray, RPI: ByteArray): ByteArray{
    val key: SecretKey = SecretKeySpec(AEMK, "AES");
    val cipher = Cipher.getInstance("AES/CTR/NoPadding")
    /*
    Source:
    https://cryptodoneright.org/articles/symmetric_algorithms/Mode_CTR/aes_ctr_dev_quickstart.html
    */
    cipher.init(Cipher.ENCRYPT_MODE, key, IvParameterSpec(RPI))

    return cipher.doFinal(distance.toByteArray());
}