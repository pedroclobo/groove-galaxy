# T52 GrooveGalaxy Project Report

## 1. Introduction

(_Provide a brief overview of your project, including the business scenario and the main components: secure documents, infrastructure, and security challenge._)

(_Include a structural diagram, in UML or other standard notation._)

## 2. Project Development

### 2.1. Secure Document Format

#### 2.1.1. Design

The custom cryptographic library addresses GrooveGalaxy business requirements of authenticating the song data, and ensuring the confidentiality of the song's content. The library was implemented under the assumption that the user and service share a secret symmetric key.

To achieve integrity, an HMAC-SHA256 is computed over the song data and metadata (which includes the freshness token), using the shared secret key. The Base64 encoded HMAC is included in the JSON object as the value of the `MIC` key.

Freshness is also added to the document to prevent replay attacks. When protecting the document, the current timestamp is added to the JSON object as the value of the `timestamp` key. The timestamp is checked when checking and unprotecting the file. If the timestamp is older than 30 seconds, the document is rejected.

The integrity and freshness given to the document, guarantee the song data authenticity.

The HMAC-SHA256 algorithm was chosen as it guarantees integrity while not using ciphers, which yeilds a better performance compared to other solutions that use ciphers, like a message integrity code. The hash is computed using the secret shared key, making the hash calculation impossible without the key. The SHA256 algorithm was chosen as it is a widely used hash function, and it is considered secure.

A timestamp was used to guarantee freshness, as the sender and receiver's clock will be loosely synchronized and, unlike a nonce or a counter, it does not require any additional state to be stored.

To ensure confidentiality, the song data is ciphered using the secret shared key. The chosen cipher is AES with the CBC block cipher mode and PKCS5 padding. The initialization vector is randomly generated and included in the JSON object as the value of the `initialization-vector` key. A new initialization vector is generated for each document. The Base64 encoded ciphered song data is then included in the JSON object as the value of the `mediaContent` key.

The AES algorithm was chosen as it a robust symmetric encryption protocol. The CBC mode was chosen as, unlike ECB, doesn't leave patterns of the plaintext in the ciphertext, as long as the initialization vector is not reused across documents. PKCS#5 padding was chosen as it is a widely used padding scheme.

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
      "mediaContent": "sRtLuIjWgOhgjgdViR6D9N8mt8I61VFTgVzZ47CE/JJ0PCzQQXFP+kNhJuj76QVV6qg8JSgBT0fXUEvjCIQMIawAumco0rkHvbXTJJkTo4UDDY+tH6j4VccTsqazAJUpw5xGtM9/OgNe9hzQIY0FBYo7dBeHkVQecunpqJcPfbFErbO/pposoiEayVoGjHCTv1ZQwTRwfwGCYK4W+uHsM3ZtVTJFtlr8tmX0bOBkVMdqtZhkQGmijrVZsDVlwxha6bU4TdM1xPWbNbxiZ4aLQ0C+sMGEPmdy+i9tuhBikkDC4qV95xiHcVIumfny/b6T77Ow3bOPxdHYpj2yI/DB3E6OLzgEcUjgCjUj6J3PSRLWfabyvQ6RlNKz9QvGNRyx5AgZO7Oa7tWoAulNj28LNJacgu2XpMIoD4bee3jyNGPqFFJvblPEN9bcDRWrKhu8qCHTbXhtFGdlo53ocEYMToO/UWdxu+JPn3W7pX4NnuOD0zOZQgI86NYDyIkMoteT0vj3jv7OOE+RGwB6AOQj6OjgNxj1XV56nzXRpp97d6RlXiNa2WmbmxSTH9Z7HC/D"
    }
  },
  "metadata": {
    "cipher": {
      "algorithm": "AES",
      "block-mode": "CBC",
      "padding": "PKCS5Padding",
      "initialization-vector": "oDIZO7isxqNFAfjNQdVOeg\u003d\u003d"
    },
    "mic": {
      "algorithm": "HmacSHA256",
      "timestamp": 1701253988903
    }
  },
  "MIC": "symKICBi4DwbXvnLEzP/4jUxXvyapCjlaoWjlCQDxDE\u003d"
}
```

#### 2.1.2. Implementation

The chosen language to implement the custom cryptographic library is Java. The used cyptographic library is the Java Cryptography API.

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

(_Describe the new requirements introduced in the security challenge and how they impacted your original design._)

#### 2.3.2. Attacker Model

(_Define who is fully trusted, partially trusted, or untrusted._)

(_Define how powerful the attacker is, with capabilities and limitations, i.e., what can he do and what he cannot do_)

#### 2.3.3. Solution Design and Implementation

(_Explain how your team redesigned and extended the solution to meet the security challenge, including key distribution and other security measures._)

(_Identify communication entities and the messages they exchange with a UML sequence or collaboration diagram._)  

## 3. Conclusion

(_State the main achievements of your work._)

(_Describe which requirements were satisfied, partially satisfied, or not satisfied; with a brief justification for each one._)

(_Identify possible enhancements in the future._)

(_Offer a concluding statement, emphasizing the value of the project experience._)

## 4. Bibliography

(_Present bibliographic references, with clickable links. Always include at least the authors, title, "where published", and year._)

----
END OF REPORT
