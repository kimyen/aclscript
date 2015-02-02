# aclscript
# build
gradle clean install

# run
gradle run -Pstack=<local/staging/prod> -Pusername=<synapseAdminUsername> -Ppassword=<password> -PfilePath=<filePath>
