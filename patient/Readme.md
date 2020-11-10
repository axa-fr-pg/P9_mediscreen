### Database password encryption

To secure database access the connection password is encrypted with JASYPT and the encryption key is hidden in the JASYPT_ENCRYPTOR_PASSWORD environment variable. 
To generate an encrypted password use the following command : 
mvn jasypt:encrypt-value -Djasypt.encryptor.password="Encryption key" -Djasypt.plugin.value="Password to be encrypted"