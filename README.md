# aclscript
# To build
gradle clean install

# To run
gradle run -Pstack=local/staging/prod -Pusername=synapseAdminUsername -PapiKey=apiKey -PfilePath=filePath

# Create input file
Run this command in the prod database

SELECT ID, 'ACCESS_CONTROL_LIST' AS OBJECT_TYPE, ETAG, 'UPDATE' AS CHANGE_TYPE
FROM ACL 
WHERE ID NOT IN (SELECT OBJECT_ID FROM CHANGES WHERE OBJECT_TYPE = "ACCESS_CONTROL_LIST");

Export the output of this command to an csv file

NOTE: Remember to remove header of the csv file.
