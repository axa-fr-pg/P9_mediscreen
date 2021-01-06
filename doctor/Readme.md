# Doctor
The doctor module contains the dedicated API to manage medical notes in mediscreen.

# Endpoints
The doctor API provides the following URIs :
* GET /notes
* GET /notes/patients/{patId}
* PUT /notes/{noteId}
* POST /notes/patients/{patId}
* POST /notes/random/{expectedNumberOfNotes}
* POST /notes/patients/{patId}/random/{expectedNumberOfNotes}
Please refer to specifications for further details.

# Database configuration

## Host name
To provide a flexible database access in production as well as during code maintenance,
the environment variable DATABASE_HOST is used to define where the database is hosted.

## Password encryption
To secure database access the connection password is encrypted with JASYPT 
and the encryption key is hidden in the JASYPT_ENCRYPTOR_PASSWORD environment variable. 
To generate an encrypted password use the following command : 
mvn jasypt:encrypt-value -Djasypt.encryptor.password="Encryption key" 
    -Djasypt.plugin.value="Password to be encrypted"
    
# Build
To install the module, run the maven goal "install".
This module can be installed directly from the parent project.
