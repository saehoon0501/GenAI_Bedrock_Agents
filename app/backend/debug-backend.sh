#Change to azure.env directory
cd ../../infra/.aws

# Export aws .env file from current environment
echo "Loading aws .env file from current environment..."
while IFS='=' read -r key value; do
    value=$(echo "$value" | sed 's/^"//' | sed 's/"$//')
    export "$key=$value"
    echo "export $key=$value"
done <<EOF
$(cat .env)
EOF

#Back to backend directory
cd ../../app/backend

# Optional: Print exported variables for verification
echo "Exported environment variables:"
env | grep -E '^AWS_|^spring\.'

# Run Spring Boot application using Maven with remote debugging enabled
echo "Starting Spring Boot application with remote debugging enabled on port 5005..."
mvn spring-boot:run -Dspring-boot.run.profiles=dev -Dspring-boot.run.jvmArguments="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005"
