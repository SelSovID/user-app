package com.example.selsovid

import java.security.Key
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import java.util.*
import javax.crypto.Cipher
import java.nio.charset.StandardCharsets
import java.security.MessageDigest


class SSICert(
  val publicKey: RSAPublicKey,
  val credentialText: String,
  val ownerSignature: ByteArray,
  val parentSignature: ByteArray? = null,
  val parent: SSICert? = null,
) {

  val export: String
          get() = "[{\"parent\":\"${parent?.export}\"},{\"publicKey\":\"${publicKey.encoded}\"},{\"credentialText\":\"$credentialText\"},{\"ownerSignature\":\"${Base64.getEncoder().encodeToString(ownerSignature)}\"},{\"parentSignature\":\"${if (parentSignature == null) "null" else Base64.getEncoder().encodeToString(parentSignature)}\"}]"

  val isSelfSigned: Boolean
    get() = parent == null

  override fun equals(other: Any?): Boolean {
    return if (other is SSICert) {
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
    ): SSICert {
      val ownerSignature = encrypt(getOwnerSignableText(publicKey, credentialText), ownerPrivateKey)
      return SSICert(publicKey, credentialText, ownerSignature, null, null)
    }

    fun create(
      publicKey: RSAPublicKey,
      ownerPrivateKey: RSAPrivateKey,
      credentialText: String,
      parent: SSICert,
      parentPrivateKey: RSAPrivateKey
    ): SSICert {
      val ownerSignature = encrypt(getOwnerSignableText(publicKey, credentialText), ownerPrivateKey)
      val parentSignature = encrypt(
        getParentSignableText(parent, publicKey, credentialText, ownerSignature),
        parentPrivateKey
      )
      return SSICert(publicKey, credentialText, ownerSignature, parentSignature, parent)
    }

//    fun import(exported: String): SSICert {
//
//    }

    private fun getOwnerSignableText(publicKey: RSAPublicKey, credentialText: String): String {
      val keyEncoded = publicKey.encoded.toString()
      return "[{\"ownerPublicKey\":\"${keyEncoded}\"},{\"credentialText\":\"$credentialText\"}]"
    }

    private fun getParentSignableText(parent: SSICert, ownerPublicKey: RSAPublicKey, credentialText: String, ownerSignature: ByteArray): String {
      val keyEncoded = ownerPublicKey.encoded.toString()
      val ownerSignatureEncoded = Base64.getEncoder().encodeToString(ownerSignature)
      return "[{\"parent\":\"${parent.export}\"},{\"owner\":\"$keyEncoded\"},{\"credentialText\":\"$credentialText\"},{\"ownerSignature\":\"$ownerSignatureEncoded\"}]"
    }

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
