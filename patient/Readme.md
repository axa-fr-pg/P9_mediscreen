### Database password encryption

To avoid unexpected database accesses the database connection password is encrypted with JASYPT and the key is hidden in the environment variable JASYPT_ENCRYPTOR_PASSWORD. To generate an encrypted password use the following command : 
java -cp C:\R\org\jasypt\jasypt\1.9.2\jasypt-1.9.2.jar org.jasypt.intf.cli.JasyptPBEStringEncryptionCLI input="your_raw_database_password" password=YourSecretEncryptionKeyToBeStoredInTheEnvVariable algorithm=PBEWithMD5AndDES

