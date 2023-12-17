# T52 GrooveGalaxy Project Report

## 1. Introduction

(_Provide a brief overview of your project, including the business scenario and the main components: secure documents, infrastructure, and security challenge._)

(_Include a structural diagram, in UML or other standard notation._)

## 2. Project Development

### 2.1. Secure Document Format

#### 2.1.1. Design

The custom cryptographic library addresses GrooveGalaxy's business requirements of authenticating the song data, and ensuring the confidentiality of the song's content. The library was implemented under the assumption that the user and service share a secret symmetric key.

To achieve integrity, an HMAC-SHA256 is computed over the song data and metadata (which includes the freshness token), using the shared secret key. The Base64 encoded HMAC is included in the JSON object as the value of the `MIC` key.

Freshness is also added to the document to prevent replay attacks. When protecting the document, the current timestamp is added to the JSON object as the value of the `timestamp` key. The timestamp is checked when checking and unprotecting the file. If the timestamp is older than 30 seconds, the document is rejected.

The integrity and freshness given to the document, guarantee the song data authenticity.

The HMAC-SHA256 algorithm was chosen as it guarantees integrity while not using ciphers, which yields a better performance compared to other solutions that use ciphers, like a message integrity code. The hash is computed using the secret shared key, making the hash calculation impossible without the key. The SHA256 algorithm was chosen as it is a widely used hash function, and it is considered secure.

A timestamp was used to guarantee freshness, as the sender and receiver's clock will be loosely synchronized and, unlike a nonce or a counter, it does not require any additional state to be stored.

To ensure confidentiality, the song data is ciphered using the secret shared key. The chosen cipher is AES with the CTR block cipher mode and no padding. The initialization vector is randomly generated and included in the JSON object as the value of the `initialization-vector` key. A new initialization vector is generated for each document. The Base64 encoded ciphered song data is then included in the JSON object as the value of the `mediaContent` key.

The AES algorithm was chosen as it a robust symmetric encryption protocol. The CTR mode was chosen as, unlike ECB, doesn't leave patterns of the plaintext in the ciphertext, as long as the initialization vector is not reused across documents. It also the advantage of providing resynchronization which, unlike with CBC, allows for random access, suitable for scenarios where playback needs to start in the middle of a stream. As it is a stream cipher, padding is not required, providing a better performance in this regard compared to other block ciphers.

An example of a protected file is shown below:

```json
{
  "data": {
    "media": {
      "mediaInfo": {
        "owner": "Bob",
        "format": "WAV",
        "artist": "Alison Chains",
        "title": "Man in the Bin",
        "genre": [
          "Grunge",
          "Alternative Metal"
        ]
      },
      "mediaContent": "jfexBdtaVPxdClBayBofE+Cw79m29xq4c4h2iDcChQ6OZaTnvaKSf3iM/OKUb/RbXKIs1x7VpsAcaR6kfxSKYNXm6i0+J5Q+J7XOzcAs1576390s+rpSogLZtKSYdIoQwr+aefQU6lGaEXyLjWeVhUow6dmQRBJicEaUZpOXRvtMmM6dpX49QI8lekJvXyTGndsLIDgQ5SngT9Wq6IHdZ8Tvo3BrvuqQHuyc/AAQpa6HjIhyuh09C2kJt9SKtAU2peNHJ8J8kV2NXAhU13M8LeT5HDvvhooQWffRah3abeS08S72cmMiLDo3wlxzM32DpTOaMuVVHuaQriEnk02bWFiJ4zvxVAi6yfbtImfj7b4Njaxe8ND+tXgrWrdASREQJAn0gl2aj1+EN31+ITwUlmUbaq6tyCjD/Annchdtbhv8HsGxMWjZMZceZx4MDYF/8LNPYuGv8ImlYnrxX2VJtJlZRsStTpJ0MQHSA9/id5lSHDiMwJBTjMzzNxQofL4YomBg17TDKQnuQAJ7Bmz2xTKV+WYh40btdwmU7fbkCMg2bDOJTpGzk7Dv"
    }
  },
  "metadata": {
    "cipher": {
      "algorithm": "AES",
      "block-mode": "CTR",
      "padding": "NoPadding",
      "initialization-vector": "slRLVrSnWODt2SomtwsJqA\u003d\u003d"
    },
    "mic": {
      "algorithm": "HmacSHA256",
      "timestamp": 1702224124362
    }
  },
  "MIC": "/B/X501hRSatMg6ZhahN3SBdBRpTp8/OU1KDoM/zT6w\u003d"
}
```

#### 2.1.2. Implementation

The chosen language to implement the custom cryptographic library is Java. The used cryptographic library is the Java Cryptography API.

(_Detail the implementation process, including the programming language and cryptographic libraries used._)

(_Include challenges faced and how they were overcome._)

### 2.2. Infrastructure

#### 2.2.1. Network and Machine Setup

(_Provide a brief description of the built infrastructure._)

(_Justify the choice of technologies for each server._)

#### 2.2.2. Server Communication Security

(_Discuss how server communications were secured, including the secure channel solutions implemented and any challenges encountered._)

(_Explain what keys exist at the start and how are they distributed?_)

### 2.3. Security Challenge

#### 2.3.1. Challenge Overview

The security challenge introduced the need to use a cryptographic solution that allow playback to quickly start in the middle of an audio stream, without compromising security.

The original solution used the CBC block cipher mode, in which the ciphertext of a block is dependent on the ciphertext of all blocks before it. It makes it impossible to start playback in the middle of a stream, as the decryption of a block depends on the previous block. This means that the entire stream needs to be decrypted before playback can start.

(_Describe the new requirements introduced in the security challenge and how they impacted your original design._)

#### 2.3.2. Attacker Model

(_Define who is fully trusted, partially trusted, or untrusted._)

(_Define how powerful the attacker is, with capabilities and limitations, i.e., what can he do and what he cannot do_)

#### 2.3.3. Solution Design and Implementation

To allow the playback to quickly start in the middle of an audio stream, the cryptographic solution was changed to use the CTR block cipher mode. This mode allows for random access, as each cipher block is generated independently from the others. This means that the decryption of a block can be done without decrypting the previous blocks, allowing for playback to start in the middle of a stream.

(_Explain how your team redesigned and extended the solution to meet the security challenge, including key distribution and other security measures._)

(_Identify communication entities and the messages they exchange with a UML sequence or collaboration diagram._)  

## 3. Conclusion

(_State the main achievements of your work._)

(_Describe which requirements were satisfied, partially satisfied, or not satisfied; with a brief justification for each one._)

(_Identify possible enhancements in the future._)

(_Offer a concluding statement, emphasizing the value of the project experience._)

## 4. Bibliography

[Wikipedia - Block Cipher Modes of Operation](https://en.wikipedia.org/wiki/Block_cipher_mode_of_operation)

(_Present bibliographic references, with clickable links. Always include at least the authors, title, "where published", and year._)

----
END OF REPORT
