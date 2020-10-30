### Database password encryption

To secure database access the connection password is encrypted with JASYPT and the encryption key is hidden in the JASYPT_ENCRYPTOR_PASSWORD environment variable. To generate an encrypted password use the following command : 
java -cp /C/R/org/jasypt/jasypt/1.9.2/jasypt-1.9.2.jar org.jasypt.intf.cli.JasyptPBEStringEncryptionCLI input="your_raw_database_password" password=EncryptionKey algorithm=PBEWITHHMACSHA512ANDAES_256

mvn jasypt:encrypt-value -Djasypt.encryptor.password="Encryption key" -Djasypt.plugin.value="Password to be encrypted"