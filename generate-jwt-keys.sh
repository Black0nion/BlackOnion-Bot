cd src/main/resources/keys || exit

openssl ecparam -name prime256v1 -genkey -noout -out key_pair.pem
openssl pkcs8 -topk8 -nocrypt -in key_pair.pem -out private_key.pem
openssl ec -in private_key.pem -pubout -out public_key.pem