docker run -it --rm \
-p 8080:8080 \
-e 00000_DB_USER_NAME="root" \
-e 00000_DB_PASSWORD="" \
-e 00000_DB_USER_NAME="asia_mile" \
-e 00000_DB_PORT="3306" \
aml-00000-mileage-calculator-demo
