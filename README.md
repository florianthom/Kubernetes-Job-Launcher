# Kubernetes-Job-Launcher
A Spring Boot app demonstrating how to launch and manage Kubernetes Jobs programmatically based on http requests and sqs messages.
Jobs are launched by using target-job-template from k8s-configmap either created by helm definition from this or external app.









Use labels → Track multiple Job runs cleanly


kubernetesjoblauncher

docker build -t kubernetesjoblauncher:latest .

docker run -p 8081:8080 kubernetesjoblauncher:latest -d

helm package ./

helm upgrade --install kubernetesjoblauncher ./kubernetesjoblauncher-0.1.0.tgz


kubectl port-forward deployment/kubernetesjoblauncher 8081:8080


kubectl delete jobs --field-selector status.successful=1


kubectl run -d -it awscli2 --image=amazon/aws-cli --restart=Never --command -- sleep infinity

kubectl exec -it awscli2 -- sh


AWS_ACCESS_KEY_ID="test" \
AWS_SECRET_ACCESS_KEY="test" \
AWS_DEFAULT_REGION="eu-central-1" \
aws --endpoint-url=http://localhost:4566 sqs purge-queue \
--queue-url http://localhost:4571/000000000000/inputqueue


# works from host
AWS_ACCESS_KEY_ID="test" \
AWS_SECRET_ACCESS_KEY="test" \
AWS_DEFAULT_REGION="eu-central-1" \
aws --endpoint-url=http://localhost:4566 sqs purge-queue \
--queue-url http://sqs.eu-central-1.localhost.localstack.cloud:4566/000000000000/inputqueue

# purge queue
AWS_ACCESS_KEY_ID=test \
AWS_SECRET_ACCESS_KEY=test \
AWS_DEFAULT_REGION=eu-central-1 \
aws --endpoint-url=http://localstack:4566 sqs purge-queue \
--queue-url http://sqs.eu-central-1.localhost.localstack.cloud:4566/000000000000/inputqueue


# list all messages in queue
AWS_ACCESS_KEY_ID=test \
AWS_SECRET_ACCESS_KEY=test \
AWS_DEFAULT_REGION=eu-central-1 \
aws --endpoint-url=http://localstack:4566 sqs receive-message \
--queue-url http://localstack:4566/000000000000/inputqueue \
--max-number-of-messages 10 \
--visibility-timeout 0 \
--wait-time-seconds 0