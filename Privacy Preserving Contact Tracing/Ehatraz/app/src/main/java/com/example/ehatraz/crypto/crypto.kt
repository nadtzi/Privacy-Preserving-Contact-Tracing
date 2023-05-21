package com.example.ehatraz.crypto
import java.security.SecureRandom
import java.nio.charset.Charset
import java.util.*

class crypto {
    fun CRNG() : ByteArray
    {
        //
        val random = SecureRandom()
        val bytes = ByteArray(20)
        random.nextBytes(bytes)
        return bytes;

    }
//    fun generate_HKDF(info:ByteArray,size:Int):ByteArray{
//        val output = CRNG();
//        //val key = Hkdf.computeHkdf("HmacSha256",output,null,info,size);
//        return key
//    }

}