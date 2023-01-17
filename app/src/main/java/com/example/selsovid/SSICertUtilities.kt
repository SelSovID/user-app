package com.example.selsovid

import com.example.selsovid.data.SsiCert.SSICert
import com.google.protobuf.kotlin.toByteString
import java.io.StringWriter
import java.security.Key
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import java.util.*
import javax.crypto.Cipher
import java.nio.charset.StandardCharsets
import java.security.MessageDigest


class SSICertUtilities(
  val publicKey: RSAPublicKey,
  val credentialText: String,
  val ownerSignature: ByteArray,
  val parentSignature: ByteArray? = null,
  val parent: SSICertUtilities? = null,
) {

  private fun export(): SSICert {
    var builder = SSICert.newBuilder()
    builder.credentialText = credentialText

    var publicKeyEncoded = Base64.getEncoder().encodeToString(publicKey.encoded)
    val publicKeyStrWriter = StringWriter()
    publicKeyStrWriter.write("-----BEGIN PUBLIC KEY-----\n");
    publicKeyStrWriter.write(publicKeyEncoded)
    publicKeyStrWriter.write("\n")
    publicKeyStrWriter.write("-----END PUBLIC KEY-----\n");
    publicKeyStrWriter.close();


    builder.publicKey = publicKeyStrWriter.toString()
    builder.ownerSignature = ownerSignature.toByteString()
    if (parentSignature != null) {
      builder.parentSignature = parentSignature.toByteString()
    }
    if (parent != null) {
      builder.parent = parent.export()
    }
    return builder.build()
  }


  val isSelfSigned: Boolean
    get() = parent == null

  override fun equals(other: Any?): Boolean {
    return if (other is SSICertUtilities) {
      (
              this.isSelfSigned == other.isSelfSigned
                      && this.publicKey == other.publicKey
                      && this.credentialText == other.credentialText
                      && this.ownerSignature.contentEquals(other.ownerSignature)
                      && this.parentSignature.contentEquals(other.parentSignature)
                      && this.parent == other.parent
              )
    } else {
      false
    }
  }
  
  companion object {
    private const val KEY_ALGORITHM = "RSA"
    private const val HASH_ALGORITHM = "SHA-256"

    fun create(
      publicKey: RSAPublicKey,
      ownerPrivateKey: RSAPrivateKey,
      credentialText: String,
    ): SSICertUtilities {
      val ownerSignature = encrypt(getOwnerSignableText(publicKey, credentialText), ownerPrivateKey)
      return SSICertUtilities(publicKey, credentialText, ownerSignature, null, null)
    }

    fun create(
      publicKey: RSAPublicKey,
      ownerPrivateKey: RSAPrivateKey,
      credentialText: String,
      parent: SSICertUtilities,
      parentPrivateKey: RSAPrivateKey
    ): SSICertUtilities {
      val ownerSignature = encrypt(getOwnerSignableText(publicKey, credentialText), ownerPrivateKey)
      val parentSignature = encrypt(
        getParentSignableText(parent, publicKey, credentialText, ownerSignature),
        parentPrivateKey
      )
      return SSICertUtilities(publicKey, credentialText, ownerSignature, parentSignature, parent)
    }

//    fun import(exported: String): SSICert {
//
//    }

//    private fun getOwnerSignableText(publicKey: RSAPublicKey, credentialText: String): String {
//      val keyEncoded = publicKey.encoded.toString()
//      return "[{\"ownerPublicKey\":\"${keyEncoded}\"},{\"credentialText\":\"$credentialText\"}]"
//    }
//
//    private fun getParentSignableText(parent: SSICertUtilities, ownerPublicKey: RSAPublicKey, credentialText: String, ownerSignature: ByteArray): String {
//      val keyEncoded = ownerPublicKey.encoded.toString()
//      val ownerSignatureEncoded = Base64.getEncoder().encodeToString(ownerSignature)
//      return "[{\"parent\":\"${parent.export}\"},{\"owner\":\"$keyEncoded\"},{\"credentialText\":\"$credentialText\"},{\"ownerSignature\":\"$ownerSignatureEncoded\"}]"
//    }

    @Throws(Exception::class)
    private fun encrypt(plainText: String, key: Key): ByteArray {
      val encryptCipher: Cipher = Cipher.getInstance(KEY_ALGORITHM)
      val digester = MessageDigest.getInstance(HASH_ALGORITHM)
      val digest = digester.digest(plainText.toByteArray(StandardCharsets.UTF_8))
      encryptCipher.init(Cipher.ENCRYPT_MODE, key)
      return encryptCipher.doFinal(digest)
    }
  }


}
