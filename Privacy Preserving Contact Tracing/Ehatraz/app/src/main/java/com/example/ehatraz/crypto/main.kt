import java.security.SecureRandom;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec
import javax.crypto.spec.IvParameterSpec;

fun main() {
	val TEK = getTEK();
  /* For whatever reason the same keys are being generated */
  println("Here is the TEK: ${TEK.contentToString()}");

  val RPIK = getKey(TEK, 
                    "EN-RPIK".toByteArray(Charsets.UTF_8),
                    16)
  println("Here is the RPIK: ${RPIK.dk.contentToString()}");

  val AEMK = getKey(TEK, 
                    "EN-AEMK".toByteArray(Charsets.UTF_8),
                    16)
  println("Here is the AEMK: ${AEMK.dk.contentToString()}");

  @OptIn(ExperimentalUnsignedTypes::class)
  val RPI = getRPI(RPIK);
  println("Here is the RPI for this 10 minutes ${RPI.toUByteArray().contentToString()}");

  @OptIn(ExperimentalUnsignedTypes::class)
  val AEM = getAEM("sth to encrypt", AEMK.dk, RPI);
  println("Here is the AEM for this person ${AEM.toUByteArray().contentToString()}");
}

fun getTEK(): ByteArray{
    val random = SecureRandom();
    val TEK = ByteArray(16);
    random.nextBytes(TEK);
    /* Save the generated TEK in the storage  */
    return TEK;
}

fun getKey(tek: ByteArray, info: ByteArray, outputLength: Int): Time_HKDF{
  return Time_HKDF(HKDF().deriveSecrets(tek, info, outputLength))
}

/* This function will be implemented every 10 minutes */
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

/* When receiveing a beacond */
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